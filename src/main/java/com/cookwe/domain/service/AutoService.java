package com.cookwe.domain.service;

import com.cookwe.data.model.LogAutoServiceModel;
import com.cookwe.data.repository.interfaces.LogAutoServiceRepository;
import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.utils.errors.RestError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cookwe.utils.image.CompressionUtils.compressByteImage;
import static com.cookwe.utils.image.CompressionUtils.encodeBase64Image;
import static com.cookwe.utils.sting.JsonUtilsProcessor.processEquationJsonString;
import static com.cookwe.utils.sting.UnitNormalizer.processRecipeJsonString;

@Service
@Data
@Slf4j
public class AutoService {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;
    private final LogAutoServiceRepository logAutoServiceRepository;

    @Value("${cook-we.perplexity.secret}")
    private String PERPLEXITY_API_SECRET;

    private static final double MAX_SIZE_MB = 2.0;

    public AutoService(RestTemplate restTemplate, ObjectMapper objectMapper, LogAutoServiceRepository logAutoServiceRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.logAutoServiceRepository = logAutoServiceRepository;
    }

    public class RequestVision {
        public String model;
        public List<Message> messages;
        public int max_tokens;

        RequestVision(String model, String image, int max_tokens) {
            this.max_tokens = max_tokens;
            this.model = model;

            messages = new ArrayList<>();

            String script =
                "Je voudrais que tu me donnes la recette sur cette image sous forme d'un JSON (sans commentaires). S'il manque des informations, tu n'es pas obligé de tout remplir. Je veux tout en français. \n" +
                    "{\n" +
                    "  \"name\": \"Nom de la recette\",\n" +
                    "  \"time\": \"Estimation du temps de préparation en minutes (number) default 0\",\n" +
                    "  \"portions\": \"Pour combien de personnes (number) default 0\",\n" +
                    "  \"season\": \"SPRING | SUMMER | AUTUMN | WINTER | ALL \",\n" +
                    "  \"type\": \"ENTREE | PLAT | DESSERT \",\n" +
                    "  \"steps\": [\n" +
                    " {\"text\": \"Étape 1\"},\n" +
                    " {\"text\": \"Étape 2\"}\n" +
                    "],\n" +
                    "  \"ingredients\": [\n" +
                    "  {\n" +
                    "    \"name\": \"Nom de l'ingrédient\",\n" +
                    "    \"quantity\": \"Quantité (nombre valide, decimal avec virgule)\",\n" +
                    "    \"unit\": \"GRAM | MILLILITER | TEASPOON | TABLESPOON | CUP | PIECE | POT | PINCH | SACHET \"\n" +
                    "  }\n" +
                    "]\n" +
                    "}";

            Content1 content_1 = new Content1("text", script);
            Content2 content_2 = new Content2(
                "image_url",
                new ContentImage(image)
            );

            List<Object> contents = new ArrayList<>();
            contents.add(content_1);
            contents.add(content_2);

            messages.add(new Message("user", contents));

        }
    }

    @AllArgsConstructor
    public class Message {
        public String role;
        public List<Object> content;
    }

    @AllArgsConstructor
    public class Content1 {
        public String type;
        public String text;
    }

    @AllArgsConstructor
    public class Content2 {
        public String type;
        public ContentImage image_url;
    }

    @AllArgsConstructor
    public class ContentImage {
        public String url;
    }

    @AllArgsConstructor
    public class MessageResponse {
        public String content;
        public String role;
    }

    private byte[] compressImageIfNecessary(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        double fileSizeInMbit = imageBytes.length * 8.0 / 1_000_000.0;

        if (fileSizeInMbit > MAX_SIZE_MB) {
            return compressByteImage(imageBytes);
        }

        return imageBytes;
    }

    public RecipeDetailDTO generateRecipeEntityWithPicture(MultipartFile file, Long userId) throws IOException {
        byte[] compressedImageBytes = compressImageIfNecessary(file);

        RequestVision request = new RequestVision(
            "gpt-4o-mini",
            "data:image/jpeg;base64," + encodeBase64Image(compressedImageBytes),
            2000
        );

        log.info("Request: {}", request);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
            "https://api.openai.com/v1/chat/completions",
            request,
            JsonNode.class
        );

        log.info("API response: {}", responseEntity.toString());

        JsonNode responseBody = responseEntity.getBody();
        if (responseBody == null) {
            throw new RuntimeException("API response is null");
        }

        Optional<JsonNode> recipeDetailNode = Optional.ofNullable(responseBody.findValue("content"));
        if (recipeDetailNode.isPresent()) {

            LogAutoServiceModel logAutoServiceModel = LogAutoServiceModel.builder()
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .pictureSize(BigDecimal.valueOf(compressedImageBytes.length * 8.0 / 1_000_000.0))
                .apiResponse(recipeDetailNode.get().asText())
                .exitCode(responseEntity.getStatusCode().toString())
                .tokenCount(responseBody.findValue("total_tokens").asText())
                .isParseSuccess(true)
                .build();

            logAutoServiceRepository.save(logAutoServiceModel);

            log.info("Recipe detail node: {}", recipeDetailNode.get().asText());

            String jsonString = processEquationJsonString(recipeDetailNode.get().asText());

            try {
                return objectMapper.readValue(processRecipeJsonString(jsonString), RecipeDetailDTO.class);
            } catch (Exception e) {
                log.warn("Direct mapping failed, attempting to extract JSON from text.", e);
            }

            Pattern jsonPattern = Pattern.compile("```json\\s*(\\{.*\\})\\s*```", Pattern.DOTALL);
            Matcher matcher = jsonPattern.matcher(jsonString);

            if (matcher.find()) {
                String jsonStringMatch = matcher.group(1);
                log.info("Extracted JSON: {}", jsonStringMatch);
                try {
                    return objectMapper.readValue(processRecipeJsonString(jsonStringMatch), RecipeDetailDTO.class);
                } catch (Exception e) {
                    log.warn("Direct mapping failed, attempting to extract JSON from text.", e);
                    logAutoServiceModel.setIsParseSuccess(false);
                    logAutoServiceModel.setException(e.getMessage());
                    logAutoServiceRepository.save(logAutoServiceModel);

                    throw RestError.INTERNAL_SERVER_ERROR.get();
                }
            } else {
                logAutoServiceModel.setIsParseSuccess(false);
                logAutoServiceModel.setException("No JSON found in the response content.");
                logAutoServiceRepository.save(logAutoServiceModel);

                throw new RuntimeException("No JSON found in the response content.");
            }
        } else {
            throw new RuntimeException("Failed to parse the API response");
        }
    }


    public RecipeDetailDTO  generateRecipeEntityWithUrl(String url,Long userId ) throws JsonProcessingException {
        log.info("URL: {}", url);
        String prompt = "Analyse l'URL suivante : " + url + " et génère une recette en français sous forme de JSON avec la structure suivante :\n" +
                "{\n" +
                "  \"name\": \"Nom de la recette\",\n" +
                "  \"time\": Estimation du temps de préparation en minutes (nombre),\n" +
                "  \"portions\": Pour combien de personnes (nombre),\n" +
                "  \"season\": \"SPRING\" ou \"SUMMER\" ou \"AUTUMN\" ou \"WINTER\" ou \"ALL\",\n" +
                "  \"type\": \"ENTREE\" ou \"PLAT\" ou \"DESSERT\",\n" +
                "  \"steps\": [\n" +
                "    {\"text\": \"Étape 1\"},\n" +
                "    {\"text\": \"Étape 2\"}\n" +
                "  ],\n" +
                "  \"ingredients\": [\n" +
                "    {\n" +
                "      \"name\": \"Nom de l'ingrédient\",\n" +
                "      \"quantity\": Quantité (nombre décimal avec virgule),\n" +
                "      \"unit\": \"GRAM\" ou \"MILLILITER\" ou \"TEASPOON\" ou \"TABLESPOON\" ou \"CUP\" ou \"PIECE\" ou \"POT\" ou \"PINCH\" ou \"SACHET\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "Si certaines informations ne sont pas disponibles, laisse les champs vides. Respecte strictement les énumérations données pour season, type et unit.";

        String apiKey = PERPLEXITY_API_SECRET;
        String apiUrl = "https://api.perplexity.ai/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "llama-3.1-sonar-large-128k-online");
        requestBodyMap.put("messages", List.of(message));

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);


        String jsonResponse = response.getBody();
        log.info("API response: {}", jsonResponse);

        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        String content = rootNode.path("choices").get(0).path("message").path("content").asText();
        String totalTokens = rootNode.path("usage").path("total_tokens").asText();

        log.info("Content: {}", content);
        log.info("Total tokens: {}", totalTokens);

        if (content != null) {

            LogAutoServiceModel logAutoServiceModel = LogAutoServiceModel.builder()
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .pictureSize(null)
                    .apiResponse(response.getBody())
                    .exitCode(response.getStatusCode().toString())
                    .tokenCount(totalTokens)
                    .isParseSuccess(true)
                    .build();

            logAutoServiceRepository.save(logAutoServiceModel);

            log.info("Recipe detail node: {}", content);

            String jsonString = processEquationJsonString(content);

            try {
                return objectMapper.readValue(processRecipeJsonString(jsonString), RecipeDetailDTO.class);
            } catch (Exception e) {
                log.warn("Direct mapping failed, attempting to extract JSON from text.", e);
            }

            Pattern jsonPattern = Pattern.compile("```json\\s*(\\{.*\\})\\s*```", Pattern.DOTALL);
            Matcher matcher = jsonPattern.matcher(jsonString);

            if (matcher.find()) {
                String jsonStringMatch = matcher.group(1);
                log.info("Extracted JSON: {}", jsonStringMatch);
                try {
                    return objectMapper.readValue(processRecipeJsonString(jsonStringMatch), RecipeDetailDTO.class);
                } catch (Exception e) {
                    log.warn("Direct mapping failed, attempting to extract JSON from text.", e);
                    logAutoServiceModel.setIsParseSuccess(false);
                    logAutoServiceModel.setException(e.getMessage());
                    logAutoServiceRepository.save(logAutoServiceModel);

                    throw RestError.INTERNAL_SERVER_ERROR.get();
                }
            } else {
                logAutoServiceModel.setIsParseSuccess(false);
                logAutoServiceModel.setException("No JSON found in the response content.");
                logAutoServiceRepository.save(logAutoServiceModel);

                throw new RuntimeException("No JSON found in the response content.");
            }
        } else {
            throw new RuntimeException("Failed to parse the API response");
        }


    }

}

package com.cookwe.domain.service;

import com.cookwe.data.model.LogAutoServiceModel;
import com.cookwe.data.repository.interfaces.LogAutoServiceRepository;
import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.utils.errors.RestError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cookwe.utils.sting.UnitNormalizer.processRecipeJsonString;

@Service
@Data
@Slf4j
public class AutoService {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;
    private final LogAutoServiceRepository logAutoServiceRepository;

    private static final double MAX_SIZE_MB = 4.0;
    public AutoService(RestTemplate restTemplate, ObjectMapper objectMapper, LogAutoServiceRepository logAutoServiceRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.logAutoServiceRepository = logAutoServiceRepository;
    }

    public String encodeImage(byte[] bytes) throws IOException {
        return Base64.encodeBase64String(bytes);
    }

    public String processEquationJsonString(String jsonString) {
        Pattern fractionPattern = Pattern.compile("(\\d+)/(\\d+)");
        Matcher matcher = fractionPattern.matcher(jsonString);

        StringBuilder result = new StringBuilder();

        int lastMatchEnd = 0;
        while (matcher.find()) {
            result.append(jsonString, lastMatchEnd, matcher.start());

            String numerator = matcher.group(1);
            String denominator = matcher.group(2);
            double fractionValue = Double.parseDouble(numerator) / Double.parseDouble(denominator);

            result.append(fractionValue);
            lastMatchEnd = matcher.end();
        }

        result.append(jsonString, lastMatchEnd, jsonString.length());
        return result.toString();
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
                    "    \"unit\": \"GRAM | MILLILITER | TEASPOON | TABLESPOON | CUP | PIECE | POT | PINCH \"\n" +
                    "  }\n" +
                    "]\n" +
                    "}";

            Content1 content_1 = new Content1("text", script);
            Content2 content_2 = new Content2(
                "image_url",
                new ContentImage(image)
                //                    "data:image/jpeg;base64," +

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

    @AllArgsConstructor
    public class ChoicesResponse {
        public MessageResponse message;
        public Integer index;
        public String finish_reason;
    }

    private byte[] compressImageIfNecessary(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        double fileSizeInMbit = imageBytes.length * 8.0 / 1_000_000.0;

        if (fileSizeInMbit > MAX_SIZE_MB) {
            return compressImage(imageBytes);
        }

        return imageBytes;
    }

    private byte[] compressImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        // Create a new compressed image
        BufferedImage compressedImage = new BufferedImage(
            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = compressedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, null);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpeg", baos);

        return baos.toByteArray();
    }


    public void printImageSizeInMbOrKb(byte[] imageBytes) throws IOException {
        int fileSizeInBytes = imageBytes.length;
        double fileSizeInKB = fileSizeInBytes / 1024.0;
        double fileSizeInMbit = fileSizeInBytes * 8.0 / 1_000_000.0;

        log.info("Image size: {} KB", fileSizeInKB);
        log.info("Image size: {} Mbit", fileSizeInMbit);
    }

    public RecipeDetailDTO generateRecipeEntityWithPicture(MultipartFile file, Long userId) throws IOException {
        byte[] compressedImageBytes = compressImageIfNecessary(file);

        RequestVision request = new RequestVision(
            "gpt-4o-mini",
            "data:image/jpeg;base64," + encodeImage(compressedImageBytes),
            800
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

            logAutoServiceRepository.save(
                LogAutoServiceModel.builder()
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .pictureSize(BigDecimal.valueOf(compressedImageBytes.length * 8.0 / 1_000_000.0))
                    .apiResponse(recipeDetailNode.get().asText())
                    .exitCode(responseEntity.getStatusCode().toString())
                    .tokenCount(responseBody.findValue("total_tokens").asText())
                    .build()
            );

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
                    throw RestError.INTERNAL_SERVER_ERROR.get();
                }
            } else {
                throw new RuntimeException("No JSON found in the response content.");
            }
        } else {
            throw new RuntimeException("Failed to parse the API response");
        }
    }

}
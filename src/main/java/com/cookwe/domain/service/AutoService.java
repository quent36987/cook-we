package com.cookwe.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class AutoService {

    // TODO : get the image and reduce the size ?
    // send the script to gtp4 image
    // get the json that we get
    // verify the enum and find the closest
    // send the recipe response to the user
    // TODO V2 - limite the number of request per user with a table and credit ^^ ?

    @Autowired
    private RestTemplate restTemplate;


    public String encode_image(MultipartFile file) throws IOException {
        return Base64.encodeBase64String(file.getBytes());
    }

    public class RequestVision {
        public String model;
        public List<Message> messages;
        public int max_tokens;

        RequestVision(String model, String image, int max_tokens) {
            this.max_tokens = max_tokens;
            this.model = model;

            messages = new ArrayList<>();


            String script = "Je voudrais que tu me donnes la recette sur cette image sous forme d'un JSON. S'il manque des informations, tu n'es pas obligé de tout remplir. Je veux tout en français. \n" +
                    "{\n" +
                    "  \"name\": \"Nom de la recette\",\n" +
                    "  \"time\": \"Estimation du temps de préparation en minutes (number) default 0\",\n" +
                    "  \"portions\": \"Pour combien de personnes (number) default 0\",\n" +
                    "  \"season\": \"SPRING | SUMMER | AUTUMN | WINTER | ALL \",\n" +
                    "  \"type\": \"ENTREE | PLAT | DESSERT \",\n" +
                    "  \"steps\": [\n" +
                    "  \"Étape 1\",\n" +
                    "  \"Étape 2, etc.\"\n" +
                    "],\n" +
                    "  \"ingredients\": [\n" +
                    "  {\n" +
                    "    \"name\": \"Nom de l'ingrédient\",\n" +
                    "    \"quantity\": \"Quantité\",\n" +
                    "    \"unit\": \"GRAM | MILLILITER | TEASPOON | TABLESPOON | CUP | PIECE \"\n" +
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


    @AllArgsConstructor
    public class ResponseVision {
//        public List<ChoicesResponse> choices;
//        public Object usage;
        public String id;
//        public Integer created;
//        public String model;
//        public String object;
    }

    public Object generateRecipeEntityWithPicture(MultipartFile file) throws IOException {
        RequestVision request = new RequestVision(
                "gpt-4-vision-preview",
                "data:image/jpeg;base64," + encode_image(file),
                //"https://www.shutterstock.com/image-vector/vector-100-percent-white-background-260nw-1017193735.jpg",
                800
        );

        System.out.println(request);

        Object response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                request,
                Object.class);

        System.out.println(response);

        // find the ```json xxxx ``` dans la requète (response. et renvoyer cette object
//        if (response != null && !response.choices.isEmpty()) {
//            return response.choices.get(0).message.content;
//        }

        return response;
    }


}

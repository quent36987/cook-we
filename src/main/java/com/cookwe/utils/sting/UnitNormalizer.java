package com.cookwe.utils.sting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public class UnitNormalizer {

    private static final Map<String, String> unitMap = new HashMap<>();

    static {
        unitMap.put("PINCEE", "PINCH");
        unitMap.put("PINCE", "PINCH");
        unitMap.put("GRAMME", "GRAM");
        unitMap.put("GRAMMES", "GRAM");
        unitMap.put("G", "GRAM");
        unitMap.put("KILOGRAMME", "KILOGRAM");
        unitMap.put("KG", "KILOGRAM");
        unitMap.put("KILOGRAMMES", "KILOGRAM");
        unitMap.put("KILOGRAMS", "KILOGRAM");
        unitMap.put("LITRE", "LITER");
        unitMap.put("LITRES", "LITER");
        unitMap.put("L", "LITER");
        unitMap.put("MILLILITRE", "MILLILITER");
        unitMap.put("MILLILITRES", "MILLILITER");
        unitMap.put("ML", "MILLILITER");
        unitMap.put("PACKET", "SACHET");
    }

    public static String normalizeUnit(String unit) {
        return unitMap.getOrDefault(unit.toUpperCase(), unit);
    }

    public static String processRecipeJsonString(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        JsonNode ingredientsNode = rootNode.path("ingredients");
        if (ingredientsNode.isArray()) {
            for (JsonNode ingredientNode : ingredientsNode) {
                JsonNode unitNode = ingredientNode.path("unit");
                if (unitNode.isTextual()) {
                    String originalUnit = unitNode.asText();
                    String normalizedUnit = normalizeUnit(originalUnit);
                    ((ObjectNode) ingredientNode).put("unit", normalizedUnit);
                }
            }
        }

        return objectMapper.writeValueAsString(rootNode);
    }
}

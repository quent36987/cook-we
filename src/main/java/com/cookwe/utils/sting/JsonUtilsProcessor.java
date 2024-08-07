package com.cookwe.utils.sting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtilsProcessor {
    public static String processEquationJsonString(String jsonString) {
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
}

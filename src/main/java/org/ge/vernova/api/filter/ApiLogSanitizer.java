package org.ge.vernova.api.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ApiLogSanitizer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "token",
            "accessToken",
            "refreshToken",
            "authorization"
    );

    private ApiLogSanitizer() {
    }

    public static String sanitize(String body) {

        if (body == null || body.isBlank()) {
            return body;
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);

            sanitizeNode(root);

            return OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);

        } catch (Exception e) {
            // Body may not be JSON.
            return body;
        }
    }

    private static void sanitizeNode(JsonNode node) {

        if (node.isObject()) {

            Iterator<Map.Entry<String, JsonNode>> fields =
                    node.fields();

            while (fields.hasNext()) {

                Map.Entry<String, JsonNode> field =
                        fields.next();

                if (SENSITIVE_FIELDS.contains(field.getKey())) {
                    ((ObjectNode) node)
                            .put(field.getKey(), "********");
                } else {
                    sanitizeNode(field.getValue());
                }
            }
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                sanitizeNode(child);
            }
        }
    }
}
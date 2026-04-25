package org.hhoa.mc.intensify.core;

public final class AttributeModifierIds {
    private static final String PREFIX = "intensify_attribute/";

    private AttributeModifierIds() {}

    public static String buildPath(String attributeId) {
        String normalized = attributeId == null ? "unknown" : attributeId.trim();
        if (normalized.startsWith("attribute.name.")) {
            normalized = normalized.substring("attribute.name.".length());
        }
        if (normalized.isEmpty()) {
            normalized = "unknown";
        }
        normalized = normalized.replace(':', '/');
        normalized = normalized.replaceAll("[^a-z0-9/._-]", "_");
        return PREFIX + normalized;
    }
}

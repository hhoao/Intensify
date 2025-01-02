package org.hhoa.mc.intensify.item;

import java.util.List;

public enum IntensifyStoneType {
    INTENSIFY_STONE("intensify_stone"),
    ENENG_STONE("eneng_stone"),
    STRENGTHENING_STONE("strengthening_stone"),
    PROTECTION_STONE("protection_stone"),
    ETERNAL_STONE("eternal_stone"),
    ;

    private final String identifier;

    IntensifyStoneType(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getIdentifier() {
        return identifier;
    }
}

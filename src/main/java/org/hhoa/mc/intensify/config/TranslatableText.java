package org.hhoa.mc.intensify.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TranslatableText {
    private final String key;

    public TranslatableText(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String get(Object... args) {
        return component(args).getString();
    }

    public MutableComponent component(Object... args) {
        return Component.translatable(key, args);
    }
}

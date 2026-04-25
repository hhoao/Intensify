package org.hhoa.mc.intensify.config;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TranslatableText {
    private final String key;
    private MutableComponent component;
    private String translated;

    public TranslatableText(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String get(Object... args) {
        if (translated == null) {
            translated = I18n.get(key, args);
        }
        return translated;
    }

    public Component component(Object... args) {
        if (component == null) {
            component = Component.translatable(key, args);
        }
        return component;
    }
}

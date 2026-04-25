package org.hhoa.mc.intensify.config;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TranslatableText {
    private final String key;
    private IFormattableTextComponent component;
    private String translated;

    public TranslatableText(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String get(Object... args) {
        if (translated == null) {
            translated = I18n.format(key, args);
        }
        return translated;
    }

    public IFormattableTextComponent component(Object... args) {
        if (component == null) {
            component = new TranslationTextComponent(key, args);
        }
        return component;
    }
}

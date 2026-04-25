package org.hhoa.mc.intensify.config;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TranslatableText {
    private final String key;

    public TranslatableText(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String get(Object... args) {
        return I18n.format(key, args);
    }

    public ITextComponent component(Object... args) {
        return new TextComponentTranslation(key, args);
    }
}

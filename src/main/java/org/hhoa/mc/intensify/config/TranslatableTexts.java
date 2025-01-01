package org.hhoa.mc.intensify.config;

import org.hhoa.mc.intensify.Intensify;

public class TranslatableTexts {
    public static final String STRENGTHENING_TITLE  = withModId("advancement.intensify.strengthening.title");
    public static final String STRENGTHENING_DESCRIPTION  = withModId("advancement.intensify.strengthening.description");
    public static final String ENENG_TITLE  = withModId("advancement.intensify.eneng.title");
    public static final String ENENG_DESCRIPTION= withModId("advancement.intensify.eneng.description");


    public static String withModId(String string) {
        return Intensify.MODID  + "." + string;
    }

}

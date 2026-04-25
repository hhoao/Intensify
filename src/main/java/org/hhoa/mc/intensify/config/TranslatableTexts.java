package org.hhoa.mc.intensify.config;

public class TranslatableTexts {
    public static final TranslatableText STRENGTHENING_ADVANCEMENT_TITLE =
            text("advancement.intensify.strengthening.title");
    public static final TranslatableText STRENGTHENING_ADVANCEMENT_DESCRIPTION =
            text("advancement.intensify.strengthening.description");
    public static final TranslatableText ENENG_ADVANCEMENT_TITLE =
            text("advancement.intensify.eneng.title");
    public static final TranslatableText ENENG_ADVANCEMENT_DESCRIPTION =
            text("advancement.intensify.eneng.description");

    public static final TranslatableText STRENGTHENING_STONE_DESCRIPTION =
            text("item.intensify.strengthening_stone.description");
    public static final TranslatableText STRENGTHENING_STONE_DESCRIPTION_TIP =
            text("item.intensify.strengthening_stone.description.tip");
    public static final TranslatableText ENENG_STONE_DESCRIPTION =
            text("item.intensify.eneng_stone.description");
    public static final TranslatableText PROTECTION_STONE_DESCRIPTION =
            text("item.intensify.protection_stone.description");
    public static final TranslatableText ETERNAL_STONE_DESCRIPTION =
            text("item.intensify.eternal_stone.description");

    public static final TranslatableText ENENG_SUCCESS = text("eneng.success");
    public static final TranslatableText STRENGTHENING_UPGRADE = text("strengthening.upgrade");
    public static final TranslatableText STRENGTHENING_UNCHANGED = text("strengthening.unchanged");
    public static final TranslatableText STRENGTHENING_PROTECTED = text("strengthening.protected");
    public static final TranslatableText STRENGTHENING_DOWNGRADE = text("strengthening.downgrade");
    public static final TranslatableText ETERNAL_SUCCESS = text("eternal.success");
    public static final TranslatableText INTENSIFY_ITEM_TIP = text("item.intensify.stone.tip");

    public static final TranslatableText SET_UPGRADE_MULTIPLIER_TIP =
            text("command.set.upgrade_multiplier");
    public static final TranslatableText SET_STONE_DROP_RATE_TIP =
            text("command.set.stone_dropout_rate");
    public static final TranslatableText SET_ATTRIBUTE_MULTIPLIER_TIP =
            text("command.set.attribute_multiplier");
    public static final TranslatableText ANNOUNCEMENT_STRENGTHENING =
            text("announcement.intensify.strengthening");
    public static final TranslatableText ANNOUNCEMENT_ETERNAL_STONE_DROP =
            text("announcement.intensify.eternal_stone_drop");
    public static final TranslatableText ADVANCEMENT_INTENSIFY_TITLE =
            text("advancement.intensify.intensify.title");
    public static final TranslatableText ADVANCEMENT_INTENSIFY_DESCRIPTION =
            text("advancement.intensify.intensify.description");
    public static TranslatableText ETERNAL_ADVANCEMENT_TITLE =
            text("advancement.intensify.eternal.title");
    public static TranslatableText ETERNAL_ADVANCEMENT_DESCRIPTION =
            text("advancement.intensify.eternal.description");

    public static final TranslatableText ADVANCEMENT_FIRST_ENENG_TITLE =
            text("advancement.intensify.first_eneng.title");
    public static final TranslatableText ADVANCEMENT_FIRST_ENENG_DESCRIPTION =
            text("advancement.intensify.first_eneng.description");

    public static final TranslatableText ADVANCEMENT_FIRST_STRENGTHENING_TITLE =
            text("advancement.intensify.first_strengthening.title");
    public static final TranslatableText ADVANCEMENT_FIRST_STRENGTHENING_DESCRIPTION =
            text("advancement.intensify.first_strengthening.description");

    public static TranslatableText text(String string) {
        return new TranslatableText(string);
    }
}

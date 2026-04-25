package org.hhoa.mc.intensify.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class IntensifySystemTest {
    @Test
    void attributeModifierPathSanitizesNamespacedAttributeIds() throws Exception {
        Class<?> helperClass =
                Class.forName("org.hhoa.mc.intensify.core.AttributeModifierIds");
        Method buildPath = helperClass.getMethod("buildPath", String.class);

        Object path = assertDoesNotThrow(() -> buildPath.invoke(null, "attributeslib:mining_speed"));

        assertEquals("intensify_attribute/attributeslib/mining_speed", path);
    }

    @Test
    void armorModifierPathsAreDifferentPerEquipmentSlot() throws Exception {
        Class<?> helperClass =
                Class.forName("org.hhoa.mc.intensify.core.AttributeModifierIds");
        Method buildPath = helperClass.getMethod("buildPath", String.class, String.class);

        Object headPath =
                assertDoesNotThrow(
                        () -> buildPath.invoke(null, "minecraft:generic.armor", "head"));
        Object chestPath =
                assertDoesNotThrow(
                        () -> buildPath.invoke(null, "minecraft:generic.armor", "chest"));

        assertEquals("intensify_attribute/minecraft/generic.armor/head", headPath);
        assertEquals("intensify_attribute/minecraft/generic.armor/chest", chestPath);
        assertNotEquals(headPath, chestPath);
    }
}

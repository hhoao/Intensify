package org.hhoa.mc.intensify.resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

public class AdvancementRewardResourcesTest {
    @Test
    public void bundledAdvancementsDeclareOneTwelveRewards() throws IOException {
        assertAdvancementRewardsLoot("intensify", "intensify:eneng_stone");
        assertAdvancementRewardsLoot("first_eneng", "intensify:strengthening_stone");
        assertAdvancementRewardsLoot("first_strengthening", "intensify:protection_stone");
    }

    @Test
    public void bundledRewardLootTablesUseOneTwelvePoolNames() throws IOException {
        assertLootTablePoolName("eneng_stone");
        assertLootTablePoolName("strengthening_stone");
        assertLootTablePoolName("protection_stone");
    }

    private static void assertAdvancementRewardsLoot(String id, String lootTable)
            throws IOException {
        String path = "src/main/resources/assets/intensify/advancements/" + id + ".json";
        String content = readSourceResource(path);
        Assert.assertTrue(path, content.contains("\"rewards\""));
        Assert.assertTrue(path, content.contains("\"loot\""));
        Assert.assertTrue(path, content.contains("\"" + lootTable + "\""));
    }

    private static void assertLootTablePoolName(String id) throws IOException {
        String path = "src/main/resources/assets/intensify/loot_tables/" + id + ".json";
        String content = readSourceResource(path);
        Assert.assertTrue(path, content.contains("\"name\": \"main\""));
    }

    private static String readSourceResource(String path) throws IOException {
        return new String(
                Files.readAllBytes(Paths.get(System.getProperty("user.dir"), path)),
                StandardCharsets.UTF_8);
    }
}

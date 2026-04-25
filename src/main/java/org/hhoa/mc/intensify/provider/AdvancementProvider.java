package org.hhoa.mc.intensify.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.EndAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> advancements =
            ImmutableList.of(
                    new EndAdvancements(),
                    new HusbandryAdvancements(),
                    new AdventureAdvancements(),
                    new NetherAdvancements(),
                    new StoryAdvancements());

    public AdvancementProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    public void act(DirectoryCache save) throws IOException {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> resourceLocations = Sets.newHashSet();
        Consumer<Advancement> consumer =
                (advancement) -> {
                    if (!resourceLocations.add(advancement.getId())) {
                        throw new IllegalStateException(
                                "Duplicate advancement " + advancement.getId());
                    } else {
                        Path path = getPath(outputFolder, advancement);

                        try {
                            IDataProvider.save(GSON, save, advancement.copy().serialize(), path);
                        } catch (IOException error) {
                            LOGGER.error("Couldn't save advancement {}", path, error);
                        }
                    }
                };

        this.registerAdvancements(consumer);
    }

    protected void registerAdvancements(Consumer<Advancement> consumer) {
        for (Consumer<Consumer<Advancement>> accept : this.advancements) {
            accept.accept(consumer);
        }
    }

    private static Path getPath(Path path, Advancement advancement) {
        return path.resolve(
                "data/"
                        + advancement.getId().getNamespace()
                        + "/advancements/"
                        + advancement.getId().getPath()
                        + ".json");
    }

    public String getName() {
        return "Advancements";
    }
}

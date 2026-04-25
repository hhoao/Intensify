package org.hhoa.mc.intensify.registry;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.hhoa.mc.intensify.Intensify;

public class AttachmentRegistry {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Intensify.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> FIRST_LOGIN =
            ATTACHMENT_TYPES.register(
                    "first_login",
                    () ->
                            AttachmentType.builder(() -> Boolean.FALSE)
                                    .serialize(Codec.BOOL.fieldOf("value"))
                                    .copyOnDeath()
                                    .build());

    public static void initialize(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

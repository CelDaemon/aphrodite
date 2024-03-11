package net.voidgroup.aphrodite.server.feature;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.math.random.Random;
import net.voidgroup.aphrodite.server.AphroditeServer;
import net.voidgroup.aphrodite.server.event.MinecraftServerEvents;
import net.voidgroup.aphrodite.server.mixin.MinecraftServerAccessor;
import org.apache.commons.lang3.StringUtils;

public class CustomMetadata {
    private static Random random;
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                random = ((MinecraftServerAccessor) server)
                        .getRandom());
        MinecraftServerEvents.MODIFY_MESSAGE_EVENT.register(() ->
                AphroditeServer.CONFIG.mainMessage() +
                AphroditeServer.CONFIG.messages().flatMap(messages ->
                                messages.getRandom(random)
                                        .map(str ->
                                                ",\n" + str))
                        .orElse(StringUtils.EMPTY));
    }
}

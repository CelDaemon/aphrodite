package net.voidgroup.aphrodite.server;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.voidgroup.aphrodite.server.feature.CustomMetadata;
import net.voidgroup.aphrodite.server.feature.DropPlayerHead;

import java.io.IOException;
import java.nio.file.Files;

public class AphroditeServer implements DedicatedServerModInitializer {
    public static final String MOD_ID = "aphrodite-server";
    public static AphroditeConfiguration CONFIG;
    @Override
    public void onInitializeServer() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("aphrodite", "configuration");
            }

            @Override
            public void reload(ResourceManager manager) {
                var path = FabricLoader.getInstance().getConfigDir().resolve(AphroditeServer.MOD_ID + ".jsonc");
                if(Files.exists(path)) {
                    JsonElement jsonObject;
                    try(var reader = Files.newBufferedReader(path)) {
                        jsonObject = JsonHelper.deserialize(reader, true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    CONFIG = AphroditeConfiguration.CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElseThrow();
                }
            }
        });
        DropPlayerHead.init();
        CustomMetadata.init();
    }
}

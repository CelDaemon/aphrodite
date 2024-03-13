package net.voidgroup.aphrodite.server.feature;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.voidgroup.aphrodite.server.AphroditeServer;
import net.voidgroup.aphrodite.server.ConfigurationManager;
import net.voidgroup.aphrodite.server.event.MinecraftServerEvents;
import net.voidgroup.aphrodite.server.mixin.MinecraftServerAccessor;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CustomMetadata {
    private static Random random;
    private static Map<String, ServerMetadata.Favicon> icons;
    private static Optional<ServerMetadata.Favicon> loadFavicon(Path path) {
        Optional<Path> optional = Optional.of(path)
                .filter(Files::isRegularFile);
        return optional.flatMap(path2 -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(path2.toFile());
                Preconditions.checkState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
                Preconditions.checkState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
                return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
            } catch (Exception var3) {
                return Optional.empty();
            }
        });
    }
    public static void init(ConfigurationManager configurationManager) {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("aphrodite", "server_icons");
            }
            @Override
            public void reload(ResourceManager manager) {
                if(icons != null) icons.clear();
                var path = FabricLoader.getInstance().getConfigDir().resolve(AphroditeServer.MOD_ID).resolve("server_icons");
                if(Files.notExists(path)) return;
                try(var files = Files.list(path)) {
                    icons = files
                            .parallel()
                            .filter(file -> file.toString().endsWith(".png")).map(file -> {
                                var split = file.getFileName().toString().split("\\.");
                                return new Pair<>(Arrays.stream(split).limit(split.length - 1).collect(Collectors.joining("")), loadFavicon(file).orElseThrow());
                            })
                            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                random = ((MinecraftServerAccessor) server)
                        .getRandom());
        MinecraftServerEvents.MODIFY_MESSAGE_EVENT.register(() -> {
            if(configurationManager.configuration == null) return null;
            return configurationManager.configuration.mainMessage() +
                    configurationManager.configuration.messages().flatMap(messages ->
                                    messages.getRandom(random)
                                            .map(str ->
                                                    ",\n" + str))
                            .orElse(StringUtils.EMPTY);
        });
        MinecraftServerEvents.MODIFY_ICON_EVENT.register(() -> {
            if(configurationManager.configuration == null) return null;
            return configurationManager.configuration.icons()
                    .flatMap(iconNames -> iconNames.getRandom(random)
                            .map(iconName -> iconName.equals("vanilla") ? null : Objects.requireNonNull(icons.get(iconName), "Icon with id: " + iconName + " not found")))
                    .orElse(null);
        });
    }
}

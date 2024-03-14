package net.voidgroup.aphrodite.server.feature;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class CustomMetadata {
    public static final String DEFAULT_ICON_NAME = "default";
    private static Random random;
    private static final IconManager MANAGER = new IconManager();
    public static boolean hasIcon(String id) {
        if(id.equals(DEFAULT_ICON_NAME)) return true;
        return MANAGER.icons.containsKey(id);
    }
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
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER);
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                random = ((MinecraftServerAccessor) server)
                        .getRandom());
        MinecraftServerEvents.MODIFY_MESSAGE_EVENT.register(() -> Optional.ofNullable(configurationManager.configuration).map(config -> config.mainMessage() +
                configurationManager.configuration.messages().flatMap(messages ->
                                messages.getRandom(random)
                                        .map(str ->
                                                ",\n" + str))
                        .orElse(StringUtils.EMPTY)));
        MinecraftServerEvents.MODIFY_ICON_EVENT.register(() -> Optional.ofNullable(configurationManager.configuration).flatMap(config -> config.icons()
                .flatMap(iconNames -> iconNames.getRandom(random)
                        .flatMap(iconName -> iconName.equals(DEFAULT_ICON_NAME) ? Optional.empty() : Optional.ofNullable(MANAGER.icons.get(iconName))))));
    }
    public static class IconManager implements SimpleResourceReloadListener<Map<String, ServerMetadata.Favicon>> {
        public static final Identifier IDENTIFIER = new Identifier(AphroditeServer.MOD_ID, "server_icon");
        private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(AphroditeServer.MOD_ID).resolve("server_icons");
        public Map<String, ServerMetadata.Favicon> icons;

        @Override
        public Identifier getFabricId() {
            return IDENTIFIER;
        }
        @Override
        public CompletableFuture<Map<String, ServerMetadata.Favicon>> load(ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                if(Files.notExists(PATH)) return null;
                try(var files = Files.list(PATH)) {
                    return files.parallel().filter(file -> file.toString().endsWith(".png")).map(file -> {
                        var split = file.getFileName().toString().split("\\.");
                        return new Pair<>(Arrays.stream(split).limit(split.length - 1).collect(Collectors.joining()), loadFavicon(file).orElseThrow());
                    }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, executor);
        }

        @Override
        public CompletableFuture<Void> apply(Map<String, ServerMetadata.Favicon> data, ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.runAsync(() -> icons = data, executor);
        }
    }
}

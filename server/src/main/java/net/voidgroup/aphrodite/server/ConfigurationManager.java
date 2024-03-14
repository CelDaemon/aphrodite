package net.voidgroup.aphrodite.server;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.dynamic.Codecs;
import net.voidgroup.aphrodite.server.data.WeightedList;
import net.voidgroup.aphrodite.server.feature.CustomMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ConfigurationManager implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(AphroditeServer.MOD_ID + ".jsonc");
    public Configuration configuration;
    public ConfigurationManager() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
    }
    @Override
    public Identifier getFabricId() {
        return new Identifier("aphrodite", "configuration");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(CustomMetadata.IconManager.IDENTIFIER);
    }

    @Override
    public void reload(ResourceManager manager) {
        if(Files.notExists(CONFIG_PATH)) return;
        JsonObject object;
        try(var reader = Files.newBufferedReader(CONFIG_PATH)) {
            object = JsonHelper.deserialize(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var result = Configuration.CODEC.parse(JsonOps.INSTANCE, object);
        result.error().ifPresentOrElse(configurationPartialResult ->
                        LOGGER.error("Failed to load config - " + configurationPartialResult.message()),
                () -> configuration = result.result().orElseThrow());
        configuration.icons.ifPresent(icons -> icons.forEach(icon -> {
            if(!CustomMetadata.hasIcon(icon.value())) LOGGER.error("Icon with id: " + icon.value() + ", not found");
        }));
    }

    public record Configuration(String mainMessage, Optional<WeightedList<String>> messages, Optional<WeightedList<String>> icons) {
        private static final Codec<Configuration> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("main_message").forGetter((Configuration obj) -> obj.mainMessage),
                        Codecs.createStrictOptionalFieldCodec(WeightedList.createCodec(Codec.STRING), "messages").forGetter((Configuration obj) -> obj.messages),
                        Codecs.createStrictOptionalFieldCodec(WeightedList.createCodec(Codec.STRING), "icons").forGetter((Configuration obj) -> obj.icons)
                ).apply(instance, Configuration::new));
    }
}

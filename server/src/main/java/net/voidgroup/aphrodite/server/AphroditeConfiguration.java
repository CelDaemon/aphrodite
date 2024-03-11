package net.voidgroup.aphrodite.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.voidgroup.aphrodite.server.data.WeightedList;

import java.util.Optional;

public record AphroditeConfiguration(String mainMessage, Optional<WeightedList<String>> messages) {
    public static final Codec<AphroditeConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("main_message").forGetter(obj -> obj.mainMessage),
                    WeightedList.createCodec(Codec.STRING).optionalFieldOf("messages").forGetter(obj -> obj.messages)
            ).apply(instance, AphroditeConfiguration::new)
    );
}

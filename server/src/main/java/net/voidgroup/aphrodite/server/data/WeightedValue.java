package net.voidgroup.aphrodite.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record WeightedValue<T>(T value, int weight) {
    public static final int DEFAULT_WEIGHT = 1000;
    public static <T> Codec<WeightedValue<T>> createCodec(PrimitiveCodec<T> valueCodec) {
        return Codecs.either(RecordCodecBuilder.create(instance -> instance.group(
                valueCodec.fieldOf("value").forGetter((WeightedValue<T> obj) -> obj.value),
                Codecs.createStrictOptionalFieldCodec(Codec.INT, "weight", DEFAULT_WEIGHT).forGetter((WeightedValue<T> obj) -> obj.weight)
        ).apply(instance, WeightedValue<T>::new)),
                valueCodec, obj -> new WeightedValue<>(obj, DEFAULT_WEIGHT));
    }
}

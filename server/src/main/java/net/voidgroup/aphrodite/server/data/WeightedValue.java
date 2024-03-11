package net.voidgroup.aphrodite.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WeightedValue<T>(T value, int weight) {
    public static <T> Codec<WeightedValue<T>> createCodec(PrimitiveCodec<T> valueCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                valueCodec.fieldOf("value").forGetter((WeightedValue<T> obj) -> obj.value),
                Codec.INT.optionalFieldOf("weight", 1000).forGetter((WeightedValue<T> obj) -> obj.weight)
        ).apply(instance, WeightedValue<T>::new));
    }
}

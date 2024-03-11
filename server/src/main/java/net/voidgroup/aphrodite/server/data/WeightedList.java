package net.voidgroup.aphrodite.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Optional;

public class WeightedList<T>{
    private final List<WeightedValue<T>> values;
    public WeightedList(List<WeightedValue<T>> values) {
        this.values = values;
        this.totalWeight = values.stream().map(WeightedValue::weight).reduce(0, Integer::sum);
    }
    private final int totalWeight;
    public Optional<T> getRandom(Random random) {
        if(totalWeight < 1) return Optional.empty();
        var rand = random.nextInt(totalWeight);
        for (var message : values) {
            if (rand < message.weight()) return Optional.of(message.value());
            rand -= message.weight();
        }
        return Optional.empty();
    }
    public static <T> Codec<WeightedList<T>> createCodec(PrimitiveCodec<T> valueCodec) {
        return WeightedValue
                .createCodec(valueCodec)
                .listOf()
                .xmap(
                        WeightedList::new,
                        (WeightedList<T> obj) -> obj.values);
    }
}

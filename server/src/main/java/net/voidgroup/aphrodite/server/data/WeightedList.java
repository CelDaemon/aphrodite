package net.voidgroup.aphrodite.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WeightedList<T> implements Iterable<WeightedValue<T>> {
    private List<WeightedValue<T>> values;
    private final int totalWeight;
    public WeightedList(List<WeightedValue<T>> values) {
        this.values = values;
        this.totalWeight = values.stream().map(WeightedValue::weight).reduce(0, Integer::sum);
    }
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
    public void validate(Predicate<T> predicate, Consumer<T> errorRunnable) {
        values = values.stream().filter(obj -> {
            if(!predicate.test(obj.value())) {
                errorRunnable.accept(obj.value());
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }
    @NotNull
    @Override
    public Iterator<WeightedValue<T>> iterator() {
        return values.iterator();
    }
}

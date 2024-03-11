import net.minecraft.util.math.random.Random;
import net.voidgroup.aphrodite.server.data.WeightedList;
import net.voidgroup.aphrodite.server.data.WeightedValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightedListTests {
    private final WeightedList<Byte> list = new WeightedList<>(List.of(new WeightedValue<>((byte) 1, 1000), new WeightedValue<>((byte) 2, 500)));

    @Test
    void accurateOffsets() {
        var random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0);
        var output = list.getRandom(random).orElseThrow();
        Assertions.assertEquals(output,  (byte) 1);
        when(random.nextInt(anyInt())).thenReturn(1000);
        output = list.getRandom(random).orElseThrow();
        System.out.println(output);
        Assertions.assertEquals(output, (byte) 2);
    }
}

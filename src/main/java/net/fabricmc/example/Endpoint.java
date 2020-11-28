package net.fabricmc.example;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Endpoint {
    public static void random(Random r) {
        try {
            Field seed = Random.class.getDeclaredField("seed");
            seed.setAccessible(true);
            seed.set(r, new AtomicLong((1234L ^ 25214903917L) & 281474976710655L));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

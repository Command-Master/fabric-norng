package org.commandmaster.norng.mixin;

import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.UUID;

@Mixin(value = MathHelper.class)
public class UUIDMixin {
	@Shadow @Final private static Random RANDOM;

	@Inject(at = @At("HEAD"), method = "randomUuid(Ljava/util/Random;)Ljava/util/UUID;", cancellable = true)
    private static void randomUUID(Random random, CallbackInfoReturnable<UUID> cir) {
    	random = RANDOM;
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | -9223372036854775808L;
    	cir.setReturnValue(new UUID(l, m));
    }
}

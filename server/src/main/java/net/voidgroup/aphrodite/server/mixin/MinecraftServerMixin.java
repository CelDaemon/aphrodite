package net.voidgroup.aphrodite.server.mixin;

import net.minecraft.server.MinecraftServer;
import net.voidgroup.aphrodite.server.event.MinecraftServerEvents;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @SuppressWarnings("SpellCheckingInspection")
    @Shadow @Nullable private String motd;

    @Redirect(method = "createMetadata", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;motd:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String getMessage(MinecraftServer instance) {
        var message = MinecraftServerEvents.MODIFY_MESSAGE_EVENT.invoker().modifyMessage();
        return message != null ? message : motd;
    }
}

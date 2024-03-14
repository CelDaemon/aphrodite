package net.voidgroup.aphrodite.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.ServerMetadata;

import java.util.Optional;

public class MinecraftServerEvents {
    public static final Event<ModifyMessage> MODIFY_MESSAGE_EVENT = EventFactory.createArrayBacked(
            ModifyMessage.class, listeners -> () -> {
                for(var listener : listeners) {
                    var message = listener.modifyMessage();
                    if(message.isPresent()) return message;
                }
                return Optional.empty();
            }
    );
    public static final Event<ModifyIcon> MODIFY_ICON_EVENT = EventFactory.createArrayBacked(
            ModifyIcon.class, listeners -> () -> {
                for(var listener : listeners) {
                    var icon = listener.modifyIcon();
                    if(icon.isPresent()) return icon;
                }
                return Optional.empty();
            }
    );
    @FunctionalInterface
    public interface ModifyMessage {
        Optional<String> modifyMessage();
    }
    public interface ModifyIcon {
        Optional<ServerMetadata.Favicon> modifyIcon();
    }
}

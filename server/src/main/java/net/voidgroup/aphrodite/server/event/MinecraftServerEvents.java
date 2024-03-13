package net.voidgroup.aphrodite.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.ServerMetadata;

public class MinecraftServerEvents {
    public static final Event<ModifyMessage> MODIFY_MESSAGE_EVENT = EventFactory.createArrayBacked(
            ModifyMessage.class, listeners -> () -> {
                for(var listener : listeners) {
                    var message = listener.modifyMessage();
                    if(message != null) return message;
                }
                return null;
            }
    );
    public static final Event<ModifyIcon> MODIFY_ICON_EVENT = EventFactory.createArrayBacked(
            ModifyIcon.class, listeners -> () -> {
                for(var listener : listeners) {
                    var icon = listener.modifyIcon();
                    if(icon != null) return icon;
                }
                return null;
            }
    );
    @FunctionalInterface
    public interface ModifyMessage {
        String modifyMessage();
    }
    public interface ModifyIcon {
        ServerMetadata.Favicon modifyIcon();
    }
}

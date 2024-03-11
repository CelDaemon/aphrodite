package net.voidgroup.aphrodite.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

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
    public interface ModifyMessage {
        String modifyMessage();
    }
}

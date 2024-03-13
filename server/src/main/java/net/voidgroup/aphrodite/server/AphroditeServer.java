package net.voidgroup.aphrodite.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.voidgroup.aphrodite.server.feature.CustomMetadata;
import net.voidgroup.aphrodite.server.feature.DropPlayerHead;

public class AphroditeServer implements DedicatedServerModInitializer {
    public static final String MOD_ID = "aphrodite";
    private static final ConfigurationManager CONFIGURATION_MANAGER = new ConfigurationManager();
    @Override
    public void onInitializeServer() {
        DropPlayerHead.init();
        CustomMetadata.init(CONFIGURATION_MANAGER);
    }
}

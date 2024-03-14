package net.voidgroup.aphrodite.server.feature;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.util.Identifier;
import net.voidgroup.aphrodite.server.AphroditeServer;

public class DropPlayerHead {
    public static final Identifier PLAYER_LOOT_TABLE_ID = new Identifier("entities/player");
    public static final Identifier MOD_PLAYER_LOOT_TABLE_ID = new Identifier(AphroditeServer.MOD_ID, "entities/player");

    public static void init() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if(!source.isBuiltin() || !PLAYER_LOOT_TABLE_ID.equals(id)) return;
            tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(MOD_PLAYER_LOOT_TABLE_ID)));
        });
    }

}

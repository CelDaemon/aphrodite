package net.voidgroup.aphrodite.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.voidgroup.aphrodite.common.enchantment.BeheadingEnchantment;

public class AphroditeCommon implements ModInitializer {
    public static final String SHARED_NAMESPACE = "aphrodite";
    public static Enchantment BEHEADING_ENCHANTMENT;
    @Override
    public void onInitialize() {
        BEHEADING_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(SHARED_NAMESPACE, "beheading"), new BeheadingEnchantment());
    }
}

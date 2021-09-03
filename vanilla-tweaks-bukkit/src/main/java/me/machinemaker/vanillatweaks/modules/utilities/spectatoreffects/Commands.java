package me.machinemaker.vanillatweaks.modules.utilities.spectatoreffects;

import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.MetaKeys;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager
                .commandBuilder("toggle-effect", RichDescription.translatable("modules.spectator-effects.commands.root"), "teffect")
                .senderType(PlayerCommandDispatcher.class)
                .meta(MetaKeys.GAMEMODE_KEY, GameMode.SPECTATOR); // TODO probably change to a permission

        manager.command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.spectatortoggle.nightvision"))
                .literal("night-vision")
                .handler(commandContext -> {
                    manager.taskRecipe().begin(commandContext).synchronous(context -> {
                        toggleEffect(PlayerCommandDispatcher.from(context), PotionEffectType.NIGHT_VISION);
                    }).execute();
                })).command(builder
                        .permission(ModulePermission.of(lifecycle, "vanillatweaks.spectatortoggle.conduitpower"))
                        .literal("conduit-power")
                        .handler(commandContext -> {
                            manager.taskRecipe().begin(commandContext).synchronous(context -> {
                                toggleEffect(PlayerCommandDispatcher.from(context), PotionEffectType.CONDUIT_POWER);
                            }).execute();
                        })
                );
    }

    private void toggleEffect(Player player, PotionEffectType type) {
        if (player.getPotionEffect(type) == null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0, false, false, true));
        } else {
            player.removePotionEffect(type);
        }
    }

}

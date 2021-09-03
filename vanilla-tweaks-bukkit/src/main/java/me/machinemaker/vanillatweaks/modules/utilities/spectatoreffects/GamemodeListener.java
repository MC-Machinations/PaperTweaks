package me.machinemaker.vanillatweaks.modules.utilities.spectatoreffects;

import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffectType;

public class GamemodeListener implements ModuleListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR && event.getNewGameMode() != GameMode.SPECTATOR) {
            event.getPlayer().removePotionEffect(PotionEffectType.CONDUIT_POWER);
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}

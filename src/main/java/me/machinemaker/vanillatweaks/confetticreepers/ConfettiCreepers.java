package me.machinemaker.vanillatweaks.confetticreepers;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class ConfettiCreepers extends BaseModule implements Listener {

    private final Config config = new Config();
    private final FireworkEffect fireworkEffect = FireworkEffect.builder()
            .flicker(false)
            .trail(false)
            .with(FireworkEffect.Type.BURST)
            .withColor(
                    Color.fromRGB(11743532),
                    Color.fromRGB(15435844),
                    Color.fromRGB(14602026),
                    Color.fromRGB(4312372),
                    Color.fromRGB(6719955),
                    Color.fromRGB(8073150),
                    Color.fromRGB(14188952)
            ).build();

    public ConfettiCreepers(VanillaTweaks plugin) {
        super(plugin, config -> config.confettiCreepers);
        config.init(plugin, new File(plugin.getDataFolder(), "confetticreepers"));
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) return;
        if (ThreadLocalRandom.current().nextDouble() < config.chance) {
            event.setFire(false);
            event.setRadius(0);
            event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Firework.class, firework -> {
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.setPower(0);
                fireworkMeta.addEffect(fireworkEffect);
                firework.setFireworkMeta(fireworkMeta);
                firework.detonate();
            });
        }
    }

    @Override
    public void reload() {
        config.reload();
    }

    @Override
    public void register() {
        registerEvents(this);
    }

    @Override
    public void unregister() {
        unregisterEvents(this);
    }
}

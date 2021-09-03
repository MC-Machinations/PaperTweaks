package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import me.machinemaker.vanillatweaks.utils.ItemDropFinder;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Marker;
import org.jetbrains.annotations.NotNull;

class ElevatorItemFinder extends ItemDropFinder {

    protected ElevatorItemFinder(@NotNull Item item) {
        super(item, 100);
    }

    @Override
    public boolean failCheck(@NotNull Item item) {
        return item.getItemStack().getAmount() != 1;
    }

    @Override
    public boolean successCheck(@NotNull Item item) {
        Location loc = item.getLocation().subtract(0, 0.25, 0);
        if (Tag.WOOL.isTagged(loc.getBlock().getType())) {
            if (VTUtils.getNearbyEntitiesOfType(Marker.class, loc.getBlock().getLocation().add(0.5, 0.5, 0.5), 0.1, 0.1, 0.1, Elevators.IS_ELEVATOR::has).isEmpty()) {
                createElevator(item, loc);
                return true;
            }
        }
        return false;
    }

    private void createElevator(Item item, Location location) {
        Block block = location.getBlock();
        Location middle = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawn(middle, Marker.class, m -> {
            m.setInvulnerable(true);
            m.setGravity(false);
            Elevators.IS_ELEVATOR.setFrom(m, true);
        });
        item.getItemStack().setAmount(0);
        item.remove();
    }
}

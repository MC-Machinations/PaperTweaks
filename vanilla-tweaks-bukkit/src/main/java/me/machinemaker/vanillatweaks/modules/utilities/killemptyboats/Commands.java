package me.machinemaker.vanillatweaks.modules.utilities.killemptyboats;

import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Boat;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("killboats", RichDescription.translatable("modules.kill-empty-boats.commands.root"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.killboats"))
                .handler(context -> {
                    int count = 0;
                    for (World world : Bukkit.getWorlds()) {
                        for (Boat boat : world.getEntitiesByClass(Boat.class)) {
                            if (boat.getPassengers().isEmpty()) {
                                count++;
                                boat.remove();
                            }
                        }
                    }
                    context.getSender().sendMessage(translatable("modules.kill-empty-boats.removed-boats", count > 0 ? YELLOW : RED));
                });
        manager.command(builder);
    }
}

package me.machinemaker.vanillatweaks.modules.mobs.silencemobs;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "SilenceMobs", configPath = "mobs.silence-mobs", description = "Silence mobs with a name tag")
public class SilenceMobs extends ModuleBase {

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.SimpleLifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(EntityInteractionListener.class);
    }
}

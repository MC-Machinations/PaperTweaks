package me.machinemaker.vanillatweaks.menus.config.types;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.menus.config.SimpleConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.options.DoubleMenuOption;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;

public class DoubleOptionBuilder extends SimpleConfigMenuOptionBuilder<Double> {

    @Override
    public Class<Double> typeClass() {
        return double.class;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> MenuOptionBuilderCreator<Double, C> getBuilder() {
        return DoubleMenuOption::builder;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> ConfigSetting<Double, C> createSetting(final ValueNode<?> valueNode) {
        return ConfigSetting.ofDouble(valueNode);
    }
}

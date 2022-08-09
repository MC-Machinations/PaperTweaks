package me.machinemaker.vanillatweaks.menus.options;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import me.machinemaker.vanillatweaks.adventure.Components;
import me.machinemaker.vanillatweaks.menus.parts.Previewable;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class DoubleMenuOption<S> extends MenuOption<Double, S> implements EditableOption<Double> {

    protected DoubleMenuOption(final Component label, final Function<S, Double> typeMapper, final Setting<Double, ?> setting, final Component extendedDescription, final @Nullable Function<Double, ClickEvent> previewAction) {
        super(label, typeMapper, setting, extendedDescription, previewAction);
    }

    public static <S> DoubleMenuOption<S> of(final String labelKey, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting).build();
    }

    public static <S> DoubleMenuOption<S> of(final String labelKey, final Setting<Double, S> setting) {
        return new Builder<>(translatable(labelKey), setting::getOrDefault, setting).build();
    }

    public static <S> Builder<S> builder(final String labelKey, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static <S> Builder<S> builder(final String labelKey, final Setting<Double, S> setting) {
        return new Builder<>(translatable(labelKey), setting::getOrDefault, setting);
    }

    @Override
    public Component build(final S object, final String commandPrefix) {
        final List<Component> components = Lists.newArrayList(this.createClickComponent(this.selected(object), commandPrefix), space());
        this.previewAction().ifPresent(previewAction -> {
            components.add(Previewable.createPreviewComponent(this.label(), previewAction.apply(this.selected(object))));
            components.add(space());
        });

        components.addAll(List.of(
                this.label(),
                space(),
                translatable("commands.config.current-value", GRAY, text(this.selected(object))),
                newline()
        ));

        return join(components.toArray(new ComponentLike[0]));
    }

    @Override
    public Component label() {
        return super.label();
    }

    @Override
    public Component defaultValueDescription() {
        return text(this.setting().defaultValue());
    }

    @Override
    public Component validations() {
        return this.setting().validations();
    }

    public static class Builder<S> extends MenuOption.Builder<Double, DoubleMenuOption<S>, S, Builder<S>> {

        protected Builder(final Component label, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
            super(label, typeMapper, setting);
        }

        @Override
        public DoubleMenuOption<S> build() {
            return new DoubleMenuOption<>(
                    this.getLabel(),
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getExtendedDescription(),
                    this.getPreviewAction()
            );
        }
    }
}

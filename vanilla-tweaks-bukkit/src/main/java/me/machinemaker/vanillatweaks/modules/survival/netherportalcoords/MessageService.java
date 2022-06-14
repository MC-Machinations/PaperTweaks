/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.vanillatweaks.modules.survival.netherportalcoords;

import me.machinemaker.vanillatweaks.adventure.TranslationRegistry;
import me.machinemaker.vanillatweaks.modules.ModuleMessageService;
import me.machinemaker.vanillatweaks.moonshine.annotation.TextColor;
import me.machinemaker.vanillatweaks.moonshine.resolvers.AbstractPlaceholderResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.util.Either;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

interface MessageService extends ModuleMessageService {

    @Message("modules.nether-portal-coords.invalid-world")
    @TextColor(TextColor.RED)
    void invalidWorld(final Audience audience);

    @Message("modules.nether-portal-coords.msg-format")
    void coordinatesMsg(final Audience audience, @Placeholder final CoordinatesComponent coords, @Placeholder @TextColor(TextColor.YELLOW) final String world);

    record CoordinatesComponent(@NotNull Location loc, @NotNull IntUnaryOperator op) { }

    class CoordinatesComponentPlaceholderResolver extends AbstractPlaceholderResolver<CoordinatesComponent> {

        @Override
        public @Nullable Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>> resolve(String placeholderName, CoordinatesComponent value, Audience receiver, Type owner, Method method, @Nullable Object[] parameters) {
            final Location loc = value.loc;
            final IntUnaryOperator op = value.op;
            final Optional<String> miniMessage = TranslationRegistry.translate("modules.nether-portal-coords.coord-format", receiver.pointers().getOrDefault(Identity.LOCALE, Locale.US));
            if (miniMessage.isPresent()) {
                final TagResolver.Builder builder = TagResolver.builder();
                builder.resolver(component("x", text(op.applyAsInt(loc.getBlockX()), GOLD)));
                builder.resolver(component("y", /* don't operate on y coord as that stays the same */ text(loc.getBlockY(), GOLD)));
                builder.resolver(component("z", text(op.applyAsInt(loc.getBlockZ()), GOLD)));
                return this.constant(placeholderName, text().append(MiniMessage.miniMessage().deserialize(miniMessage.get(), builder.build())).color(GREEN).build());
            }
            return null;
        }
    }
}

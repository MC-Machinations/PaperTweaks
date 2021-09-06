/*
 * GNU General Public License v3
 *
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.menus.parts.enums.PreviewableMenuEnum;
import me.machinemaker.vanillatweaks.settings.ModuleSettings;
import me.machinemaker.vanillatweaks.settings.SettingWrapper;
import me.machinemaker.vanillatweaks.settings.types.PlayerSetting;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

class Settings extends ModuleSettings<PlayerSetting<?>> {

    public static final SettingWrapper.PDC<Boolean> HAND_PING = SettingWrapper.pdc(Keys.key("dp.hand_ping"));
    public static final SettingWrapper.PDC<Boolean> ARMOR_PING = SettingWrapper.pdc(Keys.key("dp.armor_ping"));
    public static final SettingWrapper.PDC<Boolean> SOUND = SettingWrapper.pdc(Keys.key("dp.sound"));
    public static final SettingWrapper.PDC<DisplaySetting> DISPLAY = SettingWrapper.pdc(Keys.key("dp.display_setting"));

    @Inject
    Settings(Config config) {
        register(PlayerSetting.ofBoolean(HAND_PING, () -> config.defaultHandPing));
        register(PlayerSetting.ofBoolean(ARMOR_PING, () -> config.defaultArmorPing));
        register(PlayerSetting.ofBoolean(SOUND, () -> config.defaultPlaySound));
        register(PlayerSetting.ofEnum(DISPLAY, DisplaySetting.class, () -> config.defaultDisplaySetting));
    }

    static record Instance(boolean handPing, boolean armorPing, boolean sound, @NotNull DisplaySetting displaySetting) { }

    static Instance from(PersistentDataHolder holder) {
        return new Instance(HAND_PING.getOrDefault(holder), ARMOR_PING.getOrDefault(holder), SOUND.getOrDefault(holder), DISPLAY.getOrDefault(holder));
    }

    enum DisplaySetting implements PreviewableMenuEnum<DisplaySetting> {
        HIDDEN("Display: Hidden") {
            @Override
            void sendMessage(Audience audience, ComponentLike componentLike) {
                // pass
            }

            @Override
            public @NotNull Component build(@NotNull DisplaySetting selected, @NotNull String commandPrefix, @NotNull String optionKey) {
                return super.buildWithoutPreview(selected, commandPrefix, optionKey);
            }
        },
        SUBTITLE("Display: Subtitle") {
            @Override
            void sendMessage(Audience audience, ComponentLike componentLike) {
                audience.clearTitle();
                audience.showTitle(Title.title(Component.empty(), componentLike.asComponent()));
            }
        },
        TITLE("Display: Title") {
            @Override
            void sendMessage(Audience audience, ComponentLike componentLike) {
                audience.clearTitle();
                audience.showTitle(Title.title(componentLike.asComponent(), Component.empty()));
            }
        },
        CHAT("Display: Chat") {
            @Override
            void sendMessage(Audience audience, ComponentLike componentLike) {
                audience.sendMessage(componentLike);
            }

        },
        ACTION_BAR("Display: Action Bar") {
            @Override
            void sendMessage(Audience audience, ComponentLike componentLike) {
                audience.sendActionBar(componentLike);
            }
        };

        private final String label;

        DisplaySetting(String label) {
            this.label = label;
        }

        abstract void sendMessage(Audience audience, ComponentLike componentLike);

        @Override
        public @NotNull Component label() {
            return text(this.label);
        }

        @Override
        public @NotNull String previewCommandPrefix() {
            return "/durabilityping config preview_display";
        }
    }
}

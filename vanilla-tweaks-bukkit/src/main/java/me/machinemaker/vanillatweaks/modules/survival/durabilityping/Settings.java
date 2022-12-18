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
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

class Settings extends ModuleSettings<PlayerSetting<?>> {

    public static final SettingWrapper.PDC<Boolean> HAND_PING = SettingWrapper.pdc(Keys.key("dp.hand_ping"));
    public static final SettingWrapper.PDC<Boolean> ARMOR_PING = SettingWrapper.pdc(Keys.key("dp.armor_ping"));
    public static final SettingWrapper.PDC<Boolean> SOUND = SettingWrapper.pdc(Keys.key("dp.sound"));
    public static final SettingWrapper.PDC<DisplaySetting> DISPLAY = SettingWrapper.pdc(Keys.key("dp.display_setting"));

    @Inject
    Settings(final Config config) {
        this.register(PlayerSetting.ofBoolean(HAND_PING, () -> config.defaultHandPing));
        this.register(PlayerSetting.ofBoolean(ARMOR_PING, () -> config.defaultArmorPing));
        this.register(PlayerSetting.ofBoolean(SOUND, () -> config.defaultPlaySound));
        this.register(PlayerSetting.ofEnum(DISPLAY, DisplaySetting.class, () -> config.defaultDisplaySetting));
    }

    static Instance from(final Player player) {
        return new Instance(HAND_PING.getOrDefault(player), ARMOR_PING.getOrDefault(player), SOUND.getOrDefault(player), DISPLAY.getOrDefault(player));
    }

    enum DisplaySetting implements PreviewableMenuEnum<DisplaySetting> {
        HIDDEN("Hidden") {
            @Override
            void sendMessage(final Audience audience, final ComponentLike componentLike) {
                // pass
            }

            @Override
            public Component build(final DisplaySetting selected, final String labelKey, final String commandPrefix, final String optionKey) {
                return this.buildWithoutPreview(selected, labelKey, commandPrefix, optionKey);
            }
        },
        SUBTITLE("Subtitle") {
            @Override
            void sendMessage(final Audience audience, final ComponentLike componentLike) {
                audience.clearTitle();
                audience.showTitle(Title.title(Component.empty(), componentLike.asComponent()));
            }
        },
        TITLE("Title") {
            @Override
            void sendMessage(final Audience audience, final ComponentLike componentLike) {
                audience.clearTitle();
                audience.showTitle(Title.title(componentLike.asComponent(), Component.empty()));
            }
        },
        CHAT("Chat") {
            @Override
            void sendMessage(final Audience audience, final ComponentLike componentLike) {
                audience.sendMessage(componentLike);
            }

        },
        ACTION_BAR("Action Bar") {
            @Override
            void sendMessage(final Audience audience, final ComponentLike componentLike) {
                audience.sendActionBar(componentLike);
            }
        };

        private final String label;

        DisplaySetting(final String label) {
            this.label = label;
        }

        abstract void sendMessage(Audience audience, ComponentLike componentLike);

        @Override
        public Component label() {
            return text(this.label);
        }

        @Override
        public String previewCommandPrefix() {
            return "/durabilityping config preview_display";
        }
    }

    record Instance(boolean handPing, boolean armorPing, boolean sound, DisplaySetting displaySetting) {}
}

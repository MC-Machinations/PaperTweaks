/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.papertweaks.menus.parts.enums.PreviewableMenuEnum;
import me.machinemaker.papertweaks.settings.ModuleSettings;
import me.machinemaker.papertweaks.settings.SettingKey;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

@Singleton
class Settings extends ModuleSettings<Player, PlayerSetting<?>> {

    static final SettingKey<Boolean> HAND_PING = new SettingKey<>("dp.hand_ping");
    static final SettingKey<Boolean> ARMOR_PING = new SettingKey<>("dp.armor_ping");
    static final SettingKey<Boolean> SOUND = new SettingKey<>("dp.sound");
    static final SettingKey<DisplaySetting> DISPLAY = new SettingKey<>("dp.display_setting");

    @Inject
    Settings(final Config config) {
        this.register(PlayerSetting.ofBoolean(HAND_PING, () -> config.defaultHandPing));
        this.register(PlayerSetting.ofBoolean(ARMOR_PING, () -> config.defaultArmorPing));
        this.register(PlayerSetting.ofBoolean(SOUND, () -> config.defaultPlaySound));
        this.register(PlayerSetting.ofEnum(DISPLAY, DisplaySetting.class, () -> config.defaultDisplaySetting));
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
}

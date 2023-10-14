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
package me.machinemaker.papertweaks.modules.hermitcraft.tag;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.numbers.Max;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.papertweaks.config.I18nKey;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.menus.Menu;
import me.machinemaker.papertweaks.modules.SimpleMenuModuleConfig;
import net.kyori.adventure.text.Component;

@PTConfig
@Menu(commandPrefix = "/tag admin config")
class Config extends SimpleMenuModuleConfig<Config> {

    @Key("show-messages")
    @I18nKey("modules.tag.settings.show-messages")
    @Description("modules.tag.settings.show-messages.extended")
    public boolean showMessages = true;

    @Key("tag-cooldown-in-seconds")
    @Min(0)
    @Max(86400)
    @I18nKey("modules.tag.settings.tag-cooldown")
    @Description("modules.tag.settings.tag-cooldown.extended")
    public int timeBetweenTags = 0;

    @Override
    public Component title() {
        return buildDefaultTitle("Tag");
    }
}

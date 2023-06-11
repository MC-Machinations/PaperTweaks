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
package me.machinemaker.papertweaks.config;

import me.machinemaker.lectern.contexts.InvalidKeyHandler;
import me.machinemaker.lectern.contexts.LoadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class LoggingInvalidKeyHandler implements InvalidKeyHandler {

    private final Logger logger;

    public LoggingInvalidKeyHandler(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleInvalidKey(@NotNull final String key, @NotNull final LoadContext context) {
        this.logger.error("{} is an invalid key for file {}", key, context.root().file());
    }
}

/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.moonshine;

import java.util.Locale;
import java.util.Optional;
import me.machinemaker.papertweaks.adventure.TranslationRegistry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.moonshine.exception.MissingMessageException;
import net.kyori.moonshine.message.IMessageSource;

public final class TranslatableMessageSource implements IMessageSource<Audience, String> {

    @Override
    public String messageOf(final Audience receiver, final String messageKey) throws MissingMessageException {
        final Optional<String> msg = TranslationRegistry.translate(messageKey, receiver.pointers().getOrDefault(Identity.LOCALE, Locale.US));
        if (msg.isEmpty()) {
            throw new MissingMessageException(messageKey);
        }
        return msg.get();
    }
}

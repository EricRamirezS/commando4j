/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.command.command.util;

import com.ericramirezs.commando4j.command.CommandEngine;
import com.ericramirezs.commando4j.command.ICommandEngine;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.StringArgument;
import com.ericramirezs.commando4j.command.arguments.UnionArgument;
import com.ericramirezs.commando4j.command.command.Command;
import com.ericramirezs.commando4j.command.customizations.MultiLocaleResourceBundle;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import com.ericramirezs.commando4j.command.util.StringUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class LanguageCommand extends Command {

    public LanguageCommand() throws DuplicatedArgumentNameException {
        super("language", "util", "Command_Language_Description",
                new UnionArgument("languageCode", "Command_Language_UnionArgument")
                        .addArgumentType(new StringArgument("", "")
                                .setRegex("[a-z]{2,3}-[A-Z]{2,3}$",
                                        "Two or three characters representing language code, and score " +
                                                "and two or three characters representing country code"))
                        .addArgumentType(new StringArgument("", "")
                                .addValidValues("default"))
        );
        UnionArgument arg = (UnionArgument) getArguments().get(0);
        StringArgument regexArg = (StringArgument) arg.getArguments().get(0);

        arg.setPromptParser(x -> localizePrompt(arg, x));
        regexArg.setRegex("[a-z]{2,3}-[A-Z]{2,3}$", this::localizeRegex);

        setGuildOnly();
        addMemberPermissions(Permission.ADMINISTRATOR);

        addExamples("language",
                "language en-US",
                "language es-ES",
                "language default");
    }

    private @NotNull String localizePrompt(UnionArgument arg, Event event) {
        if (event == null) return LocalizedFormat.format(arg.getPromptRaw());
        return LocalizedFormat.format(arg.getPromptRaw(), event);
    }

    private @NotNull String localizeRegex(MessageReceivedEvent event) {
        if (event == null) return LocalizedFormat.format("Command_Language_Regex");
        return LocalizedFormat.format("Command_Language_Regex", event);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(Event event) {
        return LocalizedFormat.format(super.getDescription(), event);
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        StringArgument languageCode = (StringArgument) args.get("languageCode").getValue();
        if (languageCode == null || StringUtils.isNullOrWhiteSpace(languageCode.getValue())) {
            displayLanguage(event);
        } else if (languageCode.getValue().equals("default")) {
            resetDefaultLanguage(event);
        } else {
            setNewLanguage(event, Locale.forLanguageTag(languageCode.getValue()));
        }
    }

    private void displayLanguage(@NotNull MessageReceivedEvent event) {
        ICommandEngine engine = CommandEngine.getInstance();
        String lang = engine.getRepository().getLanguage(event.getGuild().getId());
        Locale locale = Locale.forLanguageTag(lang);
        String message = LocalizedFormat.format("Locale_CurrentLanguage", locale,
                locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale));
        sendReply(event, message);
    }

    private void resetDefaultLanguage(MessageReceivedEvent event) {
        Locale locale = CommandEngine.getInstance().getLanguage();
        if (configureLanguage(event, locale))
            sendReply(event,
                    LocalizedFormat.format("Locale_CurrentLanguage", locale,
                            locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale)));
    }

    private void setNewLanguage(MessageReceivedEvent event, Locale locale) {
        if (MultiLocaleResourceBundle.getSupportedLocale().contains(locale)) {
            if (configureLanguage(event, locale))
                sendReply(event,
                        LocalizedFormat.format("Locale_NewLanguage", locale,
                                locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale)));
        } else {
            sendReply(event, LocalizedFormat.format("Locale_UnsupportedLanguage", event));
        }
    }

    private boolean configureLanguage(MessageReceivedEvent event, Locale locale) {
        ICommandEngine engine = CommandEngine.getInstance();
        try {
            engine.getRepository().setLanguage(event.getGuild().getId(), locale.toLanguageTag());
            return true;
        } catch (Exception ex) {
            engine.logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }
}

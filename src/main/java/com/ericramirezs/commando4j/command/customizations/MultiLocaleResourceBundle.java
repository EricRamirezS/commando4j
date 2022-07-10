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

package com.ericramirezs.commando4j.command.customizations;

import com.ericramirezs.commando4j.command.CommandEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class MultiLocaleResourceBundle {
    private final Map<Locale, ResourceBundle> localeResources;

    public MultiLocaleResourceBundle() {
        localeResources = new HashMap<>();
        for (final Locale config : supportedLocale) {
            final ResourceBundle texts = ResourceBundle.getBundle("texts", config);
            localeResources.put(config, texts);
        }
    }

    public @NotNull String getString(final String key) {
        return getString(CommandEngine.getInstance().getLanguage(), key);
    }

    /**
     * Get an array from bundle.
     *
     * @param locale Desired language locale.
     * @param key    String key in bundle.
     * @return String array from library's bundle.
     */
    public @NotNull String getString(final Locale locale, final String key) {
        return localeResources.get(locale).getString(key);
    }

    /**
     * Get an array from bundle.
     *
     * @param key String key in bundle.
     * @return String array from library's bundle.
     */
    public String @NotNull [] getStringArray(final String key) {
        return getStringArray(CommandEngine.getInstance().getLanguage(), key);
    }

    /**
     * Get an array from bundle.
     *
     * @param locale Desired language locale.
     * @param key    String key in bundle.
     * @return String array from library's bundle.
     */
    public String @NotNull [] getStringArray(final Locale locale, final String key) {
        return Arrays.stream(
                        localeResources.get(locale)
                                .getString(key)
                                .split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * Retrieves a list of all supported localed by this bundle.
     *
     * @return Locale of supported languages.
     */
    public static @NotNull @UnmodifiableView List<Locale> getSupportedLocale() {
        return Collections.unmodifiableList(supportedLocale);
    }

    private static final LinkedList<Locale> supportedLocale = new LinkedList<>();

    static {
        {
            supportedLocale.add(new Locale("aa", "ER"));
            supportedLocale.add(new Locale("ae", "IR"));
            supportedLocale.add(new Locale("af", "ZA"));
            supportedLocale.add(new Locale("ak", "GH"));
            supportedLocale.add(new Locale("am", "ET"));
            supportedLocale.add(new Locale("an", "ES"));
            supportedLocale.add(new Locale("ar", "BH"));
            supportedLocale.add(new Locale("ar", "EG"));
            supportedLocale.add(new Locale("ar", "SA"));
            supportedLocale.add(new Locale("ar", "YE"));
            supportedLocale.add(new Locale("ast", "ES"));
            supportedLocale.add(new Locale("ca", "ES"));
            supportedLocale.add(new Locale("cs", "CZ"));
            supportedLocale.add(new Locale("da", "DK"));
            supportedLocale.add(new Locale("de", "DE"));
            supportedLocale.add(new Locale("de", "LU"));
            supportedLocale.add(new Locale("el", "GR"));
            supportedLocale.add(new Locale("en", "US"));
            supportedLocale.add(new Locale("es", "ES"));
            supportedLocale.add(new Locale("fi", "FI"));
            supportedLocale.add(new Locale("fr", "FR"));
            supportedLocale.add(new Locale("he", "IL"));
            supportedLocale.add(new Locale("hu", "HU"));
            supportedLocale.add(new Locale("it", "IT"));
            supportedLocale.add(new Locale("ja", "JP"));
            supportedLocale.add(new Locale("ko", "KR"));
            supportedLocale.add(new Locale("nl", "NL"));
            supportedLocale.add(new Locale("no", "NO"));
            supportedLocale.add(new Locale("pl", "PL"));
            supportedLocale.add(new Locale("pt", "BR"));
            supportedLocale.add(new Locale("pt", "PT"));
            supportedLocale.add(new Locale("ro", "RO"));
            supportedLocale.add(new Locale("ru", "RU"));
            supportedLocale.add(new Locale("sr", "SP"));
            supportedLocale.add(new Locale("sv", "SE"));
            supportedLocale.add(new Locale("tr", "TR"));
            supportedLocale.add(new Locale("uk", "UA"));
            supportedLocale.add(new Locale("vi", "VN"));
            supportedLocale.add(new Locale("zh", "CN"));
            supportedLocale.add(new Locale("zh", "TW"));
        }
    }
}

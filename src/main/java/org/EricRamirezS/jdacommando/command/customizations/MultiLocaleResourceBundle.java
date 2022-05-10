package org.EricRamirezS.jdacommando.command.customizations;

import org.EricRamirezS.jdacommando.command.Engine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class MultiLocaleResourceBundle {
    private final Map<Locale, ResourceBundle> localeResources;

    public MultiLocaleResourceBundle() {
        localeResources = new HashMap<>();
        for (Locale config : supportedLocale) {
            ResourceBundle texts = ResourceBundle.getBundle("texts", config);
            localeResources.put(config, texts);
        }
    }

    public @NotNull String getString(String key) {
        return getString(Engine.getInstance().getLanguage(), key);
    }

    public @NotNull String getString(Locale locale, String key) {
        return localeResources.get(locale).getString(key);
    }

    public String @NotNull [] getStringArray(String key) {
        return getStringArray(Engine.getInstance().getLanguage(), key);
    }

    public String @NotNull [] getStringArray(Locale locale, String key) {
        return Arrays.stream(
                        localeResources.get(locale)
                                .getString(key)
                                .split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * Retireves a list of all supported localed by this bundle
     * @return Locale list
     */
    public static @NotNull @UnmodifiableView List<Locale> getSupportedLocale(){
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
            supportedLocale.add(new Locale("as", "IN"));
            supportedLocale.add(new Locale("av", "DA"));
            supportedLocale.add(new Locale("ay", "BO"));
            supportedLocale.add(new Locale("az", "AZ"));
            supportedLocale.add(new Locale("ba", "RU"));
            supportedLocale.add(new Locale("be", "BY"));
            supportedLocale.add(new Locale("bg", "BG"));
            supportedLocale.add(new Locale("bh", "IN"));
            supportedLocale.add(new Locale("bi", "VU"));
            supportedLocale.add(new Locale("bm", "ML"));
            supportedLocale.add(new Locale("bn", "BD"));
            supportedLocale.add(new Locale("bn", "IN"));
            supportedLocale.add(new Locale("bo", "BT"));
            supportedLocale.add(new Locale("br", "FR"));
            supportedLocale.add(new Locale("bs", "BA"));
            supportedLocale.add(new Locale("ca", "ES"));
            supportedLocale.add(new Locale("ce", "CE"));
            supportedLocale.add(new Locale("chr", "US"));
            supportedLocale.add(new Locale("ch", "GU"));
            supportedLocale.add(new Locale("ckb", "IR"));
            supportedLocale.add(new Locale("co", "FR"));
            supportedLocale.add(new Locale("cr", "NT"));
            supportedLocale.add(new Locale("cs", "CZ"));
            supportedLocale.add(new Locale("cv", "CU"));
            supportedLocale.add(new Locale("cy", "GB"));
            supportedLocale.add(new Locale("da", "DK"));
            supportedLocale.add(new Locale("de", "AT"));
            supportedLocale.add(new Locale("de", "BE"));
            supportedLocale.add(new Locale("de", "CH"));
            supportedLocale.add(new Locale("de", "DE"));
            supportedLocale.add(new Locale("de", "LI"));
            supportedLocale.add(new Locale("de", "LU"));
            supportedLocale.add(new Locale("dsb", "DE"));
            supportedLocale.add(new Locale("dv", "MV"));
            supportedLocale.add(new Locale("dz", "BT"));
            supportedLocale.add(new Locale("ee", "GH"));
            supportedLocale.add(new Locale("el", "CY"));
            supportedLocale.add(new Locale("el", "GR"));
            supportedLocale.add(new Locale("en", "AR"));
            supportedLocale.add(new Locale("en", "AU"));
            supportedLocale.add(new Locale("en", "BZ"));
            supportedLocale.add(new Locale("en", "CA"));
            supportedLocale.add(new Locale("en", "CB"));
            supportedLocale.add(new Locale("en", "CN"));
            supportedLocale.add(new Locale("en", "DK"));
            supportedLocale.add(new Locale("en", "GB"));
            supportedLocale.add(new Locale("en", "HK"));
            supportedLocale.add(new Locale("en", "ID"));
            supportedLocale.add(new Locale("en", "IE"));
            supportedLocale.add(new Locale("en", "IN"));
            supportedLocale.add(new Locale("en", "JA"));
            supportedLocale.add(new Locale("en", "JM"));
            supportedLocale.add(new Locale("en", "MY"));
            supportedLocale.add(new Locale("en", "NO"));
            supportedLocale.add(new Locale("en", "NZ"));
            supportedLocale.add(new Locale("en", "PH"));
            supportedLocale.add(new Locale("en", "PR"));
            supportedLocale.add(new Locale("en", "PT"));
            supportedLocale.add(new Locale("en", "SE"));
            supportedLocale.add(new Locale("en", "SG"));
            supportedLocale.add(new Locale("en", "UD"));
            supportedLocale.add(new Locale("en", "US"));
            supportedLocale.add(new Locale("en", "ZA"));
            supportedLocale.add(new Locale("en", "ZW"));
            supportedLocale.add(new Locale("eo", "UY"));
            supportedLocale.add(new Locale("es", "419"));
            supportedLocale.add(new Locale("es", "AR"));
            supportedLocale.add(new Locale("es", "BO"));
            supportedLocale.add(new Locale("es", "CL"));
            supportedLocale.add(new Locale("es", "CO"));
            supportedLocale.add(new Locale("es", "CR"));
            supportedLocale.add(new Locale("es", "DO"));
            supportedLocale.add(new Locale("es", "EC"));
            supportedLocale.add(new Locale("es", "EM"));
            supportedLocale.add(new Locale("es", "ES"));
            supportedLocale.add(new Locale("es", "GT"));
            supportedLocale.add(new Locale("es", "HN"));
            supportedLocale.add(new Locale("es", "MX"));
            supportedLocale.add(new Locale("es", "NI"));
            supportedLocale.add(new Locale("es", "PA"));
            supportedLocale.add(new Locale("es", "PE"));
            supportedLocale.add(new Locale("es", "PR"));
            supportedLocale.add(new Locale("es", "PY"));
            supportedLocale.add(new Locale("es", "SV"));
            supportedLocale.add(new Locale("es", "US"));
            supportedLocale.add(new Locale("es", "UY"));
            supportedLocale.add(new Locale("es", "VE"));
            supportedLocale.add(new Locale("et", "EE"));
            supportedLocale.add(new Locale("eu", "ES"));
            supportedLocale.add(new Locale("fa", "AF"));
            supportedLocale.add(new Locale("fa", "IR"));
            supportedLocale.add(new Locale("ff", "ZA"));
            supportedLocale.add(new Locale("fil", "PH"));
            supportedLocale.add(new Locale("fi", "FI"));
            supportedLocale.add(new Locale("fj", "FJ"));
            supportedLocale.add(new Locale("fo", "FO"));
            supportedLocale.add(new Locale("fra", "DE"));
            supportedLocale.add(new Locale("fr", "BE"));
            supportedLocale.add(new Locale("fr", "CA"));
            supportedLocale.add(new Locale("fr", "CH"));
            supportedLocale.add(new Locale("fr", "FR"));
            supportedLocale.add(new Locale("fr", "LU"));
            supportedLocale.add(new Locale("fr", "QC"));
            supportedLocale.add(new Locale("fur", "IT"));
            supportedLocale.add(new Locale("fy", "NL"));
            supportedLocale.add(new Locale("ga", "IE"));
            supportedLocale.add(new Locale("gd", "GB"));
            supportedLocale.add(new Locale("gl", "ES"));
            supportedLocale.add(new Locale("gn", "PY"));
            supportedLocale.add(new Locale("gu", "IN"));
            supportedLocale.add(new Locale("gv", "IM"));
            supportedLocale.add(new Locale("haw", "US"));
            supportedLocale.add(new Locale("ha", "HG"));
            supportedLocale.add(new Locale("he", "IL"));
            supportedLocale.add(new Locale("hi", "IN"));
            supportedLocale.add(new Locale("ho", "PG"));
            supportedLocale.add(new Locale("hr", "HR"));
            supportedLocale.add(new Locale("hsb", "DE"));
            supportedLocale.add(new Locale("ht", "HT"));
            supportedLocale.add(new Locale("hu", "HU"));
            supportedLocale.add(new Locale("hy", "AM"));
            supportedLocale.add(new Locale("hz", "NA"));
            supportedLocale.add(new Locale("id", "ID"));
            supportedLocale.add(new Locale("ig", "NG"));
            supportedLocale.add(new Locale("ii", "CN"));
            supportedLocale.add(new Locale("io", "EN"));
            supportedLocale.add(new Locale("is", "IS"));
            supportedLocale.add(new Locale("it", "CH"));
            supportedLocale.add(new Locale("it", "IT"));
            supportedLocale.add(new Locale("iu", "NU"));
            supportedLocale.add(new Locale("ja", "JP"));
            supportedLocale.add(new Locale("jv", "ID"));
            supportedLocale.add(new Locale("kab", "KAB"));
            supportedLocale.add(new Locale("ka", "GE"));
            supportedLocale.add(new Locale("kg", "CG"));
            supportedLocale.add(new Locale("kj", "AO"));
            supportedLocale.add(new Locale("kk", "KZ"));
            supportedLocale.add(new Locale("kl", "GL"));
            supportedLocale.add(new Locale("km", "KH"));
            supportedLocale.add(new Locale("kn", "IN"));
            supportedLocale.add(new Locale("kok", "IN"));
            supportedLocale.add(new Locale("ko", "KR"));
            supportedLocale.add(new Locale("ks", "IN"));
            supportedLocale.add(new Locale("ks", "PK"));
            supportedLocale.add(new Locale("ku", "TR"));
            supportedLocale.add(new Locale("kv", "KO"));
            supportedLocale.add(new Locale("kw", "GB"));
            supportedLocale.add(new Locale("ky", "KG"));
            supportedLocale.add(new Locale("la", "LA"));
            supportedLocale.add(new Locale("lb", "LU"));
            supportedLocale.add(new Locale("lg", "UG"));
            supportedLocale.add(new Locale("li", "LI"));
            supportedLocale.add(new Locale("ln", "CD"));
            supportedLocale.add(new Locale("lo", "LA"));
            supportedLocale.add(new Locale("lt", "LT"));
            supportedLocale.add(new Locale("luy", "KE"));
            supportedLocale.add(new Locale("lv", "LV"));
            supportedLocale.add(new Locale("mg", "MG"));
            supportedLocale.add(new Locale("mh", "MH"));
            supportedLocale.add(new Locale("mi", "NZ"));
            supportedLocale.add(new Locale("mk", "MK"));
            supportedLocale.add(new Locale("ml", "IN"));
            supportedLocale.add(new Locale("mn", "MN"));
            supportedLocale.add(new Locale("mr", "IN"));
            supportedLocale.add(new Locale("ms", "BN"));
            supportedLocale.add(new Locale("ms", "MY"));
            supportedLocale.add(new Locale("mt", "MT"));
            supportedLocale.add(new Locale("my", "MM"));
            supportedLocale.add(new Locale("na", "NR"));
            supportedLocale.add(new Locale("nb", "NO"));
            supportedLocale.add(new Locale("nds", "DE"));
            supportedLocale.add(new Locale("ne", "IN"));
            supportedLocale.add(new Locale("ne", "NP"));
            supportedLocale.add(new Locale("ng", "NA"));
            supportedLocale.add(new Locale("nl", "BE"));
            supportedLocale.add(new Locale("nl", "NL"));
            supportedLocale.add(new Locale("nl", "SR"));
            supportedLocale.add(new Locale("nn", "NO"));
            supportedLocale.add(new Locale("no", "NO"));
            supportedLocale.add(new Locale("nr", "ZA"));
            supportedLocale.add(new Locale("ny", "MW"));
            supportedLocale.add(new Locale("oc", "FR"));
            supportedLocale.add(new Locale("oj", "CA"));
            supportedLocale.add(new Locale("om", "ET"));
            supportedLocale.add(new Locale("or", "IN"));
            supportedLocale.add(new Locale("os", "SE"));
            supportedLocale.add(new Locale("pa", "IN"));
            supportedLocale.add(new Locale("pa", "PK"));
            supportedLocale.add(new Locale("pi", "IN"));
            supportedLocale.add(new Locale("pl", "PL"));
            supportedLocale.add(new Locale("ps", "AF"));
            supportedLocale.add(new Locale("pt", "BR"));
            supportedLocale.add(new Locale("pt", "PT"));
            supportedLocale.add(new Locale("qu", "PE"));
            supportedLocale.add(new Locale("rm", "CH"));
            supportedLocale.add(new Locale("rn", "BI"));
            supportedLocale.add(new Locale("ro", "RO"));
            supportedLocale.add(new Locale("ru", "BY"));
            supportedLocale.add(new Locale("ru", "MD"));
            supportedLocale.add(new Locale("ru", "RU"));
            supportedLocale.add(new Locale("ru", "UA"));
            supportedLocale.add(new Locale("rw", "RW"));
            supportedLocale.add(new Locale("sah", "SAH"));
            supportedLocale.add(new Locale("sa", "IN"));
            supportedLocale.add(new Locale("sc", "IT"));
            supportedLocale.add(new Locale("sd", "PK"));
            supportedLocale.add(new Locale("se", "NO"));
            supportedLocale.add(new Locale("sg", "CF"));
            supportedLocale.add(new Locale("si", "LK"));
            supportedLocale.add(new Locale("sk", "SK"));
            supportedLocale.add(new Locale("sl", "SI"));
            supportedLocale.add(new Locale("sn", "ZW"));
            supportedLocale.add(new Locale("so", "SO"));
            supportedLocale.add(new Locale("sq", "AL"));
            supportedLocale.add(new Locale("sr", "CS"));
            supportedLocale.add(new Locale("sr_Cyrl", "ME"));
            supportedLocale.add(new Locale("sr", "SP"));
            supportedLocale.add(new Locale("ss", "ZA"));
            supportedLocale.add(new Locale("st", "ZA"));
            supportedLocale.add(new Locale("su", "ID"));
            supportedLocale.add(new Locale("sv", "FI"));
            supportedLocale.add(new Locale("sv", "SE"));
            supportedLocale.add(new Locale("sw", "KE"));
            supportedLocale.add(new Locale("sw", "TZ"));
            supportedLocale.add(new Locale("ta", "IN"));
            supportedLocale.add(new Locale("te", "IN"));
            supportedLocale.add(new Locale("tg", "TJ"));
            supportedLocale.add(new Locale("th", "TH"));
            supportedLocale.add(new Locale("ti", "ER"));
            supportedLocale.add(new Locale("tk", "TM"));
            supportedLocale.add(new Locale("tl", "PH"));
            supportedLocale.add(new Locale("tn", "ZA"));
            supportedLocale.add(new Locale("tr", "CY"));
            supportedLocale.add(new Locale("tr", "TR"));
            supportedLocale.add(new Locale("ts", "ZA"));
            supportedLocale.add(new Locale("tt", "RU"));
            supportedLocale.add(new Locale("tw", "TW"));
            supportedLocale.add(new Locale("ty", "PF"));
            supportedLocale.add(new Locale("ug", "CN"));
            supportedLocale.add(new Locale("ur", "IN"));
            supportedLocale.add(new Locale("ur", "PK"));
            supportedLocale.add(new Locale("uz", "UZ"));
            supportedLocale.add(new Locale("ve", "ZA"));
            supportedLocale.add(new Locale("vi", "VN"));
            supportedLocale.add(new Locale("wa", "BE"));
            supportedLocale.add(new Locale("wo", "SN"));
            supportedLocale.add(new Locale("xh", "ZA"));
            supportedLocale.add(new Locale("yi", "DE"));
            supportedLocale.add(new Locale("yo", "NG"));
            supportedLocale.add(new Locale("zh", "CN"));
            supportedLocale.add(new Locale("zh", "HK"));
            supportedLocale.add(new Locale("zh", "MO"));
            supportedLocale.add(new Locale("zh", "SG"));
            supportedLocale.add(new Locale("zh", "TW"));
            supportedLocale.add(new Locale("zu", "ZA"));
        }
    }
}

package it.unimib.devtrinity.moneymind.utils;

import java.util.Map;

public class CurrencyHelper {

    private static final Map<String, String> CURRENCY_MAP = Map.ofEntries(
            Map.entry("EUR", "Euro"),
            Map.entry("AUD", "Dollaro australiano"),
            Map.entry("BGN", "Lev bulgaro"),
            Map.entry("BRL", "Real brasiliano"),
            Map.entry("CAD", "Dollaro canadese"),
            Map.entry("CHF", "Franco svizzero"),
            Map.entry("CNY", "Yuan cinese"),
            Map.entry("CZK", "Corona ceca"),
            Map.entry("DKK", "Corona danese"),
            Map.entry("GBP", "Sterlina britannica"),
            Map.entry("HKD", "Dollaro di Hong Kong"),
            Map.entry("HRK", "Kuna croata"),
            Map.entry("HUF", "Fiorino ungherese"),
            Map.entry("IDR", "Rupia indonesiana"),
            Map.entry("ILS", "Nuovo siclo israeliano"),
            Map.entry("INR", "Rupia indiana"),
            Map.entry("ISK", "Corona islandese"),
            Map.entry("JPY", "Yen giapponese"),
            Map.entry("KRW", "Won sudcoreano"),
            Map.entry("MXN", "Peso messicano"),
            Map.entry("MYR", "Ringgit malese"),
            Map.entry("NOK", "Corona norvegese"),
            Map.entry("NZD", "Dollaro neozelandese"),
            Map.entry("PHP", "Peso filippino"),
            Map.entry("PLN", "Zloty polacco"),
            Map.entry("RON", "Leu rumeno"),
            Map.entry("RUB", "Rublo russo"),
            Map.entry("SEK", "Corona svedese"),
            Map.entry("SGD", "Dollaro di Singapore"),
            Map.entry("THB", "Baht thailandese"),
            Map.entry("TRY", "Lira turca"),
            Map.entry("USD", "Dollaro statunitense"),
            Map.entry("ZAR", "Rand sudafricano")
    );

    public static String getCurrencyDescription(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        String upperCode = code.toUpperCase();
        String description = CURRENCY_MAP.get(upperCode);
        if (description != null) {
            return upperCode + " - " + description;
        } else {
            return upperCode;
        }
    }
}


package it.unimib.devtrinity.moneymind.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrencyHelper {

    private static final Map<String, String> currencyMap;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("EUR", "Euro");
        map.put("AUD", "Dollaro australiano");
        map.put("BGN", "Lev bulgaro");
        map.put("BRL", "Real brasiliano");
        map.put("CAD", "Dollaro canadese");
        map.put("CHF", "Franco svizzero");
        map.put("CNY", "Yuan cinese");
        map.put("CZK", "Corona ceca");
        map.put("DKK", "Corona danese");
        map.put("GBP", "Sterlina britannica");
        map.put("HKD", "Dollaro di Hong Kong");
        map.put("HRK", "Kuna croata");
        map.put("HUF", "Fiorino ungherese");
        map.put("IDR", "Rupia indonesiana");
        map.put("ILS", "Nuovo siclo israeliano");
        map.put("INR", "Rupia indiana");
        map.put("ISK", "Corona islandese");
        map.put("JPY", "Yen giapponese");
        map.put("KRW", "Won sudcoreano");
        map.put("MXN", "Peso messicano");
        map.put("MYR", "Ringgit malese");
        map.put("NOK", "Corona norvegese");
        map.put("NZD", "Dollaro neozelandese");
        map.put("PHP", "Peso filippino");
        map.put("PLN", "Zloty polacco");
        map.put("RON", "Leu rumeno");
        map.put("RUB", "Rublo russo");
        map.put("SEK", "Corona svedese");
        map.put("SGD", "Dollaro di Singapore");
        map.put("THB", "Baht thailandese");
        map.put("TRY", "Lira turca");
        map.put("USD", "Dollaro statunitense");
        map.put("ZAR", "Rand sudafricano");

        currencyMap = Collections.unmodifiableMap(map);
    }

    public static String getCurrencyDescription(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        String upperCode = code.toUpperCase();
        String description = currencyMap.get(upperCode);
        if (description != null) {
            return upperCode + " - " + description;
        } else {
            return upperCode;
        }
    }
}


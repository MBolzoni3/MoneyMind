package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;

import java.util.Map;

import it.unimib.devtrinity.moneymind.R;

public class CurrencyHelper {

    private static final Map<String, Integer> CURRENCY_MAP = Map.ofEntries(
            Map.entry("EUR", R.string.currency_eur),
            Map.entry("AUD", R.string.currency_aud),
            Map.entry("BGN", R.string.currency_bgn),
            Map.entry("BRL", R.string.currency_brl),
            Map.entry("CAD", R.string.currency_cad),
            Map.entry("CHF", R.string.currency_chf),
            Map.entry("CNY", R.string.currency_cny),
            Map.entry("CZK", R.string.currency_czk),
            Map.entry("DKK", R.string.currency_dkk),
            Map.entry("GBP", R.string.currency_gbp),
            Map.entry("HKD", R.string.currency_hkd),
            Map.entry("HRK", R.string.currency_hrk),
            Map.entry("HUF", R.string.currency_huf),
            Map.entry("IDR", R.string.currency_idr),
            Map.entry("ILS", R.string.currency_ils),
            Map.entry("INR", R.string.currency_inr),
            Map.entry("ISK", R.string.currency_isk),
            Map.entry("JPY", R.string.currency_jpy),
            Map.entry("KRW", R.string.currency_krw),
            Map.entry("MXN", R.string.currency_mxn),
            Map.entry("MYR", R.string.currency_myr),
            Map.entry("NOK", R.string.currency_nok),
            Map.entry("NZD", R.string.currency_nzd),
            Map.entry("PHP", R.string.currency_php),
            Map.entry("PLN", R.string.currency_pln),
            Map.entry("RON", R.string.currency_ron),
            Map.entry("RUB", R.string.currency_rub),
            Map.entry("SEK", R.string.currency_sek),
            Map.entry("SGD", R.string.currency_sgd),
            Map.entry("THB", R.string.currency_thb),
            Map.entry("TRY", R.string.currency_try),
            Map.entry("USD", R.string.currency_usd),
            Map.entry("ZAR", R.string.currency_zar)
    );

    public static String getCurrencyDescription(Context context, String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        String upperCode = code.toUpperCase();
        try {
            Integer resId = CURRENCY_MAP.get(upperCode);
            if (resId != null) {
                return upperCode + " - " + context.getString(resId);
            } else {
                return upperCode;
            }
        } catch (Exception ignored){
            return upperCode;
        }
    }

}


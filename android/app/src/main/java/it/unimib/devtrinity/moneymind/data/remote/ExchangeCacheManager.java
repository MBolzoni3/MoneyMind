package it.unimib.devtrinity.moneymind.data.remote;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unimib.devtrinity.moneymind.utils.Utils;

public class ExchangeCacheManager {
    private static ExchangeCacheManager instance;
    private final Map<String, Map<String, Double>> exchangeRatesCache;

    private ExchangeCacheManager() {
        exchangeRatesCache = Collections.synchronizedMap(new HashMap<>());
    }

    public static synchronized ExchangeCacheManager getInstance() {
        if (instance == null) {
            instance = new ExchangeCacheManager();
        }
        return instance;
    }

    public Map<String, Double> getExchangeRatesCache(Date date) {
        if (date == null) {
            return Collections.emptyMap();
        }

        Date provisionalDate = date;
        int i = 0;
        while (!exchangeRatesCache.containsKey(Utils.dateToStringApi(provisionalDate))) {
            if (i >= 7) {
                return Collections.emptyMap();
            }

            provisionalDate = Utils.previousDate(provisionalDate);
            i++;
        }

        return exchangeRatesCache.get(Utils.dateToStringApi(provisionalDate));
    }

    public boolean isCacheAvailable() {
        return !exchangeRatesCache.isEmpty();
    }

    public void updateExchangeRates(ExchangeResponse response) {
        if (response == null) return;

        Map<String, Map<String, Double>> transformedData = transformResponse(response);
        exchangeRatesCache.clear();
        exchangeRatesCache.putAll(transformedData);
    }

    private Map<String, Map<String, Double>> transformResponse(ExchangeResponse response) {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();

        List<ExchangeResponse.CubeTime> dateCubes = response.getCubeRoot().getCubes();
        if (dateCubes != null) {
            for (ExchangeResponse.CubeTime dateCube : dateCubes) {
                Map<String, Double> currencyRatesMap = new LinkedHashMap<>();
                for (ExchangeResponse.CubeRate rate : dateCube.getRates()) {
                    currencyRatesMap.put(rate.getCurrency(), rate.getRate());
                }

                result.put(dateCube.getTime(), currencyRatesMap);
            }
        }

        return result;
    }
}


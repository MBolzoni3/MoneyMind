package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.ExchangeDao;
import it.unimib.devtrinity.moneymind.data.local.entity.ExchangeEntity;
import it.unimib.devtrinity.moneymind.data.remote.ExchangeDataSource;
import it.unimib.devtrinity.moneymind.data.remote.response.ExchangeResponse;
import it.unimib.devtrinity.moneymind.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRepository {

    private final ExchangeDataSource exchangeDataSource;
    private final ExchangeDao exchangeDao;
    private final ExecutorService executorService;

    private LiveData<List<ExchangeEntity>> currentRates;
    private final MediatorLiveData<List<ExchangeEntity>> exchangeRatesLiveData = new MediatorLiveData<>();

    public ExchangeRepository(Application application, ExchangeDataSource exchangeDataSource) {
        this.exchangeDataSource = exchangeDataSource;
        this.exchangeDao = DatabaseClient.getInstance(application).exchangeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ExchangeEntity>> getExchangeRates(Date date) {
        Date validDate = Utils.getDataBceValid(date);

        if (currentRates != null) {
            exchangeRatesLiveData.removeSource(currentRates);
        }

        currentRates = exchangeDao.getRatesByDate(validDate.getTime());

        exchangeRatesLiveData.addSource(currentRates, rates -> {
            if (rates != null && !rates.isEmpty()) {
                exchangeRatesLiveData.setValue(rates);
            } else {
                fetchFromApiAndSave(validDate);
            }
        });

        return exchangeRatesLiveData;
    }

    private void fetchFromApiAndSave(Date date) {
        exchangeDataSource.fetchExchangeRates(date, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeResponse> call, @NonNull Response<ExchangeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ExchangeEntity> entities = toEntities(response.body());

                    executorService.execute(() -> {
                        List<Date> existingDates = exchangeDao.getAvailableDates();

                        List<ExchangeEntity> newEntities = new ArrayList<>();
                        for (ExchangeEntity entity : entities) {
                            if (!existingDates.contains(entity.date)) {
                                newEntities.add(entity);
                            }
                        }

                        if (!newEntities.isEmpty()) {
                            exchangeDao.insertAll(newEntities);
                        }

                        List<ExchangeEntity> latestRates = exchangeDao.getRatesByDateSync(date.getTime());
                        exchangeRatesLiveData.postValue(latestRates);
                    });
                } else {
                    exchangeRatesLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeResponse> call, @NonNull Throwable t) {
                exchangeRatesLiveData.postValue(null);
            }
        });
    }

    private List<ExchangeEntity> getExchangeRatesSync(Date date) {
        Date validDate = Utils.getDataBceValid(date);

        List<ExchangeEntity> ratesRoom = exchangeDao.getRatesByDateSync(validDate.getTime());
        if (ratesRoom != null && !ratesRoom.isEmpty()) {
            return ratesRoom;
        }

        try {
            return toEntities(exchangeDataSource.fetchExchangeRatesSync(date));
        } catch (IOException e) {
            Log.e("ExchangeRepository", e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    public BigDecimal getConvertedAmount(BigDecimal amount, String currency, Date date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return amount;
        if (currency.equals("EUR")) return amount;

        List<ExchangeEntity> rates = getExchangeRatesSync(date);
        for (ExchangeEntity entity : rates) {
            if (entity.currency.equals(currency)) {
                BigDecimal rate = entity.rate;
                if (rate.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ZERO;
                }

                BigDecimal converted = amount.divide(rate, MathContext.DECIMAL128);
                return converted.setScale(4, RoundingMode.HALF_EVEN);
            }
        }

        return amount;
    }

    public BigDecimal getInverseConvertedAmount(BigDecimal amount, String currency, Date date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return amount;
        if (currency.equals("EUR")) return amount;

        List<ExchangeEntity> rates = getExchangeRatesSync(date);
        for (ExchangeEntity entity : rates) {
            if (entity.currency.equals(currency)) {
                BigDecimal rate = entity.rate;
                if (rate.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ZERO;
                }

                BigDecimal converted = amount.multiply(rate, MathContext.DECIMAL128);
                return converted.setScale(4, RoundingMode.HALF_EVEN);
            }
        }

        return amount;
    }

    private static List<ExchangeEntity> toEntities(ExchangeResponse response) {
        List<ExchangeEntity> entities = new ArrayList<>();

        if (response == null || response.dataSets == null || response.structure == null) {
            return entities;
        }

        Map<String, String> currencyMap = new HashMap<>();
        ExchangeResponse.SeriesDimension currencyDimension = response.structure.dimensions.series[1];

        for (int i = 0; i < currencyDimension.values.length; i++) {
            currencyMap.put(String.valueOf(i), currencyDimension.values[i].id);
        }

        List<String> availableDates = new ArrayList<>();
        for (ExchangeResponse.TimeValue timeValue : response.structure.dimensions.observation[0].values) {
            availableDates.add(timeValue.id);
        }

        for (Map.Entry<String, ExchangeResponse.Series> entry : response.dataSets[0].series.entrySet()) {
            String[] keyParts = entry.getKey().split(":");
            if (keyParts.length > 1) {
                String currencyId = keyParts[1];

                String currencyCode = currencyMap.getOrDefault(currencyId, "UNKNOWN");

                for (Map.Entry<String, Double[]> obsEntry : entry.getValue().observations.entrySet()) {
                    int index = Integer.parseInt(obsEntry.getKey());
                    if (index >= 0 && index < availableDates.size() && obsEntry.getValue() != null && obsEntry.getValue().length > 0) {
                        Double rateValue = obsEntry.getValue()[0];
                        if (rateValue != null) {
                            BigDecimal rate = BigDecimal.valueOf(rateValue);
                            Date date = Utils.stringToDateApi(availableDates.get(index));

                            entities.add(new ExchangeEntity(currencyCode, rate, date));
                        }
                    }
                }
            }
        }

        return entities;
    }

}
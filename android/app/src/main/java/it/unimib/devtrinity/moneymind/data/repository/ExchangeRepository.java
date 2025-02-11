package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private final MediatorLiveData<List<ExchangeEntity>> exchangeRatesLiveData = new MediatorLiveData<>();

    public ExchangeRepository(Application application, ExchangeDataSource exchangeDataSource) {
        this.exchangeDataSource = exchangeDataSource;
        this.exchangeDao = DatabaseClient.getInstance(application).exchangeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ExchangeEntity>> getExchangeRates(Date date) {
        exchangeRatesLiveData.removeSource(exchangeRatesLiveData);

        LiveData<List<ExchangeEntity>> localData = exchangeDao.getRatesByClosestDate(date.getTime());

        exchangeRatesLiveData.addSource(localData, rates -> {
            if (rates != null && !rates.isEmpty()) {
                ExchangeEntity latestRate = rates.get(0);
                Date latestStoredDate = latestRate.getDate();

                if (Utils.isDataOutdated(latestStoredDate)) {
                    fetchFromApiAndSave(date);
                } else {
                    exchangeRatesLiveData.setValue(rates);
                }
            } else {
                fetchFromApiAndSave(date);
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

                        List<ExchangeEntity> latestRates = exchangeDao.getRatesByClosestDateSync(date.getTime());
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

    public static List<ExchangeEntity> toEntities(ExchangeResponse response) {
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
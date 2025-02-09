package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.ExchangeEntity;

@Dao
public interface ExchangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ExchangeEntity> rates);

    @Query("SELECT * FROM exchange_rates WHERE date = (SELECT MAX(date) FROM exchange_rates WHERE date <= :timestamp)")
    LiveData<List<ExchangeEntity>> getRatesByClosestDate(long timestamp);

    @Query("SELECT * FROM exchange_rates WHERE date = (SELECT MAX(date) FROM exchange_rates WHERE date <= :timestamp)")
    List<ExchangeEntity> getRatesByClosestDateSync(long timestamp);

    @Query("SELECT DISTINCT date FROM exchange_rates")
    List<Date> getAvailableDates();

}


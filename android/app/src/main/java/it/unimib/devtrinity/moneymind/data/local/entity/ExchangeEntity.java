package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;

@Entity(tableName = "exchange_rates")
public class ExchangeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String currency;
    public BigDecimal rate;
    public Date date;

    public ExchangeEntity(int id, String currency, BigDecimal rate, Date date) {
        this.id = id;
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    @Ignore
    public ExchangeEntity(String currency, BigDecimal rate, Date date) {
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


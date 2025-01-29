package it.unimib.devtrinity.moneymind.domain.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Envelope", strict = false)
public class ExchangeAPIResponse {

    @Element(name = "Cube")
    private Cube cube;

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    @Root(name = "Cube", strict = false)
    public static class Cube {

        @Element(name = "Cube", required = false)
        private String time;

        @ElementList(name = "Cube", entry = "Cube", inline = true)
        private List<CurrencyRate> currencyRates;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<CurrencyRate> getCurrencyRates() {
            return currencyRates;
        }

        public void setCurrencyRates(List<CurrencyRate> currencyRates) {
            this.currencyRates = currencyRates;
        }
    }

    @Root(name = "Cube", strict = false)
    public static class CurrencyRate {

        @Element(name = "currency")
        private String currency;

        @Element(name = "rate")
        private double rate;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }
    }
}

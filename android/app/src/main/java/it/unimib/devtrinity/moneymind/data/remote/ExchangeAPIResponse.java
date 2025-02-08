package it.unimib.devtrinity.moneymind.data.remote;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Root(name = "Envelope", strict = false)
public class ExchangeAPIResponse {

    @Element(name = "Cube")
    private CubeContainer cubeContainer;

    public CubeContainer getCubeContainer() {
        return cubeContainer;
    }

    public void setCubeContainer(CubeContainer cubeContainer) {
        this.cubeContainer = cubeContainer;
    }

    @Root(name = "Cube", strict = false)
    public static class CubeContainer {

        @ElementList(entry = "Cube", inline = true)
        private List<DateCube> dateCubes;

        public List<DateCube> getDateCubes() {
            return dateCubes;
        }

        public void setDateCubes(List<DateCube> dateCubes) {
            this.dateCubes = dateCubes;
        }

        public Map<String, Map<String, Double>> toHashMap() {
            Map<String, Map<String, Double>> exchangeRatesMap = new HashMap<>();
            if (dateCubes != null) {
                for (DateCube dateCube : dateCubes) {
                    Map<String, Double> currencyRatesMap = new HashMap<>();
                    for (CurrencyRate rate : dateCube.getCurrencyRates()) {
                        currencyRatesMap.put(rate.getCurrency(), rate.getRate());
                    }
                    exchangeRatesMap.put(dateCube.getTime(), currencyRatesMap);
                }
            }
            return exchangeRatesMap;
        }
    }

    @Root(name = "Cube", strict = false)
    public static class DateCube {

        @Attribute(name = "time")
        private String time;

        @ElementList(entry = "Cube", inline = true)
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

        @Attribute(name = "currency")
        private String currency;

        @Attribute(name = "rate")
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

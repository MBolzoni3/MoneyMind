package it.unimib.devtrinity.moneymind.data.remote;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

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

        @Element(name = "Cube")
        private Cube cube;

        public Cube getCube() {
            return cube;
        }

        public void setCube(Cube cube) {
            this.cube = cube;
        }
    }

    @Root(name = "Cube", strict = false)
    public static class Cube {

        @Attribute(name = "time", required = false)
        private String time;

        @ElementList(entry = "Cube", inline = true, required = false)
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

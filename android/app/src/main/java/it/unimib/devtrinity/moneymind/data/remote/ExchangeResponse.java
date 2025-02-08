package it.unimib.devtrinity.moneymind.data.remote;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Envelope", strict = false)
public class ExchangeResponse {

    @Element(name = "Cube")
    private CubeRoot cubeRoot;

    public CubeRoot getCubeRoot() {
        return cubeRoot;
    }

    @Root(name = "Cube", strict = false)
    public static class CubeRoot {
        @ElementList(entry = "Cube", inline = true)
        private List<CubeTime> cubes;

        public List<CubeTime> getCubes() {
            return cubes;
        }
    }

    @Root(name = "Cube", strict = false)
    public static class CubeTime {
        @Attribute(name = "time", required = false)
        private String time;

        @ElementList(entry = "Cube", inline = true, required = false)
        private List<CubeRate> rates;

        public String getTime() {
            return time;
        }

        public List<CubeRate> getRates() {
            return rates;
        }
    }

    @Root(name = "Cube", strict = false)
    public static class CubeRate {
        @Attribute(name = "currency")
        private String currency;

        @Attribute(name = "rate")
        private double rate;

        public String getCurrency() {
            return currency;
        }

        public double getRate() {
            return rate;
        }
    }
}
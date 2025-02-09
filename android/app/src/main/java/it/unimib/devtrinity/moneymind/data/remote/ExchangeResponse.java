package it.unimib.devtrinity.moneymind.data.remote;

import java.util.Map;

public class ExchangeResponse {
    public Header header;
    public DataSet[] dataSets;
    public Structure structure;

    public static class Header {
        public String id;
        public boolean test;
        public String prepared;
        public Sender sender;
    }

    public static class Sender {
        public String id;
    }

    public static class DataSet {
        public String action;
        public String validFrom;
        public Map<String, Series> series;
    }

    public static class Series {
        public Map<String, Double[]> observations;
    }


    public static class Structure {
        public String name;
        public Dimensions dimensions;
    }

    public static class Dimensions {
        public SeriesDimension[] series;
        public ObservationDimension[] observation;
    }

    public static class SeriesDimension {
        public String id;
        public Value[] values;
    }

    public static class ObservationDimension {
        public String id;
        public TimeValue[] values;
    }

    public static class Value {
        public String id;
        public String name;
    }

    public static class TimeValue {
        public String id;
    }
}

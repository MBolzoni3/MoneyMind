package it.unimib.devtrinity.moneymind.utils;

public abstract class GenericState<T> {
    private GenericState() {
    }

    public static class Loading<T> extends GenericState<T> {
    }

    public static class Success<T> extends GenericState<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class Failure<T> extends GenericState<T> {
        private final String errorMessage;

        public Failure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

package it.unimib.devtrinity.moneymind.utils;

public interface GenericCallback<T> {
    void onSuccess(T result);

    void onFailure(String errorMessage);
}

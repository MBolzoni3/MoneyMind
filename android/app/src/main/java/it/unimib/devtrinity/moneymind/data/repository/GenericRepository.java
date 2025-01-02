package it.unimib.devtrinity.moneymind.data.repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GenericRepository {
    protected final ExecutorService executorService;

    public GenericRepository() {
        this.executorService = Executors.newSingleThreadExecutor();
    }
}
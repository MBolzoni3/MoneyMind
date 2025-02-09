package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeDataSource;
import it.unimib.devtrinity.moneymind.data.remote.RetrofitClient;

public class ServiceLocator {
    private static volatile ServiceLocator instance;

    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private GoalRepository goalRepository;
    private RecurringTransactionRepository recurringTransactionRepository;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private ExchangeRepository exchangeRepository;
    private ExchangeDataSource exchangeDataSource;

    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) {
                    instance = new ServiceLocator();
                }
            }
        }
        return instance;
    }

    public BudgetRepository getBudgetRepository(Context context) {
        if (budgetRepository == null) {
            budgetRepository = new BudgetRepository(context);
        }

        return budgetRepository;
    }

    public CategoryRepository getCategoryRepository(Context context) {
        if (categoryRepository == null) {
            categoryRepository = new CategoryRepository(context);
        }

        return categoryRepository;
    }

    public GoalRepository getGoalRepository(Context context) {
        if (goalRepository == null) {
            goalRepository = new GoalRepository(context);
        }

        return goalRepository;
    }

    public RecurringTransactionRepository getRecurringTransactionRepository(Context context) {
        if (recurringTransactionRepository == null) {
            recurringTransactionRepository = new RecurringTransactionRepository(context);
        }

        return recurringTransactionRepository;
    }

    public TransactionRepository getTransactionRepository(Context context) {
        if (transactionRepository == null) {
            transactionRepository = new TransactionRepository(context);
        }

        return transactionRepository;
    }

    public UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public ExchangeRepository getExchangeRepository(Context context) {
        if (exchangeRepository == null) {
            exchangeRepository = new ExchangeRepository(getExchangeDataSource(context));
        }

        return exchangeRepository;
    }

    private ExchangeDataSource getExchangeDataSource(Context context) {
        if (exchangeDataSource == null) {
            exchangeDataSource = new ExchangeDataSource(RetrofitClient.getService(context));
        }

        return exchangeDataSource;
    }

}


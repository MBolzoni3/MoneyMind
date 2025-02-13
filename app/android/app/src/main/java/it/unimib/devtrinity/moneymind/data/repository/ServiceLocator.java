package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeDataSource;
import it.unimib.devtrinity.moneymind.data.remote.RetrofitClient;

public class ServiceLocator {
    private static volatile ServiceLocator instance;

    private DatabaseRepository databaseRepository;
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

    public DatabaseRepository getDatabaseRepository(Application application) {
        if (databaseRepository == null) {
            databaseRepository = new DatabaseRepository(application);
        }

        return databaseRepository;
    }

    public BudgetRepository getBudgetRepository(Application application) {
        if (budgetRepository == null) {
            budgetRepository = new BudgetRepository(application);
        }

        return budgetRepository;
    }

    public CategoryRepository getCategoryRepository(Application application) {
        if (categoryRepository == null) {
            categoryRepository = new CategoryRepository(application);
        }

        return categoryRepository;
    }

    public GoalRepository getGoalRepository(Application application) {
        if (goalRepository == null) {
            goalRepository = new GoalRepository(application);
        }

        return goalRepository;
    }

    public RecurringTransactionRepository getRecurringTransactionRepository(Application application) {
        if (recurringTransactionRepository == null) {
            recurringTransactionRepository = new RecurringTransactionRepository(application);
        }

        return recurringTransactionRepository;
    }

    public TransactionRepository getTransactionRepository(Application application) {
        if (transactionRepository == null) {
            transactionRepository = new TransactionRepository(application);
        }

        return transactionRepository;
    }

    public UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public ExchangeRepository getExchangeRepository(Application application) {
        if (exchangeRepository == null) {
            exchangeRepository = new ExchangeRepository(application, getExchangeDataSource(application));
        }

        return exchangeRepository;
    }

    private ExchangeDataSource getExchangeDataSource(Application application) {
        if (exchangeDataSource == null) {
            exchangeDataSource = new ExchangeDataSource(RetrofitClient.getService(application));
        }

        return exchangeDataSource;
    }

}


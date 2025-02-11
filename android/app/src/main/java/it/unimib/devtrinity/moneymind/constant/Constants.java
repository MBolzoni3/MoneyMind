package it.unimib.devtrinity.moneymind.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    private Constants() {
    }

    public static final String DATABASE_NAME = "moneymind_database";
    public static final int DATABASE_VERSION = 1;

    public static final String BUDGETS_TABLE_NAME = "budgets";
    public static final String GOALS_TABLE_NAME = "goals";
    public static final String TRANSACTIONS_TABLE_NAME = "transactions";
    public static final String RECURRING_TRANSACTIONS_TABLE_NAME = "recurring_transactions";
    public static final String CATEGORIES_TABLE_NAME = "categories";
    public static final String EXCHANGE_RATES_TABLE_NAME = "exchange_rates";

    public static final String UNIQUE_WORK_NAME = "sync_work";
    public static final int REPEAT_INTERVAL_MIN = 30;

    public static final String SHARED_PREFS_NAME = "moneymind_preferences";
    public static final String TRANSACTIONS_LAST_SYNC_KEY = "transactions_last_sync";
    public static final String RECURRING_TRANSACTIONS_LAST_SYNC_KEY = "recurring_transactions_last_sync";
    public static final String GOALS_LAST_SYNC_KEY = "goals_last_sync";
    public static final String BUDGETS_LAST_SYNC_KEY = "budgets_last_sync";
    public static final String CATEGORIES_LAST_SYNC_KEY = "categories_last_sync";
    public static final String THEME_KEY = "theme";

    public static final String BASE_URL = "https://data-api.ecb.europa.eu/service/data/";
    public static final String SPECIFIC_URL = "EXR/D..EUR.SP00.A";

}

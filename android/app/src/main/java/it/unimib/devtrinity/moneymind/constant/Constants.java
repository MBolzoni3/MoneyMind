package it.unimib.devtrinity.moneymind.constant;

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

    public static final String TRANSACTIONS_COLLECTION_NAME = "transactions";
    public static final String RECURRING_TRANSACTIONS_COLLECTION_NAME = "recurring_transactions";
    public static final String GOALS_COLLECTION_NAME = "goals";
    public static final String BUDGETS_COLLECTION_NAME = "budgets";
    public static final String CATEGORIES_COLLECTION_NAME = "categories";

    public static final String UNIQUE_WORK_SYNC_NAME = "sync_work";
    public static final String MANUAL_WORK_SYNC_NAME = "manual_sync_work";
    public static final int REPEAT_INTERVAL_SYNC_MIN = 30;
    public static final String UNIQUE_WORK_RECURRING_NAME = "recurring_work";
    public static final String MANUAL_WORK_RECURRING_NAME = "manual_recurring_work";
    public static final int REPEAT_INTERVAL_RECURRING_HOURS = 4;

    public static final String SHARED_PREFS_NAME = "moneymind_preferences";
    public static final String TRANSACTIONS_LAST_SYNC_KEY = "transactions_last_sync";
    public static final String RECURRING_TRANSACTIONS_LAST_SYNC_KEY = "recurring_transactions_last_sync";
    public static final String GOALS_LAST_SYNC_KEY = "goals_last_sync";
    public static final String BUDGETS_LAST_SYNC_KEY = "budgets_last_sync";
    public static final String CATEGORIES_LAST_SYNC_KEY = "categories_last_sync";
    public static final String THEME_KEY = "theme";
    public static final String LANG_KEY = "lang";

    public static final String BASE_URL = "https://data-api.ecb.europa.eu/service/data/";
    public static final String SPECIFIC_URL = "EXR/D..EUR.SP00.A";

}

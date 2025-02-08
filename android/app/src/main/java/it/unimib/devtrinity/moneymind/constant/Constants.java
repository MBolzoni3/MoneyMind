package it.unimib.devtrinity.moneymind.constant;

public class Constants {

    private Constants() {
    }

    public static final String DATABASE_NAME = "moneymind_database";
    public static final int DATABASE_VERSION = 1;

    public static final String UNIQUE_WORK_NAME = "sync_work";
    public static final int REPEAT_INTERVAL_MIN = 30;

    public static final String SHARED_PREFS_NAME = "moneymind_preferences";
    public static final String TRANSACTIONS_LAST_SYNC_KEY = "transactions_last_sync";
    public static final String RECURRING_TRANSACTIONS_LAST_SYNC_KEY = "recurring_transactions_last_sync";
    public static final String GOALS_LAST_SYNC_KEY = "goals_last_sync";
    public static final String BUDGETS_LAST_SYNC_KEY = "budgets_last_sync";

    public static final String THEME_KEY = "theme";

    //API
    public static final String BASE_URL = "https://www.ecb.europa.eu/";
    public static final String SPECIFIC_URL = "stats/eurofxref/eurofxref-daily.xml";

}

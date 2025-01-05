package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.auth.fragment.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SyncHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        SyncHelper.scheduleSyncJob(this);

        setContentView(R.layout.activity_main);

        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            NavigationHelper.navigateToMain(this);

            /* TEST SINCRONIZZAZIONE */
            /*BudgetRepository budgetRepository = new BudgetRepository(this);
            budgetRepository.insertBudget(
                    new BudgetEntity(
                            "test budget",
                            new BigDecimal(550),
                            Utils.stringToDate("10/12/2024"),
                            Utils.stringToDate("20/12/2024"),
                            "8pNO8apiVhr80CZKyKvU",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            GoalRepository goalRepository = new GoalRepository(this);
            goalRepository.insertGoal(
                    new GoalEntity(
                            "test goal",
                            new BigDecimal(350),
                            Utils.stringToDate("10/11/2024"),
                            Utils.stringToDate("25/11/2025"),
                            "8pNO8apiVhr80CZKyKvU",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            RecurringTransactionRepository recurringTransactionRepository = new RecurringTransactionRepository(this);
            recurringTransactionRepository.insertRecurringTransaction(
                    new RecurringTransactionEntity(
                            "test recurring transaction",
                            MovementTypeEnum.EXPENSE,
                            BigDecimal.valueOf(150),
                            "EUR",
                            Utils.stringToDate("10/11/2024"),
                            RecurrenceTypeEnum.MONTHLY,
                            1,
                            Utils.stringToDate("20/11/2024"),
                            Utils.stringToDate("10/11/2024"),
                            "8pNO8apiVhr80CZKyKvU",
                            "test notes",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            TransactionRepository transactionRepository = new TransactionRepository(this);
            transactionRepository.insertTransaction(
                    new TransactionEntity(
                            "test transaction",
                            MovementTypeEnum.EXPENSE,
                            BigDecimal.valueOf(100),
                            "EUR",
                            Utils.stringToDate("15/12/2024"),
                            "8pNO8apiVhr80CZKyKvU",
                            "test notes",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );*/
            /* --------------------- */
        }

        if (savedInstanceState == null) {
            NavigationHelper.loadFragment(this, new LoginFragment());
        }
    }

}

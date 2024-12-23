package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.dao.BudgetDao;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.auth.fragment.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SyncHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;
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
                            550L,
                            Utils.stringToDate("10/12/2024"),
                            Utils.stringToDate("20/12/2024"),
                            1,
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            GoalRepository goalRepository = new GoalRepository(this);
            goalRepository.insertGoal(
                    new GoalEntity(
                            "test goal",
                            350L,
                            Utils.stringToDate("10/11/2024"),
                            Utils.stringToDate("25/11/2024"),
                            1,
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            RecurringTransactionRepository recurringTransactionRepository = new RecurringTransactionRepository(this);
            recurringTransactionRepository.insertRecurringTransaction(
                    new RecurringTransactionEntity(
                            "test recurring transaction",
                            MovementTypeEnum.EXPENSE,
                            BigDecimal.valueOf(150L),
                            "EUR",
                            Utils.stringToDate("10/11/2024"),
                            RecurrenceTypeEnum.MONTHLY,
                            1,
                            Utils.stringToDate("20/11/2024"),
                            Utils.stringToDate("10/11/2024"),
                            1,
                            "test notes",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );

            TransactionRepository transactionRepository = new TransactionRepository(this);
            transactionRepository.insertTransaction(
                    new TransactionEntity(
                            "test transaction",
                            MovementTypeEnum.EXPENSE,
                            BigDecimal.valueOf(100L),
                            "EUR",
                            Utils.stringToDate("10/11/2024"),
                            1,
                            "test notes",
                            FirebaseHelper.getInstance().getCurrentUser().getUid()
                    )
            );*/
            /* --------------------- */
        }

        if (savedInstanceState == null) {
            NavigationHelper.loadFragment(this, new LoginFragment(), false);
        }
    }

}

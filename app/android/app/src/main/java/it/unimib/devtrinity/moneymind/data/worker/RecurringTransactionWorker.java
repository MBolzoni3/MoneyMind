package it.unimib.devtrinity.moneymind.data.worker;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class RecurringTransactionWorker extends Worker {

    private static final String TAG = SyncWorker.class.getSimpleName();

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRepository exchangeRepository;

    public RecurringTransactionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        Application application = (Application) context.getApplicationContext();

        recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(application);
        transactionRepository = ServiceLocator.getInstance().getTransactionRepository(application);
        exchangeRepository = ServiceLocator.getInstance().getExchangeRepository(application);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            List<RecurringTransactionEntity> transactionsToGenerate = recurringTransactionRepository.getRecurringTransactionsToGenerate();
            for(RecurringTransactionEntity transactionToGenerate : transactionsToGenerate){
                BigDecimal realAmount = exchangeRepository.getInverseConvertedAmount(
                        transactionToGenerate.getAmount(),
                        transactionToGenerate.getCurrency(),
                        transactionToGenerate.getDate()
                );

                List<Date> datesToGenerate = Utils.getAllMissingGenerationDates(transactionToGenerate);
                for(Date generationDate : datesToGenerate) {
                    BigDecimal convertedAmount = exchangeRepository.getConvertedAmount(
                            realAmount,
                            transactionToGenerate.getCurrency(),
                            generationDate
                    );

                    transactionToGenerate.setDate(generationDate);
                    transactionToGenerate.setAmount(convertedAmount);

                    transactionRepository.insertTransactionFromRecurring(transactionToGenerate);
                    recurringTransactionRepository.setLastGeneratedDate(transactionToGenerate.getId(), generationDate);
                }
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in recurring transaction worker: " + e.getMessage(), e);
            return Result.failure();
        }
    }

}

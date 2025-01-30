package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;


public class HomeViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactions();
    }

    public String getuserName() {
        return FirebaseHelper.getInstance().getCurrentUser().getDisplayName();
    }

    public String getDate(){
        LocalDate today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
            int month = today.getMonthValue();
            int year = today.getYear();

            switch (month){
                case 1:
                    return "Gennaio " + year;
                case 2:
                    return "Febbraio " + year;
                case 3:
                    return "Marzo " + year;
                case 4:
                    return "Aprile " + year;
                case 5:
                    return "Maggio " + year;
                case 6:
                    return "Giugno " + year;
                case 7:
                    return "Luglio " + year;
                case 8:
                    return "Agosto " + year;
                case 9:
                    return "Settembre " + year;
                case 10:
                    return "Ottobre " + year;
                case 11:
                    return "Novembre " + year;
                case 12:
                    return "Dicembre " + year;

            }
        }
        return "Non disponibile";
    }

    public String getMessage(){
        int choice = (int) (Math.random() * 3);

        switch (choice){
            case 0:
                return "Ehila, " + getuserName() + "!";
            case 1:
                return "Che bello rivederti, " + getuserName() + "!";
            default:
                return "Buongiorno, " + getuserName() + "!";
        }
    }

    public int setProgressBar(BigDecimal incomeTotal) {
        double doubleIncome = incomeTotal.doubleValue();
        double roundedIncome = Math.ceil((doubleIncome + 99.0) / 100) * 100;

        return (int) ((doubleIncome*100)/roundedIncome);
    }
}

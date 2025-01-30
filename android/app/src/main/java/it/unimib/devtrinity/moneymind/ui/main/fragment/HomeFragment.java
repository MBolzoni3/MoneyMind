package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.w3c.dom.Text;

import java.math.BigDecimal;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModelFactory;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        TransactionRepository transactionRepository = new TransactionRepository(requireContext());
        HomeViewModelFactory factory = new HomeViewModelFactory(transactionRepository);
        HomeViewModel homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        TextView incomeText = rootView.findViewById(R.id.income_amount);
        TextView outflowText = rootView.findViewById(R.id.outflow_amount);
        LinearProgressIndicator incomeProgressBar = rootView.findViewById((R.id.income_progress_bar));
        LinearProgressIndicator outflowProgressBar = rootView.findViewById((R.id.outflow_progress_bar));

        homeViewModel.getTransactions().observe(getViewLifecycleOwner(), positiveTransactions -> {
            BigDecimal incomeTotal = BigDecimal.ZERO;
            BigDecimal outflowTotal = BigDecimal.ZERO;

            for (TransactionEntity transaction : positiveTransactions) {
                if(transaction.getAmount().compareTo(BigDecimal.ZERO) > 0)
                    incomeTotal = incomeTotal.add(transaction.getAmount());
                else outflowTotal = outflowTotal.add(transaction.getAmount());
            }

            incomeText.setText("€ "+ incomeTotal);
            outflowText.setText("€ " + outflowTotal);
            incomeProgressBar.setProgress(homeViewModel.setProgressBar(incomeTotal), true);
            outflowProgressBar.setProgress(homeViewModel.setProgressBar(outflowTotal), true);
        });

        TextView view;

        view = rootView.findViewById(R.id.home_title);
        view.setText(homeViewModel.getMessage());

        view = rootView.findViewById(R.id.Carousel_month);
        view.setText(homeViewModel.getDate());

        return rootView;
    }
}
package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.math.BigDecimal;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.ExchangeViewModel;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class ExchangeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);

        exchangeViewModel.callAPI().observe(getViewLifecycleOwner(), positiveTransactions -> {
            //DA FARE
        });

        return rootView;

    }
}
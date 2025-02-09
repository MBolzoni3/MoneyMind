package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.MainNavigationActivity;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.ExchangeViewModel;

public class ExchangeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);

        TextView prova1 = rootView.findViewById(R.id.prova1);
        TextView prova2 = rootView.findViewById(R.id.prova2);

        /*exchangeViewModel.callAPI().observe(getViewLifecycleOwner(), exchanges -> {
            for (Map.Entry<String, Double> entry : exchanges.entrySet()) {
                prova1.setText(entry.getKey());
                prova2.setText(entry.getValue().toString());
            }
        });*/

        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainNavigationActivity) requireActivity()).restorePreviousFragment();
            }
        });
    }

}
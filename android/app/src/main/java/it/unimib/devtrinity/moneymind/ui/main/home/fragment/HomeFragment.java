package it.unimib.devtrinity.moneymind.ui.main.home.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.main.home.viewmodel.HomeViewModel;
import it.unimib.devtrinity.moneymind.utils.GenericState;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView incomeText = rootView.findViewById(R.id.incomeText);

        homeViewModel.getHomeViewState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                Log.i("CARICAMENTO", "Caricamento in corso"); // DA RIVEDERE
            } else if (state instanceof GenericState.Success) {
                double amount = ((GenericState.Success<Double>) state).getData();
                if (incomeText != null) {
                    incomeText.setText(String.format("€ %.2f", amount));
                }
            } else if (state instanceof GenericState.Failure) {
                String errorMessage = ((GenericState.Failure<?>) state).getErrorMessage();
                Log.e("ERRORE", errorMessage);
            }
        });

        return rootView;
    }

    homeViewModel.
}
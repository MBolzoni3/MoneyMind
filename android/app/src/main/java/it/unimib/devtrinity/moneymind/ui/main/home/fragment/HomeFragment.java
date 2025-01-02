package it.unimib.devtrinity.moneymind.ui.main.home.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private LiveData<GenericState<Double>> transactions = new MutableLiveData<>();
    private final HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.expense().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                Log.i("CARICAMENTO", "Caricamento in corso"); //DA RIVEDERE
                // CARICAMENTO DA FARE
            } else if (state instanceof GenericState.Success) {
                double amount = ((GenericState.Success<Double>) state).getData();
            } else if (state instanceof GenericState.Failure) {
                String errorMessage = ((GenericState.Failure<?>) state).getErrorMessage();
            }
        });


    }

}

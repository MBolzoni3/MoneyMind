package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddMovementViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddMovementViewModelFactory;

public class AddMovementFragment extends Fragment {

    private EditText etNome, etImporto, etData, etNote, etIntervalloRicorrenza, etDataFine;
    private EditText etImportoValuta; // campo per la valuta convertita
    private Spinner spValuta, spTipologia, spTipologiaRicorrenza;
    private CheckBox cbRicorrente;
    private Button btnAggiungiMovimento;
    private AddMovementViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_movement, container, false);

        AddMovementViewModelFactory factory = new AddMovementViewModelFactory();
        viewModel = new ViewModelProvider(this).get(AddMovementViewModel.class);

        etNome = view.findViewById(R.id.et_nome);
        etImporto = view.findViewById(R.id.et_importo);
        etImportoValuta = view.findViewById(R.id.et_importo_valuta);
        etData = view.findViewById(R.id.et_data);
        etNote = view.findViewById(R.id.et_note);
        etIntervalloRicorrenza = view.findViewById(R.id.et_intervallo_ricorrenza);
        etDataFine = view.findViewById(R.id.et_data_fine);

        spValuta = view.findViewById(R.id.sp_valuta);
        spTipologia = view.findViewById(R.id.sp_tipologia);
        spTipologiaRicorrenza = view.findViewById(R.id.sp_tipologia_ricorrenza);

        cbRicorrente = view.findViewById(R.id.cb_ricorrente);
        btnAggiungiMovimento = view.findViewById(R.id.btn_aggiungi_movimento);

        // visibilità dei campi ricorrenti
        cbRicorrente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            spTipologiaRicorrenza.setVisibility(visibility);
            etIntervalloRicorrenza.setVisibility(visibility);
            etDataFine.setVisibility(visibility);
        });

        viewModel.getMovementAdded().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getActivity(), "Movimento aggiunto", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        });

        btnAggiungiMovimento.setOnClickListener(v -> aggiungiMovimento());

        return view;
    }



    private void aggiungiMovimento() {
        String nome = etNome.getText().toString().trim();
        double importo = 0;

        try {
            importo = Double.parseDouble(etImporto.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Importo non valido", Toast.LENGTH_SHORT).show();
        }

        String data = etData.getText().toString().trim();
        String valuta = spValuta.getSelectedItem().toString();
        String tipologia = spTipologia.getSelectedItem().toString();
        String tipologiaRicorrenza = spTipologiaRicorrenza.getSelectedItem().toString();
        boolean ricorrente = cbRicorrente.isChecked();
        int intervalloRicorrenza = Integer.parseInt(etIntervalloRicorrenza.getText().toString().trim());
        String dataFine = etDataFine.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        viewModel.addMovement(nome, importo, data, ricorrente, tipologiaRicorrenza, valuta, intervalloRicorrenza, dataFine);
    }
}
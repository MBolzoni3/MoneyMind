package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.unimib.devtrinity.moneymind.R;

public class AddMovementFragment extends Fragment {

    private EditText etNome, etImporto, etImportoValuta, etData, etNote, etIntervalloRicorrenza, etDataFine;
    private Spinner spValuta, spTipologia, spTipologiaRicorrenza;
    private CheckBox cbRicorrente;
    private Button btnAggiungiMovimento;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_movement, container, false);

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

        btnAggiungiMovimento.setOnClickListener(v -> aggiungiMovimento());

        return view;
    }

    private void aggiungiMovimento() {
        String nome = etNome.getText().toString().trim();
        String importo = etImporto.getText().toString().trim();
        String data = etData.getText().toString().trim();
        boolean ricorrente = cbRicorrente.isChecked();

        // controllo campi obbligatori
        if (nome.isEmpty() || importo.isEmpty() || data.isEmpty()) {
            Toast.makeText(getActivity(), "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        // messaggio di successo
        Toast.makeText(getActivity(), "Movimento aggiunto con successo!", Toast.LENGTH_SHORT).show();
    }
}
package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddMovementViewModel extends ViewModel {

    private final MutableLiveData<Boolean> movementAdded = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getMovementAdded() {return movementAdded;}
    public LiveData<String> getErrorMessage() {return errorMessage;}

    public void addMovement(String nome, Double importo, String data, boolean ricorrente, String tipologiaRicorrenza, String valuta, int intervalloRicorrenza, String dataFine) {
        if (nome == null || importo <= 0 || data == null) {
            errorMessage.setValue("Compila tutti i campi obbligatori");
        } else {
            movementAdded.setValue(true); // aggiunto con successo
        }
    }


}

package it.unimib.devtrinity.moneymind.ui;

import androidx.fragment.app.Fragment;

public interface SelectionModeListener {
    void onEnterSelectionMode();

    void onExitSelectionMode();

    void onSelectionCountChanged(int count);

    void onEnterEditMode(Fragment fragment);

    void onExitEditMode();
}

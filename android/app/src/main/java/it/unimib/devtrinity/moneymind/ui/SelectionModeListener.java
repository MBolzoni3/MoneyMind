package it.unimib.devtrinity.moneymind.ui;

public interface SelectionModeListener {
    void onEnterSelectionMode();

    void onExitSelectionMode();

    void onSelectionCountChanged(int count);
}

package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.TransactionViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.TransactionViewModelFactory;

public class TransactionFragment extends Fragment implements SelectionModeListener {

    private TransactionViewModel transactionViewModel;
    private TransactionAdapter transactionAdapter;
    private FloatingActionButton fabAddTransaction;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.transaction_recycler_view);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);

        TransactionRepository transactionRepository = ServiceLocator.getInstance().getTransactionRepository(requireActivity().getApplication());
        RecurringTransactionRepository recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(requireActivity().getApplication());

        TransactionViewModelFactory factory = new TransactionViewModelFactory(transactionRepository, recurringTransactionRepository);
        transactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);

        transactionAdapter = new TransactionAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        transactionViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList -> {
            transactionAdapter.updateList(transactionList);
            view.findViewById(R.id.emptyStateLayout).setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        });

        fabAddTransaction.setOnClickListener(v -> onEnterEditMode(new AddTransactionFragment(this)));
    }

    public List<Object> getSelectedItems() {
        return transactionAdapter.getSelectedItems();
    }

    public void deleteSelected() {
        if (getSelectedItems().isEmpty()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_transaction_confirmation_title)
                .setMessage(R.string.delete_transaction_confirmation_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    transactionViewModel.deleteTransactions(getSelectedItems());
                    transactionAdapter.clearSelection();
                    onExitSelectionMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onEnterSelectionMode() {
        fabAddTransaction.hide();

        ((SelectionModeListener) requireActivity()).onEnterSelectionMode();
    }

    @Override
    public void onExitSelectionMode() {
        fabAddTransaction.show();
        transactionAdapter.clearSelection();

        ((SelectionModeListener) requireActivity()).onExitSelectionMode();
    }

    @Override
    public void onSelectionCountChanged(int count) {
        ((SelectionModeListener) requireActivity()).onSelectionCountChanged(count);
    }

    @Override
    public void onExitEditMode() {
        ((SelectionModeListener) requireActivity()).onExitEditMode();
    }

    @Override
    public void onEnterEditMode(Fragment fragment) {
        ((SelectionModeListener) requireActivity()).onEnterEditMode(fragment);
    }

}

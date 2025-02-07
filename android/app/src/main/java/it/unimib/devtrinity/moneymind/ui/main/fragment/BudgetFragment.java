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
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.BudgetAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModelFactory;

public class BudgetFragment extends Fragment implements SelectionModeListener {

    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;
    private FloatingActionButton fabAddBudget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.budget_recycler_view);
        fabAddBudget = view.findViewById(R.id.fab_add_budget);

        BudgetRepository budgetRepository = new BudgetRepository(requireContext());
        TransactionRepository transactionRepository = new TransactionRepository(requireContext());

        BudgetViewModelFactory factory = new BudgetViewModelFactory(budgetRepository, transactionRepository);
        budgetViewModel = new ViewModelProvider(this, factory).get(BudgetViewModel.class);

        budgetAdapter = new BudgetAdapter(budgetViewModel, getViewLifecycleOwner(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(budgetAdapter);

        budgetViewModel.getBudgets().observe(getViewLifecycleOwner(), budgetList -> {
            budgetAdapter.updateBudgets(budgetList);
        });

        fabAddBudget.setOnClickListener(v -> onEnterEditMode(new AddBudgetFragment(this)));
    }

    public List<BudgetEntityWithCategory> getSelectedItems() {
        return budgetAdapter.getSelectedItems();
    }

    public void deleteSelected() {
        if (getSelectedItems().isEmpty()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_budget_confirmation_title)
                .setMessage(R.string.delete_budget_confirmation_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    List<BudgetEntityWithCategory> selectedItems = getSelectedItems();
                    budgetViewModel.deleteBudgets(selectedItems);
                    budgetAdapter.clearSelection();
                    onExitSelectionMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onEnterSelectionMode() {
        fabAddBudget.hide();

        ((SelectionModeListener) requireActivity()).onEnterSelectionMode();
    }

    @Override
    public void onExitSelectionMode() {
        fabAddBudget.show();
        budgetAdapter.clearSelection();

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

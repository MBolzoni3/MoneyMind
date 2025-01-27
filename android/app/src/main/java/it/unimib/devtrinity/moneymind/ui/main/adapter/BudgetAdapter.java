package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.AddBudgetFragment;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModel;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final BudgetViewModel budgetViewModel;
    private final LifecycleOwner lifecycleOwner;
    private final FragmentManager fragmentManager;
    private final List<BudgetEntityWithCategory> budgetList = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();
    private final SelectionModeListener selectionListener;

    private boolean isSelectionModeActive = false;

    public BudgetAdapter(BudgetViewModel viewModel, LifecycleOwner lifecycleOwner, SelectionModeListener listener, FragmentManager fragmentManager) {
        this.budgetViewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.selectionListener = listener;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_item_layout, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetEntityWithCategory budget = budgetList.get(position);

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionModeActive) {
                isSelectionModeActive = true;
                selectionListener.onEnterSelectionMode();
            }
            toggleSelection(position);
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionModeActive) {
                toggleSelection(position);
            } else {
                AddBudgetFragment addBudgetFragment = new AddBudgetFragment();
                addBudgetFragment.setBudget(budget.getBudget());

                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, addBudgetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        boolean isSelected = selectedPositions.contains(position);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
            holder.budgetProgress.setTrackColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
        } else {
            holder.cardView.setCardBackgroundColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
            holder.budgetProgress.setTrackColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        }

        holder.cardView.setChecked(isSelected);

        holder.budgetName.setText(budget.getBudget().getName());
        holder.categoryName.setText(budget.getCategory() != null ? budget.getCategory().getName() : "");
        holder.dateRange.setText(String.format("%1$td/%1$tm - %2$td/%2$tm", budget.getBudget().getStartDate(), budget.getBudget().getEndDate()));

        LiveData<Long> progressLiveData = budgetViewModel.getSpentAmount(budget);
        progressLiveData.observe(lifecycleOwner, spentAmountLong -> {
            BigDecimal spentAmount = Utils.longToBigDecimal(spentAmountLong);
            int progress = 0;

            if (spentAmount != null) {
                progress = Math.min(
                        spentAmount.multiply(BigDecimal.valueOf(100)).divide(budget.getBudget().getAmount(), 2, RoundingMode.HALF_UP).intValue(),
                        100);
            }

            holder.spentAmount.setText(String.format("Hai speso €%s di €%s previsti", spentAmount == null ? BigDecimal.ZERO : spentAmount, budget.getBudget().getAmount()));
            holder.budgetProgress.setProgress(progress);
        });
    }

    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        notifyItemChanged(position);

        if (selectedPositions.isEmpty()) {
            isSelectionModeActive = false;
            selectionListener.onExitSelectionMode();
        } else {
            selectionListener.onSelectionCountChanged(selectedPositions.size());
        }
    }

    public List<BudgetEntityWithCategory> getSelectedItems() {
        List<BudgetEntityWithCategory> selectedItems = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedItems.add(budgetList.get(position));
        }
        return selectedItems;
    }

    public void clearSelection() {
        selectedPositions.clear();
        isSelectionModeActive = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public void updateBudgets(List<BudgetEntityWithCategory> newBudgets) {
        budgetList.clear();
        budgetList.addAll(newBudgets);
        notifyDataSetChanged();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetName, categoryName, spentAmount, dateRange;
        LinearProgressIndicator budgetProgress;
        MaterialCardView cardView;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.budget_card_view);
            budgetName = itemView.findViewById(R.id.budget_name);
            categoryName = itemView.findViewById(R.id.budget_category);
            spentAmount = itemView.findViewById(R.id.spent_amount);
            dateRange = itemView.findViewById(R.id.budget_date_range);
            budgetProgress = itemView.findViewById(R.id.budget_progress);
        }
    }
}

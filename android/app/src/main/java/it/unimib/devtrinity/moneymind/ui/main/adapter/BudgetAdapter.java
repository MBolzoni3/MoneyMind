package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
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
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final BudgetViewModel budgetViewModel;
    private final LifecycleOwner lifecycleOwner;
    private final List<BudgetEntityWithCategory> budgetList = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();
    private final SelectionModeListener selectionListener;

    private boolean isSelectionModeActive = false;

    public BudgetAdapter(BudgetViewModel viewModel, LifecycleOwner lifecycleOwner, SelectionModeListener selectionListener) {
        this.budgetViewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.selectionListener = selectionListener;
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

        int iconResource = ResourceHelper.getCategoryIcon(budget.getCategory());
        holder.categoryIcon.setImageResource(iconResource);

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
                AddBudgetFragment addBudgetFragment = new AddBudgetFragment(selectionListener);
                addBudgetFragment.setBudget(budget.getBudget());

                selectionListener.onEnterEditMode(addBudgetFragment);
            }
        });

        boolean isSelected = selectedPositions.contains(position);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
            holder.budgetProgress.setTrackColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
        } else {
            holder.cardView.setCardBackgroundColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
            holder.budgetProgress.setTrackColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        }

        holder.cardView.setChecked(isSelected);

        holder.budgetName.setText(budget.getBudget().getName());
        holder.categoryName.setText(budget.getCategory() != null ? ResourceHelper.getCategoryName(holder.itemView.getContext(), budget.getCategory().getName()) : "");
        holder.dateRange.setText(ResourceHelper.getFormattedDateRange(holder.itemView.getContext(), budget.getBudget().getStartDate(), budget.getBudget().getEndDate()));

        LiveData<Long> progressLiveData = budgetViewModel.getSpentAmount(budget);
        progressLiveData.observe(lifecycleOwner, spentAmountLong -> {
            BigDecimal spentAmount = Utils.longToBigDecimal(spentAmountLong);
            int progress = 0;

            if (spentAmount != null) {
                progress = Math.min(
                        spentAmount.multiply(BigDecimal.valueOf(100)).divide(budget.getBudget().getAmount(), 2, RoundingMode.HALF_UP).intValue(),
                        100);
            }

            holder.spentAmount.setText(ResourceHelper.getBudgetMessage(holder.itemView.getContext(), spentAmount, budget.getBudget().getAmount()));
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

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetName, categoryName, spentAmount, dateRange;
        LinearProgressIndicator budgetProgress;
        ShapeableImageView categoryIcon;
        MaterialCardView cardView;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.budget_card_view);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            budgetName = itemView.findViewById(R.id.budget_name);
            categoryName = itemView.findViewById(R.id.budget_category);
            spentAmount = itemView.findViewById(R.id.spent_amount);
            dateRange = itemView.findViewById(R.id.budget_date_range);
            budgetProgress = itemView.findViewById(R.id.budget_progress);
        }
    }

}

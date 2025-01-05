package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModel;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final List<BudgetEntityWithCategory> budgetList = new ArrayList<>();
    private final BudgetViewModel budgetViewModel;
    private final LifecycleOwner lifecycleOwner;

    public BudgetAdapter(BudgetViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.budgetViewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
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

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            categoryName = itemView.findViewById(R.id.budget_category);
            spentAmount = itemView.findViewById(R.id.spent_amount);
            dateRange = itemView.findViewById(R.id.budget_date_range);
            budgetProgress = itemView.findViewById(R.id.budget_progress);
        }
    }
}

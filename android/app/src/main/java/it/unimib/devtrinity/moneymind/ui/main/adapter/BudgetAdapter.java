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
import java.util.ArrayList;
import java.util.List;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.BudgetViewModel;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final List<BudgetEntity> budgetList = new ArrayList<>();
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
        BudgetEntity budget = budgetList.get(position);

        holder.budgetName.setText(budget.getName());
        holder.budgetAmount.setText(String.format("€%d", budget.getAmount()));
        holder.dateRange.setText(String.format("%1$td/%1$tm - %2$td/%2$tm",
                budget.getStartDate(), budget.getEndDate()));

        LiveData<Integer> progressLiveData = budgetViewModel.getProgress(budget);
        progressLiveData.observe(lifecycleOwner, progress -> {
            holder.budgetProgress.setProgress(progress != null ? progress : 0);
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public void updateBudgets(List<BudgetEntity> newBudgets) {
        budgetList.clear();
        budgetList.addAll(newBudgets);
        notifyDataSetChanged();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetName, budgetAmount, dateRange;
        LinearProgressIndicator budgetProgress;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            budgetAmount = itemView.findViewById(R.id.budget_amount);
            dateRange = itemView.findViewById(R.id.budget_date_range);
            budgetProgress = itemView.findViewById(R.id.budget_progress);
        }
    }
}

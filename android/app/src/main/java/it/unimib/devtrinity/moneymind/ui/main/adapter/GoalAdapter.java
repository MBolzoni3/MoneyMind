package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.AddGoalFragment;
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private final List<GoalEntityWithCategory> goalList = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();
    private final SelectionModeListener selectionListener;

    private boolean isSelectionModeActive = false;

    public GoalAdapter(SelectionModeListener listener) {
        this.selectionListener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item_layout, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        GoalEntityWithCategory goal = goalList.get(position);

        int iconResource = ResourceHelper.getCategoryIcon(goal.getCategory());
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
                AddGoalFragment addGoalFragment = new AddGoalFragment(selectionListener);
                addGoalFragment.setGoal(goal.getGoal());

                selectionListener.onEnterEditMode(addGoalFragment);
            }
        });

        boolean isSelected = selectedPositions.contains(position);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
            holder.goalProgress.setTrackColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
        } else {
            holder.cardView.setCardBackgroundColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
            holder.goalProgress.setTrackColor(ResourceHelper.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        }

        holder.cardView.setChecked(isSelected);

        holder.goalName.setText(goal.getGoal().getName());
        holder.categoryName.setText(goal.getCategory() != null ? goal.getCategory().getName() : "");
        holder.dateRange.setText(ResourceHelper.getFormattedDateRange(holder.itemView.getContext(), goal.getGoal().getStartDate(), goal.getGoal().getEndDate()));

        int progress = 0;
        if (goal.getGoal().getSavedAmount() != null) {
            progress = Math.min(
                    goal.getGoal().getSavedAmount().multiply(BigDecimal.valueOf(100)).divide(goal.getGoal().getTargetAmount(), 2, RoundingMode.HALF_UP).intValue(),
                    100);
        }

        holder.savedAmount.setText(ResourceHelper.getGoalMessage(holder.itemView.getContext(), goal.getGoal().getSavedAmount(), goal.getGoal().getTargetAmount()));
        holder.goalProgress.setProgress(progress);
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

    public List<GoalEntityWithCategory> getSelectedItems() {
        List<GoalEntityWithCategory> selectedItems = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedItems.add(goalList.get(position));
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
        return goalList.size();
    }

    public void updateGoals(List<GoalEntityWithCategory> newGoals) {
        goalList.clear();
        goalList.addAll(newGoals);
        notifyDataSetChanged();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView goalName, categoryName, savedAmount, dateRange;
        LinearProgressIndicator goalProgress;
        ShapeableImageView categoryIcon;
        MaterialCardView cardView;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.goal_card_view);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            goalName = itemView.findViewById(R.id.goal_name);
            categoryName = itemView.findViewById(R.id.goal_category);
            savedAmount = itemView.findViewById(R.id.saved_amount);
            dateRange = itemView.findViewById(R.id.goal_date_range);
            goalProgress = itemView.findViewById(R.id.goal_progress);
        }
    }
}

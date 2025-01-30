package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RECURRING = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;
    private static final int VIEW_TYPE_TRANSACTION = 2;

    private final List<Object> transactionsList = new ArrayList<>();

    private final FragmentManager fragmentManager;
    private final Set<Integer> selectedPositions = new HashSet<>();
    private final SelectionModeListener selectionListener;

    private boolean isSelectionModeActive = false;

    public TransactionAdapter(SelectionModeListener listener, FragmentManager fragmentManager) {
        this.selectionListener = listener;
        this.fragmentManager = fragmentManager;
    }

    public void updateList(List<Object> newList) {
        transactionsList.clear();
        transactionsList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = transactionsList.get(position);
        if (item instanceof RecurringTransactionEntityWithCategory) {
            return VIEW_TYPE_RECURRING;
        } else if (item instanceof TransactionEntityWithCategory) {
            return VIEW_TYPE_TRANSACTION;
        } else if (item instanceof String && item.equals("divider")) {
            return VIEW_TYPE_DIVIDER;
        }

        throw new IllegalArgumentException("Unknown item type at position " + position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_RECURRING || viewType == VIEW_TYPE_TRANSACTION) {
            View view = inflater.inflate(R.layout.transaction_item_layout, parent, false);
            return new TransactionViewHolder(view);
        } else if (viewType == VIEW_TYPE_DIVIDER) {
            View view = inflater.inflate(R.layout.transaction_item_divider_layout, parent, false);
            return new DividerViewHolder(view);
        }

        throw new IllegalArgumentException("Unknown view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_RECURRING) {
            RecurringTransactionEntityWithCategory recurringTransaction = (RecurringTransactionEntityWithCategory) transactionsList.get(position);
            ((TransactionViewHolder) holder).bind(recurringTransaction);
        } else if (viewType == VIEW_TYPE_TRANSACTION) {
            TransactionEntityWithCategory transaction = (TransactionEntityWithCategory) transactionsList.get(position);
            ((TransactionViewHolder) holder).bind(transaction);
        } else if (viewType == VIEW_TYPE_DIVIDER) {
            ((DividerViewHolder) holder).bind("Movimenti Normali");
            return;
        }

        TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
        transactionViewHolder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionModeActive) {
                isSelectionModeActive = true;
                selectionListener.onEnterSelectionMode();
            }

            toggleSelection(position);
            return true;
        });

        transactionViewHolder.itemView.setOnClickListener(v -> {
            if (isSelectionModeActive) {
                toggleSelection(position);
            } else {
                /*AddGoalFragment addGoalFragment = new AddGoalFragment();
                addGoalFragment.setGoal(goal.getGoal());

                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, addGoalFragment)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        boolean isSelected = selectedPositions.contains(position);
        if (isSelected) {
            transactionViewHolder.cardView.setCardBackgroundColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
        } else {
            transactionViewHolder.cardView.setCardBackgroundColor(Utils.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceContainer));
        }

        transactionViewHolder.cardView.setChecked(isSelected);
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

    public List<Object> getSelectedItems() {
        List<Object> selectedItems = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedItems.add(transactionsList.get(position));
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
        return transactionsList.size();
    }

    public void updateTransactions(List<Object> newTransactions) {
        transactionsList.clear();
        transactionsList.addAll(newTransactions);
        notifyDataSetChanged();
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {
        TextView dividerTitle;

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            dividerTitle = itemView.findViewById(R.id.divider_title);
        }

        void bind(String title) {
            dividerTitle.setText(title);
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, date;
        ShapeableImageView categoryIcon, typeIcon;
        MaterialCardView cardView;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.transaction_card_view);
            name = itemView.findViewById(R.id.transaction_name);
            amount = itemView.findViewById(R.id.transaction_amount);
            date = itemView.findViewById(R.id.transaction_date);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            typeIcon = itemView.findViewById(R.id.transaction_type_icon);
        }

        void bind(TransactionEntityWithCategory transaction) {
            int iconResource = Utils.getCategoryIcon(transaction.getCategory());
            categoryIcon.setImageResource(iconResource);

            name.setText(transaction.getTransaction().getName());
            amount.setText(String.format("%.2f", transaction.getTransaction().getAmount()));
            date.setText(Utils.dateToString(transaction.getTransaction().getDate()));
        }

        void bind(RecurringTransactionEntityWithCategory recurringTransaction) {
            int iconResource = Utils.getCategoryIcon(recurringTransaction.getCategory());
            categoryIcon.setImageResource(iconResource);

            name.setText(recurringTransaction.getRecurringTransaction().getName());
            amount.setText(String.format("%.2f", recurringTransaction.getRecurringTransaction().getAmount()));
            date.setText(Utils.dateToString(recurringTransaction.getRecurringTransaction().getDate()));
        }
    }
}

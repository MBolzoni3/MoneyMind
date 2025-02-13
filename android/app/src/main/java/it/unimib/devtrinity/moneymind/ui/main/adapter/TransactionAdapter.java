package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.AddTransactionFragment;
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RECURRING = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;
    private static final int VIEW_TYPE_DIVIDER_NO_TEXT = 2;
    private static final int VIEW_TYPE_TRANSACTION = 3;

    private final SelectionModeListener selectionListener;
    private final List<Object> transactionsList = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();

    private boolean isSelectionModeActive = false;

    public TransactionAdapter(SelectionModeListener listener) {
        this.selectionListener = listener;
    }

    public void updateList(List<Object> newList) {
        transactionsList.clear();
        transactionsList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = transactionsList.get(position);
        if (item instanceof TransactionEntityWithCategory) {
            TransactionEntityWithCategory transaction = (TransactionEntityWithCategory) item;
            if (transaction.getTransaction() instanceof RecurringTransactionEntity) {
                return VIEW_TYPE_RECURRING;
            } else if (transaction.getTransaction() != null) {
                return VIEW_TYPE_TRANSACTION;
            }
        } else if ("divider".equals(item)) {
            return VIEW_TYPE_DIVIDER;
        } else if ("divider-no-text".equals(item)) {
            return VIEW_TYPE_DIVIDER_NO_TEXT;
        }

        throw new IllegalArgumentException("Unknown item type at position " + position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_RECURRING || viewType == VIEW_TYPE_TRANSACTION) {
            return new TransactionViewHolder(inflater.inflate(R.layout.transaction_item_layout, parent, false));
        } else if (viewType == VIEW_TYPE_DIVIDER) {
            return new DividerViewHolder(inflater.inflate(R.layout.transaction_item_divider_layout, parent, false));
        } else if (viewType == VIEW_TYPE_DIVIDER_NO_TEXT) {
            return new DividerNoTextViewHolder(inflater.inflate(R.layout.transaction_item_divider_layout, parent, false));
        }

        throw new IllegalArgumentException("Unknown view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = transactionsList.get(position);

        if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
            transactionViewHolder.bind((TransactionEntityWithCategory) item);

            setupItemClickListener(transactionViewHolder, position);
            updateSelectionState(transactionViewHolder, position);
        } else if (holder instanceof DividerViewHolder) {
            if (position == 0 && transactionsList.size() > 1) {
                TransactionEntityWithCategory transaction = (TransactionEntityWithCategory) transactionsList.get(position + 1);
                if (transaction.getTransaction() instanceof RecurringTransactionEntity) {
                    ((DividerViewHolder) holder).bind(holder.itemView.getContext().getString(R.string.recurring_movements));
                } else {
                    ((DividerViewHolder) holder).bind(holder.itemView.getContext().getString(R.string.movements));
                }
            } else {
                ((DividerViewHolder) holder).bind(holder.itemView.getContext().getString(R.string.movements));
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    private void setupItemClickListener(TransactionViewHolder holder, int position) {
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
                TransactionEntityWithCategory transactionEntityWithCategory = (TransactionEntityWithCategory) transactionsList.get(position);
                AddTransactionFragment addTransactionFragment = new AddTransactionFragment(selectionListener);
                addTransactionFragment.setTransaction(transactionEntityWithCategory.getTransaction());

                selectionListener.onEnterEditMode(addTransactionFragment);
            }
        });
    }

    private void updateSelectionState(TransactionViewHolder holder, int position) {
        boolean isSelected = selectedPositions.contains(position);
        holder.cardView.setCardBackgroundColor(ResourceHelper.getThemeColor(
                holder.itemView.getContext(),
                isSelected ? com.google.android.material.R.attr.colorSurfaceContainerHighest
                        : com.google.android.material.R.attr.colorSurfaceContainer
        ));
        holder.cardView.setChecked(isSelected);
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

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView dividerTextView;

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            dividerTextView = itemView.findViewById(R.id.divider);
        }

        void bind(String title) {
            dividerTextView.setText(title);
        }

    }

    static class DividerNoTextViewHolder extends RecyclerView.ViewHolder {

        DividerNoTextViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, amount, date;
        private final ShapeableImageView categoryIcon, typeIcon;
        private final MaterialCardView cardView;

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
            Context context = itemView.getContext();

            categoryIcon.setImageResource(ResourceHelper.getCategoryIcon(transaction.getCategory()));
            name.setText(transaction.getTransaction().getName());
            amount.setText(Utils.formatTransactionAmount(transaction.getTransaction().getAmount(), transaction.getTransaction().getType()));
            typeIcon.setImageResource(ResourceHelper.getTypeIcon(transaction.getTransaction().getType()));

            if (transaction.getTransaction() instanceof RecurringTransactionEntity) {
                RecurringTransactionEntity recurringTransaction = (RecurringTransactionEntity) transaction.getTransaction();
                date.setText(recurringTransaction.getFormattedRecurrence(context));
            } else {
                date.setText(Utils.dateToString(transaction.getTransaction().getDate()));
            }

            int typeColor = transaction.getTransaction().getType().equals(MovementTypeEnum.INCOME)
                    ? ResourceHelper.getThemeColor(context, com.google.android.material.R.attr.colorPrimary)
                    : ResourceHelper.getThemeColor(context, com.google.android.material.R.attr.colorError);

            typeIcon.setColorFilter(typeColor);
        }
    }
}

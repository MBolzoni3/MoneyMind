package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.AddTransactionFragment;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class TransactionHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TransactionEntityWithCategory> transactionsList = new ArrayList<>();

    public void updateList(List<TransactionEntityWithCategory> newList) {
        transactionsList.clear();
        transactionsList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TransactionViewHolder(inflater.inflate(R.layout.transaction_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionEntityWithCategory transaction = transactionsList.get(position);

        TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
        transactionViewHolder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, amount, date;
        private final ShapeableImageView categoryIcon, typeIcon;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.transaction_name);
            amount = itemView.findViewById(R.id.transaction_amount);
            date = itemView.findViewById(R.id.transaction_date);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            typeIcon = itemView.findViewById(R.id.transaction_type_icon);
        }

        void bind(TransactionEntityWithCategory transaction) {
            Context context = itemView.getContext();

            categoryIcon.setImageResource(Utils.getCategoryIcon(transaction.getCategory()));
            name.setText(transaction.getTransaction().getName());
            amount.setText(Utils.formatTransactionAmount(transaction.getTransaction().getAmount(), transaction.getTransaction().getType()));
            typeIcon.setImageResource(Utils.getTypeIcon(transaction.getTransaction().getType()));
            date.setText(Utils.dateToString(transaction.getTransaction().getDate()));

            int typeColor = transaction.getTransaction().getType().equals(MovementTypeEnum.INCOME)
                    ? Utils.getThemeColor(context, com.google.android.material.R.attr.colorPrimary)
                    : Utils.getThemeColor(context, com.google.android.material.R.attr.colorError);

            typeIcon.setColorFilter(typeColor);
        }
    }
}

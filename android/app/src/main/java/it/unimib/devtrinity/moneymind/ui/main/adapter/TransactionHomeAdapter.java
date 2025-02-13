package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.AddTransactionFragment;

public class TransactionHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SelectionModeListener selectionListener;

    private final List<TransactionEntityWithCategory> transactionsList = new ArrayList<>();

    public TransactionHomeAdapter(SelectionModeListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void updateList(List<TransactionEntityWithCategory> newList) {
        transactionsList.clear();
        transactionsList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TransactionAdapter.TransactionViewHolder(inflater.inflate(R.layout.transaction_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionEntityWithCategory transaction = transactionsList.get(position);

        TransactionAdapter.TransactionViewHolder transactionViewHolder = (TransactionAdapter.TransactionViewHolder) holder;
        transactionViewHolder.bind(transaction);
        setupItemClickListener(transactionViewHolder, position);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    private void setupItemClickListener(TransactionAdapter.TransactionViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            TransactionEntityWithCategory transactionEntityWithCategory = (TransactionEntityWithCategory) transactionsList.get(position);
            AddTransactionFragment addTransactionFragment = new AddTransactionFragment(selectionListener);
            addTransactionFragment.setTransaction(transactionEntityWithCategory.getTransaction());

            selectionListener.onEnterEditMode(addTransactionFragment);
        });
    }

}

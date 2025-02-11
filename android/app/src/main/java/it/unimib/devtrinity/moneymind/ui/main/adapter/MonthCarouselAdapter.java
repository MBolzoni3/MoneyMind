package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class MonthCarouselAdapter extends RecyclerView.Adapter<MonthCarouselAdapter.MonthViewHolder> {

    private final List<String> monthKeys = new ArrayList<>();
    private final Map<String, List<TransactionEntity>> transactionsListByMonth = new LinkedHashMap<>();

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_month_item, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        String monthKey = monthKeys.get(position);
        List<TransactionEntity> transactionsForMonth = transactionsListByMonth.get(monthKey);

        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal expenseTotal = BigDecimal.ZERO;

        if (transactionsForMonth != null) {
            for (TransactionEntity transaction : transactionsForMonth) {
                if (transaction.getType().equals(MovementTypeEnum.INCOME)) {
                    incomeTotal = incomeTotal.add(transaction.getAmount());
                } else {
                    expenseTotal = expenseTotal.add(transaction.getAmount());
                }
            }
        }

        holder.incomeText.setText(Utils.formatTransactionAmount(incomeTotal));
        holder.expenseText.setText(Utils.formatTransactionAmount(expenseTotal));

        if (incomeTotal.compareTo(expenseTotal) > 0) {
            holder.incomeProgressBar.setProgress(setFirstProgressBar(incomeTotal), true);
            holder.expenseProgressBar.setProgress(setSecondProgressBar(expenseTotal, incomeTotal), true);
        } else {
            holder.expenseProgressBar.setProgress(setFirstProgressBar(expenseTotal), true);
            holder.incomeProgressBar.setProgress(setSecondProgressBar(incomeTotal, expenseTotal), true);
        }

        holder.monthTextView.setText(Utils.formatMonthYear(monthKey));
        holder.monthHint.setText(ResourceHelper.getCurrentBalanceMessage(holder.itemView.getContext(), incomeTotal, expenseTotal));
    }

    @Override
    public int getItemCount() {
        return monthKeys.size();
    }

    public void updateMap(Map<String, List<TransactionEntity>> newMap) {
        transactionsListByMonth.putAll(newMap);
        monthKeys.clear();
        monthKeys.addAll(newMap.keySet());
        Collections.reverse(monthKeys);
        notifyDataSetChanged();
    }

    public int setFirstProgressBar(BigDecimal total) {
        double doubleTotal = total.doubleValue();
        double roundedTotal = Math.ceil((doubleTotal + 199.0) / 200) * 200;

        return (int) ((doubleTotal * 100) / roundedTotal);
    }

    public int setSecondProgressBar(BigDecimal minTotal, BigDecimal maxTotal) {
        double doubleMax = maxTotal.doubleValue();
        doubleMax = Math.ceil((doubleMax + 199.0) / 200) * 200;
        double doubleMin = minTotal.doubleValue();

        return (int) ((doubleMin * 100) / doubleMax);
    }

    public static class MonthViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView monthTextView;
        MaterialTextView monthHint;
        LinearProgressIndicator incomeProgressBar;
        LinearProgressIndicator expenseProgressBar;
        MaterialTextView incomeText;
        MaterialTextView expenseText;

        public MonthViewHolder(View itemView) {
            super(itemView);
            monthTextView = itemView.findViewById(R.id.carousel_month);
            monthHint = itemView.findViewById(R.id.carousel_month_hint);
            incomeProgressBar = itemView.findViewById(R.id.income_progress_bar);
            expenseProgressBar = itemView.findViewById(R.id.outflow_progress_bar);
            incomeText = itemView.findViewById(R.id.income_amount);
            expenseText = itemView.findViewById(R.id.outflow_amount);
        }
    }
}


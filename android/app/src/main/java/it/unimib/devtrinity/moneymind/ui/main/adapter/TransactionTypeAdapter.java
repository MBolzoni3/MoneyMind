package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;

public class TransactionTypeAdapter extends ArrayAdapter<MovementTypeEnum> {

    public TransactionTypeAdapter(@NonNull Context context, @NonNull List<MovementTypeEnum> types) {
        super(context, R.layout.category_item_layout, types);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item_layout, parent, false);
        }

        ShapeableImageView icon = convertView.findViewById(R.id.category_icon);
        TextView name = convertView.findViewById(R.id.category_name);

        MovementTypeEnum type = getItem(position);
        if(type != null) {
            switch (type) {
                case INCOME:
                    name.setText(getContext().getString(R.string.income));
                    icon.setImageResource(R.drawable.ic_trending_up);
                    break;
                case EXPENSE:
                    name.setText(getContext().getString(R.string.expense));
                    icon.setImageResource(R.drawable.ic_trending_down);
                    break;
            }
        }

        return convertView;
    }

}


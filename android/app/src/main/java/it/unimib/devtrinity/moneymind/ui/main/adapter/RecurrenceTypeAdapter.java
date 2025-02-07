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
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;

public class RecurrenceTypeAdapter extends ArrayAdapter<RecurrenceTypeEnum> {

    public RecurrenceTypeAdapter(@NonNull Context context, @NonNull List<RecurrenceTypeEnum> types) {
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

        RecurrenceTypeEnum type = getItem(position);
        if(type != null) {
            switch (type) {
                case DAILY:
                    name.setText(R.string.daily);
                    break;
                case WEEKLY:
                    name.setText(R.string.weekly);
                    break;
                case MONTHLY:
                    name.setText(R.string.monthly);
                    break;
                case YEARLY:
                    name.setText(R.string.yearly);
                    break;
            }
        }

        icon.setVisibility(View.GONE);

        return convertView;
    }

}


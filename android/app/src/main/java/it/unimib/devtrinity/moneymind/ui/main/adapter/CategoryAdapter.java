package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;

public class CategoryAdapter extends ArrayAdapter<CategoryEntity> {

    public CategoryAdapter(@NonNull Context context, @NonNull List<CategoryEntity> categories) {
        super(context, android.R.layout.simple_dropdown_item_1line, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = (TextView) convertView;
        CategoryEntity category = getItem(position);

        if (category != null) {
            textView.setText(category.getName());
        }

        return convertView;
    }
}


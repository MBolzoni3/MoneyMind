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
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;

public class CategoryAdapter extends ArrayAdapter<CategoryEntity> {

    public CategoryAdapter(@NonNull Context context, @NonNull List<CategoryEntity> categories) {
        super(context, R.layout.category_item_layout, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item_layout, parent, false);
        }

        ShapeableImageView categoryIcon = convertView.findViewById(R.id.category_icon);
        TextView categoryName = convertView.findViewById(R.id.category_name);

        CategoryEntity category = getItem(position);
        if (category != null) {
            categoryName.setText(ResourceHelper.getCategoryName(getContext(), category.getName()));

            int iconResource = ResourceHelper.getCategoryIcon(category);
            categoryIcon.setImageResource(iconResource);
        }

        return convertView;
    }

}


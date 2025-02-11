package it.unimib.devtrinity.moneymind.ui.main.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.R;

import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class InfiniteDotsAdapter extends RecyclerView.Adapter<InfiniteDotsAdapter.DotViewHolder> {
    private static final int DOT_COUNT = 3;
    private int selectedPosition = 0;
    private final Context context;

    public InfiniteDotsAdapter(Context context) {
        this.context = context;
    }

    public void setSelectedPosition(int position) {
        int newSelected = (position + 2) % DOT_COUNT;
        if (newSelected != selectedPosition) {
            selectedPosition = newSelected;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dot = new View(parent.getContext());
        int size = 8 * parent.getContext().getResources().getDisplayMetrics().densityDpi / 160;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(8, 0, 8, 0);
        dot.setLayoutParams(params);
        return new DotViewHolder(dot);
    }

    @Override
    public void onBindViewHolder(@NonNull DotViewHolder holder, int position) {
        boolean isSelected = (position % DOT_COUNT == selectedPosition);

        holder.dotView.setBackground(createDotDrawable(isSelected));
        if (isSelected) {
            holder.dotView.animate()
                    .scaleX(1.5f)
                    .scaleY(1.2f)
                    .setDuration(150)
                    .start();
        } else {
            holder.dotView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return DOT_COUNT;
    }

    private GradientDrawable createDotDrawable(boolean isSelected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        int color = isSelected ?
                ResourceHelper.getThemeColor(context, R.attr.colorPrimary) :
                ResourceHelper.getThemeColor(context, R.attr.colorSurfaceContainer);
        drawable.setColor(color);
        return drawable;
    }

    static class DotViewHolder extends RecyclerView.ViewHolder {
        View dotView;

        DotViewHolder(View itemView) {
            super(itemView);
            dotView = itemView;
        }
    }
}

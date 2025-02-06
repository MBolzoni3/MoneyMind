package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.math.BigDecimal;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.main.adapter.MonthCarouselAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionHomeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class HomeFragment extends Fragment {

    ViewPager2 monthsViewPager;
    MonthCarouselAdapter monthCarouselAdapter;
    boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TransactionRepository transactionRepository = new TransactionRepository(requireContext());
        HomeViewModelFactory factory = new HomeViewModelFactory(transactionRepository);
        HomeViewModel viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        //MaterialTextView welcomeTextView = view.findViewById(R.id.home_title);
        //welcomeTextView.setText(getWelcomeMessage());

        monthsViewPager = view.findViewById(R.id.months_view_pager);
        monthCarouselAdapter = new MonthCarouselAdapter();
        monthsViewPager.setAdapter(monthCarouselAdapter);

        viewModel.getTransactionsByMonth().observe(getViewLifecycleOwner(), transactionsByMonth -> {
            int oldSize = monthCarouselAdapter.getItemCount();
            int oldPosition = monthsViewPager.getCurrentItem();

            monthCarouselAdapter.updateMap(transactionsByMonth);

            int newSize = monthCarouselAdapter.getItemCount();

            if (oldSize > 0 && oldPosition == 0 && newSize > oldSize) {
                int insertedCount = newSize - oldSize;
                monthsViewPager.setCurrentItem(oldPosition + insertedCount, false);
            }

            if (isFirstLoad) {
                monthsViewPager.setCurrentItem(newSize - 1, false);
                isFirstLoad = false;
            }
        });

        monthsViewPager.setClipToPadding(false);
        monthsViewPager.setClipChildren(false);
        monthsViewPager.setOffscreenPageLimit(3);

        monthsViewPager.setPageTransformer((page, position) -> {
            int pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
            int pagerOffset = getResources().getDimensionPixelOffset(R.dimen.pagerOffset);
            float offset = position * -(2 * pagerOffset + pageMargin);

            if (monthsViewPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (page.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    page.setTranslationX(-offset);
                } else {
                    page.setTranslationX(offset);
                }
            } else {
                page.setTranslationY(offset);
            }

            float scaleFactor = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scaleFactor);
            page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f);

            float interpolatedZ = 1f - Math.min(1f, Math.abs(position));
            page.setTranslationZ(interpolatedZ);
        });

        monthsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    viewModel.loadMoreMonths();
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.last_transactions_recycler);
        TransactionHomeAdapter transactionAdapter = new TransactionHomeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        viewModel.getLastTransactions().observe(getViewLifecycleOwner(), transactionAdapter::updateList);
    }

    private String getWelcomeMessage(){
        String displayName = FirebaseHelper.getInstance().getCurrentUser().getDisplayName();
        int choice = (int) (Math.random() * 3);

        switch (choice) {
            case 0:
                return "Ehila, " + displayName + "!";
            case 1:
                return "Che bello rivederti, " + displayName + "!";
            default:
                return "Buongiorno, " + displayName + "!";
        }
    }
}
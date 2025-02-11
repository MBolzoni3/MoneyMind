package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.InfiniteDotsAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.MonthCarouselAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionHomeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.HomeViewModelFactory;

public class HomeFragment extends Fragment implements SelectionModeListener {

    private HomeViewModel viewModel;
    private ViewPager2 monthsViewPager;
    private MonthCarouselAdapter monthCarouselAdapter;
    private final Observer<Integer> pageObserver = savedPage -> {
        if (monthCarouselAdapter != null) {
            int pageToLoad = (savedPage != null && savedPage >= 0) ? savedPage : monthCarouselAdapter.getItemCount() - 1;
            monthsViewPager.setCurrentItem(pageToLoad, false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TransactionRepository transactionRepository = ServiceLocator.getInstance().getTransactionRepository(requireActivity().getApplication());
        HomeViewModelFactory factory = new HomeViewModelFactory(transactionRepository);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        monthsViewPager = view.findViewById(R.id.months_view_pager);
        RecyclerView dotsRecycler = view.findViewById(R.id.dots_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dotsRecycler.setLayoutManager(layoutManager);

        InfiniteDotsAdapter dotsAdapter = new InfiniteDotsAdapter(getContext());
        dotsRecycler.setAdapter(dotsAdapter);

        monthCarouselAdapter = new MonthCarouselAdapter();
        monthsViewPager.setAdapter(monthCarouselAdapter);
        monthsViewPager.setClipToPadding(false);
        monthsViewPager.setClipChildren(false);
        monthsViewPager.setOffscreenPageLimit(3);

        monthsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                dotsRecycler.smoothScrollToPosition(position);
                dotsAdapter.setSelectedPosition(position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    viewModel.loadMoreMonths();
                }
            }
        });

        viewModel.getTransactionsByMonth().observe(getViewLifecycleOwner(), transactionsByMonth -> {
            int oldSize = monthCarouselAdapter.getItemCount();
            int oldPosition = monthsViewPager.getCurrentItem();

            monthCarouselAdapter.updateMap(transactionsByMonth);
            int newSize = monthCarouselAdapter.getItemCount();
            if (oldSize > 0 && oldPosition == 0 && newSize > oldSize) {
                int insertedCount = newSize - oldSize;
                monthsViewPager.setCurrentItem(oldPosition + insertedCount, false);
            }

            viewModel.getCurrentPage().removeObserver(pageObserver);
            viewModel.getCurrentPage().observe(getViewLifecycleOwner(), pageObserver);
        });

        RecyclerView recyclerView = view.findViewById(R.id.last_transactions_recycler);
        TransactionHomeAdapter transactionAdapter = new TransactionHomeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        viewModel.getLastTransactions().observe(getViewLifecycleOwner(), newList -> {
            transactionAdapter.updateList(newList);
            view.findViewById(R.id.emptyStateLayout).setVisibility(newList.isEmpty() ? View.VISIBLE : View.GONE);
        });

        ExtendedFloatingActionButton addTransactionButton = view.findViewById(R.id.fab_add_transaction);
        addTransactionButton.setOnClickListener(v -> onEnterEditMode(new AddTransactionFragment(this)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getCurrentPage().removeObserver(pageObserver);
        viewModel.setCurrentPage(monthsViewPager.getCurrentItem());
    }

    @Override
    public void onExitEditMode() {
        ((SelectionModeListener) requireActivity()).onExitEditMode();
    }

    @Override
    public void onEnterEditMode(Fragment fragment) {
        ((SelectionModeListener) requireActivity()).onEnterEditMode(fragment);
    }

    @Override
    public void onSelectionCountChanged(int count) {

    }

    @Override
    public void onExitSelectionMode() {

    }

    @Override
    public void onEnterSelectionMode() {

    }

}
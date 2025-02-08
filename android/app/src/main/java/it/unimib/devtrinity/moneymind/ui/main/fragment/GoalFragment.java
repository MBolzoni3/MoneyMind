package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.GoalAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.GoalViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.GoalViewModelFactory;

public class GoalFragment extends Fragment implements SelectionModeListener {

    private GoalViewModel goalViewModel;
    private GoalAdapter goalAdapter;
    private FloatingActionButton fabAddGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.goal_recycler_view);
        fabAddGoal = view.findViewById(R.id.fab_add_goal);

        GoalRepository goalRepository = ServiceLocator.getInstance().getGoalRepository(requireContext());
        GoalViewModelFactory factory = new GoalViewModelFactory(goalRepository);
        goalViewModel = new ViewModelProvider(this, factory).get(GoalViewModel.class);

        goalAdapter = new GoalAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(goalAdapter);

        goalViewModel.getGoals().observe(getViewLifecycleOwner(), goalList -> {
            goalAdapter.updateGoals(goalList);
        });

        fabAddGoal.setOnClickListener(v -> onEnterEditMode(new AddGoalFragment(this)));
    }

    public List<GoalEntityWithCategory> getSelectedItems() {
        return goalAdapter.getSelectedItems();
    }

    public void deleteSelected() {
        if (getSelectedItems().isEmpty()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_goal_confirmation_title)
                .setMessage(R.string.delete_goal_confirmation_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    List<GoalEntityWithCategory> selectedItems = getSelectedItems();
                    goalViewModel.deleteGoals(selectedItems);
                    goalAdapter.clearSelection();
                    onExitSelectionMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onEnterSelectionMode() {
        fabAddGoal.hide();

        ((SelectionModeListener) requireActivity()).onEnterSelectionMode();
    }

    @Override
    public void onExitSelectionMode() {
        fabAddGoal.show();
        goalAdapter.clearSelection();

        ((SelectionModeListener) requireActivity()).onExitSelectionMode();
    }

    @Override
    public void onSelectionCountChanged(int count) {
        ((SelectionModeListener) requireActivity()).onSelectionCountChanged(count);
    }

    @Override
    public void onExitEditMode() {
        ((SelectionModeListener) requireActivity()).onExitEditMode();
    }

    @Override
    public void onEnterEditMode(Fragment fragment) {
        ((SelectionModeListener) requireActivity()).onEnterEditMode(fragment);
    }

}

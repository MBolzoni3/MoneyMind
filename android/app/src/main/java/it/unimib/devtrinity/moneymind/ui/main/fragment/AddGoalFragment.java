package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModelFactory;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddGoalViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddGoalViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddGoalFragment extends Fragment {

    private GoalEntity currentGoal;
    private GoalRepository goalRepository;
    private CategoryRepository categoryRepository;

    private TextInputEditText nameField;
    private TextInputEditText targetAmountField;
    private TextInputEditText savedAmountField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText startDateField;
    private TextInputEditText endDateField;

    public void setGoal(GoalEntity goal) {
        this.currentGoal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_goal_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(currentGoal == null ? "Aggiungi Obiettivo" : "Modifica Obiettivo");

        goalRepository = new GoalRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());
        AddGoalViewModelFactory factory = new AddGoalViewModelFactory(categoryRepository);
        AddGoalViewModel viewModel = new ViewModelProvider(this, factory).get(AddGoalViewModel.class);

        nameField = view.findViewById(R.id.edit_goal_name);
        targetAmountField = view.findViewById(R.id.edit_goal_target_amount);
        savedAmountField = view.findViewById(R.id.edit_goal_saved_amount);

        categoryDropdown = view.findViewById(R.id.edit_goal_category);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories);
            categoryDropdown.setAdapter(adapter);

            if (currentGoal != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CategoryEntity category = adapter.getItem(i);

                    if (category != null && category.getFirestoreId().equals(currentGoal.getCategoryId())) {
                        selectedCategory = category;
                        categoryDropdown.setText(category.getName(), false);
                        break;
                    }
                }
            }
        });

        categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCategory = (CategoryEntity) parent.getItemAtPosition(position);
            if (selectedCategory != null) {
                categoryDropdown.setText(selectedCategory.getName(), false);
            }
        });

        startDateField = view.findViewById(R.id.edit_start_date);
        endDateField = view.findViewById(R.id.edit_end_date);

        startDateField.setOnClickListener(v -> {
            showDatePicker(startDateField::setText);
        });

        endDateField.setOnClickListener(v -> {
            showDatePicker(endDateField::setText);
        });

        MaterialButton saveButton = view.findViewById(R.id.button_save_goal);
        MaterialButton cancelButton = view.findViewById(R.id.button_cancel);

        saveButton.setOnClickListener(v -> {
            saveGoal();
            navigateBack();
        });

        cancelButton.setOnClickListener(v -> navigateBack());

        compileFields();
    }

    private void compileFields(){
        if(currentGoal == null) return;

        nameField.setText(currentGoal.getName());
        targetAmountField.setText(currentGoal.getTargetAmount().toString());
        savedAmountField.setText(currentGoal.getSavedAmount().toString());
        startDateField.setText(Utils.dateToString(currentGoal.getStartDate()));
        endDateField.setText(Utils.dateToString(currentGoal.getEndDate()));
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }

    private void saveGoal() {
        GoalEntity goal = new GoalEntity(
                nameField.getText().toString(),
                Utils.safeParseBigDecimal(targetAmountField.getText().toString(), BigDecimal.ZERO),
                Utils.safeParseBigDecimal(savedAmountField.getText().toString(), BigDecimal.ZERO),
                Utils.stringToDate(startDateField.getText().toString()),
                Utils.stringToDate(endDateField.getText().toString()),
                selectedCategory.getFirestoreId(),
                FirebaseHelper.getInstance().getCurrentUser().getUid());

        if(currentGoal != null){
            currentGoal.setName(nameField.getText().toString());
            currentGoal.setTargetAmount(Utils.safeParseBigDecimal(targetAmountField.getText().toString(), BigDecimal.ZERO));
            currentGoal.setSavedAmount(Utils.safeParseBigDecimal(savedAmountField.getText().toString(), BigDecimal.ZERO));
            currentGoal.setStartDate(Utils.stringToDate(startDateField.getText().toString()));
            currentGoal.setEndDate(Utils.stringToDate(endDateField.getText().toString()));
            currentGoal.setCategoryId(selectedCategory.getFirestoreId());
            currentGoal.setUpdatedAt(Timestamp.now());
            currentGoal.setSynced(false);

            goal = currentGoal;
        }

        goalRepository.insertGoal(
                goal,
                new GenericCallback<>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        navigateBack();
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                }
        );
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @FunctionalInterface
    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
}


package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddGoalViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddGoalViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.TextInputHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddGoalFragment extends Fragment {

    private AddGoalViewModel viewModel;
    private final SelectionModeListener selectionModeListener;

    private GoalEntity currentGoal;

    private TextInputEditText nameField;
    private TextInputEditText targetAmountField;
    private TextInputEditText savedAmountField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText startDateField;
    private TextInputEditText endDateField;

    private TextInputLayout nameFieldLayout, targetAmountFieldLayout, savedAmountFieldLayout, startDateFieldLayout, endDateFieldLayout, categoryFieldLayout;

    private View thisView;

    public AddGoalFragment(SelectionModeListener selectionModeListener) {
        this.selectionModeListener = selectionModeListener;
    }

    public void setGoal(GoalEntity goal) {
        this.currentGoal = goal;
    }

    public GoalEntity getGoal() {
        return this.currentGoal;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thisView = view;

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                }
        );

        GoalRepository goalRepository = ServiceLocator.getInstance().getGoalRepository(requireActivity().getApplication());
        CategoryRepository categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireActivity().getApplication());

        AddGoalViewModelFactory factory = new AddGoalViewModelFactory(goalRepository, categoryRepository);
        viewModel = new ViewModelProvider(this, factory).get(AddGoalViewModel.class);

        nameField = view.findViewById(R.id.edit_goal_name);
        targetAmountField = view.findViewById(R.id.edit_goal_target_amount);
        savedAmountField = view.findViewById(R.id.edit_goal_saved_amount);

        nameFieldLayout = view.findViewById(R.id.input_goal_name);
        targetAmountFieldLayout = view.findViewById(R.id.input_goal_target_amount);
        savedAmountFieldLayout = view.findViewById(R.id.input_goal_saved_amount);
        startDateFieldLayout = view.findViewById(R.id.input_start_date);
        endDateFieldLayout = view.findViewById(R.id.input_end_date);
        categoryFieldLayout = view.findViewById(R.id.input_goal_category);

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
            Utils.showDatePicker(startDateField::setText, this);
        });

        endDateField.setOnClickListener(v -> {
            Utils.showEndDatePicker(endDateField::setText, this, startDateField);
        });

        compileFields();
        bindInputValidation();
    }

    public void onSaveButtonClick() {
        if (validateFields() && validateAmounts()){
            saveGoal();
            navigateBack();
        }

    }

    private boolean validateAmounts() {
        BigDecimal targetAmount = Utils.safeParseBigDecimal(targetAmountField.getText().toString(), BigDecimal.ZERO);
        BigDecimal savedAmount = Utils.safeParseBigDecimal(savedAmountField.getText().toString(), BigDecimal.ZERO);

        if(targetAmount.compareTo(savedAmount) <= 0){
            Toast.makeText(requireContext(), R.string.invalid_amounts_error, Toast.LENGTH_SHORT).show();
            targetAmountFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            savedAmountFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            return false;
        } else {
            return true;
        }
    }

    private void bindInputValidation(){
        TextInputHelper.addValidationWatcher(nameFieldLayout, nameField, getString(R.string.error_field_required), getString(R.string.invalid_name_error), TextInputHelper.ENTITY_NAME_REGEX);
        TextInputHelper.addValidationWatcher(targetAmountFieldLayout, targetAmountField, getString(R.string.empty_targetamount_error), null, null);
        TextInputHelper.addValidationWatcher(savedAmountFieldLayout, savedAmountField, getString(R.string.empty_savedamount_error), null, null);
        TextInputHelper.addValidationWatcher(categoryFieldLayout, categoryDropdown, getString(R.string.empty_category_error), null, null);
        TextInputHelper.addValidationWatcher(startDateFieldLayout, startDateField, getString(R.string.empty_startdate_error), null, null);
        TextInputHelper.addValidationWatcher(endDateFieldLayout, endDateField, getString(R.string.empty_enddate_error), null, null);
    }

    private boolean validateFields() {
        if(!TextInputHelper.validateField(nameFieldLayout, nameField, getString(R.string.error_field_required), getString(R.string.invalid_name_error), TextInputHelper.ENTITY_NAME_REGEX)){
            return false;
        }

        if(!TextInputHelper.validateField(targetAmountFieldLayout, targetAmountField, getString(R.string.empty_targetamount_error), null, null)){
            return false;
        }

        if(!TextInputHelper.validateField(savedAmountFieldLayout, savedAmountField, getString(R.string.empty_savedamount_error), null, null)){
            return false;
        }

        if(!TextInputHelper.validateField(categoryFieldLayout, selectedCategory == null ? "" : "category", getString(R.string.empty_category_error), null, null)){
            return false;
        }

        if(!TextInputHelper.validateField(startDateFieldLayout, startDateField, getString(R.string.empty_startdate_error), null, null)){
            return false;
        }

        if(!TextInputHelper.validateField(endDateFieldLayout, endDateField, getString(R.string.empty_enddate_error), null, null)){
            return false;
        }

        return true;
    }

    private void compileFields() {
        if (currentGoal == null) return;

        nameField.setText(currentGoal.getName());
        targetAmountField.setText(currentGoal.getTargetAmount().toString());
        savedAmountField.setText(currentGoal.getSavedAmount().toString());
        startDateField.setText(Utils.dateToString(currentGoal.getStartDate()));
        endDateField.setText(Utils.dateToString(currentGoal.getEndDate()));
    }

    private void navigateBack() {
        selectionModeListener.onExitEditMode();
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

        if (currentGoal != null) {
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

        viewModel.insertGoal(goal, new GenericCallback<>() {
            @Override
            public void onSuccess(Void result) {
                navigateBack();
            }

            @Override
            public void onFailure(String errorMessage) {
                Utils.makeSnackBar(thisView, errorMessage);
            }
        });
    }
}

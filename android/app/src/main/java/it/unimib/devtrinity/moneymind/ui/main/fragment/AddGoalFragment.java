package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.text.TextUtils;
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
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddGoalFragment extends Fragment {

    private final SelectionModeListener selectionModeListener;

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

    TextInputLayout nameFieldLayout;
    TextInputLayout targetAmountFieldLayout;
    TextInputLayout savedAmountFieldLayout;
    TextInputLayout startDateFieldLayout;
    TextInputLayout endDateFieldLayout;
    TextInputLayout categoryFieldLayout;



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

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                }
        );

        goalRepository = ServiceLocator.getInstance().getGoalRepository(requireContext());
        categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireContext());

        AddGoalViewModelFactory factory = new AddGoalViewModelFactory(categoryRepository);
        AddGoalViewModel viewModel = new ViewModelProvider(this, factory).get(AddGoalViewModel.class);

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
    }

    public void onSaveButtonClick() {
        if (validateField() && validateAmounts() && validateName(nameField, nameFieldLayout)) {
            saveGoal();
            navigateBack();
        }
    }

    private boolean validateField() {
        if (isEmptyField(nameField, nameFieldLayout, "Il campo relativo al nome del budget non può essere vuoto")) {
            return false;
        }
        if (isEmptyField(targetAmountField, targetAmountFieldLayout, "Il campo relativo all'importo target non può essere vuoto")) {
            return false;
        }
        if (isEmptyField(savedAmountField, savedAmountFieldLayout, "Il campo relativo all'importo risparmiato non può essere vuoto")) {
            return false;
        }
        if (selectedCategory == null) {
            categoryFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            Toast.makeText(requireContext(), "Il campo categoria non può essere vuoto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmptyField(startDateField, startDateFieldLayout, "Il campo relativo alla data di inizio non può essere vuoto")) {
            return false;
        }
        if (isEmptyField(endDateField, endDateFieldLayout, "Il campo relativo alla data di fine non può essere vuoto")) {
            return false;
        }
        return true;
    }

    private boolean isEmptyField(TextInputEditText field, TextInputLayout fieldLayout, String fieldError) {

        if (TextUtils.isEmpty(field.getText())) {
            fieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            Toast.makeText(requireContext(), fieldError, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private boolean validateAmounts(){
        BigDecimal targetAmount = Utils.safeParseBigDecimal(targetAmountField.getText().toString(), BigDecimal.ZERO);
        BigDecimal savedAmount = Utils.safeParseBigDecimal(savedAmountField.getText().toString(), BigDecimal.ZERO);

        if(targetAmount.compareTo(savedAmount) <= 0){
            Toast.makeText(requireContext(), "L'importo target deve essere maggiore dell'importo risparmiato", Toast.LENGTH_SHORT).show();
            targetAmountFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            savedAmountFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            return false;
        } else{
            return true;
        }
    }

    private boolean validateName(TextInputEditText field, TextInputLayout fieldLayout){
        String name = field.getText().toString();
        if (!name.matches("[a-zA-Z]+")) {
            fieldLayout.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.md_theme_error));
            fieldLayout.setHintTextColor(ContextCompat.getColorStateList(requireContext(), R.color.md_theme_error));
            Toast.makeText(requireContext(), "Il nome inserito deve contenere solo lettere", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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
}


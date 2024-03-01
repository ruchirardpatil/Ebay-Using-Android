package com.appname.ebaysearch.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;


import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Activity.ResultsActivity;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    // Define member variables for UI elements here
    // ...

    private CheckBox checkBoxEnableNearbySearch,checkBoxNew,checkBoxUsed,checkBoxUnspecified,checkBoxLocalPickup,checkBoxFreeShipping;
    private LinearLayout layoutNearbySearchOptions;
    private EditText editTextKeyword,editTextDistance;
    private AutoCompleteTextView editTextZipcode;
    private RadioButton radioButtonZipcode,radioButtonCurrentLocation;
    private TextView textViewKeywordError, textViewZipcodeError;
    private Button buttonSearch;
    private Button buttonClear;
    private Spinner spinner_category;
    ArrayList<String> selectedConditions = new ArrayList<>();
    ArrayList<String> selectedShipping = new ArrayList<>();
    JSONArray conditionsJsonArray;
    JSONArray shippingJsonArray;



    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);

        editTextKeyword = view.findViewById(R.id.edittext_keyword);
        editTextZipcode = view.findViewById(R.id.edittext_zipcode);
        editTextDistance = view.findViewById(R.id.edittext_distance);
        radioButtonZipcode = view.findViewById(R.id.radiobutton_zipcode);
        radioButtonCurrentLocation = view.findViewById(R.id.radiobutton_current_location);
        buttonSearch = view.findViewById(R.id.button_search);
        buttonClear = view.findViewById(R.id.button_clear);
        textViewKeywordError = view.findViewById(R.id.textview_keyword_error);
        textViewZipcodeError = view.findViewById(R.id.textview_zipcode_error);


        // Initialize your UI elements here
        // ...
        checkBoxEnableNearbySearch = view.findViewById(R.id.checkbox_enable_nearby_search);
        checkBoxNew = view.findViewById(R.id.checkbox_new);
        checkBoxUsed = view.findViewById(R.id.checkbox_used);
        checkBoxUnspecified = view.findViewById(R.id.checkbox_unspecified);
        checkBoxLocalPickup = view.findViewById(R.id.checkbox_local_pickup);
        checkBoxFreeShipping = view.findViewById(R.id.checkbox_free_shipping);
        spinner_category = view.findViewById(R.id.spinner_category);
        layoutNearbySearchOptions = view.findViewById(R.id.layout_nearby_search_options);


        // Set a listener on the checkbox
        checkBoxEnableNearbySearch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Set the visibility of the layout based on whether the checkbox is checked
            layoutNearbySearchOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                radioButtonCurrentLocation.setChecked(true);
            }
        });

        radioButtonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When "Current Location" is clicked, uncheck "Zipcode"
                if (radioButtonCurrentLocation.isChecked()) {
                    radioButtonZipcode.setChecked(false);
                }
            }
        });

        radioButtonZipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When "Zipcode" is clicked, uncheck "Current Location"
                if (radioButtonZipcode.isChecked()) {
                    radioButtonCurrentLocation.setChecked(false);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);

        // Listeners for condition checkboxes
        CompoundButton.OnCheckedChangeListener conditionListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = buttonView.getText().toString();

                if (isChecked) {
                    // Add the condition value if checked
                    selectedConditions.add(value);
                } else {
                    // Remove the condition value if unchecked
                    selectedConditions.remove(value);
                }
            }
        };

        checkBoxNew.setOnCheckedChangeListener(conditionListener);
        checkBoxUsed.setOnCheckedChangeListener(conditionListener);
        checkBoxUnspecified.setOnCheckedChangeListener(conditionListener);

        // Listeners for shipping checkboxes
        CompoundButton.OnCheckedChangeListener shippingListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = buttonView.getText().toString();

                if (isChecked) {
                    // Add the shipping option if checked
                    selectedShipping.add(value);
                } else {
                    // Remove the shipping option if unchecked
                    selectedShipping.remove(value);
                }
            }
        };

        checkBoxLocalPickup.setOnCheckedChangeListener(shippingListener);
        checkBoxFreeShipping.setOnCheckedChangeListener(shippingListener);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
            }
        });

        setupZipcodeAutocomplete();
        return view;
    }


    private void clearForm() {
        // Clear the EditText fields
        editTextKeyword.setText("");
        editTextZipcode.setText("");
        editTextDistance.setText("");

        // Reset the CheckBoxes
        checkBoxNew.setChecked(false);
        checkBoxUsed.setChecked(false);
        checkBoxUnspecified.setChecked(false);
        checkBoxLocalPickup.setChecked(false);
        checkBoxFreeShipping.setChecked(false);

        // Reset the RadioButtons
        radioButtonCurrentLocation.setChecked(false);
        radioButtonZipcode.setChecked(false);

        // Reset the Spinner to the first item
        spinner_category.setSelection(0);

        // Hide error messages
        textViewKeywordError.setVisibility(View.GONE);
        textViewZipcodeError.setVisibility(View.GONE);

        // Hide the nearby search options if they are visible
        layoutNearbySearchOptions.setVisibility(View.GONE);
        checkBoxEnableNearbySearch.setChecked(false);
    }

    private void setupZipcodeAutocomplete() {
        editTextZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    loadAutocompleteOptions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadAutocompleteOptions(String keyword) {
        // Call your API to get autocomplete options
        // For example, using your existing getautocomplete method
        APICalls.getInstance(getContext()).getautocomplete(keyword, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray postalCodes = response.getJSONArray("postalCodes");
                    ArrayList<String> zipcodes = new ArrayList<>();
                    for (int i = 0; i < postalCodes.length(); i++) {
                        JSONObject postalCode = postalCodes.getJSONObject(i);
                        zipcodes.add(postalCode.getString("postalCode"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, zipcodes);
                    editTextZipcode.setAdapter(adapter);
                    editTextZipcode.showDropDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(Exception error) {
                Log.d("Values", error.getMessage());
            }
        });

    }

    // Implement your event handling logic here
    // ...
    private void performSearch() {
        // Trim keyword to remove leading and trailing spaces
        String keyword = editTextKeyword.getText().toString().trim();
        String zipcode = editTextZipcode.getText().toString().trim();
        String distance = editTextDistance.getText().toString().trim();
        String selectedCategory = spinner_category.getSelectedItem().toString();




        boolean hasError = false;
        String toastMessage = "Please fix all fields with errors";

        textViewKeywordError.setVisibility(View.GONE);
        textViewZipcodeError.setVisibility(View.GONE);

        // Check if keyword is empty
        if (keyword.isEmpty()) {
            textViewKeywordError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        // Validate Zipcode if the corresponding RadioButton is checked
        if (layoutNearbySearchOptions.getVisibility() == View.VISIBLE &&
                radioButtonZipcode.isChecked() && zipcode.isEmpty()) {
            textViewZipcodeError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        // If there are any errors, show a Toast with all error messages
        if (hasError) {
            Toast.makeText(getContext(), toastMessage.toString(), Toast.LENGTH_LONG).show();
            return; // Stop further processing
        }
        else {
            conditionsJsonArray = new JSONArray(selectedConditions);
            shippingJsonArray = new JSONArray(selectedShipping);


            if(selectedCategory.contains("All")){
                selectedCategory = "all";
            }
            else if(selectedCategory.contains("Art")){
                selectedCategory = "art";
            }
            else if(selectedCategory.contains("Baby")){
                selectedCategory = "baby";
            }
            else if(selectedCategory.contains("Books")){
                selectedCategory = "books";
            }
            else if(selectedCategory.contains("Clothing")){
                selectedCategory = "clothing";
            }
            else if(selectedCategory.contains("Computer")){
                selectedCategory = "computer";
            }
            else if(selectedCategory.contains("Health")){
                selectedCategory = "health";
            }
            else if(selectedCategory.contains("Music")){
                selectedCategory = "music";
            }
            else if(selectedCategory.contains("Video")){
                selectedCategory = "video";
            }

            if(distance.isEmpty()){
                distance = "10";
            }
            if(zipcode.isEmpty()){
                zipcode = "90007";
            }

//            Log.d("Values", keyword);
//            Log.d("Values", selectedCategory);
//            Log.d("Values", String.valueOf(conditionsJsonArray));
//            Log.d("Values", String.valueOf(shippingJsonArray));
//            Log.d("Values", distance);
//            Log.d("Values", zipcode);

            Intent intent = new Intent(getContext(), ResultsActivity.class);
            intent.putExtra("keyword", keyword);
            intent.putExtra("category", selectedCategory);
            intent.putExtra("distance", distance);
            intent.putExtra("zipcode", zipcode);

            // Since JSONArray is not Serializable or Parcelable, you should convert it to a String
            intent.putExtra("conditions", conditionsJsonArray.toString());
            intent.putExtra("shipping", shippingJsonArray.toString());

            startActivity(intent);



        }
    }
}

package com.example.cmg_demo_1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class SettingsFragment extends DialogFragment {
    //widget
    private Spinner spinner_StateCode, spinner_PEC_1, spinner_PEC_2;
    private EditText editText_SignID;
    private ToggleButton toggleButton_MFC;
    private Button button_Confirm, button_Cancel;

    //vars
    private String mStateCodeValue, mSignIdValue, mPEC1Value, mPEC2Value, mMFCValue = "CM";
    private ArrayAdapter<CharSequence> stateCodeOptionsAdapter, panelEffectCode1OptionsAdapter, panelEffectCode2OptionsAdapter;
    private SharedPreferences sharedPreferences;
    private SharedViewModel sharedViewModel_settings;

    public static final String SHARED_PREFS = "SharedPreferenceSettings";
    public static final String STATE_CODE = "StateCodeValue";
    public static final String MFC_CODE = "ManufacturerCodeValue";
    public static final String SIGN_ID = "SignIdValue";
    public static final String PANEL_EFFECT_CODE_1 = "PanelEffectCode1Value";
    public static final String PANEL_EFFECT_CODE_2 = "PanelEffectCode2Vlaue";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_settings, container, false);
        //prevent dialog fragment dismiss when touch outside
        getDialog().setCanceledOnTouchOutside(false);

        rootview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                return false;
            }
        });
        //dismiss soft keyboard when dialog fragment pops up.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setWidget(rootview);

        loadSettings();

        initializeWidgets();

        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //nputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);

        //register the view model
        sharedViewModel_settings = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(
                    getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels
            );
        }catch (NullPointerException e){
            Log.e("SettingsFragment", e.getMessage());
        }
    }

    private void setWidget(View v) {
        spinner_StateCode = v.findViewById(R.id.spinner_Settings_StateCode_Value);
        spinner_PEC_1 = v.findViewById(R.id.spinner_Settings_PEC_1_Value);
        spinner_PEC_2 = v.findViewById(R.id.spinner_Settings_PEC_2_Value);
        editText_SignID = v.findViewById(R.id.editText_Settings_SignId_Value);
        toggleButton_MFC = v.findViewById(R.id.toggleButton_Settings_MfcCode_Value);
        button_Confirm = v.findViewById(R.id.button_Settings_Confirm);
        button_Cancel = v.findViewById(R.id.button_Settings_Cancel);

        stateCodeOptionsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.state_code, R.layout.spinner_item_layout_settings);
        //stateCodeOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_StateCode.setAdapter(stateCodeOptionsAdapter);
        spinner_StateCode.setSelection(0);

        panelEffectCode1OptionsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.panel_effect_code_1, R.layout.spinner_item_layout_settings);
        spinner_PEC_1.setAdapter(panelEffectCode1OptionsAdapter);
        spinner_PEC_1.setSelection(0);

        panelEffectCode2OptionsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.panel_effect_code_2, R.layout.spinner_item_layout_settings);
        spinner_PEC_2.setAdapter(panelEffectCode2OptionsAdapter);
        spinner_PEC_2.setSelection(0);

        toggleButton_MFC.setChecked(true);
        toggleButton_MFC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mMFCValue = compoundButton.getText().toString();
            }
        });

        spinner_StateCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String[] tempStr;
                //tempStr = adapterView.getItemAtPosition(i).toString().split("-");
                //mStateCodeValue = tempStr[1];
                mStateCodeValue = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_PEC_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String[] tempStr;
                //tempStr = adapterView.getItemAtPosition(i).toString().split("-");
                //mPEC1Value = tempStr[0];
                mPEC1Value = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_PEC_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String[] tempStr;
                //tempStr = adapterView.getItemAtPosition(i).toString().split("-");
                //mPEC2Value = tempStr[0];
                mPEC2Value = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                notifyViewModel();
                dismiss();
            }
        });

        button_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void saveSettings() {
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(MFC_CODE, toggleButton_MFC.getText().toString());
        editor.putString(STATE_CODE, spinner_StateCode.getSelectedItem().toString());
        editor.putString(SIGN_ID, editText_SignID.getText().toString());
        editor.putString(PANEL_EFFECT_CODE_1, spinner_PEC_1.getSelectedItem().toString());
        editor.putString(PANEL_EFFECT_CODE_2, spinner_PEC_2.getSelectedItem().toString());

        editor.apply();
        Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadSettings(){
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        mMFCValue = sharedPreferences.getString(MFC_CODE, "CM");
        mStateCodeValue = sharedPreferences.getString(STATE_CODE, "CM");
        mSignIdValue = sharedPreferences.getString(SIGN_ID, "000001");
        mPEC1Value = sharedPreferences.getString(PANEL_EFFECT_CODE_1, "Null-No panel effect code");
        mPEC2Value = sharedPreferences.getString(PANEL_EFFECT_CODE_2, "Null-No panel effect code");
    }

    private void initializeWidgets(){
        if (mMFCValue.equals("CM")) {
            toggleButton_MFC.setChecked(true);
        }else{
            toggleButton_MFC.setChecked(false);
        }

        editText_SignID.setText(mSignIdValue);

        ArrayAdapter arrayAdapterStateCode = (ArrayAdapter) spinner_StateCode.getAdapter();
        spinner_StateCode.setSelection(arrayAdapterStateCode.getPosition(mStateCodeValue));

        ArrayAdapter arrayAdapterPanelEffectCode1 = (ArrayAdapter) spinner_PEC_1.getAdapter();
        spinner_PEC_1.setSelection(arrayAdapterPanelEffectCode1.getPosition(mPEC1Value));

        ArrayAdapter arrayAdapterPanelEffectCode2 = (ArrayAdapter) spinner_PEC_2.getAdapter();
        spinner_PEC_2.setSelection(arrayAdapterPanelEffectCode2.getPosition(mPEC2Value));
    }

    private void readCurrentValueFromWidget(){
        mMFCValue = toggleButton_MFC.getText().toString();
        mStateCodeValue = spinner_StateCode.getSelectedItem().toString();
        mSignIdValue = editText_SignID.getText().toString();
        mPEC1Value = spinner_PEC_1.getSelectedItem().toString();
        mPEC2Value = spinner_PEC_2.getSelectedItem().toString();
    }

    private void notifyViewModel() {
        readCurrentValueFromWidget();
        sharedViewModel_settings.setMfc_code_value(mMFCValue);
        sharedViewModel_settings.setState_code_value(mStateCodeValue);
        sharedViewModel_settings.setSign_id_valuee(mSignIdValue);
        sharedViewModel_settings.setPanel_effect_code_1_value(mPEC1Value);
        sharedViewModel_settings.setPanel_effect_code_2_value(mPEC2Value);
    }

}

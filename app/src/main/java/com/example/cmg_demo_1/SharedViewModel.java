package com.example.cmg_demo_1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> mfc_code_value = new MutableLiveData<>();
    private final MutableLiveData<String> state_code_value = new MutableLiveData<>();
    private final MutableLiveData<String> sign_id_value = new MutableLiveData<>();
    private final MutableLiveData<String> panel_effect_code_1_value = new MutableLiveData<>();
    private final MutableLiveData<String> panel_effect_code_2_value = new MutableLiveData<>();

    public void setMfc_code_value (String s){
        mfc_code_value.setValue(s);
    }
    public void setState_code_value (String s){
        state_code_value.setValue(s);
    }
    public void setSign_id_valuee (String s){
        sign_id_value.setValue(s);
    }
    public void setPanel_effect_code_1_value (String s){
        panel_effect_code_1_value.setValue(s);
    }
    public void setPanel_effect_code_2_value (String s){
        panel_effect_code_2_value.setValue(s);
    }

    public LiveData<String> getMfc_code_value() {
        return mfc_code_value;
    }

    public LiveData<String> getState_code_value() {
        return state_code_value;
    }

    public LiveData<String> getSign_id_value() {
        return sign_id_value;
    }

    public LiveData<String> getPanel_effect_code_1_value() {
        return panel_effect_code_1_value;
    }

    public LiveData<String> getPanel_effect_code_2_value() {
        return panel_effect_code_2_value;
    }
}

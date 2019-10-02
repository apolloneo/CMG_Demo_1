package com.example.cmg_demo_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Set;

public class SignController_1_Activity extends AppCompatActivity {
    //widgets
    Button updateButton, goHomebutton, helpbutton, backbutton;
    ImageButton settingsButton;
    CheckBox autoUpdateCheckBox;
    TextView textViewTest;
    EditText[] editTextJackpotValues = new EditText[JACKPOT_QTY];
    EditText[] editTextMsgStrings = new EditText[MSG_QTY];
    Spinner[] spinnerDigitEffectValues = new Spinner[JACKPOT_QTY];
    Spinner[] spinnerMsgEffectValues = new Spinner[MSG_QTY];
    Group signCtrl_1_Hidden_Group, help_Page_Group;

    //vars
    private static final int JACKPOT_QTY = 4;
    private static final int MSG_QTY = 4;
    private String mStateCodeValue, mSignIdValue, mPEC1Value, mPEC2Value, mMFCValue;
    private String[] mCurrentJackpotValueArray = new String[JACKPOT_QTY],
            mCurrentMsgValueArray = new String[MSG_QTY],
            mCurrentDigitEffectCodeArray = new String[JACKPOT_QTY],
            mCurrentMsgEffectCodeArray = new String[MSG_QTY];
    private ArrayAdapter<CharSequence> spinnerSharedArrayAdapter;
    private SharedViewModel mSharedViewModel;
    //private UsbService usbService;
    //private MyHandler mHandler;
    private Handler mMsgSendHandler;
    private MyRunnable mRunnableCode;
    private int mAutoUpdateTimerInterval = 5000;
    private static int mButtonClickCounter = 1;
    private long mButtonClickTimer = 0;

    //this broadcaster receiver handles the data from UsbService received data from serial port
    private final BroadcastReceiver mSerialDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                if (action.equals(UsbService.ACTION_USB_DATA_RECEIVED)) {
                    byte[] dataReceived = intent.getExtras().getByteArray(UsbService.SERIAL_DATA_RECEIVED);
                    String readableData = null;
                    try {
                        readableData = new String(dataReceived,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Serial Data Received: " + readableData, Toast.LENGTH_SHORT).show();
                }
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };
    //private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        switch (intent.getAction()) {
    //            case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
    //                Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
    //                break;
    //            case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
    //                Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
    //                break;
    //            case UsbService.ACTION_NO_USB: // NO USB CONNECTED
    //                Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
    //                break;
    //            case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
    //                Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
    //                break;
    //            case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
    //                Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
    //                break;
    //        }
    //    }
    //};

    //private final ServiceConnection usbConnection = new ServiceConnection() {
    //    @Override
    //    public void onServiceConnected(ComponentName arg0, IBinder arg1) {
    //        usbService = ((UsbService.UsbBinder) arg1).getService();
    //        usbService.setHandler(mHandler);
    //    }
//
    //    @Override
    //    public void onServiceDisconnected(ComponentName arg0) {
    //        usbService = null;
    //    }
    //};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_controller_1);
        //prevent UI to show up
        updateUI();
        //register the view model
        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        //mHandler = new MyHandler(this);
        mMsgSendHandler = new Handler();
        mRunnableCode = new MyRunnable(mAutoUpdateTimerInterval);

        textViewTest = findViewById(R.id.textView_SignController_1_Test);

        setWidget();

        loadSettings();

        setObserves();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        setSerialDataReceiveFilter();
        //setFilters();  // Start listening notifications from UsbService
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSerialDataReceiver);
        //unregisterReceiver(mUsbReceiver);
        //unbindService(usbConnection);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    public void updateUI() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    public class MyRunnable implements Runnable {
        private int interval;

        public MyRunnable(int millisecs) {
            this.interval = millisecs;
        }

        @Override
        public void run() {
            // Do something here on the main thread
            //int r1 = new Random().nextInt(999) + 1;
            //int r2 = new Random().nextInt(999) + 1;
            String[] randomJackpotValues = new String[JACKPOT_QTY];
            for (int n = 0; n < JACKPOT_QTY; n++) {
                int tempInt = new Random().nextInt(899) + 100;
                randomJackpotValues[n] = String.valueOf(tempInt);
            }
            String[] randomMsgValues = new String[MSG_QTY];
            for (int n = 0; n < JACKPOT_QTY; n++) {
                StringBuilder randomStringBuilder = new StringBuilder();
                //int randomLength = new Random().nextInt(5);
                String strCollection = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!$&*@#%,.";
                char tempChar;
                for (int i = 0; i < 5; i++) {
                    //tempChar = (char) (new Random().nextInt(96) + 32);
                    tempChar = strCollection.charAt(new Random().nextInt(strCollection.length()));
                    randomStringBuilder.append(tempChar);
                }
                randomMsgValues[n] = randomStringBuilder.toString();
            }
            String[] digitEffectCodes = new String[JACKPOT_QTY];
            for (int x = 0; x < JACKPOT_QTY; x++) {
                digitEffectCodes[x] = spinnerDigitEffectValues[x].getSelectedItem().toString();
            }

            String[] msgEffectCodes = new String[MSG_QTY];
            for (int y = 0; y < JACKPOT_QTY; y++) {
                msgEffectCodes[y] = spinnerMsgEffectValues[y].getSelectedItem().toString();
            }

            String data = buildProtocol(randomJackpotValues, digitEffectCodes, randomMsgValues, msgEffectCodes);
            //when usb service started in other activity, e.g. homeScreenActicity,
            //use intent to carry extra method pass the data to other activity and
            // write to serial by broadcast receiver in the usb service
            sendDataToUsbServiceToWrite(data.getBytes());
            //Toast.makeText(getApplicationContext(), "Sending: " + data, Toast.LENGTH_SHORT).show();
            textViewTest.append(data + "\n");

            //when usb service started in this current activity, use following code to write to serial port directly
            //if (usbService != null) {
            //    usbService.write(data.getBytes());
            //    Toast.makeText(getApplicationContext(), "Sending: " + data, Toast.LENGTH_SHORT).show();
            //    textViewTest.append(data + "\n");
            //}
            mMsgSendHandler.postDelayed(this, interval);
        }
    }

    private void setSerialDataReceiveFilter()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbService.ACTION_USB_DATA_RECEIVED);
        registerReceiver(mSerialDataReceiver, intentFilter);
    }
    //private Runnable mRunnableCode = new Runnable() {
    //    @Override
    //    public void run() {
    //        // Do something here on the main thread
    //        int r1 = new Random().nextInt(999) + 1;
    //        int r2 = new Random().nextInt(999) + 1;
    //        String data = buildProtocol(r1, r2);
    //        if (usbService != null) {
    //            usbService.write(data.getBytes());
    //            Toast.makeText(getApplicationContext(), "Running" + r1, Toast.LENGTH_SHORT).show();
    //        }
    //        mMsgSendHandler.postDelayed(mRunnableCode, 3000);
    //    }
    //};

    //private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
    //    if (!UsbService.SERVICE_CONNECTED) {
    //        Intent startService = new Intent(this, service);
    //        if (extras != null && !extras.isEmpty()) {
    //            Set<String> keys = extras.keySet();
    //            for (String key : keys) {
    //                String extra = extras.getString(key);
    //                startService.putExtra(key, extra);
    //            }
    //        }
    //        startService(startService);
    //    }
    //    Intent bindingIntent = new Intent(this, service);
    //    bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    //}
//
    //private void setFilters() {
    //    IntentFilter filter = new IntentFilter();
    //    filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
    //    filter.addAction(UsbService.ACTION_NO_USB);
    //    filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
    //    filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
    //    filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
    //    registerReceiver(mUsbReceiver, filter);
    //}
//
    //private static class MyHandler extends Handler {
    //    private final WeakReference<SignController_1_Activity> mActivity;
//
    //    private MyHandler(SignController_1_Activity activity) {
    //        mActivity = new WeakReference<>(activity);
    //    }
//
    //    @Override
    //    public void handleMessage(Message msg) {
    //        switch (msg.what) {
    //            case UsbService.MESSAGE_FROM_SERIAL_PORT:
    //                String data = (String) msg.obj;
    //                //mActivity.get().display.append(data);
    //                Toast.makeText(mActivity.get(), "Feedback: " + data, Toast.LENGTH_SHORT).show();
    //                break;
    //            case UsbService.CTS_CHANGE:
    //                Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
    //                break;
    //            case UsbService.DSR_CHANGE:
    //                Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
    //                break;
    //        }
    //    }
    //}

    private String buildProtocol(String[] jackpotValues, String[] digitEffectCodeValues, String[] msgStrings, String[] msgEffectCodeValues) {
        String protocolStr = "";
        int jackpotQty = jackpotValues.length;
        int msgQty = msgStrings.length;
        StringBuilder stringBuilderTempStr1 = new StringBuilder();
        StringBuilder stringBuilderTempStr2 = new StringBuilder();
        if (jackpotQty != 0 || msgQty != 0) {
            for (int i = 0; i < jackpotQty; i++) {
                if (mMFCValue.equals("CM")) {
                    String[] tempDigitEffectCode = digitEffectCodeValues[i].split("-");
                    stringBuilderTempStr1.append("<W").append(i + 1).append("><").append(tempDigitEffectCode[0]).append(">").append(jackpotValues[i]);
                } else {
                    String[] tempDigitEffectCode = digitEffectCodeValues[i].split("-");
                    stringBuilderTempStr1.append("<L").append(i + 1).append("><").append(tempDigitEffectCode[0]).append(">").append(jackpotValues[i]);
                }
            }
            for (int j = 0; j < jackpotQty; j++) {
                String[] tempMsgEffectCode = msgEffectCodeValues[j].split("-");
                stringBuilderTempStr1.append("<M").append(j + 1).append("><").append(tempMsgEffectCode[0]).append(">").append(msgStrings[j]);
            }
            if (mPEC1Value.contains("Null") || mPEC2Value.contains("Null")) {
                String[] tempStateCode = mStateCodeValue.split("-");
                protocolStr = "<" + mMFCValue + tempStateCode[1] + "LT>" + "<ID" + mSignIdValue + ">" +
                        stringBuilderTempStr1.toString() + stringBuilderTempStr2.toString() + "<E>";
            } else {
                String[] tempStr1 = mPEC1Value.split("-");
                String[] tempStr2 = mPEC2Value.split("-");
                protocolStr = "<" + mMFCValue + mStateCodeValue + "LT>" + "<ID" + mSignIdValue + ">" +
                        tempStr1[0] + tempStr2[0] + stringBuilderTempStr1.toString() + stringBuilderTempStr2.toString() + "<E>";
            }

        }
        return protocolStr;
    }

    private void setWidget() {
        for (int i = 0; i < JACKPOT_QTY; i++) {
            String str = "editText_SignController_1_Jackpot_Value_" + (i + 1);
            int targetWidgetId = getResources().getIdentifier(str, "id", this.getPackageName());
            editTextJackpotValues[i] = findViewById(targetWidgetId);
        }

        for (int j = 0; j < MSG_QTY; j++) {
            String str = "editText_SignController_1_Msg_Str_" + (j + 1);
            int targetWidgetId = getResources().getIdentifier(str, "id", this.getPackageName());
            editTextMsgStrings[j] = findViewById(targetWidgetId);
        }
        spinnerSharedArrayAdapter = ArrayAdapter.createFromResource(this, R.array.digit_effect_code, R.layout.spinner_item_layout_settings);
        for (int x = 0; x < JACKPOT_QTY; x++) {
            String str = "spinner_SignController_1_DigitEffectCode_" + (x + 1);
            int targetWidgetId = getResources().getIdentifier(str, "id", this.getPackageName());
            spinnerDigitEffectValues[x] = findViewById(targetWidgetId);
            spinnerDigitEffectValues[x].setAdapter(spinnerSharedArrayAdapter);
            spinnerDigitEffectValues[x].setOnItemSelectedListener(new SharedOnItemSelected());
        }
        for (int y = 0; y < MSG_QTY; y++) {
            String str = "spinner_SignController_1_MsgEffectCode_" + (y + 1);
            int targetWidgetId = getResources().getIdentifier(str, "id", this.getPackageName());
            spinnerMsgEffectValues[y] = findViewById(targetWidgetId);
            spinnerMsgEffectValues[y].setAdapter(spinnerSharedArrayAdapter);
            spinnerMsgEffectValues[y].setOnItemSelectedListener(new SharedOnItemSelected());
        }
        settingsButton = findViewById(R.id.button_SignController_1_Settings);
        updateButton = findViewById(R.id.button_SignController_1_Update);
        autoUpdateCheckBox = findViewById(R.id.checkBox_SignController_1_AutoUpdate);
        goHomebutton = findViewById(R.id.button_SignController_1_Home);
        helpbutton = findViewById(R.id.button_SignController_1_Help);
        backbutton = findViewById(R.id.button_Help_Back);
        signCtrl_1_Hidden_Group =findViewById(R.id.group_SignController_1_Hide);
        help_Page_Group = findViewById(R.id.group_Help_Page);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();

                //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                if (mButtonClickTimer == 0 || (currentTime - mButtonClickTimer > 3000)) {
                    mButtonClickTimer = currentTime;
                    mButtonClickCounter = 1;
                } else {
                    mButtonClickCounter++;
                }
                if (mButtonClickCounter == 5) {
                    mButtonClickCounter = 0;
                    SettingsFragment settingsFragment = new SettingsFragment();
                    settingsFragment.show(getSupportFragmentManager(), "Settings");
                }
            }
        });

        autoUpdateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    updateButton.setText(R.string.auto_update);
                } else {
                    updateButton.setText(R.string.manual_update);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autoUpdateCheckBox.isChecked()) {
                    updateButton.setText(R.string.manual_update);
                    //String[] mCurrentJackpotValueArray = new String[JACKPOT_QTY];
                    for (int n = 0; n < JACKPOT_QTY; n++) {
                        mCurrentJackpotValueArray[n] = editTextJackpotValues[n].getText().toString();
                    }
                    //String[] mCurrentMsgValueArray = new String[MSG_QTY];
                    for (int n = 0; n < JACKPOT_QTY; n++) {
                        mCurrentMsgValueArray[n] = editTextMsgStrings[n].getText().toString();
                    }
                    //String[] mCurrentDigitEffectCodeArray = new String[JACKPOT_QTY];
                    for (int x = 0; x < JACKPOT_QTY; x++) {
                        mCurrentDigitEffectCodeArray[x] = spinnerDigitEffectValues[x].getSelectedItem().toString();
                    }
                    //String[] mCurrentMsgEffectCodeArray = new String[MSG_QTY];
                    for (int y = 0; y < JACKPOT_QTY; y++) {
                        mCurrentMsgEffectCodeArray[y] = spinnerMsgEffectValues[y].getSelectedItem().toString();
                    }
                    String data = buildProtocol(mCurrentJackpotValueArray, mCurrentDigitEffectCodeArray, mCurrentMsgValueArray, mCurrentMsgEffectCodeArray);
                    textViewTest.setText(data);
                    //when usb service started in other activity, e.g. homeScreenActicity,
                    //use intent to carry extra method pass the data to other activity and
                    // write to serial by broadcast receiver in the usb service
                    sendDataToUsbServiceToWrite(data.getBytes());
                    //Toast.makeText(getApplicationContext(), "Sending: " + data, Toast.LENGTH_SHORT).show();

                    //when usb service started in this current activity, use following code to write to serial port directly
                    //if (usbService != null) {
                    //    usbService.write(data.getBytes());
                    //    Toast.makeText(getApplicationContext(), "Sending: " + data, Toast.LENGTH_SHORT).show();
                    //}
                } else {
                    if (updateButton.getText().equals(getResources().getString(R.string.auto_update))) {
                        updateButton.setText(getResources().getString(R.string.stop));
                        mMsgSendHandler.postDelayed(mRunnableCode, 0);
                    } else {
                        updateButton.setText(getResources().getString(R.string.auto_update));
                        mMsgSendHandler.removeCallbacks(mRunnableCode);
                    }
                }
            }
        });

        goHomebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignController_1_Activity.this, HomeScreenActivity.class));
                finish();
            }
        });

        helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsButton.setEnabled(false);
                help_Page_Group.setVisibility(View.VISIBLE);
                signCtrl_1_Hidden_Group.setVisibility(View.GONE);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signCtrl_1_Hidden_Group.setVisibility(View.VISIBLE);
                help_Page_Group.setVisibility(View.GONE);
                settingsButton.setEnabled(true);
            }
        });
    }

    class SharedOnItemSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Spinner targetSpinner = (Spinner) adapterView;
            String[] tempStr = targetSpinner.getItemAtPosition(i).toString().split("-");
            if (targetSpinner.getId() == R.id.spinner_SignController_1_DigitEffectCode_1) {
                mCurrentDigitEffectCodeArray[0] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_DigitEffectCode_2) {
                mCurrentDigitEffectCodeArray[1] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_DigitEffectCode_3) {
                mCurrentDigitEffectCodeArray[2] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_DigitEffectCode_4) {
                mCurrentDigitEffectCodeArray[3] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_MsgEffectCode_1) {
                mCurrentMsgEffectCodeArray[0] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_MsgEffectCode_2) {
                mCurrentMsgEffectCodeArray[1] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_MsgEffectCode_3) {
                mCurrentMsgEffectCodeArray[2] = tempStr[0];
            } else if (targetSpinner.getId() == R.id.spinner_SignController_1_MsgEffectCode_4) {
                mCurrentMsgEffectCodeArray[3] = tempStr[0];
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void setObserves() {
        mSharedViewModel.getMfc_code_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mMFCValue = s;
                textViewTest.setText(mMFCValue + ", " + mStateCodeValue + ", " + mSignIdValue + ", " + mPEC1Value + ", " + mPEC2Value);
            }
        });

        mSharedViewModel.getState_code_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mStateCodeValue = s;
                textViewTest.setText(mMFCValue + ", " + mStateCodeValue + ", " + mSignIdValue + ", " + mPEC1Value + ", " + mPEC2Value);
            }
        });

        mSharedViewModel.getSign_id_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mSignIdValue = s;
                textViewTest.setText(mMFCValue + ", " + mStateCodeValue + ", " + mSignIdValue + ", " + mPEC1Value + ", " + mPEC2Value);
            }
        });

        mSharedViewModel.getPanel_effect_code_1_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mPEC1Value = s;
                textViewTest.setText(mMFCValue + ", " + mStateCodeValue + ", " + mSignIdValue + ", " + mPEC1Value + ", " + mPEC2Value);
            }
        });

        mSharedViewModel.getPanel_effect_code_2_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mPEC2Value = s;
                textViewTest.setText(mMFCValue + ", " + mStateCodeValue + ", " + mSignIdValue + ", " + mPEC1Value + ", " + mPEC2Value);
            }
        });
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SettingsFragment.SHARED_PREFS, MODE_PRIVATE);
        mMFCValue = sharedPreferences.getString(SettingsFragment.MFC_CODE, "CM");
        mStateCodeValue = sharedPreferences.getString(SettingsFragment.STATE_CODE, "CM");
        mSignIdValue = sharedPreferences.getString(SettingsFragment.SIGN_ID, "000001");
        mPEC1Value = sharedPreferences.getString(SettingsFragment.PANEL_EFFECT_CODE_1, "Null-No panel effect code");
        mPEC2Value = sharedPreferences.getString(SettingsFragment.PANEL_EFFECT_CODE_2, "Null-No panel effect code");
    }

    private void sendDataToUsbServiceToWrite(byte[] data) {
        Intent writeSerialIntent = new Intent();
        writeSerialIntent.putExtra(UsbService.SERIAL_DATA_READY_TO_SEND, data);
        writeSerialIntent.setAction(UsbService.ACTION_USB_HAS_DATA_TO_SEND);
        sendBroadcast(writeSerialIntent);
    }
}

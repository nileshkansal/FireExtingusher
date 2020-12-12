package com.fireextinguisher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.receiver.IncomingSms;
import com.fireextinguisher.receiver.SmsListener;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.TypeOTPLayout;
import com.fireextinguisher.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationCodeActivity extends AppCompatActivity {

    private static final String TAG = VerificationCodeActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    TypeOTPLayout txtPinEntry;
    LinearLayout resend_layout, timertxt_layout;
    TextView description_text, edit_number, timertxt;
    IncomingSms incomingSms;
    Button next_btn;
    String empId = "", email = "", mobile = "", password = "", otp = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.verificationCode));

        incomingSms = new IncomingSms();

        resend_layout = findViewById(R.id.resend_layout);
        timertxt_layout = findViewById(R.id.timertxt_layout);
        next_btn = findViewById(R.id.next_btn);
        description_text = findViewById(R.id.description_text);
        timertxt = findViewById(R.id.timertxt);

        empId = getIntent().getStringExtra(Constant.empid);
        mobile = getIntent().getStringExtra(Constant.mobile);
        email = getIntent().getStringExtra(Constant.email);
        password = getIntent().getStringExtra(Constant.password);

        String mobileNo = mobile.substring(mobile.length() - 4);
        description_text.setText("One Time Password (OTP) has been sent\n to your ******" + mobileNo
                + ", please enter the\n same here to Register");

        resend_layout.setVisibility(View.GONE);

        new CountDownTimer(61000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "timer ======> " + millisUntilFinished);
                long minutes = millisUntilFinished / 1000;
                if (minutes < 10) {
                    timertxt.setText("00:0" + minutes);
                } else {
                    timertxt.setText("00:" + minutes);
                }
            }

            public void onFinish() {
                resend_layout.setVisibility(View.VISIBLE);
                timertxt_layout.setVisibility(View.GONE);
            }
        }.start();

        edit_number = findViewById(R.id.edit_number);
        edit_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtPinEntry = findViewById(R.id.pin_entry);

        resend_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp != null && !otp.equals("")) {
                    resendSms();
                } else {
                    sendSMS();
                }
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp != null && !otp.equals("")) {
                    if (otp.equals(txtPinEntry.getText().toString().trim())) {
                        signUpTask();
                    } else {
                        Utility.ShowToastMessage(mContext, "Please enter correct code");
                    }
                } else {
                    Utility.ShowToastMessage(mContext, "OTP not found. Please resend OTP.");
                }
            }
        });

        sendSMS();

        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                txtPinEntry.setText(messageText);
            }
        });
    }

    @Override
    protected void onDestroy() {
        IncomingSms.unbindListener();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void showLoading() {
        dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void signUpTask() {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.signup(empId, mobile, email, password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Utility.setSharedPreference(mContext, Constant.email, object.optString(Constant.email));
                            Utility.setSharedPreference(mContext, Constant.password, password);
                            Utility.setSharedPreference(mContext, Constant.mobile, object.optString(Constant.mobile));
                            Utility.setSharedPreference(mContext, Constant.token, object.optString(Constant.token));
                            Utility.setSharedPreference(mContext, Constant.empid, object.optString(Constant.empid));
                            Utility.setSharedPreference(mContext, Constant.userId, object.optString(Constant.id));

                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                            hideDialog();
                            Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                        }
                    } catch (Exception e) {
                        hideDialog();
                        e.printStackTrace();
                    }
                } else {
                    hideDialog();
                    Utility.ShowToastMessage(mContext, getResources().getString(R.string.server_not_responding));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                hideDialog();
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });
    }

    private void sendSMS() {

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.sendSms(mobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            otp = object.optString("otp");

                        } else {
                            Utility.ShowToastMessage(mContext, "Unable to send OTP at this time please try after some time.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.ShowToastMessage(mContext, "Unable to send OTP at this time please try after some time.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });
    }

    private void resendSms() {
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.reSendSms(mobile, otp);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            otp = object.optString("otp");
                        } else {
                            Utility.ShowToastMessage(mContext, "Unable to send OTP at this time please try after some time.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.ShowToastMessage(mContext, "Unable to send OTP at this time please try after some time.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });
    }
}
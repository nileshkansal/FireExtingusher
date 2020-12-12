package com.fireextinguisher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.client.ClientLoginActivity;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    Context mContext;
    TextInputLayout input_layout_email, input_layout_password;
    TextInputEditText email, password;
    Button login_btn;
    TextView forgot_password;
    LinearLayout register_layout, client_layout;
    Toolbar toolbar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.login));

        input_layout_email = findViewById(R.id.input_layout_email);
        input_layout_password = findViewById(R.id.input_layout_password);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login_btn = findViewById(R.id.login_btn);
        forgot_password = findViewById(R.id.forgot_password);
        register_layout = findViewById(R.id.register_layout);
        client_layout = findViewById(R.id.client_layout);

        login_btn.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        register_layout.setOnClickListener(this);
        client_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                loginTask();
                break;
            case R.id.forgot_password:
                /*Intent intent = new Intent(mContext, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();*/
                break;
            case R.id.register_layout:
                /*Intent intentRegister = new Intent(mContext, RegisterActivity.class);
                startActivity(intentRegister);
                finish();*/
                break;
            case R.id.client_layout:
                Intent intentClientLogin = new Intent(mContext, ClientLoginActivity.class);
                startActivity(intentClientLogin);
                finish();
                break;
        }
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

    private void loginTask() {

        if (email.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "email cannot be empty");
        } else if (!Utility.isValidEmail(email.getText().toString().trim())) {
            Utility.ShowToastMessage(mContext, "please provide correct email");
        } else if (password.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "password cannot be empty");
        } else {
            showLoading();
            ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.login(email.getText().toString().trim(), password.getText().toString().trim());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            if (jsonObject.getBoolean(Constant.status)) {
                                JSONObject object = jsonObject.optJSONObject(Constant.object);

                                Utility.setSharedPreference(mContext, Constant.email, object.optString(Constant.email));
                                Utility.setSharedPreference(mContext, Constant.password, password.getText().toString().trim());
                                Utility.setSharedPreference(mContext, Constant.mobile, object.optString(Constant.mobile));
                                Utility.setSharedPreference(mContext, Constant.token, object.optString(Constant.token));
                                Utility.setSharedPreference(mContext, Constant.empid, object.optString(Constant.empid));
                                Utility.setSharedPreference(mContext, Constant.userId, object.optString(Constant.id));

                                Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));

                                hideDialog();

                                Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                        Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideDialog();
                    Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                    Log.e(TAG, "t =======> " + t.getLocalizedMessage());
                }
            });
        }
    }
}
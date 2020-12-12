package com.fireextinguisher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirePumpVerifyActivity extends AppCompatActivity {

    private static final String TAG = FirePumpVerifyActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    EditText model_edit_text;
    Button scan_btn;
    ProgressDialog dialog;
    String modelNo = "", productId = "", location = "", spare_part_label = "", remarks = "", spare_part_item_label = "",
            client_name = "", hp = "", head = "", kw = "", pump_no = "", pump_type = "", rpm = "", motor_no = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_verify_model);
        mContext =  this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.scan_product));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location = getIntent().getStringExtra(Constant.FirePump.location);
        spare_part_label = getIntent().getStringExtra(Constant.FirePump.spare_part_label);

        hp = getIntent().getStringExtra(Constant.FirePump.HP);
        head = getIntent().getStringExtra(Constant.FirePump.Head);
        kw = getIntent().getStringExtra(Constant.FirePump.KW);
        pump_no = getIntent().getStringExtra(Constant.FirePump.pumpNo);
        pump_type = getIntent().getStringExtra(Constant.FirePump.pumpType);
        rpm = getIntent().getStringExtra(Constant.FirePump.RPM);
        motor_no = getIntent().getStringExtra(Constant.FirePump.motorNo);

        if (getIntent().getStringExtra(Constant.HoseReelInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.HoseReelInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.HoseReelInfo.remarks).isEmpty()) {
            remarks = getIntent().getStringExtra(Constant.HoseReelInfo.remarks);
        } else {
            remarks = "";
        }

        if (spare_part_label != null && spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_item_label);
        }

        client_name = getIntent().getStringExtra(Constant.HoseReelInfo.clientName);

        model_edit_text = findViewById(R.id.model_edit_text);
        scan_btn = findViewById(R.id.scan_btn);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model_edit_text.getText().toString().trim().length() <= 0) {
                    Utility.ShowToastMessage(mContext, "Please enter model number to verify details");
                } else if (!model_edit_text.getText().toString().trim().equals(modelNo)) {
                    Utility.ShowToastMessage(mContext, "Model number not matched");
                } else {
                    Utility.hideKeyBoard(model_edit_text, mContext);
                    if (Utility.isNetworkConnected(mContext)) {
                        insertNewProduct();
                    } else {
                        Utility.ShowToastMessage(mContext, R.string.internetconnection);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
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

    private void insertNewProduct(){
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.createFirePump(Utility.getSharedPreferences(mContext, Constant.userId),
                modelNo, productId, location, spare_part_label, remarks, spare_part_item_label, client_name, hp, head, kw,
                pump_no, pump_type, rpm, motor_no);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            showAlert();
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

    private void showAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Product detail submitted successfully.");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}

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

public class FireHydrantVerifyActivity extends AppCompatActivity {

    private static final String TAG = FireHydrantVerifyActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    EditText model_edit_text;
    Button scan_btn;
    ProgressDialog dialog;
    String modelNo = "", productId = "", location = "", spare_part_label = "", remarks = "", spare_part_item_label = "",
            client_name = "", hose_pipe = "", hydrant_valve = "", black_cap = "", shunt_wheel = "", hose_box = "", hoses = "",
            glasses = "", branch_pipe = "", keys = "", glass_hammer = "", observation = "", action = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_verify_model);
        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.scan_product));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location = getIntent().getStringExtra(Constant.ProductInfo.location);
        spare_part_label = getIntent().getStringExtra(Constant.ProductInfo.spare_part_label);

        hose_pipe = getIntent().getStringExtra(Constant.FireHydrantInfo.hose_pipe);
        hydrant_valve = getIntent().getStringExtra(Constant.FireHydrantInfo.hydrant_valve);
        black_cap = getIntent().getStringExtra(Constant.FireHydrantInfo.black_cap);
        shunt_wheel = getIntent().getStringExtra(Constant.FireHydrantInfo.shunt_wheel);
        hose_box = getIntent().getStringExtra(Constant.FireHydrantInfo.hose_box);
        hoses = getIntent().getStringExtra(Constant.FireHydrantInfo.hoses);
        glasses = getIntent().getStringExtra(Constant.FireHydrantInfo.glasses);
        branch_pipe = getIntent().getStringExtra(Constant.FireHydrantInfo.branch_pipe);
        keys = getIntent().getStringExtra(Constant.FireHydrantInfo.keys);
        glass_hammer = getIntent().getStringExtra(Constant.FireHydrantInfo.glass_hammer);
        observation = getIntent().getStringExtra(Constant.FireHydrantInfo.observation);
        action = getIntent().getStringExtra(Constant.FireHydrantInfo.action);

        if (getIntent().getStringExtra(Constant.FireHydrantInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.FireHydrantInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.FireHydrantInfo.remarks).isEmpty()) {
            remarks = getIntent().getStringExtra(Constant.FireHydrantInfo.remarks);
        } else {
            remarks = "";
        }

        if (spare_part_label != null && spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.FireHydrantInfo.spare_part_item_label);
        }

        client_name = getIntent().getStringExtra(Constant.FireHydrantInfo.clientName);

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
                    Utility.hideKeyBoard(v, mContext);
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

    private void insertNewProduct() {
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.createFireHydrant(Utility.getSharedPreferences(mContext, Constant.userId),
                modelNo, productId, location, spare_part_label, remarks, spare_part_item_label, client_name, hose_pipe,
                hydrant_valve, black_cap, shunt_wheel, hose_box, hoses, glasses, branch_pipe, keys, glass_hammer,
                observation, action);
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
package com.fireextinguisher;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;

public class FireHydrantOtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FireBucketOtherActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    RelativeLayout refill_image, hpt_image, replace_image;

    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "",
            spare_part_item_label = "", clientName = "", hose_pipe_label = "", hydrant_valve_label = "", black_cap_label = "",
            shunt_wheel_label = "", hose_box_label = "", hoses_label = "", glasses_label = "", branch_pipe_label = "",
            keys_label = "", glass_hammer_label = "", observation_label = "", action_label = "";

    TextView remarks, spare_part, location, spare_part_selection, client_name, hose_pipe, hydrant_valve, black_cap, shunt_wheel,
            hose_box, hoses, glasses, branch_pipe, keys, glass_hammer, observation, action;
    LinearLayout spare_part_selection_layout;

    boolean isRefillSelected = false, isHPTSelected = false, isReplaceSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_fire_bucket);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.other));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.FireHydrantInfo.location);

        hose_pipe_label = getIntent().getStringExtra(Constant.FireHydrantInfo.hose_pipe);
        hydrant_valve_label = getIntent().getStringExtra(Constant.FireHydrantInfo.hydrant_valve);
        black_cap_label = getIntent().getStringExtra(Constant.FireHydrantInfo.black_cap);
        shunt_wheel_label = getIntent().getStringExtra(Constant.FireHydrantInfo.shunt_wheel);
        hose_box_label = getIntent().getStringExtra(Constant.FireHydrantInfo.hose_box);
        hoses_label = getIntent().getStringExtra(Constant.FireHydrantInfo.hoses);
        glasses_label = getIntent().getStringExtra(Constant.FireHydrantInfo.glasses);
        branch_pipe_label = getIntent().getStringExtra(Constant.FireHydrantInfo.branch_pipe);
        keys_label = getIntent().getStringExtra(Constant.FireHydrantInfo.keys);
        glass_hammer_label = getIntent().getStringExtra(Constant.FireHydrantInfo.glass_hammer);
        observation_label = getIntent().getStringExtra(Constant.FireHydrantInfo.observation);
        action_label = getIntent().getStringExtra(Constant.FireHydrantInfo.action);

        spare_part_label = getIntent().getStringExtra(Constant.FireHydrantInfo.spare_part_label);

        if (getIntent().getStringExtra(Constant.FireHydrantInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.FireHydrantInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.FireHydrantInfo.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.FireHydrantInfo.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.FireHydrantInfo.clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.FireHydrantInfo.spare_part_item_label);
        }

        bind();
    }

    private void bind() {
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        continue_btn.setVisibility(View.GONE);

        refill_image = findViewById(R.id.refill_image);
        hpt_image = findViewById(R.id.hpt_image);
        replace_image = findViewById(R.id.replace_image);

        refill_image.setOnClickListener(this);
        hpt_image.setOnClickListener(this);
        replace_image.setOnClickListener(this);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        hose_pipe = findViewById(R.id.hose_pipe);
        hydrant_valve = findViewById(R.id.hydrant_valve);
        black_cap = findViewById(R.id.black_cap);
        shunt_wheel = findViewById(R.id.shunt_wheel);
        hose_box = findViewById(R.id.hose_box);
        hoses = findViewById(R.id.hoses);
        glasses = findViewById(R.id.glasses);
        branch_pipe = findViewById(R.id.branch_pipe);
        keys = findViewById(R.id.keys);
        glass_hammer = findViewById(R.id.glass_hammer);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);

        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        location.setText(location_label);
        spare_part.setText(spare_part_label);
        remarks.setText(remarks_label);
        client_name.setText(clientName);
        hose_pipe.setText(hose_pipe_label);
        hydrant_valve.setText(hydrant_valve_label);
        black_cap.setText(black_cap_label);
        shunt_wheel.setText(shunt_wheel_label);
        hose_box.setText(hose_box_label);
        hoses.setText(hoses_label);
        glasses.setText(glasses_label);
        branch_pipe.setText(branch_pipe_label);
        keys.setText(keys_label);
        glass_hammer.setText(glass_hammer_label);
        observation.setText(observation_label);
        action.setText(action_label);

        if (spare_part_label.equals("Yes")){
            spare_part_selection.setText(spare_part_item_label);
            spare_part_selection_layout.setVisibility(View.VISIBLE);
        } else {
            spare_part_selection_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refill_image:
                if (isRefillSelected) {
                    isRefillSelected = false;
                    refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    continue_btn.setVisibility(View.GONE);
                } else {
                    isRefillSelected = true;
                    refill_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                    if (isHPTSelected) {
                        isHPTSelected = false;
                        hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    if (isReplaceSelected) {
                        isReplaceSelected = false;
                        replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    continue_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.hpt_image:
                if (isHPTSelected) {
                    isHPTSelected = false;
                    hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    continue_btn.setVisibility(View.GONE);
                } else {
                    isHPTSelected = true;
                    hpt_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                    if (isRefillSelected) {
                        isRefillSelected = false;
                        refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    if (isReplaceSelected) {
                        isReplaceSelected = false;
                        replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    continue_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.replace_image:
                if (isReplaceSelected) {
                    isReplaceSelected = false;
                    replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    continue_btn.setVisibility(View.GONE);
                } else {
                    isReplaceSelected = true;
                    replace_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                    if (isRefillSelected) {
                        isRefillSelected = false;
                        refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    if (isHPTSelected) {
                        isHPTSelected = false;
                        hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    continue_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.continue_btn:
                if (isReplaceSelected) {
                    Utility.ShowToastMessage(mContext, "Replace selected");
                } else if (isHPTSelected) {
                    Utility.ShowToastMessage(mContext, "Other selected");
                    /*Intent serviceActivity = new Intent(mContext, FireExtinguisherOtherActivity.class);
                    startActivity(serviceActivity);*/
                } else if (isRefillSelected) {
                    showAlert();
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Details submitted successfully.");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}
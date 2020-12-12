package com.fireextinguisher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;

public class HoseReelInstalledActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HoseReelInstalledActivity.class.getSimpleName();
    Context mContext;
    //Button service_btn, other_btn;
    Toolbar toolbar;
    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", observation_label = "", action_label = "", hose_reel_label = "", shut_off_nozzel_label = "",
            ball_valve_label = "", jubli_clip_label = "", conecting_ruber_hose_label = "";

    TextView remarks, spare_part, location, spare_part_selection, client_name, hose_reel, shut_off_nozzel, ball_valve,
            jubli_clip, conecting_ruber_hose, observation, action;
    LinearLayout spare_part_selection_layout;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hose_reel_installed);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.product_info));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.HoseReelInfo.location);
        observation_label = getIntent().getStringExtra(Constant.HoseReelInfo.observation);
        action_label = getIntent().getStringExtra(Constant.HoseReelInfo.action);

        hose_reel_label = getIntent().getStringExtra(Constant.HoseReelInfo.hose_reel);
        shut_off_nozzel_label = getIntent().getStringExtra(Constant.HoseReelInfo.shut_off_nozzel);
        ball_valve_label = getIntent().getStringExtra(Constant.HoseReelInfo.ball_valve);
        jubli_clip_label = getIntent().getStringExtra(Constant.HoseReelInfo.jubli_clip);
        conecting_ruber_hose_label = getIntent().getStringExtra(Constant.HoseReelInfo.conecting_ruber_hose);


        spare_part_label = getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_label);

        if (getIntent().getStringExtra(Constant.HoseReelInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.HoseReelInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.HoseReelInfo.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.HoseReelInfo.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.HoseReelInfo.clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_item_label);
        }

        bind();
    }

    private void bind() {
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        continue_btn.setVisibility(View.GONE);

        service_image = findViewById(R.id.service_image);
        other_image = findViewById(R.id.other_image);

        service_image.setOnClickListener(this);
        other_image.setOnClickListener(this);

        other_image.setVisibility(View.GONE);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        hose_reel = findViewById(R.id.hose_reel);
        shut_off_nozzel = findViewById(R.id.shut_off_nozzel);
        ball_valve = findViewById(R.id.ball_valve);
        jubli_clip = findViewById(R.id.jubli_clip);
        conecting_ruber_hose = findViewById(R.id.conecting_ruber_hose);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);

        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);
        hose_reel.setText(hose_reel_label);
        shut_off_nozzel.setText(shut_off_nozzel_label);
        ball_valve.setText(ball_valve_label);
        jubli_clip.setText(jubli_clip_label);
        conecting_ruber_hose.setText(conecting_ruber_hose_label);
        observation.setText(observation_label);
        action.setText(action_label);


        if (spare_part_label.equals("Yes")) {
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
            case R.id.service_image:
                if (isServiceSelected) {
                    isServiceSelected = false;
                    service_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    continue_btn.setVisibility(View.GONE);
                } else {
                    isServiceSelected = true;
                    service_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                    if (isOtherSelected) {
                        isOtherSelected = false;
                        other_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    continue_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.other_image:
                if (isOtherSelected) {
                    isOtherSelected = false;
                    other_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    continue_btn.setVisibility(View.GONE);
                } else {
                    isOtherSelected = true;
                    other_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                    if (isServiceSelected) {
                        isServiceSelected = false;
                        service_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                    }
                    continue_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.continue_btn:
                if (isOtherSelected) {
                    Intent otherActivity = new Intent(mContext, HoseReelOtherActivity.class);
                    otherActivity.putExtra(Constant.modelNo, modelNo);
                    otherActivity.putExtra(Constant.productId, productId);
                    otherActivity.putExtra(Constant.HoseReelInfo.location, location_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.observation, observation_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.action, action_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.hose_reel, hose_reel_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.shut_off_nozzel, shut_off_nozzel_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.ball_valve, ball_valve_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.jubli_clip, jubli_clip_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.conecting_ruber_hose, conecting_ruber_hose_label);
                    otherActivity.putExtra(Constant.HoseReelInfo.spare_part_label, spare_part_label);
                    if (!remarks.equals("")) {
                        otherActivity.putExtra(Constant.HoseReelInfo.remarks, remarks_label);
                    }
                    otherActivity.putExtra(Constant.HoseReelInfo.clientName, clientName);
                    otherActivity.putExtra(Constant.HoseReelInfo.spare_part_item_label, spare_part_item_label);

                    startActivity(otherActivity);
                } else if (isServiceSelected) {
                    Intent serviceActivity = new Intent(mContext, HoseReelServiceActivity.class);
                    serviceActivity.putExtra(Constant.modelNo, modelNo);
                    serviceActivity.putExtra(Constant.HoseReelInfo.spare_part_item_label, spare_part_item_label);
                    startActivity(serviceActivity);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }
}

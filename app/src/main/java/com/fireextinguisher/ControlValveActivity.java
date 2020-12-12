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

public class ControlValveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ControlValveActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    LinearLayout spare_part_selection_layout;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;
    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", type_of_valve_label = "", moc_label = "", size_label = "", spindle_label = "", wheel_lever_label = "",
            gasket_label = "", gland_packing_label = "", drain_valve_label = "", pressure_gauge_label = "", pressure_label = "",
            flow_label = "", test_valve_label = "", soleniod_valve_actuator_label = "", internal_disc_flap_label = "",
            gong_bell_label = "";

    TextView spare_part, spare_part_selection, remarks, client_name, location, type_of_valve, moc, size, spindle, wheel_lever, gasket,
            gland_packing, drain_valve, pressure_gauge, pressure, flow, test_valve, soleniod_valve_actuator, internal_disc_flap,
            gong_bell;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_valve);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.product_info));


        type_of_valve_label = getIntent().getStringExtra(Constant.ControlValve.typeOfValve);
        moc_label = getIntent().getStringExtra(Constant.ControlValve.moc);
        size_label = getIntent().getStringExtra(Constant.ControlValve.size);
        spindle_label = getIntent().getStringExtra(Constant.ControlValve.spindle);
        wheel_lever_label = getIntent().getStringExtra(Constant.ControlValve.wheelLever);
        gasket_label = getIntent().getStringExtra(Constant.ControlValve.gasket);
        gland_packing_label = getIntent().getStringExtra(Constant.ControlValve.glandPacking);
        drain_valve_label = getIntent().getStringExtra(Constant.ControlValve.drainValve);

        pressure_gauge_label = getIntent().getStringExtra(Constant.ControlValve.pressureGuage);
        pressure_label = getIntent().getStringExtra(Constant.ControlValve.pressure);
        flow_label = getIntent().getStringExtra(Constant.ControlValve.flow);
        test_valve_label = getIntent().getStringExtra(Constant.ControlValve.testValve);
        soleniod_valve_actuator_label = getIntent().getStringExtra(Constant.ControlValve.soleniodValveActuator);
        internal_disc_flap_label = getIntent().getStringExtra(Constant.ControlValve.internalDiscFlap);
        gong_bell_label = getIntent().getStringExtra(Constant.ControlValve.gongBell);


        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.ControlValve.location);
        clientName = getIntent().getStringExtra(Constant.ControlValve.clientName);

        spare_part_label = getIntent().getStringExtra(Constant.ControlValve.spare_part_label);

        if (getIntent().getStringExtra(Constant.ControlValve.remarks) != null &&
                !getIntent().getStringExtra(Constant.ControlValve.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.ControlValve.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.ControlValve.remarks);
        } else {
            remarks_label = "";
        }

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.ControlValve.spare_part_item_label);
        }

        bind();
    }

    private void bind() {

        type_of_valve = findViewById(R.id.type_of_valve);
        moc = findViewById(R.id.moc);
        size = findViewById(R.id.size);
        spindle = findViewById(R.id.spindle);
        wheel_lever = findViewById(R.id.wheel_lever);
        gasket = findViewById(R.id.gasket);
        gland_packing = findViewById(R.id.gland_packing);
        drain_valve = findViewById(R.id.drain_valve);

        pressure_gauge = findViewById(R.id.pressure_gauge);
        pressure = findViewById(R.id.pressure);
        flow = findViewById(R.id.flow);
        test_valve = findViewById(R.id.test_valve);
        soleniod_valve_actuator = findViewById(R.id.soleniod_valve_actuator);
        internal_disc_flap = findViewById(R.id.internal_disc_flap);
        gong_bell = findViewById(R.id.gong_bell);

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
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_selection.setText(spare_part_item_label);
            spare_part_selection_layout.setVisibility(View.VISIBLE);
        } else {
            spare_part_selection_layout.setVisibility(View.GONE);
        }

        type_of_valve.setText(type_of_valve_label);
        moc.setText(moc_label);
        size.setText(size_label);
        spindle.setText(spindle_label);
        wheel_lever.setText(wheel_lever_label);
        gasket.setText(gasket_label);
        gland_packing.setText(gland_packing_label);
        drain_valve.setText(drain_valve_label);

        pressure_gauge.setText(pressure_gauge_label);
        pressure.setText(pressure_label);
        flow.setText(flow_label);
        test_valve.setText(test_valve_label);
        soleniod_valve_actuator.setText(soleniod_valve_actuator_label);
        internal_disc_flap.setText(internal_disc_flap_label);
        gong_bell.setText(gong_bell_label);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
                    /*Intent otherActivity = new Intent(mContext, FireBucketOtherActivity.class);
                    otherActivity.putExtra(Constant.modelNo, modelNo);
                    otherActivity.putExtra(Constant.productId, productId);
                    otherActivity.putExtra(Constant.FireBucket.location, location_label);
                    otherActivity.putExtra(Constant.FireBucket.observation, observation_label);
                    otherActivity.putExtra(Constant.FireBucket.action, action_label);
                    otherActivity.putExtra(Constant.FireBucket.number_of_fire_bucket, number_of_fire_bucket_label);
                    otherActivity.putExtra(Constant.FireBucket.buckets, bucket_label);
                    otherActivity.putExtra(Constant.FireBucket.stand, stand_label);
                    otherActivity.putExtra(Constant.FireBucket.sand, sand_label);
                    otherActivity.putExtra(Constant.FireBucket.spare_part_label, spare_part_label);
                    if (!remarks.equals("")) {
                        otherActivity.putExtra(Constant.FireBucket.remarks, remarks_label);
                    }
                    otherActivity.putExtra(Constant.FireBucket.clientName, clientName);
                    otherActivity.putExtra(Constant.FireBucket.spare_part_item_label, spare_part_item_label);

                    startActivity(otherActivity);*/
                } else if (isServiceSelected) {
                    Intent serviceActivity = new Intent(mContext, ControlValveServiceActivity.class);
                    serviceActivity.putExtra(Constant.FireBucket.spare_part_item_label, spare_part_item_label);
                    serviceActivity.putExtra(Constant.modelNo, modelNo);
                    startActivity(serviceActivity);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }
}
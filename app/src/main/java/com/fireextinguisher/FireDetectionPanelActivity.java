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

public class FireDetectionPanelActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FireDetectionPanelActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    LinearLayout spare_part_selection_layout;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;

    TextView remarks, client_name, location;
    String modelNo = "", productId = "", location_label = "", remarks_label = "", clientName = "";
    TextView specs_of_panel, type_of_system, make, loops_zone, model_no, repeater_panel, isolator_module, heat_detector, flame_detector,
            smoke_detector, multi_detector, gas_detector, beam_detector, manual_call_point, hooter_cum_sounder, zone_monitor_module,
            monitor_module, control_module, power_supply_unit, zener_barrier, siren, cables, battery_backup;

    String specs_of_panel_label = "", type_of_system_label = "", make_label = "", loops_zone_label = "", model_no_label = "",
            repeater_panel_label = "", isolator_module_label = "", heat_detector_label = "", flame_detector_label = "",
            smoke_detector_label = "", multi_detector_label = "", gas_detector_label = "", beam_detector_label = "",
            manual_call_point_label = "", hooter_cum_sounder_label = "", zone_monitor_module_label = "", monitor_module_label = "",
            control_module_label = "", power_supply_unit_label = "", zener_barrier_label = "", siren_label = "", cables_label = "",
            battery_backup_label = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_detection_panel);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.product_info));

        specs_of_panel_label = getIntent().getStringExtra(Constant.FireDetectionPanel.specsOfPanel);
        type_of_system_label = getIntent().getStringExtra(Constant.FireDetectionPanel.typeOfSystem);
        make_label = getIntent().getStringExtra(Constant.FireDetectionPanel.make);
        loops_zone_label = getIntent().getStringExtra(Constant.FireDetectionPanel.loopsZone);
        repeater_panel_label = getIntent().getStringExtra(Constant.FireDetectionPanel.repeaterPanel);
        isolator_module_label = getIntent().getStringExtra(Constant.FireDetectionPanel.isolatorModule);
        heat_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.heatDetector);
        flame_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.flameDetector);
        smoke_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.smokeDetector);
        multi_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.multiDetector);
        gas_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.gasDetector);
        beam_detector_label = getIntent().getStringExtra(Constant.FireDetectionPanel.beamDetector);
        manual_call_point_label = getIntent().getStringExtra(Constant.FireDetectionPanel.manualCallPoint);
        hooter_cum_sounder_label = getIntent().getStringExtra(Constant.FireDetectionPanel.hooterCumSounder);
        zone_monitor_module_label = getIntent().getStringExtra(Constant.FireDetectionPanel.zoneMonitorModule);
        monitor_module_label = getIntent().getStringExtra(Constant.FireDetectionPanel.monitorModule);
        control_module_label = getIntent().getStringExtra(Constant.FireDetectionPanel.controlModule);
        power_supply_unit_label = getIntent().getStringExtra(Constant.FireDetectionPanel.powerSupplyUnit);
        zener_barrier_label = getIntent().getStringExtra(Constant.FireDetectionPanel.zenerBarrier);
        siren_label = getIntent().getStringExtra(Constant.FireDetectionPanel.siren);
        cables_label = getIntent().getStringExtra(Constant.FireDetectionPanel.cables);
        battery_backup_label = getIntent().getStringExtra(Constant.FireDetectionPanel.batteryBackup);

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.FireDetectionPanel.location);
        clientName = getIntent().getStringExtra(Constant.FireDetectionPanel.clientName);

        if (getIntent().getStringExtra(Constant.FireDetectionPanel.remarks) != null &&
                !getIntent().getStringExtra(Constant.FireDetectionPanel.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.FireDetectionPanel.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.FireDetectionPanel.remarks);
        } else {
            remarks_label = "";
        }

        model_no_label = modelNo;

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
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);

        remarks.setText(remarks_label);
        //spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);

        specs_of_panel = findViewById(R.id.specs_of_panel);
        type_of_system = findViewById(R.id.type_of_system);
        make = findViewById(R.id.make);
        loops_zone = findViewById(R.id.loops_zone);
        model_no = findViewById(R.id.model_no);
        repeater_panel = findViewById(R.id.repeater_panel);
        isolator_module = findViewById(R.id.isolator_module);
        heat_detector = findViewById(R.id.heat_detector);
        flame_detector = findViewById(R.id.flame_detector);
        smoke_detector = findViewById(R.id.smoke_detector);
        multi_detector = findViewById(R.id.multi_detector);
        gas_detector = findViewById(R.id.gas_detector);
        beam_detector = findViewById(R.id.beam_detector);
        manual_call_point = findViewById(R.id.manual_call_point);
        hooter_cum_sounder = findViewById(R.id.hooter_cum_sounder);
        zone_monitor_module = findViewById(R.id.zone_monitor_module);
        monitor_module = findViewById(R.id.monitor_module);
        control_module = findViewById(R.id.control_module);
        power_supply_unit = findViewById(R.id.power_supply_unit);
        zener_barrier = findViewById(R.id.zener_barrier);
        siren = findViewById(R.id.siren);
        cables = findViewById(R.id.cables);
        battery_backup = findViewById(R.id.battery_backup);

        specs_of_panel.setText(specs_of_panel_label);
        type_of_system.setText(type_of_system_label);
        make.setText(make_label);
        loops_zone.setText(loops_zone_label);
        model_no.setText(model_no_label);
        repeater_panel.setText(repeater_panel_label);
        isolator_module.setText(isolator_module_label);
        heat_detector.setText(heat_detector_label);
        flame_detector.setText(flame_detector_label);
        smoke_detector.setText(smoke_detector_label);
        multi_detector.setText(multi_detector_label);
        gas_detector.setText(gas_detector_label);
        beam_detector.setText(beam_detector_label);
        manual_call_point.setText(manual_call_point_label);
        hooter_cum_sounder.setText(hooter_cum_sounder_label);
        zone_monitor_module.setText(zone_monitor_module_label);
        monitor_module.setText(monitor_module_label);
        control_module.setText(control_module_label);
        power_supply_unit.setText(power_supply_unit_label);
        zener_barrier.setText(zener_barrier_label);
        siren.setText(siren_label);
        cables.setText(cables_label);
        battery_backup.setText(battery_backup_label);

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
                    Intent serviceActivity = new Intent(mContext, FireDetectionPanelServiceActivity.class);
                    //serviceActivity.putExtra(Constant.FireBucket.spare_part_item_label, "");
                    serviceActivity.putExtra(Constant.modelNo, modelNo);
                    startActivity(serviceActivity);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }
}
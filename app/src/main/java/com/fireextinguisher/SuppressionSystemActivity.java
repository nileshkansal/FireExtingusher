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

public class SuppressionSystemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SuppressionSystemActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    LinearLayout spare_part_selection_layout;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;

    TextView remarks, client_name, location;
    String modelNo = "", productId = "", location_label = "", remarks_label = "", clientName = "";

    String specs_of_suppression_system_label = "", specs_of_panel_and_make_label = "", type_of_system_label = "", make_label = "",
            loops_zone_label = "", model_no_suppression_label = "", capacity_of_cylinder_label = "",
            no_of_cylinders_label = "", empty_weight_label = "", full_weight_label = "", suppression_gas_filled_label = "",
            pressure_gauge_reading_label = "", electromagnetic_actuator_label = "", pneumatic_actuator_label = "",
            pressure_supervisory_switch_label = "", flexible_discharge_hose_label = "", flexible_actuator_hose_label = "",
            nozzles_suppression_label = "", abort_switch_label = "", heat_detector_label = "", manifold_label = "", piping_label = "",
            flame_detector_label = "", smoke_detector_label = "", multi_detector_label = "", gas_detector_label = "",
            vesda_detector_panel_label = "", manual_call_point_label = "", hooter_cum_sounder_label = "", zone_monitor_module_label = "",
            monitor_module_label = "", control_module_label = "", power_supply_unit_label = "", manual_gas_release_switch_label = "",
            siren_label = "", cables_label = "", special_detector_label = "", battery_backup_label = "";

    TextView specs_of_suppression_system, specs_of_panel_and_make, type_of_system, make, loops_zone, model_no_suppression, capacity_of_cylinder,
            no_of_cylinders, empty_weight, full_weight, suppression_gas_filled, pressure_gauge_reading, electromagnetic_actuator,
            pneumatic_actuator, pressure_supervisory_switch, flexible_discharge_hose, flexible_actuator_hose, nozzles_suppression,
            abort_switch, heat_detector, manifold, piping, flame_detector, smoke_detector, multi_detector, gas_detector, vesda_detector_panel,
            manual_call_point, hooter_cum_sounder, zone_monitor_module, monitor_module, control_module, power_supply_unit,
            manual_gas_release_switch, siren, cables, special_detector, battery_backup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppression_system);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.product_info));

        make_label = getIntent().getStringExtra(Constant.SuppressionSystem.make);
        manifold_label = getIntent().getStringExtra(Constant.SuppressionSystem.manifold);
        piping_label = getIntent().getStringExtra(Constant.SuppressionSystem.piping);
        siren_label = getIntent().getStringExtra(Constant.SuppressionSystem.siren);
        cables_label = getIntent().getStringExtra(Constant.SuppressionSystem.cables);
        specs_of_suppression_system_label = getIntent().getStringExtra(Constant.SuppressionSystem.specsOfSupressionWSystem);
        specs_of_panel_and_make_label = getIntent().getStringExtra(Constant.SuppressionSystem.specsOfPanelAndMake);
        type_of_system_label = getIntent().getStringExtra(Constant.SuppressionSystem.typeOfSystem);
        loops_zone_label = getIntent().getStringExtra(Constant.SuppressionSystem.loopsZone);
        model_no_suppression_label = getIntent().getStringExtra(Constant.SuppressionSystem.modelNoSuppresion);
        capacity_of_cylinder_label = getIntent().getStringExtra(Constant.SuppressionSystem.capacityOfClyinder);
        no_of_cylinders_label = getIntent().getStringExtra(Constant.SuppressionSystem.noOfCylinders);
        empty_weight_label = getIntent().getStringExtra(Constant.SuppressionSystem.emptyWeight);
        full_weight_label = getIntent().getStringExtra(Constant.SuppressionSystem.fullWeight);
        suppression_gas_filled_label = getIntent().getStringExtra(Constant.SuppressionSystem.suppressionGasFilled);
        pressure_gauge_reading_label = getIntent().getStringExtra(Constant.SuppressionSystem.pressureGauageReading);
        electromagnetic_actuator_label = getIntent().getStringExtra(Constant.SuppressionSystem.electromagneticActuator);
        pneumatic_actuator_label = getIntent().getStringExtra(Constant.SuppressionSystem.pneumaticActuator);
        pressure_supervisory_switch_label = getIntent().getStringExtra(Constant.SuppressionSystem.pressureSupervisorySwitch);
        flexible_discharge_hose_label = getIntent().getStringExtra(Constant.SuppressionSystem.flexibleDischargeHose);
        flexible_actuator_hose_label = getIntent().getStringExtra(Constant.SuppressionSystem.flexibleActuatorHose);
        nozzles_suppression_label = getIntent().getStringExtra(Constant.SuppressionSystem.nozzlesSuppresion);
        abort_switch_label = getIntent().getStringExtra(Constant.SuppressionSystem.abortSwitch);
        heat_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.heatDetector);
        flame_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.flameDetector);
        smoke_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.smokeDetector);
        multi_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.multiDetector);
        gas_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.gasDetector);
        vesda_detector_panel_label = getIntent().getStringExtra(Constant.SuppressionSystem.vesdaDetectorPanel);
        manual_call_point_label = getIntent().getStringExtra(Constant.SuppressionSystem.manualCallPoint);
        hooter_cum_sounder_label = getIntent().getStringExtra(Constant.SuppressionSystem.hooterCumSounder);
        zone_monitor_module_label = getIntent().getStringExtra(Constant.SuppressionSystem.zoneMonitorModule);
        monitor_module_label = getIntent().getStringExtra(Constant.SuppressionSystem.monitorModule);
        control_module_label = getIntent().getStringExtra(Constant.SuppressionSystem.controlModule);
        power_supply_unit_label = getIntent().getStringExtra(Constant.SuppressionSystem.powerSupplyUnit);
        manual_gas_release_switch_label = getIntent().getStringExtra(Constant.SuppressionSystem.manualGasReleaseSwitch);
        special_detector_label = getIntent().getStringExtra(Constant.SuppressionSystem.specialDetector);
        battery_backup_label = getIntent().getStringExtra(Constant.SuppressionSystem.batteryBackup);

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.SuppressionSystem.location);
        clientName = getIntent().getStringExtra(Constant.SuppressionSystem.clientName);

        if (getIntent().getStringExtra(Constant.SuppressionSystem.remarks) != null &&
                !getIntent().getStringExtra(Constant.SuppressionSystem.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.SuppressionSystem.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.SuppressionSystem.remarks);
        } else {
            remarks_label = "";
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
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);

        remarks.setText(remarks_label);
        //spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);

        specs_of_suppression_system = findViewById(R.id.specs_of_suppression_system);
        specs_of_panel_and_make = findViewById(R.id.specs_of_panel_and_make);
        type_of_system = findViewById(R.id.type_of_system);
        make = findViewById(R.id.make);
        loops_zone = findViewById(R.id.loops_zone);
        model_no_suppression = findViewById(R.id.model_no_suppression);
        capacity_of_cylinder = findViewById(R.id.capacity_of_cylinder);
        no_of_cylinders = findViewById(R.id.no_of_cylinders);
        empty_weight = findViewById(R.id.empty_weight);
        full_weight = findViewById(R.id.full_weight);
        suppression_gas_filled = findViewById(R.id.suppression_gas_filled);
        pressure_gauge_reading = findViewById(R.id.pressure_gauge_reading);
        electromagnetic_actuator = findViewById(R.id.electromagnetic_actuator);
        pneumatic_actuator = findViewById(R.id.pneumatic_actuator);
        pressure_supervisory_switch = findViewById(R.id.pressure_supervisory_switch);
        flexible_discharge_hose = findViewById(R.id.flexible_discharge_hose);
        flexible_actuator_hose = findViewById(R.id.flexible_actuator_hose);
        nozzles_suppression = findViewById(R.id.nozzles_suppression);
        abort_switch = findViewById(R.id.abort_switch);
        heat_detector = findViewById(R.id.heat_detector);
        manifold = findViewById(R.id.manifold);
        piping = findViewById(R.id.piping);
        flame_detector = findViewById(R.id.flame_detector);
        smoke_detector = findViewById(R.id.smoke_detector);
        multi_detector = findViewById(R.id.multi_detector);
        gas_detector = findViewById(R.id.gas_detector);
        vesda_detector_panel = findViewById(R.id.vesda_detector_panel);
        manual_call_point = findViewById(R.id.manual_call_point);
        hooter_cum_sounder = findViewById(R.id.hooter_cum_sounder);
        zone_monitor_module = findViewById(R.id.zone_monitor_module);
        monitor_module = findViewById(R.id.monitor_module);
        control_module = findViewById(R.id.control_module);
        power_supply_unit = findViewById(R.id.power_supply_unit);
        manual_gas_release_switch = findViewById(R.id.manual_gas_release_switch);
        siren = findViewById(R.id.siren);
        cables = findViewById(R.id.cables);
        special_detector = findViewById(R.id.special_detector);
        battery_backup = findViewById(R.id.battery_backup);

        specs_of_suppression_system.setText(specs_of_suppression_system_label);
        specs_of_panel_and_make.setText(specs_of_panel_and_make_label);
        type_of_system.setText(type_of_system_label);
        make.setText(make_label);
        loops_zone.setText(loops_zone_label);
        model_no_suppression.setText(model_no_suppression_label);
        capacity_of_cylinder.setText(capacity_of_cylinder_label);
        no_of_cylinders.setText(no_of_cylinders_label);
        empty_weight.setText(empty_weight_label);
        full_weight.setText(full_weight_label);
        suppression_gas_filled.setText(suppression_gas_filled_label);
        pressure_gauge_reading.setText(pressure_gauge_reading_label);
        electromagnetic_actuator.setText(electromagnetic_actuator_label);
        pneumatic_actuator.setText(pneumatic_actuator_label);
        pressure_supervisory_switch.setText(pressure_supervisory_switch_label);
        flexible_discharge_hose.setText(flexible_discharge_hose_label);
        flexible_actuator_hose.setText(flexible_actuator_hose_label);
        nozzles_suppression.setText(nozzles_suppression_label);
        abort_switch.setText(abort_switch_label);
        heat_detector.setText(heat_detector_label);
        manifold.setText(manifold_label);
        piping.setText(piping_label);
        flame_detector.setText(flame_detector_label);
        smoke_detector.setText(smoke_detector_label);
        multi_detector.setText(multi_detector_label);
        gas_detector.setText(gas_detector_label);
        vesda_detector_panel.setText(vesda_detector_panel_label);
        manual_call_point.setText(manual_call_point_label);
        hooter_cum_sounder.setText(hooter_cum_sounder_label);
        zone_monitor_module.setText(zone_monitor_module_label);
        monitor_module.setText(monitor_module_label);
        control_module.setText(control_module_label);
        power_supply_unit.setText(power_supply_unit_label);
        manual_gas_release_switch.setText(manual_gas_release_switch_label);
        siren.setText(siren_label);
        cables.setText(cables_label);
        special_detector.setText(special_detector_label);
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
                    Intent serviceActivity = new Intent(mContext, SuppressionSystemServiceActivity.class);
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

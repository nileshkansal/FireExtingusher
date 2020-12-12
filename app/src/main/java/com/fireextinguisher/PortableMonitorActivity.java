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

public class PortableMonitorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PortableMonitorActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    LinearLayout spare_part_selection_layout;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;

    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", type_of_monitor_label = "", moc_body_label = "", rotation_label = "", capacity_label = "", flow_label = "",
            pressure_label = "", size_label = "", throw_range_label = "", foam_tank_label = "", foam_inductor_label = "",
            handle_rotation_wheel_label = "", foam_inductor_pipe_label = "", nozzle_type_label = "", operation_manual_electric_label = "";

    TextView spare_part, spare_part_selection, remarks, client_name, location, type_of_monitor, moc_body, rotation, capacity, flow,
            pressure, size, throw_range, foam_tank, foam_inductor, handle_rotation_wheel, foam_inductor_pipe, nozzle_type,
            operation_manual_electric;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_portable_monitor);

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
        location_label = getIntent().getStringExtra(Constant.PortableMonitor.location);
        clientName = getIntent().getStringExtra(Constant.PortableMonitor.clientName);

        spare_part_label = getIntent().getStringExtra(Constant.PortableMonitor.spare_part_label);

        if (getIntent().getStringExtra(Constant.PortableMonitor.remarks) != null &&
                !getIntent().getStringExtra(Constant.PortableMonitor.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.PortableMonitor.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.PortableMonitor.remarks);
        } else {
            remarks_label = "";
        }

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.PortableMonitor.spare_part_item_label);
        }

        type_of_monitor_label = getIntent().getStringExtra(Constant.PortableMonitor.typeOfMonitor);
        moc_body_label = getIntent().getStringExtra(Constant.PortableMonitor.mocBody);
        rotation_label = getIntent().getStringExtra(Constant.PortableMonitor.rotation);
        capacity_label = getIntent().getStringExtra(Constant.PortableMonitor.capacity);
        flow_label = getIntent().getStringExtra(Constant.PortableMonitor.flow);
        pressure_label = getIntent().getStringExtra(Constant.PortableMonitor.pressure);
        size_label = getIntent().getStringExtra(Constant.PortableMonitor.size);
        throw_range_label = getIntent().getStringExtra(Constant.PortableMonitor.throwRange);
        foam_tank_label = getIntent().getStringExtra(Constant.PortableMonitor.foamTank);
        foam_inductor_label = getIntent().getStringExtra(Constant.PortableMonitor.foamInductor);
        handle_rotation_wheel_label = getIntent().getStringExtra(Constant.PortableMonitor.handleRotationWheel);
        foam_inductor_pipe_label = getIntent().getStringExtra(Constant.PortableMonitor.foamInductorPipe);
        nozzle_type_label = getIntent().getStringExtra(Constant.PortableMonitor.nozzleType);
        operation_manual_electric_label = getIntent().getStringExtra(Constant.PortableMonitor.operationManualElectric);

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
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        type_of_monitor = findViewById(R.id.type_of_monitor);
        moc_body = findViewById(R.id.moc_body);
        rotation = findViewById(R.id.rotation);
        capacity = findViewById(R.id.capacity);
        flow = findViewById(R.id.flow);
        pressure = findViewById(R.id.pressure);
        size = findViewById(R.id.size);
        throw_range = findViewById(R.id.throw_range);
        foam_tank = findViewById(R.id.foam_tank);
        foam_inductor = findViewById(R.id.foam_inductor);
        handle_rotation_wheel = findViewById(R.id.handle_rotation_wheel);
        foam_inductor_pipe = findViewById(R.id.foam_inductor_pipe);
        nozzle_type = findViewById(R.id.nozzle_type);
        operation_manual_electric = findViewById(R.id.operation_manual_electric);


        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);

        type_of_monitor.setText(type_of_monitor_label);
        moc_body.setText(moc_body_label);
        rotation.setText(rotation_label);
        capacity.setText(capacity_label);
        flow.setText(flow_label);
        pressure.setText(pressure_label);
        size.setText(size_label);
        throw_range.setText(throw_range_label);
        foam_tank.setText(foam_tank_label);
        foam_inductor.setText(foam_inductor_label);
        foam_inductor_pipe.setText(foam_inductor_pipe_label);
        handle_rotation_wheel.setText(handle_rotation_wheel_label);
        nozzle_type.setText(nozzle_type_label);
        operation_manual_electric.setText(operation_manual_electric_label);


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
                    Intent serviceActivity = new Intent(mContext, PortableMonitorServiceActivity.class);
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
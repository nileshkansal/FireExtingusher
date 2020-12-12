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

public class FirePumpInstalledActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FirePumpInstalledActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", hp = "", head = "", kw = "", pump_no = "", pump_type = "", rpm = "", motor_no = "";
    TextView remarks, spare_part, location, spare_part_selection, client_name, hp_txt, head_txt, kw_txt, pump_no_txt,
            pump_type_txt, rpm_txt, motor_no_txt, service_text;

    LinearLayout spare_part_selection_layout;
    //Button service_btn, other_btn;
    RelativeLayout service_image, other_image;
    Button continue_btn;
    boolean isServiceSelected = false, isOtherSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_pump_installed);
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
        location_label = getIntent().getStringExtra(Constant.FirePump.location);
        spare_part_label = getIntent().getStringExtra(Constant.FirePump.spare_part_label);

        hp = getIntent().getStringExtra(Constant.FirePump.HP);
        head = getIntent().getStringExtra(Constant.FirePump.Head);
        kw = getIntent().getStringExtra(Constant.FirePump.KW);
        pump_no = getIntent().getStringExtra(Constant.FirePump.pumpNo);
        pump_type = getIntent().getStringExtra(Constant.FirePump.pumpType);
        rpm = getIntent().getStringExtra(Constant.FirePump.RPM);
        motor_no = getIntent().getStringExtra(Constant.FirePump.motorNo);

        if (getIntent().getStringExtra(Constant.FirePump.remarks) != null &&
                !getIntent().getStringExtra(Constant.FirePump.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.FirePump.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.FirePump.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.FirePump.clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.FirePump.spare_part_item_label);
        }

        bind();
    }

    private void bind() {

        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        continue_btn.setVisibility(View.GONE);

        service_image = findViewById(R.id.service_image);
        other_image = findViewById(R.id.other_image);

        service_text = findViewById(R.id.service_text);

        service_image.setOnClickListener(this);
        other_image.setOnClickListener(this);

        other_image.setVisibility(View.GONE);
        service_text.setText(mContext.getResources().getString(R.string.life_test));

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);

        hp_txt = findViewById(R.id.hp_txt);
        head_txt = findViewById(R.id.head_txt);
        kw_txt = findViewById(R.id.kw_txt);
        pump_no_txt = findViewById(R.id.pump_no_txt);
        pump_type_txt = findViewById(R.id.pump_type_txt);
        rpm_txt = findViewById(R.id.rpm_txt);
        motor_no_txt = findViewById(R.id.motor_no_txt);

        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);
        hp_txt.setText(hp);
        head_txt.setText(head);
        kw_txt.setText(kw);
        pump_no_txt.setText(pump_no);
        pump_type_txt.setText(pump_type);
        rpm_txt.setText(rpm);
        motor_no_txt.setText(motor_no);

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
                    Intent otherActivity = new Intent(mContext, FirePumpOtherActivity.class);
                    otherActivity.putExtra(Constant.modelNo, modelNo);
                    otherActivity.putExtra(Constant.productId, productId);
                    otherActivity.putExtra(Constant.FirePump.location, location_label);
                    otherActivity.putExtra(Constant.FirePump.HP, hp);
                    otherActivity.putExtra(Constant.FirePump.Head, head);
                    otherActivity.putExtra(Constant.FirePump.KW, kw);
                    otherActivity.putExtra(Constant.FirePump.pumpNo, pump_no);
                    otherActivity.putExtra(Constant.FirePump.pumpType, pump_type);
                    otherActivity.putExtra(Constant.FirePump.RPM, rpm);
                    otherActivity.putExtra(Constant.FirePump.motorNo, motor_no);
                    otherActivity.putExtra(Constant.FirePump.spare_part_label, spare_part_label);
                    if (!remarks_label.equals("")) {
                        otherActivity.putExtra(Constant.FirePump.remarks, remarks_label);
                    }
                    otherActivity.putExtra(Constant.FirePump.clientName, clientName);
                    otherActivity.putExtra(Constant.FirePump.spare_part_item_label, spare_part_item_label);

                    startActivity(otherActivity);
                } else if (isServiceSelected) {
                    Intent serviceActivity = new Intent(mContext, FirePumpServiceActivity.class);
                    serviceActivity.putExtra(Constant.FirePump.pumpType, pump_type);
                    //serviceActivity.putExtra(Constant.FirePump.spare_part_item_label, spare_part_item_label);
                    serviceActivity.putExtra(Constant.modelNo, modelNo);
                    startActivity(serviceActivity);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }
}

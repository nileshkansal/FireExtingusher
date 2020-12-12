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

public class InstalledFromActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InstalledFromActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    RelativeLayout service_image, other_image;
    LinearLayout spare_part_selection_layout;
    TextView remarks, spare_part, due_date_hpt, last_date_hpt, due_date_refill, last_date_refill,
            net_cylinder_pressure, full_cylinder_pressure, empty_cylinder_pressure, mfg_year, capacity, f_e_type,
            f_e_no, current_location, location, client_name, spare_part_selection;

    String modelNo = "", productId = "", location_label = "", f_e_no_label = "", fe_type_label = "", capacity_label = "",
            mfg_year_label = "", empty_cylinder_pressure_label = "", full_cylinder_pressure_label = "",
            net_cylinder_pressure_label = "", last_date_refill_label = "", due_date_refill_label = "", last_date_hpt_label = "",
            due_date_hpt_label = "", spare_part_label = "", remarks_label = "", clientName = "", spare_part_item_label = "";

    boolean isServiceSelected = false, isOtherSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_form);

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
        location_label = getIntent().getStringExtra(Constant.ProductInfo.location);
        f_e_no_label = getIntent().getStringExtra(Constant.ProductInfo.f_e_no);
        fe_type_label = getIntent().getStringExtra(Constant.ProductInfo.fe_type_label);
        capacity_label = getIntent().getStringExtra(Constant.ProductInfo.capacity_label);
        mfg_year_label = getIntent().getStringExtra(Constant.ProductInfo.mfg_year_label);
        empty_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.empty_cylinder_pressure);
        full_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.full_cylinder_pressure);
        net_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.net_cylinder_pressure);
        last_date_refill_label = getIntent().getStringExtra(Constant.ProductInfo.last_date_refill_label);
        due_date_refill_label = getIntent().getStringExtra(Constant.ProductInfo.due_date_refill_label);
        last_date_hpt_label = getIntent().getStringExtra(Constant.ProductInfo.last_date_hpt_label);
        due_date_hpt_label = getIntent().getStringExtra(Constant.ProductInfo.due_date_hpt_label);
        spare_part_label = getIntent().getStringExtra(Constant.ProductInfo.spare_part_label);

        if (getIntent().getStringExtra(Constant.ProductInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.ProductInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.ProductInfo.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.ProductInfo.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.ProductInfo.clientName);

        if (spare_part_label != null && spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.ProductInfo.spare_part_item_label);
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

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        due_date_hpt = findViewById(R.id.due_date_hpt);
        last_date_hpt = findViewById(R.id.last_date_hpt);
        due_date_refill = findViewById(R.id.due_date_refill);
        last_date_refill = findViewById(R.id.last_date_refill);
        net_cylinder_pressure = findViewById(R.id.net_cylinder_pressure);
        full_cylinder_pressure = findViewById(R.id.full_cylinder_pressure);
        empty_cylinder_pressure = findViewById(R.id.empty_cylinder_pressure);
        mfg_year = findViewById(R.id.mfg_year);
        capacity = findViewById(R.id.capacity);
        f_e_type = findViewById(R.id.f_e_type);
        f_e_no = findViewById(R.id.f_e_no);
        current_location = findViewById(R.id.current_location);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        location.setText(location_label);
        f_e_no.setText(f_e_no_label);
        f_e_type.setText(fe_type_label);
        capacity.setText(capacity_label);
        mfg_year.setText(mfg_year_label);
        empty_cylinder_pressure.setText(empty_cylinder_pressure_label);
        full_cylinder_pressure.setText(full_cylinder_pressure_label);
        net_cylinder_pressure.setText(net_cylinder_pressure_label);
        last_date_refill.setText(last_date_refill_label);
        due_date_refill.setText(due_date_refill_label);
        last_date_hpt.setText(last_date_hpt_label);
        due_date_hpt.setText(due_date_hpt_label);
        spare_part.setText(spare_part_label);
        remarks.setText(remarks_label);

        client_name.setText(clientName);

        if (spare_part_label != null && spare_part_label.equals("Yes")) {
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
                    Intent otherActivity = new Intent(mContext, FireExtinguisherOtherActivity.class);
                    otherActivity.putExtra(Constant.modelNo, modelNo);
                    otherActivity.putExtra(Constant.productId, productId);
                    otherActivity.putExtra(Constant.ProductInfo.location, location_label);
                    otherActivity.putExtra(Constant.ProductInfo.f_e_no, f_e_no_label);
                    otherActivity.putExtra(Constant.ProductInfo.fe_type_label, fe_type_label);
                    otherActivity.putExtra(Constant.ProductInfo.capacity_label, capacity_label);
                    otherActivity.putExtra(Constant.ProductInfo.mfg_year_label, mfg_year_label);
                    otherActivity.putExtra(Constant.ProductInfo.empty_cylinder_pressure, empty_cylinder_pressure_label);
                    otherActivity.putExtra(Constant.ProductInfo.full_cylinder_pressure, full_cylinder_pressure_label);
                    otherActivity.putExtra(Constant.ProductInfo.net_cylinder_pressure, net_cylinder_pressure_label);
                    otherActivity.putExtra(Constant.ProductInfo.last_date_refill_label, last_date_refill_label);
                    otherActivity.putExtra(Constant.ProductInfo.due_date_refill_label, due_date_refill_label);
                    otherActivity.putExtra(Constant.ProductInfo.last_date_hpt_label, last_date_hpt_label);
                    otherActivity.putExtra(Constant.ProductInfo.due_date_hpt_label, due_date_hpt_label);
                    otherActivity.putExtra(Constant.ProductInfo.spare_part_label, spare_part_label);
                    if (!remarks_label.equals("")) {
                        otherActivity.putExtra(Constant.ProductInfo.remarks, remarks_label);
                    }
                    otherActivity.putExtra(Constant.ProductInfo.clientName, clientName);
                    otherActivity.putExtra(Constant.ProductInfo.spare_part_item_label, spare_part_item_label);

                    startActivity(otherActivity);
                } else if (isServiceSelected) {
                    Intent serviceActivity = new Intent(mContext, ServiceActivity.class);
                    serviceActivity.putExtra(Constant.ProductInfo.spare_part_item_label, spare_part_item_label);
                    serviceActivity.putExtra(Constant.modelNo, modelNo);
                    startActivity(serviceActivity);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any option");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceSelected) {
            isServiceSelected = false;
            service_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
        }

        if (isOtherSelected) {
            isOtherSelected = false;
            other_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
        }
        continue_btn.setVisibility(View.GONE);
    }
}
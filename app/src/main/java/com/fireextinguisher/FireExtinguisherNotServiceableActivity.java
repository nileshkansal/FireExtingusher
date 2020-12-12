package com.fireextinguisher;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.model.ServiceModel;

import java.util.ArrayList;

public class FireExtinguisherNotServiceableActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FireExtinguisherNotServiceableActivity.class.getSimpleName();
    Context mContext;
    Button service_btn;
    Toolbar toolbar;
    ListView list_view;
    ArrayList<ServiceModel> modelArrayList;
    ServiceListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_extinguisher_not_serviceable);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.non_serviceable_title));

        bind();
    }

    private void bind() {
        service_btn = findViewById(R.id.service_btn);
        service_btn.setOnClickListener(this);

        list_view = findViewById(R.id.list_view);

        modelArrayList = new ArrayList<>();
        String[] serviceItemArray = mContext.getResources().getStringArray(R.array.non_service_item);

        for (int i = 0; i < serviceItemArray.length; i++) {
            ServiceModel model = new ServiceModel(i, serviceItemArray[i], false);
            modelArrayList.add(model);
        }

        adapter = new ServiceListAdapter(mContext, modelArrayList);
        list_view.setAdapter(adapter);
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
        if (v.getId() == R.id.service_btn) {
            showAlert();
        }
    }

    private void showAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Details submitted successfully.");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private class ServiceListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<ServiceModel> arrayList;

        ServiceListAdapter(Context context, ArrayList<ServiceModel> models) {
            mContext = context;
            arrayList = models;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.service_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ServiceModel model = arrayList.get(position);
            viewHolder.service_name.setText(model.getServiceName());
            if (model.isSelected()) {
                viewHolder.checkbox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkbox.setVisibility(View.GONE);
            }
            viewHolder.select_layout.setOnClickListener(new CheckClick(position, viewHolder));
            return convertView;
        }

        class ViewHolder {

            TextView service_name;
            ImageView checkbox;
            RelativeLayout select_layout;

            ViewHolder(View view) {
                select_layout = view.findViewById(R.id.select_layout);
                service_name = view.findViewById(R.id.service_name);
                checkbox = view.findViewById(R.id.checkbox);
            }
        }

        class CheckClick implements View.OnClickListener {

            int position;
            ViewHolder holder;

            CheckClick(int pos, ViewHolder viewHolder) {
                position = pos;
                holder = viewHolder;
            }

            @Override
            public void onClick(View v) {

                int id = arrayList.get(position).getId();

                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (id == modelArrayList.get(position).getId()) {
                        if (modelArrayList.get(position).isSelected()) {
                            modelArrayList.get(position).setSelected(false);
                            arrayList.get(position).setSelected(false);
                            holder.checkbox.setVisibility(View.GONE);
                        } else {
                            modelArrayList.get(position).setSelected(true);
                            arrayList.get(position).setSelected(true);
                            holder.checkbox.setVisibility(View.VISIBLE);
                        }
                    }
                }
                //notifyDataSetChanged();
            }
        }
    }
}

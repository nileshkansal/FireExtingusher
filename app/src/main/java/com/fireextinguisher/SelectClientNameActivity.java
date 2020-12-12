package com.fireextinguisher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fireextinguisher.model.ClientNameModel;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.RuntimePermissionsActivity;
import com.fireextinguisher.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectClientNameActivity extends RuntimePermissionsActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = SelectClientNameActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION = 1000;
    Context mContext;
    EditText editText;
    ListView list_view;
    Button continue_btn, submit_btn;
    ClientListAdapter adapter;
    ArrayList<ClientNameModel> modelArrayList = new ArrayList<>();
    ProgressDialog dialog;
    String clientName = "";
    int clientId = 0;
    Toolbar toolbar;
    SwipeRefreshLayout refresh_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_client_name);
        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.select_client));

        continue_btn = findViewById(R.id.continue_btn);
        submit_btn = findViewById(R.id.submit_btn);
        list_view = findViewById(R.id.list_view);
        editText = findViewById(R.id.name_edit_text);

        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this);
        refresh_layout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary));

        getClientName(true);

        View footerView = getLayoutInflater().inflate(R.layout.item_group_header, null);
        list_view.addFooterView(footerView);

        continue_btn.setOnClickListener(v -> {

            if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "",
                        CAMERA_PERMISSION);
            } else {
                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (modelArrayList.get(i).isSelected()) {
                        clientId = modelArrayList.get(i).getId();
                        clientName = modelArrayList.get(i).getName();
                        break;
                    }
                }

                if (clientId > 0) {
                    Utility.setSharedPreference(mContext, Constant.clientId, String.valueOf(clientId));
                    Intent intent = new Intent(mContext, QRCodeScannerActivity.class);
                    intent.putExtra(Constant.clientName, clientName);
                    intent.putExtra(Constant.clientId, clientId);
                    startActivity(intent);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select any client");
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == CAMERA_PERMISSION) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                if (modelArrayList.get(i).isSelected()) {
                    clientId = modelArrayList.get(i).getId();
                    clientName = modelArrayList.get(i).getName();
                    break;
                }
            }

            if (clientId > 0) {
                Intent intent = new Intent(mContext, QRCodeScannerActivity.class);
                Utility.setSharedPreference(mContext, Constant.clientId, String.valueOf(clientId));
                intent.putExtra(Constant.clientName, clientName);
                intent.putExtra(Constant.clientId, clientId);
                startActivity(intent);
            } else {
                Utility.ShowToastMessage(mContext, "Please select any client");
            }
        }
    }

    @Override
    public void onPermissionDenial(int requestCode) {
        Utility.ShowToastMessage(mContext, getResources().getString(R.string.denied_permission));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                Utility.ShowToastMessage(mContext, "Logged out successfully");
                Utility.logout(SelectClientNameActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
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

    private void getClientName(boolean showLoading) {
        if (showLoading)
            showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getAllClientName(Utility.getSharedPreferences(mContext, Constant.userId));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (refresh_layout.isRefreshing())
                    refresh_layout.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONArray array = jsonObject.optJSONArray(Constant.object);
                            modelArrayList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                ClientNameModel model = new ClientNameModel(object.getInt("id"), object.getString("name"), false);
                                modelArrayList.add(model);
                            }

                            adapter = new ClientListAdapter(mContext, modelArrayList);
                            list_view.setAdapter(adapter);

                            hideDialog();
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
                if (refresh_layout.isRefreshing())
                    refresh_layout.setRefreshing(false);

                hideDialog();
                Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        getClientName(false);
    }

    class ClientListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<ClientNameModel> arrayList;

        public ClientListAdapter(Context context, ArrayList<ClientNameModel> models) {
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
                convertView = inflater.inflate(R.layout.item_client_name, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                ClientNameModel model = arrayList.get(position);

                viewHolder.client_name.setText(model.getName());
                if (model.isSelected()) {
                    viewHolder.checkbox.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.checkbox.setVisibility(View.GONE);
                }
                viewHolder.select_layout.setOnClickListener(new CheckClick(position, viewHolder));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            RelativeLayout select_layout;
            ImageView checkbox;
            TextView client_name;

            ViewHolder(View view) {
                select_layout = view.findViewById(R.id.select_layout);
                checkbox = view.findViewById(R.id.checkbox);
                client_name = view.findViewById(R.id.client_name);
            }
        }

        class CheckClick implements View.OnClickListener {

            int pos;
            ViewHolder holder;

            private CheckClick(int position, ViewHolder viewHolder) {
                pos = position;
                holder = viewHolder;
            }

            @Override
            public void onClick(View v) {
                int id = arrayList.get(pos).getId();
                int count = arrayList.size();
                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (id == modelArrayList.get(i).getId()) {
                        modelArrayList.get(i).setSelected(true);
                        //arrayList.get(i).setSelected(true);
                        //holder.checkbox.setVisibility(View.VISIBLE);
                    } else {
                        modelArrayList.get(i).setSelected(false);
                        count = count - 1;
                        //arrayList.get(i).setSelected(false);
                        //holder.checkbox.setVisibility(View.GONE);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }
}
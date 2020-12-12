package com.fireextinguisher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fireextinguisher.model.InstallationCategoryModel;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstallationCategoryActivity extends AppCompatActivity {

    private static final String TAG = InstallationCategoryActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    GridView gridView1;
    InstallationCategoryAdapter categoryAdapter;
    ArrayList<InstallationCategoryModel> mainModelArrayList;
    Button continue_btn;
    String id = "", modelNo = "", clientName = "";
    int clientId = 0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_category);
        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.installation));

        gridView1 = findViewById(R.id.gridView1);
        continue_btn = findViewById(R.id.continue_btn);
        modelNo = getIntent().getStringExtra(Constant.modelNo);
        clientName = getIntent().getStringExtra(Constant.clientName);
        clientId = getIntent().getIntExtra(Constant.clientId, 0);

        getCategories();

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mainModelArrayList.size(); i++) {
                    if (mainModelArrayList.get(i).isSelected()) {
                        id = mainModelArrayList.get(i).getId();
                        break;
                    }
                }

                if (!id.equals("")) {
                    if (id.equals("1")) {
                        Intent intent = new Intent(mContext, InstallationFormActivity.class);
                        intent.putExtra(Constant.modelNo, modelNo);
                        intent.putExtra(Constant.productId, id);
                        intent.putExtra(Constant.clientName, clientName);
                        intent.putExtra(Constant.clientId, clientId);
                        startActivity(intent);
                    } else if (id.equals("2")) {
                        Intent intent = new Intent(mContext, FireHydrantInstallActivity.class);
                        intent.putExtra(Constant.modelNo, modelNo);
                        intent.putExtra(Constant.productId, id);
                        intent.putExtra(Constant.clientName, clientName);
                        intent.putExtra(Constant.clientId, clientId);
                        startActivity(intent);
                    } else if (id.equals("3")) {
                        Intent intent = new Intent(mContext, HoseReelInstallActivity.class);
                        intent.putExtra(Constant.modelNo, modelNo);
                        intent.putExtra(Constant.productId, id);
                        intent.putExtra(Constant.clientName, clientName);
                        intent.putExtra(Constant.clientId, clientId);
                        startActivity(intent);
                    } else if (id.equals("4")) {
                        Intent intent = new Intent(mContext, FireBucketInstallActivity.class);
                        intent.putExtra(Constant.modelNo, modelNo);
                        intent.putExtra(Constant.productId, id);
                        intent.putExtra(Constant.clientName, clientName);
                        intent.putExtra(Constant.clientId, clientId);
                        startActivity(intent);
                    } else if (id.equals("5")) {
                        Intent intent = new Intent(mContext, FirePumpSelectActivity.class);
                        intent.putExtra(Constant.modelNo, modelNo);
                        intent.putExtra(Constant.productId, id);
                        intent.putExtra(Constant.clientName, clientName);
                        intent.putExtra(Constant.clientId, clientId);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
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

    private void getCategories() {
        mainModelArrayList = new ArrayList<>();
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProduct();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONArray object = jsonObject.optJSONArray(Constant.object);
                            hideDialog();
                            if (object != null)
                                for (int i = 0; i < object.length(); i++) {
                                    JSONObject data = object.optJSONObject(i);
                                    String image = Constant.IMAGE_PATH + data.optString("ppath") + "/" +
                                            data.optString("pimage");
                                    InstallationCategoryModel model = new InstallationCategoryModel(data.optString("pid"),
                                            data.optString("pname"), image, false);
                                    mainModelArrayList.add(model);
                                }

                            categoryAdapter = new InstallationCategoryAdapter(mContext, mainModelArrayList);
                            gridView1.setAdapter(categoryAdapter);
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
                hideDialog();
                Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });

    }

    class InstallationCategoryAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<InstallationCategoryModel> modelArrayList;
        LayoutInflater inflater;

        InstallationCategoryAdapter(Context context, ArrayList<InstallationCategoryModel> models) {
            mContext = context;
            modelArrayList = models;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return modelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return modelArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.instalation_category_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                InstallationCategoryModel model = modelArrayList.get(position);

                Glide.with(mContext).load(model.getImageURL()).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.product_image);
                viewHolder.product_label.setText(model.getName());

                if (model.isSelected()) {
                    viewHolder.item_layout.setBackgroundResource(R.drawable.selected_category_back);
                } else {
                    viewHolder.item_layout.setBackgroundResource(R.drawable.unselected_category_back);
                }
                viewHolder.item_layout.setOnClickListener(new ItemClicked(position, viewHolder));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {

            RelativeLayout item_layout;
            TextView product_label;
            RoundedImageView product_image;

            ViewHolder(View view) {
                item_layout = view.findViewById(R.id.item_layout);
                product_label = view.findViewById(R.id.product_label);
                product_image = view.findViewById(R.id.product_image);
            }
        }

        class ItemClicked implements View.OnClickListener {

            int pos;
            ViewHolder viewHolder;

            ItemClicked(int position, ViewHolder viewHolder) {
                pos = position;
                this.viewHolder = viewHolder;
            }

            @Override
            public void onClick(View v) {

                String id = modelArrayList.get(pos).getId();

                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (modelArrayList.get(i).getId().equals(id)) {
                        if (modelArrayList.get(i).isSelected()) {
                            modelArrayList.get(i).setSelected(false);
                            mainModelArrayList.get(i).setSelected(false);
                            viewHolder.item_layout.setBackgroundResource(R.drawable.unselected_category_back);
                        } else {
                            modelArrayList.get(i).setSelected(true);
                            mainModelArrayList.get(i).setSelected(true);
                            viewHolder.item_layout.setBackgroundResource(R.drawable.selected_category_back);
                        }
                    } else {
                        modelArrayList.get(i).setSelected(false);
                        mainModelArrayList.get(i).setSelected(false);
                        viewHolder.item_layout.setBackgroundResource(R.drawable.unselected_category_back);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }
}
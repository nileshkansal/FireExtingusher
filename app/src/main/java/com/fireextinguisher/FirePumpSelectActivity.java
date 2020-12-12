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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fireextinguisher.model.FirePumpModel;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class FirePumpSelectActivity extends AppCompatActivity {

    private static final String TAG = FirePumpSelectActivity.class.getSimpleName();
    Context mContext;
    Toolbar toolbar;
    GridView gridView1;
    Button continue_btn;
    String modelNo = "", productId = "", clientName = "", pumpType = "";
    int clientId = 0;
    ProgressDialog dialog;
    ArrayList<FirePumpModel> mainModelArrayList = new ArrayList<>();
    String imageURL = "assets/pimage/images-17.jpeg";
    FirePumpCategoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_pump_select);

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
        productId = getIntent().getStringExtra(Constant.productId);
        clientName = getIntent().getStringExtra(Constant.clientName);
        clientId = getIntent().getIntExtra(Constant.clientId, 0);

        getFirePumps();

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mainModelArrayList.size(); i++) {
                    if (mainModelArrayList.get(i).isSelected()) {
                        pumpType = mainModelArrayList.get(i).getName();
                        break;
                    }
                }
                Log.e(TAG, "pumpType =====> " + pumpType);
                if (!pumpType.equals("")) {
                    Intent intent = new Intent(mContext, FirePumpInstallActivity.class);
                    intent.putExtra(Constant.modelNo, modelNo);
                    intent.putExtra(Constant.productId, productId);
                    intent.putExtra(Constant.clientName, clientName);
                    intent.putExtra(Constant.clientId, clientId);
                    intent.putExtra(Constant.pumpType, pumpType);
                    startActivity(intent);
                } else {
                    Utility.ShowToastMessage(mContext, "Please select pump");
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

    private void getFirePumps() {
        String[] serviceItemArray = new String[]{"Jockey Pump Electric", "Main Pump Electric", "Diesel Engine", "Standby Pump"};

        for (int i = 0; i < serviceItemArray.length; i++) {
            FirePumpModel model = new FirePumpModel(i, serviceItemArray[i], Constant.IMAGE_PATH + imageURL, false);
            mainModelArrayList.add(model);
        }

        adapter = new FirePumpCategoryAdapter(mContext, mainModelArrayList);
        gridView1.setAdapter(adapter);
    }

    class FirePumpCategoryAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<FirePumpModel> modelArrayList;
        LayoutInflater inflater;

        FirePumpCategoryAdapter(Context context, ArrayList<FirePumpModel> models) {
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
                convertView = inflater.inflate(R.layout.instalation_category_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                FirePumpModel model = modelArrayList.get(position);

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

                int id = modelArrayList.get(pos).getId();

                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (modelArrayList.get(i).getId() == id) {
                        if (modelArrayList.get(i).isSelected()) {
                            modelArrayList.get(i).setSelected(false);
                            mainModelArrayList.get(i).setSelected(false);
                            viewHolder.item_layout.setBackgroundResource(R.drawable.unselected_category_back);

                            pumpType = "";
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
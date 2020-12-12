package com.fireextinguisher;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.fireextinguisher.model.FireDetectionServiceModel;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.RuntimePermissionsActivity;
import com.fireextinguisher.utils.Utility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireDetectionPanelServiceActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final String TAG = FireDetectionPanelServiceActivity.class.getSimpleName();
    private static final int PERMISSION = 1010;
    Context mContext;
    Button service_btn, not_serviceable_btn, ok_btn;
    Toolbar toolbar;
    ListView list_view;
    ArrayList<FireDetectionServiceModel> modelArrayList;
    //ServiceListAdapter adapter;
    ArrayList<Integer> count = new ArrayList<>();
    ArrayList<String> services = new ArrayList<>();
    List<String> oldServices = new ArrayList<>();
    ProgressDialog dialog;
    String modelNo = "", spare_part_item_label = "", picturePath = "";
    AlertDialog remarkDialog;
    RelativeLayout select_image_layout;
    TextInputLayout current_refill_layout;
    TextInputEditText current_refill_text;
    ImageView image_view;
    Dialog sdialog;
    Uri outputFileUri;
    String actualType = "";
    Bitmap myBitmap;
    View customLayout;
    String serviceRemark = "", serviceQty = "", serviceLocation = "", serviceDetail = "";
    ArrayList<String> locationList = new ArrayList<>();
    ArrayList<String> qtyList = new ArrayList<>();
    ArrayList<String> remarkList = new ArrayList<>();

    TextInputEditText location_text1, location_text2, location_text3, location_text4, location_text5, location_text6, location_text7,
            location_text8, location_text9, location_text10, location_text11, location_text12, location_text13, location_text14,
            location_text15, location_text16, location_text17, location_text18, location_text19, location_text20, location_text21,
            location_text22;

    TextInputEditText qty_text1, qty_text2, qty_text3, qty_text4, qty_text5, qty_text6, qty_text7, qty_text8, qty_text9, qty_text10,
            qty_text11, qty_text12, qty_text13, qty_text14, qty_text15, qty_text16, qty_text17, qty_text18, qty_text19, qty_text20,
            qty_text21, qty_text22;

    TextInputEditText remark_text1, remark_text2, remark_text3, remark_text4, remark_text5, remark_text6, remark_text7, remark_text8,
            remark_text9, remark_text10, remark_text11, remark_text12, remark_text13, remark_text14, remark_text15, remark_text16,
            remark_text17, remark_text18, remark_text19, remark_text20, remark_text21, remark_text22;

    TextView service_name1, service_name2, service_name3, service_name4, service_name5, service_name6, service_name7, service_name8,
            service_name9, service_name10, service_name11, service_name12, service_name13, service_name14, service_name15, service_name16,
            service_name17, service_name18, service_name19, service_name20, service_name21, service_name22;

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        Log.e(TAG, "Rotating Image If Required");
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Log.e(TAG, "Rotating Image");
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException("No resource string found with name " + resName);
        } else {
            return ResourceID;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_detection_service);
        //setContentView(R.layout.activity_service);


        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Fire Detection Panel Service");

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        if (getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_item_label) != null &&
                !getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_item_label).equals("")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.HoseReelInfo.spare_part_item_label);
            String[] myArray = spare_part_item_label.split(",");
            oldServices = Arrays.asList(myArray);
        }

        bind();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (PERMISSION == requestCode) {
            if (actualType.equals("service")) {
                count.clear();
                services.clear();
                locationList.clear();
                qtyList.clear();
                remarkList.clear();

                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (modelArrayList.get(i).isSelected()) {
                        count.add(modelArrayList.get(i).getId());
                        services.add(modelArrayList.get(i).getServiceName());

                        String location_name = "location_text" + (i + 1);
                        if (((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString().length() > 0) {
                            String location = ((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString();
                            modelArrayList.get(i).setLocation(location);
                        } else {
                            modelArrayList.get(i).setLocation("");
                        }

                        String qty_name = "qty_text" + (i + 1);
                        if (((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString().length() > 0) {
                            String qty = ((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString();
                            modelArrayList.get(i).setQty(qty);
                        } else {
                            modelArrayList.get(i).setQty("");
                        }

                        String remark_name = "remark_text" + (i + 1);
                        if (((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString().length() > 0) {
                            String remark = ((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString();
                            modelArrayList.get(i).setRemark(remark);
                        } else {
                            modelArrayList.get(i).setRemark("");
                        }

                        if (!modelArrayList.get(i).getLocation().equals("")) {
                            locationList.add(modelArrayList.get(i).getLocation());
                        }
                        if (!modelArrayList.get(i).getQty().equals("")) {
                            qtyList.add(modelArrayList.get(i).getQty());
                        }
                        if (!modelArrayList.get(i).getRemark().equals("")) {
                            remarkList.add(modelArrayList.get(i).getRemark());
                        }
                    }
                }
                if (count.size() > 0) {
                    if (!(count.size() == locationList.size())) {
                        runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service locations"));
                    } else if (!(count.size() == qtyList.size())) {
                        runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service required Qty(s)"));
                    } else if (!(count.size() == remarkList.size())) {
                        runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service remarks"));
                    } else {
                        showRemarkAlert(actualType);
                    }
                } else {
                    runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please select service first"));
                }
            } else {
                showRemarkAlert(actualType);
            }
        }
    }

    @Override
    public void onPermissionDenial(int requestCode) {

    }

    private void bind() {
        service_btn = findViewById(R.id.service_btn);
        service_btn.setOnClickListener(this);

        not_serviceable_btn = findViewById(R.id.not_serviceable_btn);
        not_serviceable_btn.setOnClickListener(this);

        ok_btn = findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(this);

        list_view = findViewById(R.id.list_view);

        modelArrayList = new ArrayList<>();
        String[] serviceItemArray = mContext.getResources().getStringArray(R.array.fire_detection_panel_service);

        if (oldServices.size() > 0) {
            for (int i = 0; i < serviceItemArray.length; i++) {
                FireDetectionServiceModel model = new FireDetectionServiceModel(i, serviceItemArray[i], false, "",
                        "", "");
                modelArrayList.add(model);

                String empty_name = "checkbox_empty_" + (i + 1);
                Log.e(TAG, "checkbox ========> " + empty_name);
                findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.VISIBLE);

            }

            for (int i = 0; i < modelArrayList.size(); i++) {
                for (int j = 0; j < oldServices.size(); j++) {
                    if (modelArrayList.get(i).getServiceName().equalsIgnoreCase(oldServices.get(j))) {
                        modelArrayList.get(i).setSelected(true);

                        String name = "checkbox" + (i + 1);
                        String empty_name = "checkbox_empty_" + (i + 1);
                        Log.e(TAG, "checkbox ========> " + empty_name);

                        Log.e(TAG, "checkbox ========> " + name);
                        if (modelArrayList.get(i).isSelected()) {
                            findViewById(getResourceID(name, "id", mContext)).setVisibility(View.GONE);
                            findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(getResourceID(name, "id", mContext)).setVisibility(View.VISIBLE);
                            findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < serviceItemArray.length; i++) {
                FireDetectionServiceModel model = new FireDetectionServiceModel(i, serviceItemArray[i], false, "",
                        "", "");
                modelArrayList.add(model);

                String empty_name = "checkbox_empty_" + (i + 1);
                Log.e(TAG, "checkbox ========> " + empty_name);
                findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.VISIBLE);

            }
        }

        /*adapter = new ServiceListAdapter(mContext, modelArrayList);
        list_view.setAdapter(adapter);*/
        bindAllEditText();
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
            case R.id.not_serviceable_btn:
                actualType = "non service";
                if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, "",
                            PERMISSION);
                } else {
                    showRemarkAlert(actualType);
                }
                break;
            case R.id.service_btn:
                actualType = "service";
                if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, "",
                            PERMISSION);
                } else {
                    count.clear();
                    services.clear();
                    locationList.clear();
                    qtyList.clear();
                    remarkList.clear();

                    for (int i = 0; i < modelArrayList.size(); i++) {
                        if (modelArrayList.get(i).isSelected()) {
                            count.add(modelArrayList.get(i).getId());
                            services.add(modelArrayList.get(i).getServiceName());

                            String location_name = "location_text" + (i + 1);
                            if (((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString().length() > 0) {
                                String location = ((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString();
                                modelArrayList.get(i).setLocation(location);
                            } else {
                                modelArrayList.get(i).setLocation("");
                            }

                            String qty_name = "qty_text" + (i + 1);
                            if (((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString().length() > 0) {
                                String qty = ((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString();
                                modelArrayList.get(i).setQty(qty);
                            } else {
                                modelArrayList.get(i).setQty("");
                            }

                            String remark_name = "remark_text" + (i + 1);
                            if (((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString().length() > 0) {
                                String remark = ((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString();
                                modelArrayList.get(i).setRemark(remark);
                            } else {
                                modelArrayList.get(i).setRemark("");
                            }

                            if (!modelArrayList.get(i).getLocation().equals("")) {
                                locationList.add(modelArrayList.get(i).getLocation());
                            }
                            if (!modelArrayList.get(i).getQty().equals("")) {
                                qtyList.add(modelArrayList.get(i).getQty());
                            }
                            if (!modelArrayList.get(i).getRemark().equals("")) {
                                remarkList.add(modelArrayList.get(i).getRemark());
                            }
                        }
                    }
                    if (count.size() > 0) {
                        if (!(count.size() == locationList.size())) {
                            runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service locations"));
                        } else if (!(count.size() == qtyList.size())) {
                            runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service required Qty(s)"));
                        } else if (!(count.size() == remarkList.size())) {
                            runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please fill all selected service remarks"));
                        } else {
                            showRemarkAlert(actualType);
                        }
                    } else {
                        runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please select service first"));
                    }
                }
                break;
            case R.id.ok_btn:
                actualType = "ok press";
                if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, "",
                            PERMISSION);
                } else {
                    showRemarkAlert(actualType);
                }
                break;
        }
    }

    private void apiCall() {
        count.clear();
        services.clear();
        locationList.clear();
        qtyList.clear();
        remarkList.clear();

        for (int i = 0; i < modelArrayList.size(); i++) {
            if (modelArrayList.get(i).isSelected()) {
                count.add(modelArrayList.get(i).getId());
                services.add(modelArrayList.get(i).getServiceName());

                String location_name = "location_text" + (i + 1);
                if (((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString().length() > 0) {
                    String location = ((TextInputEditText) findViewById(getResourceID(location_name, "id", mContext))).getText().toString();
                    modelArrayList.get(i).setLocation(location);
                } else {
                    modelArrayList.get(i).setLocation("");
                }

                String qty_name = "qty_text" + (i + 1);
                if (((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString().length() > 0) {
                    String qty = ((TextInputEditText) findViewById(getResourceID(qty_name, "id", mContext))).getText().toString();
                    modelArrayList.get(i).setQty(qty);
                } else {
                    modelArrayList.get(i).setQty("");
                }

                String remark_name = "remark_text" + (i + 1);
                if (((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString().length() > 0) {
                    String remark = ((TextInputEditText) findViewById(getResourceID(remark_name, "id", mContext))).getText().toString();
                    modelArrayList.get(i).setRemark(remark);
                } else {
                    modelArrayList.get(i).setRemark("");
                }

                if (!modelArrayList.get(i).getLocation().equals("")) {
                    locationList.add(modelArrayList.get(i).getLocation());
                }
                if (!modelArrayList.get(i).getQty().equals("")) {
                    qtyList.add(modelArrayList.get(i).getQty());
                }
                if (!modelArrayList.get(i).getRemark().equals("")) {
                    remarkList.add(modelArrayList.get(i).getRemark());
                }
            }
        }
        if (count.size() > 0) {
            StringBuilder sbString = new StringBuilder();
            for (String language : services) {
                sbString.append(language).append(",");
            }
            String strList = sbString.toString();
            if (strList.length() > 0)
                strList = strList.substring(0, strList.length() - 1);

            serviceDetail = strList;

            StringBuilder sbLocation = new StringBuilder();
            for (String language : locationList) {
                sbLocation.append(language).append(",");
            }
            String strLocation = sbLocation.toString();
            if (strLocation.length() > 0)
                strLocation = strLocation.substring(0, strLocation.length() - 1);

            serviceLocation = strLocation;

            StringBuilder sbQty = new StringBuilder();
            for (String language : qtyList) {
                sbQty.append(language).append(",");
            }
            String strQty = sbQty.toString();
            if (strQty.length() > 0)
                strQty = strQty.substring(0, strQty.length() - 1);

            serviceQty = strQty;

            StringBuilder sbRemark = new StringBuilder();
            for (String language : remarkList) {
                sbRemark.append(language).append(",");
            }
            String strRemark = sbRemark.toString();
            if (strRemark.length() > 0)
                strRemark = strRemark.substring(0, strRemark.length() - 1);

            serviceRemark = strRemark;


            //String servicesSeparated = String.join(",", services);
            askConfirmation("Are you sure, you want to update this spare parts?", "service");
        } else {
            runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please select service first"));
        }
    }

    private void askConfirmation(String message, String type) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage(message);
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (type.equals("service")) {
                    updateService();
                } else {
                    updateNonService();
                }
            }
        });
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void showAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Details submitted successfully.");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, QRCodeScannerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
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

    private void updateService() {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {

            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("file", panFile.getName(),
                    requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.userId)));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("comment", createPartFromString("Service"));

            map.put("serviceDetail", createPartFromString(serviceDetail));
            map.put("serviceLocation", createPartFromString(serviceLocation));
            map.put("serviceQty", createPartFromString(serviceQty));
            map.put("serviceRemark", createPartFromString(serviceRemark));
            map.put("remarks", createPartFromString(current_refill_text.getText().toString().trim()));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));

            call = apiInterface.updateFireDetectionWithImage(map, multipartBodyImage);
         /*else {
            call = apiInterface.updatePortableMonitorsService(Utility.getSharedPreferences(mContext, Constant.userId),
                    modelNo, "Yes", servicesSeparated, current_refill_text.getText().toString().trim());
        }*/
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            if (jsonObject.getBoolean(Constant.status)) {
                                JSONObject object = jsonObject.optJSONObject(Constant.object);
                                hideDialog();
                                showAlert();
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
        } else {
            Utility.ShowToastMessage(mContext, "Please select image");
        }
    }

    private void updateNonService() {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {

            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("file", panFile.getName(),
                    requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.userId)));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("remarks", createPartFromString(current_refill_text.getText().toString().trim()));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));
            if (actualType.equals("non service")) {
                map.put("comment", createPartFromString("Non Serviceable"));
            } else {
                map.put("comment", createPartFromString("All OK"));
            }
            call = apiInterface.updateFireDetectionWithImage(map, multipartBodyImage);
         /*else {
            if (actualType.equals("non service")) {
                call = apiInterface.updatePortableMonitorsNonService(
                        Utility.getSharedPreferences(mContext, Constant.userId), modelNo,
                        current_refill_text.getText().toString().trim(), "Non Serviceable");
            }  else {
                call = apiInterface.updatePortableMonitorsNonService(
                        Utility.getSharedPreferences(mContext, Constant.userId), modelNo,
                        current_refill_text.getText().toString().trim(), "All OK");
            }
        }*/
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            if (jsonObject.getBoolean(Constant.status)) {
                                JSONObject object = jsonObject.optJSONObject(Constant.object);
                                hideDialog();
                                showAlert();
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
        } else {
            Utility.ShowToastMessage(mContext, "Please select image");
        }
    }

    private void showRemarkAlert(String type) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        customLayout = getLayoutInflater().inflate(R.layout.dialog_remark_layout, null);
        mBuilder.setView(customLayout);

        select_image_layout = customLayout.findViewById(R.id.select_image_layout);
        current_refill_layout = customLayout.findViewById(R.id.current_refill_layout);
        current_refill_text = customLayout.findViewById(R.id.current_refill_text);
        image_view = customLayout.findViewById(R.id.image_view);

        mBuilder.setPositiveButton("OK", null);
        mBuilder.setNegativeButton("Cancel", null);
        remarkDialog = mBuilder.create();

        select_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        remarkDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btn_positive = remarkDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (current_refill_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please add remarks");
                        } else {
                            switch (type) {
                                case "ok press":
                                    askConfirmation("Are you sure, you want to submit report for All Okay?", "okay");
                                    break;
                                case "non service":
                                    askConfirmation("Are you sure, you want to submit report for non-serviceable?", "non-service");
                                    break;
                                case "service":
                                    apiCall();
                                    break;
                            }
                            remarkDialog.dismiss();
                        }
                    }
                });

                Button btn_negative = remarkDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        remarkDialog.dismiss();
                    }
                });
            }
        });
        remarkDialog.show();
    }

    public void SelectImage() {

        sdialog = new Dialog(mContext, R.style.MyDialog);
        sdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sdialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        sdialog.setContentView(R.layout.dialog_imgeselect);
        sdialog.setCancelable(true);
        sdialog.show();
        ((TextView) sdialog.findViewById(R.id.dialog_pack_name)).setText("Select Image");
        sdialog.findViewById(R.id.dialog_close).setOnClickListener(new MyDialogClick());
        sdialog.findViewById(R.id.camera).setOnClickListener(new MyDialogClick());
        sdialog.findViewById(R.id.gallery).setOnClickListener(new MyDialogClick());
        sdialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
    }

    private void beginCrop(Uri source) {
        Utility.createFolders();
        File pro = new File(Utility.MakeDir(Constant.MYFOLDER, mContext), System.currentTimeMillis() + Constant.SEND);
        Uri destination1 = Uri.fromFile(pro);
        Crop.of(source, destination1).withMaxSize(1920, 1080).start(this);
    }

    public Uri getPickImageResultUri(Intent data) {
        /*boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();*/

        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Utility.createFolders();

        /*Uri outputFileUri = null;
        String outputFileName = System.currentTimeMillis() + ".jpeg";
        //String filePath = Constant.MYFOLDER_DIR_NAME_SEPARATOR + outputFileName;
        File getImage = new File(Constant.MYFOLDER_DIR_NAME, outputFileName);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            outputFileUri = Uri.fromFile(getImage);
        } else {
            outputFileUri = FileProvider.getUriForFile(mContext, "com.fireextinguisher.provider", getImage);
        }
        return outputFileUri;*/

        Uri outputFileUri = null;
        File getImage = new File(Constant.MYFOLDER_DIR_NAME_SEPARATOR);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), System.currentTimeMillis() + ".jpeg"));
        } else {
            outputFileUri = FileProvider.getUriForFile(mContext, "com.fireextinguisher.provider",
                    new File(getImage.getPath(), System.currentTimeMillis() + ".jpeg"));
        }
        return outputFileUri;
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "picturePath =======>  " + picturePath);
            picturePath = Crop.getOutput(result).getPath();
            if (remarkDialog != null && remarkDialog.isShowing()) {
                Log.e(TAG, "remarkDialog =====> " + remarkDialog.isShowing());
            } else {
                Log.e(TAG, "remarkDialog =====> null");
            }
            if (image_view == null) {
                image_view = customLayout.findViewById(R.id.image_view);
            }
            image_view.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(picturePath).into(image_view);
        } else if (resultCode == Crop.RESULT_ERROR) {
            if (image_view == null) {
                image_view = customLayout.findViewById(R.id.image_view);
            }
            image_view.setVisibility(View.GONE);
            Utility.ShowToastMessage(mContext, Crop.getError(result).getMessage());
        }
    }

    private RequestBody createPartFromString(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (getPickImageResultUri(data) != null) {
                    outputFileUri = getPickImageResultUri(data);
                    try {
                        myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                        myBitmap = rotateImageIfRequired(myBitmap, outputFileUri);
                        //myBitmap = getResizedBitmap(myBitmap, 500);
                        uri = getImageUri(mContext, myBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    myBitmap = (Bitmap) data.getExtras().get("data");
                    uri = getImageUri(mContext, myBitmap);

                }

                beginCrop(uri);
            } else {
                Utility.ShowToastMessage(mContext, "Unable to get image please re-select image");
                Log.e(TAG, "result not OK");
            }
        } else if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Log.e(TAG, "data ======> " + data.getData());
                    Uri selectedImage = data.getData();
                    beginCrop(selectedImage);
                } else {
                    Utility.ShowToastMessage(mContext, "Unable to get image please re-select image");
                }
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                handleCrop(resultCode, data);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private class MyDialogClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.dialog_close) {
                if (sdialog.isShowing() && sdialog != null) {
                    sdialog.dismiss();
                }
            } else if (id == R.id.camera) {
                if (sdialog.isShowing() && sdialog != null) {
                    sdialog.dismiss();
                }
                //Uri outputFileUri = getCaptureImageOutputUri();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                intent.putExtra("return-data", true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, 11);
            } else if (id == R.id.gallery) {
                if (sdialog.isShowing() && sdialog != null) {
                    sdialog.dismiss();
                }
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, 12);
            }
        }
    }

    public void bindAllEditText() {
        location_text1 = findViewById(R.id.location_text1);
        location_text2 = findViewById(R.id.location_text2);
        location_text3 = findViewById(R.id.location_text3);
        location_text4 = findViewById(R.id.location_text4);
        location_text5 = findViewById(R.id.location_text5);
        location_text6 = findViewById(R.id.location_text6);
        location_text7 = findViewById(R.id.location_text7);
        location_text8 = findViewById(R.id.location_text8);
        location_text9 = findViewById(R.id.location_text9);
        location_text10 = findViewById(R.id.location_text10);
        location_text11 = findViewById(R.id.location_text11);
        location_text12 = findViewById(R.id.location_text12);
        location_text13 = findViewById(R.id.location_text13);
        location_text14 = findViewById(R.id.location_text14);
        location_text15 = findViewById(R.id.location_text15);
        location_text16 = findViewById(R.id.location_text16);
        location_text17 = findViewById(R.id.location_text17);
        location_text18 = findViewById(R.id.location_text18);
        location_text19 = findViewById(R.id.location_text19);
        location_text20 = findViewById(R.id.location_text20);
        location_text21 = findViewById(R.id.location_text21);
        location_text22 = findViewById(R.id.location_text22);

        qty_text1 = findViewById(R.id.qty_text1);
        qty_text2 = findViewById(R.id.qty_text2);
        qty_text3 = findViewById(R.id.qty_text3);
        qty_text4 = findViewById(R.id.qty_text4);
        qty_text5 = findViewById(R.id.qty_text5);
        qty_text6 = findViewById(R.id.qty_text6);
        qty_text7 = findViewById(R.id.qty_text7);
        qty_text8 = findViewById(R.id.qty_text8);
        qty_text9 = findViewById(R.id.qty_text9);
        qty_text10 = findViewById(R.id.qty_text10);
        qty_text11 = findViewById(R.id.qty_text11);
        qty_text12 = findViewById(R.id.qty_text12);
        qty_text13 = findViewById(R.id.qty_text13);
        qty_text14 = findViewById(R.id.qty_text14);
        qty_text15 = findViewById(R.id.qty_text15);
        qty_text16 = findViewById(R.id.qty_text16);
        qty_text17 = findViewById(R.id.qty_text17);
        qty_text18 = findViewById(R.id.qty_text18);
        qty_text19 = findViewById(R.id.qty_text19);
        qty_text20 = findViewById(R.id.qty_text20);
        qty_text21 = findViewById(R.id.qty_text21);
        qty_text22 = findViewById(R.id.qty_text22);

        remark_text1 = findViewById(R.id.remark_text1);
        remark_text2 = findViewById(R.id.remark_text2);
        remark_text3 = findViewById(R.id.remark_text3);
        remark_text4 = findViewById(R.id.remark_text4);
        remark_text5 = findViewById(R.id.remark_text5);
        remark_text6 = findViewById(R.id.remark_text6);
        remark_text7 = findViewById(R.id.remark_text7);
        remark_text8 = findViewById(R.id.remark_text8);
        remark_text9 = findViewById(R.id.remark_text9);
        remark_text10 = findViewById(R.id.remark_text10);
        remark_text11 = findViewById(R.id.remark_text11);
        remark_text12 = findViewById(R.id.remark_text12);
        remark_text13 = findViewById(R.id.remark_text13);
        remark_text14 = findViewById(R.id.remark_text14);
        remark_text15 = findViewById(R.id.remark_text15);
        remark_text16 = findViewById(R.id.remark_text16);
        remark_text17 = findViewById(R.id.remark_text17);
        remark_text18 = findViewById(R.id.remark_text18);
        remark_text19 = findViewById(R.id.remark_text19);
        remark_text20 = findViewById(R.id.remark_text20);
        remark_text21 = findViewById(R.id.remark_text21);
        remark_text22 = findViewById(R.id.remark_text22);

        service_name1 = findViewById(R.id.service_name1);
        service_name2 = findViewById(R.id.service_name2);
        service_name3 = findViewById(R.id.service_name3);
        service_name4 = findViewById(R.id.service_name4);
        service_name5 = findViewById(R.id.service_name5);
        service_name6 = findViewById(R.id.service_name6);
        service_name7 = findViewById(R.id.service_name7);
        service_name8 = findViewById(R.id.service_name8);
        service_name9 = findViewById(R.id.service_name9);
        service_name10 = findViewById(R.id.service_name10);
        service_name11 = findViewById(R.id.service_name11);
        service_name12 = findViewById(R.id.service_name12);
        service_name13 = findViewById(R.id.service_name13);
        service_name14 = findViewById(R.id.service_name14);
        service_name15 = findViewById(R.id.service_name15);
        service_name16 = findViewById(R.id.service_name16);
        service_name17 = findViewById(R.id.service_name17);
        service_name18 = findViewById(R.id.service_name18);
        service_name19 = findViewById(R.id.service_name19);
        service_name20 = findViewById(R.id.service_name20);
        service_name21 = findViewById(R.id.service_name21);
        service_name22 = findViewById(R.id.service_name22);

        for (int i = 0; i < modelArrayList.size(); i++) {
            String name = "service_name" + (i + 1);
            ((TextView) findViewById(getResourceID(name, "id", mContext))).setText(modelArrayList.get(i).getServiceName());
        }

        service_name1.setOnClickListener(new ListClick(0));
        service_name2.setOnClickListener(new ListClick(1));
        service_name3.setOnClickListener(new ListClick(2));
        service_name4.setOnClickListener(new ListClick(3));
        service_name5.setOnClickListener(new ListClick(4));
        service_name6.setOnClickListener(new ListClick(5));
        service_name7.setOnClickListener(new ListClick(6));
        service_name8.setOnClickListener(new ListClick(7));
        service_name9.setOnClickListener(new ListClick(8));
        service_name10.setOnClickListener(new ListClick(9));
        service_name11.setOnClickListener(new ListClick(10));
        service_name12.setOnClickListener(new ListClick(11));
        service_name13.setOnClickListener(new ListClick(12));
        service_name14.setOnClickListener(new ListClick(13));
        service_name15.setOnClickListener(new ListClick(14));
        service_name16.setOnClickListener(new ListClick(15));
        service_name17.setOnClickListener(new ListClick(16));
        service_name18.setOnClickListener(new ListClick(17));
        service_name19.setOnClickListener(new ListClick(18));
        service_name20.setOnClickListener(new ListClick(19));
        service_name21.setOnClickListener(new ListClick(20));
        service_name22.setOnClickListener(new ListClick(21));


        ((ImageView)findViewById(R.id.checkbox1)).setOnClickListener(new ListClick(0));
        ((ImageView)findViewById(R.id.checkbox2)).setOnClickListener(new ListClick(1));
        ((ImageView)findViewById(R.id.checkbox3)).setOnClickListener(new ListClick(2));
        ((ImageView)findViewById(R.id.checkbox4)).setOnClickListener(new ListClick(3));
        ((ImageView)findViewById(R.id.checkbox5)).setOnClickListener(new ListClick(4));
        ((ImageView)findViewById(R.id.checkbox6)).setOnClickListener(new ListClick(5));
        ((ImageView)findViewById(R.id.checkbox7)).setOnClickListener(new ListClick(6));
        ((ImageView)findViewById(R.id.checkbox8)).setOnClickListener(new ListClick(7));
        ((ImageView)findViewById(R.id.checkbox9)).setOnClickListener(new ListClick(8));
        ((ImageView)findViewById(R.id.checkbox10)).setOnClickListener(new ListClick(9));
        ((ImageView)findViewById(R.id.checkbox11)).setOnClickListener(new ListClick(10));
        ((ImageView)findViewById(R.id.checkbox12)).setOnClickListener(new ListClick(11));
        ((ImageView)findViewById(R.id.checkbox13)).setOnClickListener(new ListClick(12));
        ((ImageView)findViewById(R.id.checkbox14)).setOnClickListener(new ListClick(13));
        ((ImageView)findViewById(R.id.checkbox15)).setOnClickListener(new ListClick(14));
        ((ImageView)findViewById(R.id.checkbox16)).setOnClickListener(new ListClick(15));
        ((ImageView)findViewById(R.id.checkbox17)).setOnClickListener(new ListClick(16));
        ((ImageView)findViewById(R.id.checkbox18)).setOnClickListener(new ListClick(17));
        ((ImageView)findViewById(R.id.checkbox19)).setOnClickListener(new ListClick(18));
        ((ImageView)findViewById(R.id.checkbox20)).setOnClickListener(new ListClick(19));
        ((ImageView)findViewById(R.id.checkbox21)).setOnClickListener(new ListClick(20));
        ((ImageView)findViewById(R.id.checkbox22)).setOnClickListener(new ListClick(21));

        ((ImageView)findViewById(R.id.checkbox_empty_1)).setOnClickListener(new ListClick(0));
        ((ImageView)findViewById(R.id.checkbox_empty_2)).setOnClickListener(new ListClick(1));
        ((ImageView)findViewById(R.id.checkbox_empty_3)).setOnClickListener(new ListClick(2));
        ((ImageView)findViewById(R.id.checkbox_empty_4)).setOnClickListener(new ListClick(3));
        ((ImageView)findViewById(R.id.checkbox_empty_5)).setOnClickListener(new ListClick(4));
        ((ImageView)findViewById(R.id.checkbox_empty_6)).setOnClickListener(new ListClick(5));
        ((ImageView)findViewById(R.id.checkbox_empty_7)).setOnClickListener(new ListClick(6));
        ((ImageView)findViewById(R.id.checkbox_empty_8)).setOnClickListener(new ListClick(7));
        ((ImageView)findViewById(R.id.checkbox_empty_9)).setOnClickListener(new ListClick(8));
        ((ImageView)findViewById(R.id.checkbox_empty_10)).setOnClickListener(new ListClick(9));
        ((ImageView)findViewById(R.id.checkbox_empty_11)).setOnClickListener(new ListClick(10));
        ((ImageView)findViewById(R.id.checkbox_empty_12)).setOnClickListener(new ListClick(11));
        ((ImageView)findViewById(R.id.checkbox_empty_13)).setOnClickListener(new ListClick(12));
        ((ImageView)findViewById(R.id.checkbox_empty_14)).setOnClickListener(new ListClick(13));
        ((ImageView)findViewById(R.id.checkbox_empty_15)).setOnClickListener(new ListClick(14));
        ((ImageView)findViewById(R.id.checkbox_empty_16)).setOnClickListener(new ListClick(15));
        ((ImageView)findViewById(R.id.checkbox_empty_17)).setOnClickListener(new ListClick(16));
        ((ImageView)findViewById(R.id.checkbox_empty_18)).setOnClickListener(new ListClick(17));
        ((ImageView)findViewById(R.id.checkbox_empty_19)).setOnClickListener(new ListClick(18));
        ((ImageView)findViewById(R.id.checkbox_empty_20)).setOnClickListener(new ListClick(19));
        ((ImageView)findViewById(R.id.checkbox_empty_21)).setOnClickListener(new ListClick(20));
        ((ImageView)findViewById(R.id.checkbox_empty_22)).setOnClickListener(new ListClick(21));

    }

    private class ListClick implements View.OnClickListener {

        int pos;

        public ListClick(int position) {
            pos = position;
        }

        @Override
        public void onClick(View v) {
            String name = "checkbox" + (pos + 1);
            String empty_name = "checkbox_empty_" + (pos + 1);
            Log.e(TAG, "checkbox ========> " + name);
            if (modelArrayList.get(pos).isSelected()) {
                modelArrayList.get(pos).setSelected(false);
                findViewById(getResourceID(name, "id", mContext)).setVisibility(View.GONE);
                findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.VISIBLE);
            } else {
                modelArrayList.get(pos).setSelected(true);
                findViewById(getResourceID(name, "id", mContext)).setVisibility(View.VISIBLE);
                findViewById(getResourceID(empty_name, "id", mContext)).setVisibility(View.GONE);
            }
        }
    }

}

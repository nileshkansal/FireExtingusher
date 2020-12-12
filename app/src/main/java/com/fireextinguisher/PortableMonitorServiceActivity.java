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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
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
import com.fireextinguisher.model.ServiceModel;
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

public class PortableMonitorServiceActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final String TAG = PortableMonitorServiceActivity.class.getSimpleName();
    private static final int PERMISSION = 1010;
    Context mContext;
    Button service_btn, not_serviceable_btn, ok_btn;
    Toolbar toolbar;
    ListView list_view;
    ArrayList<ServiceModel> modelArrayList;
    ServiceListAdapter adapter;
    String PumpType;
    ArrayList<Integer> count = new ArrayList<>();
    ArrayList<String> services = new ArrayList<>();
    List<String> oldServices = new ArrayList<>();
    ProgressDialog dialog;
    String modelNo = "", spare_part_item_label = "";
    String servicesSeparated = "", picturePath = "";
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Portable Monitor Service");

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
            showRemarkAlert(actualType);
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
        String[] serviceItemArray = mContext.getResources().getStringArray(R.array.portable_monitors_service);

        if (oldServices.size() > 0) {
            for (int i = 0; i < serviceItemArray.length; i++) {
                ServiceModel model = new ServiceModel(i, serviceItemArray[i], false);
                modelArrayList.add(model);
            }

            for (int i = 0; i < modelArrayList.size(); i++) {
                for (int j = 0; j < oldServices.size(); j++) {
                    if (modelArrayList.get(i).getServiceName().equalsIgnoreCase(oldServices.get(j))) {
                        modelArrayList.get(i).setSelected(true);
                    }
                }
            }
        } else {
            for (int i = 0; i < serviceItemArray.length; i++) {
                ServiceModel model = new ServiceModel(i, serviceItemArray[i], false);
                modelArrayList.add(model);
            }
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
                    showRemarkAlert(actualType);
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
        for (int i = 0; i < modelArrayList.size(); i++) {
            if (modelArrayList.get(i).isSelected()) {
                count.add(modelArrayList.get(i).getId());
                services.add(modelArrayList.get(i).getServiceName());
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

            servicesSeparated = strList;
            //String servicesSeparated = String.join(",", services);
            askConfirmation("Are you sure, you want to update this spare parts?", "service");
        } else {
            runOnUiThread(() -> Utility.ShowToastMessage(mContext, "Please select spare parts first"));
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
            map.put("sparePartRequired", createPartFromString("Yes"));
            map.put("sparePartItemRequired", createPartFromString(servicesSeparated));
            map.put("remarks", createPartFromString(current_refill_text.getText().toString().trim()));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));
            map.put("comment", createPartFromString("Service"));

            call = apiInterface.updatePortableMonitorsWithImage(map, multipartBodyImage);
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
            call = apiInterface.updatePortableMonitorsWithImage(map, multipartBodyImage);
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
            if (remarkDialog != null  && remarkDialog.isShowing()){
                Log.e(TAG, "remarkDialog =====> "+ remarkDialog.isShowing());
            } else  {
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

    public class ServiceListAdapter extends BaseAdapter {

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
                convertView = inflater.inflate(R.layout.service_list_item, parent, false);
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
            viewHolder.select_layout.setOnClickListener(new CheckClick(position));
            return convertView;
        }

        class ViewHolder {

            TextView service_name;
            RelativeLayout select_layout;
            ImageView checkbox;

            ViewHolder(View view) {
                service_name = view.findViewById(R.id.service_name);
                select_layout = view.findViewById(R.id.select_layout);
                checkbox = view.findViewById(R.id.checkbox);
            }
        }

        class CheckClick implements View.OnClickListener {

            int pos;

            private CheckClick(int position) {
                pos = position;
            }

            @Override
            public void onClick(View v) {
                //int id = arrayList.get(pos).getId();

                if (modelArrayList.get(pos).isSelected()) {
                    modelArrayList.get(pos).setSelected(false);
                    arrayList.get(pos).setSelected(false);
                } else {
                    modelArrayList.get(pos).setSelected(true);
                    arrayList.get(pos).setSelected(true);
                }
                notifyDataSetChanged();
            }
        }
    }
}
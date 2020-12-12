package com.fireextinguisher.client;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.fireextinguisher.R;
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

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientFireBucketActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final String TAG = ClientFireBucketActivity.class.getSimpleName();
    private static final int PERMISSION = 1010;
    Context mContext;
    Toolbar toolbar;
    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", observation_label = "", action_label = "", number_of_fire_bucket_label = "", bucket_label = "",
            stand_label = "", sand_label = "";

    TextView remarks, spare_part, location, spare_part_selection, client_name, observation, action, number_of_fire_bucket,
            bucket, stand, sand;

    LinearLayout spare_part_selection_layout;
    Button remarkBtn;
    ProgressDialog dialog;
    String picturePath = "", empId = "";
    AlertDialog remarkDialog;
    RelativeLayout select_image_layout;
    TextInputLayout current_refill_layout;
    TextInputEditText current_refill_text;
    ImageView image_view;
    Dialog sdialog;
    Uri outputFileUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_fire_bucket);

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
        empId = getIntent().getStringExtra(Constant.empid);
        location_label = getIntent().getStringExtra(Constant.FireBucket.location);
        observation_label = getIntent().getStringExtra(Constant.FireBucket.observation);
        action_label = getIntent().getStringExtra(Constant.FireBucket.action);

        number_of_fire_bucket_label = getIntent().getStringExtra(Constant.FireBucket.number_of_fire_bucket);
        bucket_label = getIntent().getStringExtra(Constant.FireBucket.buckets);
        stand_label = getIntent().getStringExtra(Constant.FireBucket.stand);
        sand_label = getIntent().getStringExtra(Constant.FireBucket.sand);

        spare_part_label = getIntent().getStringExtra(Constant.FireBucket.spare_part_label);

        if (getIntent().getStringExtra(Constant.FireBucket.remarks) != null &&
                !getIntent().getStringExtra(Constant.FireBucket.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.FireBucket.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.FireBucket.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.FireBucket.clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.FireBucket.spare_part_item_label);
        }

        bind();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (PERMISSION == requestCode) {
            showRemarkAlert();
        }
    }

    @Override
    public void onPermissionDenial(int requestCode) {

    }

    private void bind() {
        remarkBtn = findViewById(R.id.remarkBtn);
        remarkBtn.setOnClickListener(this);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);
        number_of_fire_bucket = findViewById(R.id.number_of_fire_bucket);
        bucket = findViewById(R.id.bucket);
        stand = findViewById(R.id.stand);
        sand = findViewById(R.id.sand);

        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);
        observation.setText(observation_label);
        action.setText(action_label);

        number_of_fire_bucket.setText(number_of_fire_bucket_label);
        bucket.setText(bucket_label);
        stand.setText(stand_label);
        sand.setText(sand_label);


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
        if (v.getId() == R.id.remarkBtn) {
            if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, "",
                        PERMISSION);
            } else {
                showRemarkAlert();
            }
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
                /*Intent intent = new Intent(mContext, QRCodeScannerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
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

    private void showRemarkAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_remark_layout, null);
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
                            Utility.hideKeyBoard(current_refill_text, mContext);
                            updateClientRemark();
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

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        Utility.createFolders();

        String outputFileName = System.currentTimeMillis() + ".jpeg";
        //String filePath = Constant.MYFOLDER_DIR_NAME_SEPARATOR + outputFileName;

        File getImage = new File(Constant.MYFOLDER_DIR_NAME, outputFileName);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            outputFileUri = Uri.fromFile(getImage);
        } else {
            outputFileUri = FileProvider.getUriForFile(mContext, "com.fireextinguisher.provider", getImage);
        }
        return outputFileUri;
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {

            Log.e(TAG, "picturePath =======>  " + picturePath);
            picturePath = Crop.getOutput(result).getPath();
            image_view.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(picturePath).into(image_view);
        } else if (resultCode == Crop.RESULT_ERROR) {
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
                beginCrop(outputFileUri);
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

    private void updateClientRemark() {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {
            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("clientFile", panFile.getName(), requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(empId));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("clientId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.clientId)));
            map.put("clientComment", createPartFromString(current_refill_text.getText().toString().trim()));

            call = apiInterface.clientUpdateFireBucketWithImage(map, multipartBodyImage);
        /*}  else {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(empId));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("clientId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.clientId)));
            map.put("clientComment", createPartFromString(current_refill_text.getText().toString().trim()));

            call = apiInterface.clientUpdateFireBucket(map);
        }*/

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            if (jsonObject.getBoolean(Constant.status)) {
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

    private class MyDialogClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_close:
                    if (sdialog.isShowing() && sdialog != null) {
                        sdialog.dismiss();
                    }
                    break;
                case R.id.camera:
                    if (sdialog.isShowing() && sdialog != null) {
                        sdialog.dismiss();
                    }
                    outputFileUri = getCaptureImageOutputUri();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, 11);
                    break;
                case R.id.gallery:
                    if (sdialog.isShowing() && sdialog != null) {
                        sdialog.dismiss();
                    }
                    Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent1, 12);
                    break;
            }
        }
    }

}
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

public class ClientControlValveActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final String TAG = ClientControlValveActivity.class.getSimpleName();
    private static final int PERMISSION = 1010;
    Context mContext;
    Toolbar toolbar;

    String modelNo = "", productId = "", location_label = "", spare_part_label = "", remarks_label = "", spare_part_item_label = "",
            clientName = "", type_of_valve_label = "", moc_label = "", size_label = "", spindle_label = "", wheel_lever_label = "",
            gasket_label = "", gland_packing_label = "", drain_valve_label = "", pressure_gauge_label = "", pressure_label = "",
            flow_label = "", test_valve_label = "", soleniod_valve_actuator_label = "", internal_disc_flap_label = "",
            gong_bell_label = "";

    TextView spare_part, spare_part_selection, remarks, client_name, location, type_of_valve, moc, size, spindle, wheel_lever, gasket,
            gland_packing, drain_valve, pressure_gauge, pressure, flow, test_valve, soleniod_valve_actuator, internal_disc_flap,
            gong_bell;

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
        setContentView(R.layout.activity_client_control_valve);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.product_info));

        type_of_valve_label = getIntent().getStringExtra(Constant.ControlValve.typeOfValve);
        moc_label = getIntent().getStringExtra(Constant.ControlValve.moc);
        size_label = getIntent().getStringExtra(Constant.ControlValve.size);
        spindle_label = getIntent().getStringExtra(Constant.ControlValve.spindle);
        wheel_lever_label = getIntent().getStringExtra(Constant.ControlValve.wheelLever);
        gasket_label = getIntent().getStringExtra(Constant.ControlValve.gasket);
        gland_packing_label = getIntent().getStringExtra(Constant.ControlValve.glandPacking);
        drain_valve_label = getIntent().getStringExtra(Constant.ControlValve.drainValve);

        pressure_gauge_label = getIntent().getStringExtra(Constant.ControlValve.pressureGuage);
        pressure_label = getIntent().getStringExtra(Constant.ControlValve.pressure);
        flow_label = getIntent().getStringExtra(Constant.ControlValve.flow);
        test_valve_label = getIntent().getStringExtra(Constant.ControlValve.testValve);
        soleniod_valve_actuator_label = getIntent().getStringExtra(Constant.ControlValve.soleniodValveActuator);
        internal_disc_flap_label = getIntent().getStringExtra(Constant.ControlValve.internalDiscFlap);
        gong_bell_label = getIntent().getStringExtra(Constant.ControlValve.gongBell);


        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.ControlValve.location);
        clientName = getIntent().getStringExtra(Constant.ControlValve.clientName);

        spare_part_label = getIntent().getStringExtra(Constant.ControlValve.spare_part_label);

        if (getIntent().getStringExtra(Constant.ControlValve.remarks) != null &&
                !getIntent().getStringExtra(Constant.ControlValve.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.ControlValve.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.ControlValve.remarks);
        } else {
            remarks_label = "";
        }

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.ControlValve.spare_part_item_label);
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

        type_of_valve = findViewById(R.id.type_of_valve);
        moc = findViewById(R.id.moc);
        size = findViewById(R.id.size);
        spindle = findViewById(R.id.spindle);
        wheel_lever = findViewById(R.id.wheel_lever);
        gasket = findViewById(R.id.gasket);
        gland_packing = findViewById(R.id.gland_packing);
        drain_valve = findViewById(R.id.drain_valve);

        pressure_gauge = findViewById(R.id.pressure_gauge);
        pressure = findViewById(R.id.pressure);
        flow = findViewById(R.id.flow);
        test_valve = findViewById(R.id.test_valve);
        soleniod_valve_actuator = findViewById(R.id.soleniod_valve_actuator);
        internal_disc_flap = findViewById(R.id.internal_disc_flap);
        gong_bell = findViewById(R.id.gong_bell);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks.setText(remarks_label);
        spare_part.setText(spare_part_label);
        location.setText(location_label);
        client_name.setText(clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_selection.setText(spare_part_item_label);
            spare_part_selection_layout.setVisibility(View.VISIBLE);
        } else {
            spare_part_selection_layout.setVisibility(View.GONE);
        }

        type_of_valve.setText(type_of_valve_label);
        moc.setText(moc_label);
        size.setText(size_label);
        spindle.setText(spindle_label);
        wheel_lever.setText(wheel_lever_label);
        gasket.setText(gasket_label);
        gland_packing.setText(gland_packing_label);
        drain_valve.setText(drain_valve_label);

        pressure_gauge.setText(pressure_gauge_label);
        pressure.setText(pressure_label);
        flow.setText(flow_label);
        test_valve.setText(test_valve_label);
        soleniod_valve_actuator.setText(soleniod_valve_actuator_label);
        internal_disc_flap.setText(internal_disc_flap_label);
        gong_bell.setText(gong_bell_label);
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
                /*Intent intent = new Intent(mContext, ClientQRCodeScanActivity.class);
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
        if (picturePath != null &&  !picturePath.equals("")) {
            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("clientFile", panFile.getName(), requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(empId));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("clientId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.clientId)));
            map.put("clientComment", createPartFromString(current_refill_text.getText().toString().trim()));

            call = apiInterface.updateControlValveWithImage(map, multipartBodyImage);
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

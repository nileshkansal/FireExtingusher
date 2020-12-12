package com.fireextinguisher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.fireextinguisher.scanner.camera.CameraManager;
import com.fireextinguisher.scanner.decode.CaptureActivityHandler;
import com.fireextinguisher.scanner.decode.DecodeManager;
import com.fireextinguisher.scanner.decode.InactivityTimer;
import com.fireextinguisher.scanner.view.QrCodeFinderView;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRCodeScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;
    private static final String TAG = QRCodeScannerActivity.class.getSimpleName();
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private static final int REQUEST_SYSTEM_PICTURE = 0;
    private static final int REQUEST_PICTURE = 1004;
    /* location functions */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /* location functions */

    private final DecodeManager mDecodeManager = new DecodeManager();
    private final String GOT_RESULT = "got_qr_scan_result";
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    Context mContext;
    Toolbar toolbar;
    EditText model_edit_text;
    Button scan_btn;
    ProgressDialog dialog;
    String clientName = "";
    int clientId = 0;
    /* location functions */
    String modelNo;
    private ImageView mIvFlashLight;
    private QrCodeFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private View mLlFlashLight;
    private boolean mHasSurface;
    private InactivityTimer mInactivityTimer;
    private CaptureActivityHandler mCaptureActivityHandler;
    private Executor mQrCodeExecutor;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;
    /* location functions */
    private Double longitude = 0.0, latitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.scan_product));

        model_edit_text = findViewById(R.id.model_edit_text);
        scan_btn = findViewById(R.id.scan_btn);

        clientName = getIntent().getStringExtra(Constant.clientName);
        clientId = getIntent().getIntExtra(Constant.clientId, 0);

        scan_btn.setOnClickListener(v -> {
            if (model_edit_text.getText().toString().trim().length() <= 0) {
                Utility.ShowToastMessage(mContext, "Please enter model number");
            } else if (latitude == 0.0 && longitude == 0.0) {
                startLocationUpdates();
                Utility.ShowToastMessage(mContext, "Location not found please enable GPS Location");
            } else {
                checkModelNo();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mSettingsClient = LocationServices.getSettingsClient(mContext);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
        initData();
    }

    @Override
    public void onBackPressed() {
        Utility.setSharedPreference(mContext, Constant.clientId, null);
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

    private void checkModelNo(String modelNo) {
        showLoading();

        Log.e(TAG, "clientId  =========> " + Utility.getSharedPreferences(mContext, Constant.clientId));

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getModelNo(modelNo, Utility.getSharedPreferences(mContext, Constant.clientId));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);
                            QRCodeScannerActivity.this.modelNo = object.getString("modelNo");
                            if (latitude > 0 && longitude > 0) {
                                updateTrackingStatus(object.getString("modelNo"));
                            }
                            if (object.optString("pId").equals("1")) {
                                checkProductNo(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("2")) {
                                checkFireHydrant(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("3")) {
                                checkHoseReel(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("4")) {
                                checkFireBucket(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("5")) {
                                checkFirePump(object.getString("modelNo"));
                            } else if (object.getString("pId").equals("6")) {
                                checkFireDetectionPanel(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("7")) {
                                checkPortableMonitor(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("8")) {
                                checkSuppressionSystem(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("9")) {
                                checkControlValve(object.getString("modelNo"));
                            }
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

    private void checkModelNo() {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getModelNo(model_edit_text.getText().toString().trim(),
                Utility.getSharedPreferences(mContext, Constant.clientId));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);
                            QRCodeScannerActivity.this.modelNo = object.getString("modelNo");
                            if (latitude > 0 && longitude > 0) {
                                updateTrackingStatus(object.getString("modelNo"));
                            }
                            if (object.optString("pId").equals("1")) {
                                checkProductNo(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("2")) {
                                checkFireHydrant(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("3")) {
                                checkHoseReel(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("4")) {
                                checkFireBucket(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("5")) {
                                checkFirePump(object.getString("modelNo"));
                            } else if (object.getString("pId").equals("6")) {
                                checkFireDetectionPanel(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("7")) {
                                checkPortableMonitor(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("8")) {
                                checkSuppressionSystem(object.getString("modelNo"));
                            } else if (object.optString("pId").equals("9")) {
                                checkControlValve(object.getString("modelNo"));
                            }
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

    private void updateTrackingStatus(String modelNo) {
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.updateTrackingStatus(modelNo, "REACH",
                Utility.getSharedPreferences(mContext, Constant.userId), String.valueOf(latitude), String.valueOf(longitude));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            JSONObject object = jsonObject.optJSONObject(Constant.object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Utility.ShowToastMessage(mContext, R.string.server_not_responding);
                Log.e(TAG, "t =======> " + t.getLocalizedMessage());
            }
        });
    }

    private void checkProductNo(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProductByModel(modelNo, "1");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, InstalledFromActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.ProductInfo.location, object.optString(Constant.ProductInfo.location));
                            intent.putExtra(Constant.ProductInfo.f_e_no, object.optString(Constant.ProductInfo.f_e_no));
                            intent.putExtra(Constant.ProductInfo.fe_type_label, object.optString(Constant.ProductInfo.fe_type_label));
                            intent.putExtra(Constant.ProductInfo.capacity_label, object.optString(Constant.ProductInfo.capacity_label));
                            intent.putExtra(Constant.ProductInfo.mfg_year_label, object.optString(Constant.ProductInfo.mfg_year_label));
                            intent.putExtra(Constant.ProductInfo.empty_cylinder_pressure, object.optString(Constant.ProductInfo.empty_cylinder_pressure));
                            intent.putExtra(Constant.ProductInfo.full_cylinder_pressure, object.optString(Constant.ProductInfo.full_cylinder_pressure));
                            intent.putExtra(Constant.ProductInfo.net_cylinder_pressure, object.optString(Constant.ProductInfo.net_cylinder_pressure));
                            intent.putExtra(Constant.ProductInfo.last_date_refill_label, object.optString(Constant.ProductInfo.last_date_refill_label));
                            intent.putExtra(Constant.ProductInfo.due_date_refill_label, object.optString(Constant.ProductInfo.due_date_refill_label));
                            intent.putExtra(Constant.ProductInfo.last_date_hpt_label, object.optString(Constant.ProductInfo.last_date_hpt_label));
                            intent.putExtra(Constant.ProductInfo.due_date_hpt_label, object.optString(Constant.ProductInfo.due_date_hpt_label));
                            intent.putExtra(Constant.ProductInfo.spare_part_label, object.optString(Constant.ProductInfo.spare_part_label));
                            if (!object.optString(Constant.ProductInfo.remarks).equals("")) {
                                intent.putExtra(Constant.ProductInfo.remarks, object.optString(Constant.ProductInfo.remarks));
                            }
                            intent.putExtra(Constant.ProductInfo.clientName, object.optString(Constant.ProductInfo.clientName));
                            intent.putExtra(Constant.ProductInfo.spare_part_item_label, object.optString(Constant.ProductInfo.spare_part_item_label));

                            startActivity(intent);
                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                                /*Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.putExtra(Constant.modelNo, model_edit_text.getText().toString().trim());
                                startActivity(intent);*/
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

    private void checkFireHydrant(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFireHydrant(modelNo, "2");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, FireHydrantInstalledActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.FireHydrantInfo.location, object.optString(Constant.FireHydrantInfo.location));

                            intent.putExtra(Constant.FireHydrantInfo.hose_pipe, object.optString(Constant.FireHydrantInfo.hose_pipe));
                            intent.putExtra(Constant.FireHydrantInfo.hydrant_valve, object.optString(Constant.FireHydrantInfo.hydrant_valve));
                            intent.putExtra(Constant.FireHydrantInfo.black_cap, object.optString(Constant.FireHydrantInfo.black_cap));
                            intent.putExtra(Constant.FireHydrantInfo.shunt_wheel, object.optString(Constant.FireHydrantInfo.shunt_wheel));
                            intent.putExtra(Constant.FireHydrantInfo.hose_box, object.optString(Constant.FireHydrantInfo.hose_box));
                            intent.putExtra(Constant.FireHydrantInfo.hoses, object.optString(Constant.FireHydrantInfo.hoses));
                            intent.putExtra(Constant.FireHydrantInfo.glasses, object.optString(Constant.FireHydrantInfo.glasses));
                            intent.putExtra(Constant.FireHydrantInfo.branch_pipe, object.optString(Constant.FireHydrantInfo.branch_pipe));
                            intent.putExtra(Constant.FireHydrantInfo.keys, object.optString(Constant.FireHydrantInfo.keys));
                            intent.putExtra(Constant.FireHydrantInfo.glass_hammer, object.optString(Constant.FireHydrantInfo.glass_hammer));
                            intent.putExtra(Constant.FireHydrantInfo.observation, object.optString(Constant.FireHydrantInfo.observation));
                            intent.putExtra(Constant.FireHydrantInfo.action, object.optString(Constant.FireHydrantInfo.action));

                            intent.putExtra(Constant.FireHydrantInfo.spare_part_label, object.optString(Constant.FireHydrantInfo.spare_part_label));
                            if (!object.optString(Constant.FireHydrantInfo.remarks).equals("")) {
                                intent.putExtra(Constant.FireHydrantInfo.remarks, object.optString(Constant.FireHydrantInfo.remarks));
                            }
                            intent.putExtra(Constant.FireHydrantInfo.clientName, object.optString(Constant.FireHydrantInfo.clientName));
                            intent.putExtra(Constant.FireHydrantInfo.spare_part_item_label, object.optString(Constant.FireHydrantInfo.spare_part_item_label));

                            startActivity(intent);

                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                                /*Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.putExtra(Constant.modelNo, model_edit_text.getText().toString().trim());
                                startActivity(intent);*/
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

    private void checkHoseReel(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getHoseReel(modelNo, "3");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, HoseReelInstalledActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.HoseReelInfo.location, object.optString(Constant.HoseReelInfo.location));

                            intent.putExtra(Constant.HoseReelInfo.observation, object.optString(Constant.HoseReelInfo.observation));
                            intent.putExtra(Constant.HoseReelInfo.action, object.optString(Constant.HoseReelInfo.action));

                            intent.putExtra(Constant.HoseReelInfo.hose_reel, object.optString(Constant.HoseReelInfo.hose_reel));
                            intent.putExtra(Constant.HoseReelInfo.shut_off_nozzel, object.optString(Constant.HoseReelInfo.shut_off_nozzel));
                            intent.putExtra(Constant.HoseReelInfo.ball_valve, object.optString(Constant.HoseReelInfo.ball_valve));
                            intent.putExtra(Constant.HoseReelInfo.jubli_clip, object.optString(Constant.HoseReelInfo.jubli_clip));
                            intent.putExtra(Constant.HoseReelInfo.conecting_ruber_hose, object.optString(Constant.HoseReelInfo.conecting_ruber_hose));

                            intent.putExtra(Constant.HoseReelInfo.spare_part_label, object.optString(Constant.HoseReelInfo.spare_part_label));
                            if (!object.optString(Constant.HoseReelInfo.remarks).equals("")) {
                                intent.putExtra(Constant.HoseReelInfo.remarks, object.optString(Constant.HoseReelInfo.remarks));
                            }
                            intent.putExtra(Constant.HoseReelInfo.clientName, object.optString(Constant.HoseReelInfo.clientName));
                            intent.putExtra(Constant.HoseReelInfo.spare_part_item_label, object.optString(Constant.HoseReelInfo.spare_part_item_label));

                            startActivity(intent);

                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                                /*Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.putExtra(Constant.modelNo, model_edit_text.getText().toString().trim());
                                startActivity(intent);*/
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

    private void checkFireBucket(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFireBucket(modelNo, "4");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, FireBucketInstalledActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.FireBucket.location, object.optString(Constant.FireBucket.location));

                            intent.putExtra(Constant.FireBucket.observation, object.optString(Constant.FireBucket.observation));
                            intent.putExtra(Constant.FireBucket.action, object.optString(Constant.FireBucket.action));

                            intent.putExtra(Constant.FireBucket.number_of_fire_bucket, object.optString(Constant.FireBucket.number_of_fire_bucket));
                            intent.putExtra(Constant.FireBucket.buckets, object.optString(Constant.FireBucket.buckets));
                            intent.putExtra(Constant.FireBucket.stand, object.optString(Constant.FireBucket.stand));
                            intent.putExtra(Constant.FireBucket.sand, object.optString(Constant.FireBucket.sand));

                            intent.putExtra(Constant.FireBucket.spare_part_label, object.optString(Constant.FireBucket.spare_part_label));
                            if (!object.optString(Constant.FireBucket.remarks).equals("")) {
                                intent.putExtra(Constant.FireBucket.remarks, object.optString(Constant.FireBucket.remarks));
                            }
                            intent.putExtra(Constant.FireBucket.clientName, object.optString(Constant.FireBucket.clientName));
                            intent.putExtra(Constant.FireBucket.spare_part_item_label, object.optString(Constant.FireBucket.spare_part_item_label));

                            startActivity(intent);

                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                                /*Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.putExtra(Constant.modelNo, model_edit_text.getText().toString().trim());
                                startActivity(intent);*/
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

    private void checkFirePump(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFirePump(modelNo, "5");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, FirePumpInstalledActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.FirePump.location, object.optString(Constant.FirePump.location));

                            intent.putExtra(Constant.FirePump.HP, object.optString(Constant.FirePump.HP));
                            intent.putExtra(Constant.FirePump.Head, object.optString(Constant.FirePump.Head));

                            intent.putExtra(Constant.FirePump.KW, object.optString(Constant.FirePump.KW));
                            intent.putExtra(Constant.FirePump.pumpNo, object.optString(Constant.FirePump.pumpNo));
                            intent.putExtra(Constant.FirePump.pumpType, object.optString(Constant.FirePump.pumpType));
                            intent.putExtra(Constant.FirePump.RPM, object.optString(Constant.FirePump.RPM));
                            intent.putExtra(Constant.FirePump.motorNo, object.optString(Constant.FirePump.motorNo));

                            intent.putExtra(Constant.FirePump.spare_part_label, object.optString(Constant.FirePump.spare_part_label));
                            if (!object.optString(Constant.FirePump.remarks).equals("")) {
                                intent.putExtra(Constant.FirePump.remarks, object.optString(Constant.FirePump.remarks));
                            }
                            intent.putExtra(Constant.FirePump.clientName, object.optString(Constant.FirePump.clientName));
                            intent.putExtra(Constant.FirePump.spare_part_item_label, object.optString(Constant.FirePump.spare_part_item_label));

                            startActivity(intent);

                        } else {
                            hideDialog();
                            Utility.ShowToastMessage(mContext, jsonObject.optString(Constant.message));
                                /*Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                                intent.putExtra(Constant.modelNo, model_edit_text.getText().toString().trim());
                                startActivity(intent);*/
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

    private void initData() {

        mIvFlashLight = findViewById(R.id.qr_code_iv_flash_light);
        TextView mTvFlashLightText = findViewById(R.id.qr_code_tv_flash_light);
        mQrCodeFinderView = findViewById(R.id.qr_code_view_finder);
        mSurfaceView = findViewById(R.id.qr_code_preview_view);
        mLlFlashLight = findViewById(R.id.qr_code_ll_flash_light);

        mHasSurface = false;
        mIvFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CameraManager.init(mContext);
        mInactivityTimer = new InactivityTimer(QRCodeScannerActivity.this);
        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new WeakHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        //turnFlashLightOff();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
        CameraManager.get().closeDriver();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        stopLocationUpdates();
        super.onDestroy();
    }

    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(mContext, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            String resultString = result.getText();

            handleResult(resultString);

        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder, mSurfaceView);
        } catch (IOException e) {
            Utility.ShowToastMessage(mContext, getString(R.string.qr_code_camera_not_found));
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            mDecodeManager.showPermissionDeniedDialog(this);
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        mLlFlashLight.setVisibility(View.GONE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(QRCodeScannerActivity.this);
        }
    }

    private void restartPreview() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler.restartPreviewAndDecode();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                Log.e(TAG, "User agreed to make required location settings changes.");
                startLocationUpdates();
                // Nothing to do. startLocationUpdates() gets called in onResume again.
                break;
            case REQUEST_PICTURE:
                finish();
                break;
        }
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Utility.ShowToastMessage(mContext, "Unable to scan qr code");
            mDecodeManager.showCouldNotReadQrCodeFromScanner(mContext, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else if (latitude == 0.0 && longitude == 0.0) {
            startLocationUpdates();
            Utility.ShowToastMessage(mContext, "Location not found please enable GPS Location");
            mDecodeManager.showCouldNotReadQrCodeFromScanner(mContext, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            Log.e(TAG, "Got scan result from qr code ====> " + resultString);
            checkModelNo(resultString);
        }
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                updateUI();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        //showLoading();
        Log.e(TAG, "startLocationUpdates");

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e(TAG, "onSuccess");
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                updateUI();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure gone");
                //hideDialog();
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(QRCodeScannerActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Utility.ShowToastMessage(mContext, mContext.getResources().getString(R.string.internetconnection));
                }
            }
        });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideDialog();
                //Log.e(TAG, "stopLocationUpdates: updates never requested, no-op.");
            }
        });
    }

    public void updateUI() {
        if (checkLocation()) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            Utility.setSharedPreference(mContext, Constant.latitude, String.valueOf(latitude));
            Utility.setSharedPreference(mContext, Constant.longitude, String.valueOf(longitude));
            //hideDialog();
            if (modelNo != null && !modelNo.equals("")) {
                updateTrackingStatus(QRCodeScannerActivity.this.modelNo);
            }
        }
    }

    private boolean checkLocation() {
        if (mCurrentLocation == null) {
            startLocationUpdates();
            Log.e(TAG, "return false");
            return false;
        } else {
            Log.e(TAG, "return true");
            return true;
        }
    }

    private void checkPortableMonitor(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getPortableMonitors(modelNo, "7");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, PortableMonitorActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.PortableMonitor.location, object.optString(Constant.PortableMonitor.location));

                            intent.putExtra(Constant.PortableMonitor.rotation, object.optString(Constant.PortableMonitor.rotation));
                            intent.putExtra(Constant.PortableMonitor.capacity, object.optString(Constant.PortableMonitor.capacity));
                            intent.putExtra(Constant.PortableMonitor.flow, object.optString(Constant.PortableMonitor.flow));
                            intent.putExtra(Constant.PortableMonitor.pressure, object.optString(Constant.PortableMonitor.pressure));
                            intent.putExtra(Constant.PortableMonitor.size, object.optString(Constant.PortableMonitor.size));
                            intent.putExtra(Constant.PortableMonitor.typeOfMonitor, object.optString(Constant.PortableMonitor.typeOfMonitor));
                            intent.putExtra(Constant.PortableMonitor.mocBody, object.optString(Constant.PortableMonitor.mocBody));
                            intent.putExtra(Constant.PortableMonitor.throwRange, object.optString(Constant.PortableMonitor.throwRange));
                            intent.putExtra(Constant.PortableMonitor.foamTank, object.optString(Constant.PortableMonitor.foamTank));
                            intent.putExtra(Constant.PortableMonitor.foamInductor, object.optString(Constant.PortableMonitor.foamInductor));
                            intent.putExtra(Constant.PortableMonitor.handleRotationWheel, object.optString(Constant.PortableMonitor.handleRotationWheel));
                            intent.putExtra(Constant.PortableMonitor.foamInductorPipe, object.optString(Constant.PortableMonitor.foamInductorPipe));
                            intent.putExtra(Constant.PortableMonitor.nozzleType, object.optString(Constant.PortableMonitor.nozzleType));
                            intent.putExtra(Constant.PortableMonitor.operationManualElectric, object.optString(Constant.PortableMonitor.operationManualElectric));


                            intent.putExtra(Constant.PortableMonitor.spare_part_label, object.optString(Constant.PortableMonitor.spare_part_label));
                            if (!object.optString(Constant.PortableMonitor.remarks).equals("")) {
                                intent.putExtra(Constant.PortableMonitor.remarks, object.optString(Constant.PortableMonitor.remarks));
                            }
                            intent.putExtra(Constant.PortableMonitor.clientName, object.optString(Constant.PortableMonitor.clientName));
                            intent.putExtra(Constant.PortableMonitor.spare_part_item_label, object.optString(Constant.PortableMonitor.spare_part_item_label));

                            startActivity(intent);

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

    private void checkFireDetectionPanel(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFireDetection(modelNo, "6");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, FireDetectionPanelActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.FireDetectionPanel.location, object.optString(Constant.FireDetectionPanel.location));

                            intent.putExtra(Constant.FireDetectionPanel.make, object.optString(Constant.FireDetectionPanel.make));
                            intent.putExtra(Constant.FireDetectionPanel.siren, object.optString(Constant.FireDetectionPanel.siren));
                            intent.putExtra(Constant.FireDetectionPanel.cables, object.optString(Constant.FireDetectionPanel.cables));
                            intent.putExtra(Constant.FireDetectionPanel.specsOfPanel, object.optString(Constant.FireDetectionPanel.specsOfPanel));
                            intent.putExtra(Constant.FireDetectionPanel.typeOfSystem, object.optString(Constant.FireDetectionPanel.typeOfSystem));
                            intent.putExtra(Constant.FireDetectionPanel.loopsZone, object.optString(Constant.FireDetectionPanel.loopsZone));
                            intent.putExtra(Constant.FireDetectionPanel.repeaterPanel, object.optString(Constant.FireDetectionPanel.repeaterPanel));
                            intent.putExtra(Constant.FireDetectionPanel.isolatorModule, object.optString(Constant.FireDetectionPanel.isolatorModule));
                            intent.putExtra(Constant.FireDetectionPanel.heatDetector, object.optString(Constant.FireDetectionPanel.heatDetector));
                            intent.putExtra(Constant.FireDetectionPanel.flameDetector, object.optString(Constant.FireDetectionPanel.flameDetector));
                            intent.putExtra(Constant.FireDetectionPanel.smokeDetector, object.optString(Constant.FireDetectionPanel.smokeDetector));
                            intent.putExtra(Constant.FireDetectionPanel.multiDetector, object.optString(Constant.FireDetectionPanel.multiDetector));
                            intent.putExtra(Constant.FireDetectionPanel.gasDetector, object.optString(Constant.FireDetectionPanel.gasDetector));
                            intent.putExtra(Constant.FireDetectionPanel.beamDetector, object.optString(Constant.FireDetectionPanel.beamDetector));
                            intent.putExtra(Constant.FireDetectionPanel.manualCallPoint, object.optString(Constant.FireDetectionPanel.manualCallPoint));
                            intent.putExtra(Constant.FireDetectionPanel.hooterCumSounder, object.optString(Constant.FireDetectionPanel.hooterCumSounder));
                            intent.putExtra(Constant.FireDetectionPanel.zoneMonitorModule, object.optString(Constant.FireDetectionPanel.zoneMonitorModule));
                            intent.putExtra(Constant.FireDetectionPanel.monitorModule, object.optString(Constant.FireDetectionPanel.monitorModule));
                            intent.putExtra(Constant.FireDetectionPanel.controlModule, object.optString(Constant.FireDetectionPanel.controlModule));
                            intent.putExtra(Constant.FireDetectionPanel.powerSupplyUnit, object.optString(Constant.FireDetectionPanel.powerSupplyUnit));
                            intent.putExtra(Constant.FireDetectionPanel.zenerBarrier, object.optString(Constant.FireDetectionPanel.zenerBarrier));
                            intent.putExtra(Constant.FireDetectionPanel.batteryBackup, object.optString(Constant.FireDetectionPanel.batteryBackup));

                            //intent.putExtra(Constant.FireDetectionPanel.spare_part_label, object.optString(Constant.FireDetectionPanel.spare_part_label));
                            if (!object.optString(Constant.FireDetectionPanel.remarks).equals("")) {
                                intent.putExtra(Constant.FireDetectionPanel.remarks, object.optString(Constant.FireDetectionPanel.remarks));
                            }
                            intent.putExtra(Constant.FireDetectionPanel.clientName, object.optString(Constant.FireDetectionPanel.clientName));
                            //intent.putExtra(Constant.FireDetectionPanel.spare_part_item_label, object.optString(Constant.FireDetectionPanel.spare_part_item_label));

                            startActivity(intent);

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

    private void checkControlValve(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getControlValve(modelNo, "9");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, ControlValveActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.ControlValve.location, object.optString(Constant.ControlValve.location));

                            intent.putExtra(Constant.ControlValve.moc, object.optString(Constant.ControlValve.moc));
                            intent.putExtra(Constant.ControlValve.size, object.optString(Constant.ControlValve.size));
                            intent.putExtra(Constant.ControlValve.spindle, object.optString(Constant.ControlValve.spindle));
                            intent.putExtra(Constant.ControlValve.gasket, object.optString(Constant.ControlValve.gasket));
                            intent.putExtra(Constant.ControlValve.pressure, object.optString(Constant.ControlValve.pressure));
                            intent.putExtra(Constant.ControlValve.flow, object.optString(Constant.ControlValve.flow));

                            intent.putExtra(Constant.ControlValve.typeOfValve, object.optString(Constant.ControlValve.typeOfValve));
                            intent.putExtra(Constant.ControlValve.wheelLever, object.optString(Constant.ControlValve.wheelLever));
                            intent.putExtra(Constant.ControlValve.glandPacking, object.optString(Constant.ControlValve.glandPacking));
                            intent.putExtra(Constant.ControlValve.drainValve, object.optString(Constant.ControlValve.drainValve));

                            intent.putExtra(Constant.ControlValve.pressureGuage, object.optString(Constant.ControlValve.pressureGuage));
                            intent.putExtra(Constant.ControlValve.testValve, object.optString(Constant.ControlValve.testValve));
                            intent.putExtra(Constant.ControlValve.soleniodValveActuator, object.optString(Constant.ControlValve.soleniodValveActuator));
                            intent.putExtra(Constant.ControlValve.internalDiscFlap, object.optString(Constant.ControlValve.internalDiscFlap));
                            intent.putExtra(Constant.ControlValve.gongBell, object.optString(Constant.ControlValve.gongBell));

                            intent.putExtra(Constant.ControlValve.spare_part_label, object.optString(Constant.ControlValve.spare_part_label));
                            if (!object.optString(Constant.ControlValve.remarks).equals("")) {
                                intent.putExtra(Constant.ControlValve.remarks, object.optString(Constant.ControlValve.remarks));
                            }
                            intent.putExtra(Constant.ControlValve.clientName, object.optString(Constant.ControlValve.clientName));
                            intent.putExtra(Constant.ControlValve.spare_part_item_label, object.optString(Constant.ControlValve.spare_part_item_label));

                            startActivity(intent);

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

    private void checkSuppressionSystem(String modelNo) {
        Utility.hideKeyBoard(model_edit_text, mContext);
        showLoading();

        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getSupressionSystem(modelNo, "8");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getBoolean(Constant.status)) {
                            hideDialog();
                            JSONObject object = jsonObject.optJSONObject(Constant.object);

                            Intent intent = new Intent(mContext, SuppressionSystemActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.SuppressionSystem.location, object.optString(Constant.SuppressionSystem.location));
                            if (!object.optString(Constant.SuppressionSystem.remarks).equals("")) {
                                intent.putExtra(Constant.SuppressionSystem.remarks, object.optString(Constant.SuppressionSystem.remarks));
                            }
                            intent.putExtra(Constant.SuppressionSystem.clientName, object.optString(Constant.SuppressionSystem.clientName));

                            intent.putExtra(Constant.SuppressionSystem.make, object.optString(Constant.SuppressionSystem.make));
                            intent.putExtra(Constant.SuppressionSystem.manifold, object.optString(Constant.SuppressionSystem.manifold));
                            intent.putExtra(Constant.SuppressionSystem.piping, object.optString(Constant.SuppressionSystem.piping));
                            intent.putExtra(Constant.SuppressionSystem.siren, object.optString(Constant.SuppressionSystem.siren));
                            intent.putExtra(Constant.SuppressionSystem.cables, object.optString(Constant.SuppressionSystem.cables));
                            intent.putExtra(Constant.SuppressionSystem.specsOfSupressionWSystem, object.optString(Constant.SuppressionSystem.specsOfSupressionWSystem));
                            intent.putExtra(Constant.SuppressionSystem.specsOfPanelAndMake, object.optString(Constant.SuppressionSystem.specsOfPanelAndMake));
                            intent.putExtra(Constant.SuppressionSystem.typeOfSystem, object.optString(Constant.SuppressionSystem.typeOfSystem));
                            intent.putExtra(Constant.SuppressionSystem.loopsZone, object.optString(Constant.SuppressionSystem.loopsZone));
                            intent.putExtra(Constant.SuppressionSystem.modelNoSuppresion, object.optString(Constant.SuppressionSystem.modelNoSuppresion));
                            intent.putExtra(Constant.SuppressionSystem.capacityOfClyinder, object.optString(Constant.SuppressionSystem.capacityOfClyinder));
                            intent.putExtra(Constant.SuppressionSystem.noOfCylinders, object.optString(Constant.SuppressionSystem.noOfCylinders));
                            intent.putExtra(Constant.SuppressionSystem.emptyWeight, object.optString(Constant.SuppressionSystem.emptyWeight));
                            intent.putExtra(Constant.SuppressionSystem.fullWeight, object.optString(Constant.SuppressionSystem.fullWeight));
                            intent.putExtra(Constant.SuppressionSystem.suppressionGasFilled, object.optString(Constant.SuppressionSystem.suppressionGasFilled));
                            intent.putExtra(Constant.SuppressionSystem.pressureGauageReading, object.optString(Constant.SuppressionSystem.pressureGauageReading));
                            intent.putExtra(Constant.SuppressionSystem.electromagneticActuator, object.optString(Constant.SuppressionSystem.electromagneticActuator));
                            intent.putExtra(Constant.SuppressionSystem.pneumaticActuator, object.optString(Constant.SuppressionSystem.pneumaticActuator));
                            intent.putExtra(Constant.SuppressionSystem.pressureSupervisorySwitch, object.optString(Constant.SuppressionSystem.pressureSupervisorySwitch));
                            intent.putExtra(Constant.SuppressionSystem.flexibleDischargeHose, object.optString(Constant.SuppressionSystem.flexibleDischargeHose));
                            intent.putExtra(Constant.SuppressionSystem.flexibleActuatorHose, object.optString(Constant.SuppressionSystem.flexibleActuatorHose));
                            intent.putExtra(Constant.SuppressionSystem.nozzlesSuppresion, object.optString(Constant.SuppressionSystem.nozzlesSuppresion));
                            intent.putExtra(Constant.SuppressionSystem.abortSwitch, object.optString(Constant.SuppressionSystem.abortSwitch));
                            intent.putExtra(Constant.SuppressionSystem.heatDetector, object.optString(Constant.SuppressionSystem.heatDetector));
                            intent.putExtra(Constant.SuppressionSystem.flameDetector, object.optString(Constant.SuppressionSystem.flameDetector));
                            intent.putExtra(Constant.SuppressionSystem.smokeDetector, object.optString(Constant.SuppressionSystem.smokeDetector));
                            intent.putExtra(Constant.SuppressionSystem.multiDetector, object.optString(Constant.SuppressionSystem.multiDetector));
                            intent.putExtra(Constant.SuppressionSystem.gasDetector, object.optString(Constant.SuppressionSystem.gasDetector));
                            intent.putExtra(Constant.SuppressionSystem.vesdaDetectorPanel, object.optString(Constant.SuppressionSystem.vesdaDetectorPanel));
                            intent.putExtra(Constant.SuppressionSystem.manualCallPoint, object.optString(Constant.SuppressionSystem.manualCallPoint));
                            intent.putExtra(Constant.SuppressionSystem.hooterCumSounder, object.optString(Constant.SuppressionSystem.hooterCumSounder));
                            intent.putExtra(Constant.SuppressionSystem.zoneMonitorModule, object.optString(Constant.SuppressionSystem.zoneMonitorModule));
                            intent.putExtra(Constant.SuppressionSystem.monitorModule, object.optString(Constant.SuppressionSystem.monitorModule));
                            intent.putExtra(Constant.SuppressionSystem.controlModule, object.optString(Constant.SuppressionSystem.controlModule));
                            intent.putExtra(Constant.SuppressionSystem.powerSupplyUnit, object.optString(Constant.SuppressionSystem.powerSupplyUnit));
                            intent.putExtra(Constant.SuppressionSystem.manualGasReleaseSwitch, object.optString(Constant.SuppressionSystem.manualGasReleaseSwitch));
                            intent.putExtra(Constant.SuppressionSystem.specialDetector, object.optString(Constant.SuppressionSystem.specialDetector));
                            intent.putExtra(Constant.SuppressionSystem.batteryBackup, object.optString(Constant.SuppressionSystem.batteryBackup));

                            startActivity(intent);

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

    private static class WeakHandler extends Handler {
        private final WeakReference<QRCodeScannerActivity> mWeakQrCodeActivity;
        private final DecodeManager mDecodeManager = new DecodeManager();

        public WeakHandler(QRCodeScannerActivity imagePickerActivity) {
            super();
            this.mWeakQrCodeActivity = new WeakReference<>(imagePickerActivity);
        }


        @Override
        public void handleMessage(Message msg) {
            QRCodeScannerActivity qrCodeActivity = mWeakQrCodeActivity.get();
            switch (msg.what) {
                case MSG_DECODE_SUCCEED:
                    Result result = (Result) msg.obj;
                    if (null == result) {
                        mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    } else {
                        String resultString = result.getText();
                        handleResult(resultString);
                    }
                    break;
                case MSG_DECODE_FAIL:
                    mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    break;
            }
            super.handleMessage(msg);
        }

        private void handleResult(String resultString) {
            QRCodeScannerActivity imagePickerActivity = mWeakQrCodeActivity.get();

            mDecodeManager.showResultDialog(imagePickerActivity, resultString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

    }
}
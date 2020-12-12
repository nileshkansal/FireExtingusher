package com.fireextinguisher.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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

import com.fireextinguisher.QRCodeScannerActivity;
import com.fireextinguisher.R;
import com.fireextinguisher.scanner.camera.CameraManager;
import com.fireextinguisher.scanner.decode.CaptureActivityHandler;
import com.fireextinguisher.scanner.decode.DecodeManager;
import com.fireextinguisher.scanner.decode.InactivityTimer;
import com.fireextinguisher.scanner.view.QrCodeFinderView;
import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
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

public class ClientQRCodeScanActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;
    private static final String TAG = QRCodeScannerActivity.class.getSimpleName();
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private static final int REQUEST_SYSTEM_PICTURE = 0;
    private static final int REQUEST_PICTURE = 1004;

    private final DecodeManager mDecodeManager = new DecodeManager();
    private final String GOT_RESULT = "got_qr_scan_result";

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
            } else {
                checkModelNo();
            }
        });

        initData();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
                Utility.logout(ClientQRCodeScanActivity.this);
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
                            ClientQRCodeScanActivity.this.modelNo = object.getString("modelNo");
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
                            ClientQRCodeScanActivity.this.modelNo = object.getString("modelNo");
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

                            Intent intent = new Intent(mContext, ClientFireExtinguisherActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.empid, object.optString(Constant.empId));
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

                            Intent intent = new Intent(mContext, ClientFireHydrantActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.empid, object.optString(Constant.empId));
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

                            Intent intent = new Intent(mContext, ClientHoseReelActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.empid, object.optString(Constant.empId));
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

                            Intent intent = new Intent(mContext, ClientFireBucketActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.empid, object.optString(Constant.empId));
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

                            Intent intent = new Intent(mContext, ClientFirePumpActivity.class);
                            intent.putExtra(Constant.modelNo, object.optString(Constant.modelNo));
                            intent.putExtra(Constant.productId, object.optString(Constant.productId));
                            intent.putExtra(Constant.empid, object.optString(Constant.empId));
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
        mInactivityTimer = new InactivityTimer(ClientQRCodeScanActivity.this);
        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new ClientQRCodeScanActivity.WeakHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
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
        //stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        //stopLocationUpdates();
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
            mCaptureActivityHandler = new CaptureActivityHandler(ClientQRCodeScanActivity.this);
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

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Utility.ShowToastMessage(mContext, "Unable to scan qr code");
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


    private static class WeakHandler extends Handler {
        private final WeakReference<ClientQRCodeScanActivity> mWeakQrCodeActivity;
        private final DecodeManager mDecodeManager = new DecodeManager();

        public WeakHandler(ClientQRCodeScanActivity imagePickerActivity) {
            super();
            this.mWeakQrCodeActivity = new WeakReference<>(imagePickerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ClientQRCodeScanActivity qrCodeActivity = mWeakQrCodeActivity.get();
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
            ClientQRCodeScanActivity imagePickerActivity = mWeakQrCodeActivity.get();

            mDecodeManager.showResultDialog(imagePickerActivity, resultString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
    }
}
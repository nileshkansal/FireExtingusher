package com.fireextinguisher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireExtinguisherOtherActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final String TAG = FireExtinguisherOtherActivity.class.getSimpleName();
    private static final int PERMISSION = 1010;
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    RelativeLayout refill_image, hpt_image, replace_image;
    LinearLayout spare_part_selection_layout;
    TextView remarks, spare_part, due_date_hpt, last_date_hpt, due_date_refill, last_date_refill,
            net_cylinder_pressure, full_cylinder_pressure, empty_cylinder_pressure, mfg_year, capacity, f_e_type,
            f_e_no, current_location, location, client_name, spare_part_selection;

    String modelNo = "", productId = "", location_label = "", f_e_no_label = "", fe_type_label = "", capacity_label = "",
            mfg_year_label = "", empty_cylinder_pressure_label = "", full_cylinder_pressure_label = "",
            net_cylinder_pressure_label = "", last_date_refill_label = "", due_date_refill_label = "", last_date_hpt_label = "",
            due_date_hpt_label = "", spare_part_label = "", remarks_label = "", clientName = "", spare_part_item_label = "";

    boolean isRefillSelected = false, isHPTSelected = false, isReplaceSelected = false;
    DatePickerDialog lastDateRefillPicker, dueDateRefillPicker, lastDateHPTPicker, dueDateHPTPicker;
    TextInputEditText current_refill_text, next_refill_text, current_hpt_text, next_hpt_text;

    ProgressDialog dialog;
    String servicesSeparated = "", picturePath = "";
    AlertDialog remarkDialog;
    RelativeLayout select_image_layout;
    TextInputLayout remark_layout;
    TextInputEditText remark_text;
    ImageView image_view;
    Dialog sdialog;
    Uri outputFileUri;
    String actualType = "", HPT_currentDate, HPT_dueDate, refill_currentDate, refill_dueDate;
    Bitmap myBitmap;
    View customLayout;

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
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
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_fire_extinguisher);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.other));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        location_label = getIntent().getStringExtra(Constant.ProductInfo.location);
        f_e_no_label = getIntent().getStringExtra(Constant.ProductInfo.f_e_no);
        fe_type_label = getIntent().getStringExtra(Constant.ProductInfo.fe_type_label);
        capacity_label = getIntent().getStringExtra(Constant.ProductInfo.capacity_label);
        mfg_year_label = getIntent().getStringExtra(Constant.ProductInfo.mfg_year_label);
        empty_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.empty_cylinder_pressure);
        full_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.full_cylinder_pressure);
        net_cylinder_pressure_label = getIntent().getStringExtra(Constant.ProductInfo.net_cylinder_pressure);
        last_date_refill_label = getIntent().getStringExtra(Constant.ProductInfo.last_date_refill_label);
        due_date_refill_label = getIntent().getStringExtra(Constant.ProductInfo.due_date_refill_label);
        last_date_hpt_label = getIntent().getStringExtra(Constant.ProductInfo.last_date_hpt_label);
        due_date_hpt_label = getIntent().getStringExtra(Constant.ProductInfo.due_date_hpt_label);
        spare_part_label = getIntent().getStringExtra(Constant.ProductInfo.spare_part_label);

        if (getIntent().getStringExtra(Constant.ProductInfo.remarks) != null &&
                !getIntent().getStringExtra(Constant.ProductInfo.remarks).equals("") &&
                !getIntent().getStringExtra(Constant.ProductInfo.remarks).isEmpty()) {
            remarks_label = getIntent().getStringExtra(Constant.ProductInfo.remarks);
        } else {
            remarks_label = "";
        }

        clientName = getIntent().getStringExtra(Constant.ProductInfo.clientName);

        if (spare_part_label.equals("Yes")) {
            spare_part_item_label = getIntent().getStringExtra(Constant.ProductInfo.spare_part_item_label);
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
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        continue_btn.setVisibility(View.GONE);

        refill_image = findViewById(R.id.refill_image);
        hpt_image = findViewById(R.id.hpt_image);
        replace_image = findViewById(R.id.replace_image);

        refill_image.setOnClickListener(this);
        hpt_image.setOnClickListener(this);
        replace_image.setOnClickListener(this);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        due_date_hpt = findViewById(R.id.due_date_hpt);
        last_date_hpt = findViewById(R.id.last_date_hpt);
        due_date_refill = findViewById(R.id.due_date_refill);
        last_date_refill = findViewById(R.id.last_date_refill);
        net_cylinder_pressure = findViewById(R.id.net_cylinder_pressure);
        full_cylinder_pressure = findViewById(R.id.full_cylinder_pressure);
        empty_cylinder_pressure = findViewById(R.id.empty_cylinder_pressure);
        mfg_year = findViewById(R.id.mfg_year);
        capacity = findViewById(R.id.capacity);
        f_e_type = findViewById(R.id.f_e_type);
        f_e_no = findViewById(R.id.f_e_no);
        current_location = findViewById(R.id.current_location);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        location.setText(location_label);
        f_e_no.setText(f_e_no_label);
        f_e_type.setText(fe_type_label);
        capacity.setText(capacity_label);
        mfg_year.setText(mfg_year_label);
        empty_cylinder_pressure.setText(empty_cylinder_pressure_label);
        full_cylinder_pressure.setText(full_cylinder_pressure_label);
        net_cylinder_pressure.setText(net_cylinder_pressure_label);
        last_date_refill.setText(last_date_refill_label);
        due_date_refill.setText(due_date_refill_label);
        last_date_hpt.setText(last_date_hpt_label);
        due_date_hpt.setText(due_date_hpt_label);
        spare_part.setText(spare_part_label);
        remarks.setText(remarks_label);

        client_name.setText(clientName);

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
        int id = v.getId();
        if (id == R.id.refill_image) {
            if (isRefillSelected) {
                isRefillSelected = false;
                refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                continue_btn.setVisibility(View.GONE);
            } else {
                isRefillSelected = true;
                refill_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                if (isHPTSelected) {
                    isHPTSelected = false;
                    hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                if (isReplaceSelected) {
                    isReplaceSelected = false;
                    replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                continue_btn.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.hpt_image) {
            if (isHPTSelected) {
                isHPTSelected = false;
                hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                continue_btn.setVisibility(View.GONE);
            } else {
                isHPTSelected = true;
                hpt_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                if (isRefillSelected) {
                    isRefillSelected = false;
                    refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                if (isReplaceSelected) {
                    isReplaceSelected = false;
                    replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                continue_btn.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.replace_image) {
            if (isReplaceSelected) {
                isReplaceSelected = false;
                replace_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                continue_btn.setVisibility(View.GONE);
            } else {
                isReplaceSelected = true;
                replace_image.setBackground(getResources().getDrawable(R.drawable.selected_service_back));
                if (isRefillSelected) {
                    isRefillSelected = false;
                    refill_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                if (isHPTSelected) {
                    isHPTSelected = false;
                    hpt_image.setBackground(getResources().getDrawable(R.drawable.un_selected_service_back));
                }
                continue_btn.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.continue_btn) {
            if (isReplaceSelected) {
                showRemarkAlert("replace");
            } else if (isHPTSelected) {
                showHPTDateSelectionAlert();
            } else if (isRefillSelected) {
                showRefillDateSelectionAlert();
            } else {
                Utility.ShowToastMessage(mContext, "Please select any option");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void askReplaceConfirmation() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Are you sure you want to submit replace product request?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateReplace();
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

    private void showReplaceRequest() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCancelable(false);
        mBuilder.setMessage("Replace product request submitted successfully.");
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

    private void showRefillDateSelectionAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_refill_date_layout, null);
        mBuilder.setView(customLayout);
        current_refill_text = customLayout.findViewById(R.id.current_refill_text);
        next_refill_text = customLayout.findViewById(R.id.next_refill_text);

        current_refill_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLastDateRefill();
            }
        });

        next_refill_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectDueDateRefill();
            }
        });

        mBuilder.setPositiveButton("OK", null);
        mBuilder.setNegativeButton("Cancel", null);
        AlertDialog mDialog = mBuilder.create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btn_positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (current_refill_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please select current refill date");
                        } else if (next_refill_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please select next refill date");
                        } else if (next_refill_text.getText().toString().trim().equals(current_refill_text.getText().toString().trim())) {
                            Utility.ShowToastMessage(mContext, "Current refill date and next refill date can not be same");
                        } else {
                            dialog.dismiss();
                            refill_currentDate = current_refill_text.getText().toString().trim();
                            refill_dueDate = next_refill_text.getText().toString().trim();
                            showRemarkAlert("refill");
                        }
                    }
                });

                Button btn_negative = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
            }
        });

        mDialog.show();
    }

    private void selectLastDateRefill() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        lastDateRefillPicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int a = monthOfYear + 1;
                DecimalFormat formatter = new DecimalFormat("00");
                String month = formatter.format(a);

                DecimalFormat formatter2 = new DecimalFormat("00");
                String date = formatter2.format(dayOfMonth);

                current_refill_text.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        //lastDateRefillPicker.getDatePicker().setMinDate(System.currentTimeMillis());
        lastDateRefillPicker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        lastDateRefillPicker.show();
    }

    private void selectDueDateRefill() {
        final Calendar cldr = Calendar.getInstance();
        try {
            String lastDate = current_refill_text.getText().toString();
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(lastDate);
            if (date != null) {
                cldr.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        // date picker dialog
        dueDateRefillPicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int a = monthOfYear + 1;
                DecimalFormat formatter = new DecimalFormat("00");
                String month = formatter.format(a);

                DecimalFormat formatter2 = new DecimalFormat("00");
                String date = formatter2.format(dayOfMonth);

                next_refill_text.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);

        dueDateRefillPicker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        dueDateRefillPicker.show();
    }

    private void showHPTDateSelectionAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_hpt_date_layout, null);
        mBuilder.setView(customLayout);
        current_hpt_text = customLayout.findViewById(R.id.current_hpt_text);
        next_hpt_text = customLayout.findViewById(R.id.next_hpt_text);

        current_hpt_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLastDateHPT();
            }
        });

        next_hpt_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectDueDateHPT();
            }
        });

        mBuilder.setPositiveButton("OK", null);
        mBuilder.setNegativeButton("Cancel", null);
        AlertDialog mDialog = mBuilder.create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btn_positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (current_hpt_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please select current HPT date");
                        } else if (next_hpt_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please select next HPT date");
                        } else if (next_hpt_text.getText().toString().trim().equals(current_hpt_text.getText().toString().trim())) {
                            Utility.ShowToastMessage(mContext, "Current HPT date and next HPT date can not be same");
                        } else {
                            mDialog.dismiss();
                            showRemarkAlert("hpt");
                        }
                    }
                });

                Button btn_negative = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_negative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
            }
        });

        mDialog.show();
    }

    private void selectLastDateHPT() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        lastDateHPTPicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int a = monthOfYear + 1;
                DecimalFormat formatter = new DecimalFormat("00");
                String month = formatter.format(a);

                DecimalFormat formatter2 = new DecimalFormat("00");
                String date = formatter2.format(dayOfMonth);

                current_hpt_text.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        lastDateHPTPicker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        lastDateHPTPicker.show();
    }

    private void selectDueDateHPT() {
        final Calendar cldr = Calendar.getInstance();
        try {
            String lastDate = current_hpt_text.getText().toString();
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(lastDate);
            if (date != null) {
                cldr.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        dueDateHPTPicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int a = monthOfYear + 1;
                DecimalFormat formatter = new DecimalFormat("00");
                String month = formatter.format(a);

                DecimalFormat formatter2 = new DecimalFormat("00");
                String date = formatter2.format(dayOfMonth);

                next_hpt_text.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        dueDateHPTPicker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        dueDateHPTPicker.show();
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

    private void updateReplace() {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {

            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("file", panFile.getName(), requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.userId)));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("remarks", createPartFromString(remark_text.getText().toString().trim()));
            map.put("comment", createPartFromString("Replace"));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));

            call = apiInterface.updateSiteModelProductWithImage(map, multipartBodyImage);
        /*} else {
            call = apiInterface.updateFireExtinguisherReplaceOrNonService(
                    Utility.getSharedPreferences(mContext, Constant.userId), modelNo,
                    remark_text.getText().toString().trim(), "Replace");
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
                                showReplaceRequest();
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

    private void updateRefillProduct(String currentDate, String dueDate) {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {

            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("file", panFile.getName(), requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.userId)));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("lastDateRefilling", createPartFromString(currentDate));
            map.put("dueDateRefiiling", createPartFromString(dueDate));
            map.put("remarks", createPartFromString(remark_text.getText().toString().trim()));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));

            call = apiInterface.updateSiteModelProductWithImage(map, multipartBodyImage);
        /*} else {
            call = apiInterface.updateFireExtinguisherRefill(Utility.getSharedPreferences(mContext, Constant.userId),
                    modelNo, currentDate, dueDate, remark_text.getText().toString().trim());
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

    private void updateHPTProduct(String currentDate, String dueDate) {
        showLoading();
        ApiInterface apiInterface = new APIClient().getClient().create(ApiInterface.class);
        Call<JsonObject> call;
        if (picturePath != null && !picturePath.equals("")) {

            File panFile = new File(picturePath);
            RequestBody requestfile = RequestBody.create(MediaType.parse("multipart/jpg"), panFile);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("file", panFile.getName(), requestfile);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("empId", createPartFromString(Utility.getSharedPreferences(mContext, Constant.userId)));
            map.put("modelNo", createPartFromString(modelNo));
            map.put("lastDateHpt", createPartFromString(currentDate));
            map.put("dueDateHpt", createPartFromString(dueDate));
            map.put("remarks", createPartFromString(remark_text.getText().toString().trim()));
            map.put("lat", createPartFromString(Utility.getSharedPreferences(mContext, Constant.latitude)));
            map.put("lang", createPartFromString(Utility.getSharedPreferences(mContext, Constant.longitude)));

            call = apiInterface.updateSiteModelProductWithImage(map, multipartBodyImage);
        /*} else {
            call = apiInterface.updateFireExtinguisherHPT(Utility.getSharedPreferences(mContext, Constant.userId),
                    modelNo, currentDate, dueDate, remark_text.getText().toString().trim());
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
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_remark_layout, null);
        mBuilder.setView(customLayout);

        select_image_layout = customLayout.findViewById(R.id.select_image_layout);
        remark_layout = customLayout.findViewById(R.id.current_refill_layout);
        remark_text = customLayout.findViewById(R.id.current_refill_text);
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
                        if (remark_text.getText().toString().trim().isEmpty()) {
                            Utility.ShowToastMessage(mContext, "Please add remarks");
                        } else {
                            switch (type) {
                                case "hpt":
                                    updateHPTProduct(HPT_currentDate, HPT_dueDate);
                                    break;
                                case "refill":
                                    updateRefillProduct(refill_currentDate, refill_dueDate);
                                    break;
                                case "replace":
                                    askReplaceConfirmation();
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
}
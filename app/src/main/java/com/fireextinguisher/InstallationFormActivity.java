package com.fireextinguisher;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InstallationFormActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InstallationFormActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;

    TextView remarks, spare_part, due_date_hpt, last_date_hpt, due_date_refill, last_date_refill,
            net_cylinder_pressure, full_cylinder_pressure, empty_cylinder_pressure, mfg_year, capacity, f_e_type,
            f_e_no, current_location, location, spare_part_selection, client_name;

    LinearLayout location_layout, f_e_no_layout, f_e_type_layout, capacity_layout, mfg_year_layout,
            empty_cylinder_pressure_layout, full_cylinder_pressure_layout, net_cylinder_pressure_layout,
            last_date_refill_layout, due_date_refill_layout, last_date_hpt_layout, due_date_hpt_layout,
            spare_part_layout, remarks_layout, spare_part_selection_layout, client_name_layout;

    DatePickerDialog mfgYearPicker, lastDateRefillPicker, dueDateRefillPicker, lastDateHPTPicker, dueDateHPTPicker;
    String modelNo = "", productId = "", clientName = "";
    int clientId = 0;
    boolean[] checkedItems = new boolean[]{false, false, false, false, false, false, false, false, false, false, false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_from);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.installation));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        clientName = getIntent().getStringExtra(Constant.clientName);
        clientId = getIntent().getIntExtra(Constant.clientId, 0);

        bind();
    }

    private void bind() {
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);

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

        remarks_layout = findViewById(R.id.remarks_layout);
        spare_part_layout = findViewById(R.id.spare_part_layout);
        due_date_hpt_layout = findViewById(R.id.due_date_hpt_layout);
        last_date_hpt_layout = findViewById(R.id.last_date_hpt_layout);
        due_date_refill_layout = findViewById(R.id.due_date_refill_layout);
        last_date_refill_layout = findViewById(R.id.last_date_refill_layout);
        net_cylinder_pressure_layout = findViewById(R.id.net_cylinder_pressure_layout);
        full_cylinder_pressure_layout = findViewById(R.id.full_cylinder_pressure_layout);
        empty_cylinder_pressure_layout = findViewById(R.id.empty_cylinder_pressure_layout);
        mfg_year_layout = findViewById(R.id.mfg_year_layout);
        capacity_layout = findViewById(R.id.capacity_layout);
        f_e_type_layout = findViewById(R.id.f_e_type_layout);
        f_e_no_layout = findViewById(R.id.f_e_no_layout);
        location_layout = findViewById(R.id.location_layout);
        client_name_layout = findViewById(R.id.client_name_layout);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);

        remarks_layout.setOnClickListener(this);
        spare_part_layout.setOnClickListener(this);
        due_date_hpt_layout.setOnClickListener(this);
        last_date_hpt_layout.setOnClickListener(this);
        due_date_refill_layout.setOnClickListener(this);
        last_date_refill_layout.setOnClickListener(this);
        net_cylinder_pressure_layout.setOnClickListener(this);
        full_cylinder_pressure_layout.setOnClickListener(this);
        empty_cylinder_pressure_layout.setOnClickListener(this);
        mfg_year_layout.setOnClickListener(this);
        capacity_layout.setOnClickListener(this);
        f_e_type_layout.setOnClickListener(this);
        f_e_no_layout.setOnClickListener(this);
        current_location.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        client_name_layout.setOnClickListener(this);
        spare_part_selection_layout.setOnClickListener(this);

        spare_part_selection_layout.setVisibility(View.GONE);

        client_name.setText(clientName);
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
        showCancelAlert();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remarks_layout:
                selectRemarks();
                break;
            case R.id.net_cylinder_pressure_layout:
                selectNetCylinder();
                break;
            case R.id.full_cylinder_pressure_layout:
                selectFullCylinder();
                break;
            case R.id.empty_cylinder_pressure_layout:
                selectEmptyCylinder();
                break;
            case R.id.f_e_no_layout:
                selectFENO();
                break;
            case R.id.current_location:
                selectCurrentLocation();
                break;
            case R.id.location_layout:
                selectLocation();
                break;
            case R.id.continue_btn:
                uploadDataTask();
                break;
            case R.id.f_e_type_layout:
                selectFEType();
                break;
            case R.id.capacity_layout:
                selectCapacity();
                break;
            case R.id.mfg_year_layout:
                selectMFGYear();
                break;
            case R.id.last_date_refill_layout:
                selectLastDateRefill();
                break;
            case R.id.due_date_refill_layout:
                if (last_date_refill.getText().toString().length() <= 0) {
                    Utility.ShowToastMessage(mContext, "Please select Last date of Refill");
                } else {
                    selectDueDateRefill();
                }
                break;
            case R.id.last_date_hpt_layout:
                selectLastDateHPT();
                break;
            case R.id.due_date_hpt_layout:
                if (last_date_hpt.getText().toString().length() <= 0) {
                    Utility.ShowToastMessage(mContext, "Please select Last date of HPT");
                } else {
                    selectDueDateHPT();
                }
                break;
            case R.id.spare_part_layout:
                sparePartsRequired();
                break;
            case R.id.spare_part_selection_layout:
                sparePartsSelection();
                break;
            case R.id.client_name_layout:
                //selectClientName();
                break;
        }
    }

    private void showCancelAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Alert");
        dialog.setMessage("Are you sure you want to cancel this installation?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();
    }

    private void selectClientName() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint("Client Name");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    client_name.setText(editText.getText().toString().trim());
                } else {
                    client_name.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectLocation() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint("Location");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    location.setText(editText.getText().toString().trim());
                } else {
                    location.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectCurrentLocation() {

    }

    private void selectFENO() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint("F.E.NO");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    f_e_no.setText(editText.getText().toString().trim());
                } else {
                    f_e_no.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectFEType() {
        String[] listItems = getResources().getStringArray(R.array.fe_type_item);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                f_e_type.setText(listItems[i]);
                capacity.setText("");
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectCapacity() {
        if (f_e_type.getText().toString().trim() != null && !f_e_type.getText().toString().trim().equals("")) {
            String[] listItems = null;
            String type = f_e_type.getText().toString();
            switch (type) {
                case "ABC":
                    listItems = getResources().getStringArray(R.array.abc_item);
                    break;
                case "CO2":
                    listItems = getResources().getStringArray(R.array.co_2_item);
                    break;
                case "DCP":
                    listItems = getResources().getStringArray(R.array.dcp_item);
                    break;
                case "WCO2":
                    listItems = getResources().getStringArray(R.array.w_co_2_item);
                    break;
                case "MFOAM":
                    listItems = getResources().getStringArray(R.array.m_foam_item);
                    break;
                case "CLEAN AGENT":
                    listItems = getResources().getStringArray(R.array.clean_agent_item);
                    break;
            }

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            String[] finalListItems = listItems;
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    capacity.setText(finalListItems[i]);
                    dialogInterface.dismiss();
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } else {
            Utility.ShowToastMessage(mContext, "Please select F.E Type first");
        }
    }

    private void selectMFGYear() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        mfgYearPicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int a = monthOfYear + 1;
                DecimalFormat formatter = new DecimalFormat("00");
                String month = formatter.format(a);

                DecimalFormat formatter2 = new DecimalFormat("00");
                String date = formatter2.format(dayOfMonth);

                mfg_year.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        mfgYearPicker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        mfgYearPicker.show();
    }

    private void selectEmptyCylinder() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.empty_cylinder_wt_pressure));
        editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    empty_cylinder_pressure.setText(editText.getText().toString().trim());
                } else {
                    empty_cylinder_pressure.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectFullCylinder() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.full_cylinder_wt_pressure));
        editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    full_cylinder_pressure.setText(editText.getText().toString().trim());
                } else {
                    full_cylinder_pressure.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectNetCylinder() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.net_wt_pressure));
        editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                if (editText.getText().toString().trim().length() > 0) {
                    net_cylinder_pressure.setText(editText.getText().toString().trim());
                } else {
                    net_cylinder_pressure.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
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

                last_date_refill.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        //lastDateRefillPicker.getDatePicker().setMinDate(System.currentTimeMillis());
        lastDateRefillPicker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        lastDateRefillPicker.show();
    }

    private void selectDueDateRefill() {
        final Calendar cldr = Calendar.getInstance();
        try {
            String lastDate = last_date_refill.getText().toString();
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

                due_date_refill.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);

        dueDateRefillPicker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        dueDateRefillPicker.show();
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

                last_date_hpt.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        lastDateHPTPicker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        lastDateHPTPicker.show();
    }

    private void selectDueDateHPT() {
        final Calendar cldr = Calendar.getInstance();
        try {
            String lastDate = last_date_refill.getText().toString();
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

                due_date_hpt.setText(String.format(Locale.ENGLISH, "%d-%s-%s", year, month, date));
            }
        }, year, month, day);
        dueDateHPTPicker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        dueDateHPTPicker.show();
    }

    private void sparePartsRequired() {
        String[] listItems = getResources().getStringArray(R.array.spare_part_required);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                spare_part.setText(listItems[i]);

                if (listItems[i].equals("Yes")) {
                    spare_part_selection_layout.setVisibility(View.VISIBLE);
                    spare_part_selection.setText("");
                } else {
                    spare_part_selection_layout.setVisibility(View.GONE);
                    spare_part_selection.setText("");
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void sparePartsSelection() {
        String[] listItems = getResources().getStringArray(R.array.spare_part_item);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update the current focused item's checked status
                checkedItems[which] = isChecked;
                // Get the current focused item
                //String currentItem = colorsList.get(which);
            }
        });

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button
                spare_part_selection.setText("");
                for (int i = 0; i < checkedItems.length; i++) {
                    boolean checked = checkedItems[i];
                    if (checked) {
                        spare_part_selection.setText(String.format(Locale.ENGLISH, "%s%s\n", spare_part_selection.getText(), listItems[i]));
                    }
                }
                String text = spare_part_selection.getText().toString().trim().replaceAll("\n$", "");
                Log.e(TAG, "text =======> " + text);
                spare_part_selection.setText(text);

                dialog.dismiss();
            }
        });

        // Set the negative/no button click listener
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectRemarks() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        editText.setHint(getString(R.string.remarks));
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    remarks.setText(editText.getText().toString().trim());
                } else {
                    remarks.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void uploadDataTask() {
        if (client_name.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Client Name");
        } else if (location.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Location");
        } else if (f_e_no.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide F.E. NO");
        } else if (f_e_type.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select F.E. Type");
        } else if (capacity.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Capacity");
        } else if (mfg_year.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Mfg Year");
        } else if (empty_cylinder_pressure.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Empty Cylinder Wt./Pressure");
        } else if (full_cylinder_pressure.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Full Cylinder Wt./Pressure");
        } else if (net_cylinder_pressure.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Net Wt./Pressure");
        } else if (last_date_refill.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Last date of Refill");
        } else if (due_date_refill.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Mfg Year");
        } else if (last_date_hpt.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Last date of HPT");
        } else if (due_date_hpt.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Due date of HPT");
        } else if (spare_part.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Spare Parts are required or not");
        } else if (spare_part.getText().toString().trim().equals("Yes") && spare_part_selection.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select required Spare Parts");
        } else if (remarks.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide remark");
        } else {

            Intent intent = new Intent(mContext, InstallationVerifyModelActivity.class);
            intent.putExtra(Constant.modelNo, modelNo);
            intent.putExtra(Constant.productId, productId);
            intent.putExtra(Constant.ProductInfo.location, location.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.f_e_no, f_e_no.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.fe_type_label, f_e_type.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.capacity_label, capacity.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.mfg_year_label, mfg_year.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.empty_cylinder_pressure, empty_cylinder_pressure.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.full_cylinder_pressure, full_cylinder_pressure.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.net_cylinder_pressure, net_cylinder_pressure.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.last_date_refill_label, last_date_refill.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.due_date_refill_label, due_date_refill.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.last_date_hpt_label, last_date_hpt.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.due_date_hpt_label, due_date_hpt.getText().toString().trim());
            intent.putExtra(Constant.ProductInfo.spare_part_label, spare_part.getText().toString().trim());
            if (remarks.getText().toString().trim().length() > 0) {
                intent.putExtra(Constant.ProductInfo.remarks, remarks.getText().toString().trim());
            }
            if (spare_part.getText().toString().trim().equals("Yes")) {
                intent.putExtra(Constant.ProductInfo.spare_part_item_label, spare_part_selection.getText().toString().trim());
            }
            intent.putExtra(Constant.ProductInfo.clientName, client_name.getText().toString().trim());

            startActivity(intent);
        }
    }
}

package com.fireextinguisher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.Objects;

public class FirePumpInstallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FirePumpInstallActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    String modelNo = "", productId = "", clientName = "", pumpType = "";
    int clientId = 0;
    TextView client_name, location, hp, head, kw, pump_no, pump_type, rpm, motor_no, spare_part, spare_part_selection,
            remarks, pump_no_text, hp_text;

    LinearLayout client_name_layout, location_layout, hp_layout, head_layout, kw_layout, pump_no_layout, pump_type_layout,
            rpm_layout, motor_no_layout, spare_part_layout, spare_part_selection_layout, remarks_layout;

    boolean[] checkedItems = new boolean[]{false, false, false, false, false, false, false, false, false, false,
            false, false, false, false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_pump_install);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.installation));

        modelNo = getIntent().getStringExtra(Constant.modelNo);
        productId = getIntent().getStringExtra(Constant.productId);
        clientName = getIntent().getStringExtra(Constant.clientName);
        clientId = getIntent().getIntExtra(Constant.clientId, 0);
        pumpType = getIntent().getStringExtra(Constant.pumpType);

        bind();
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
        //finish();
        showCancelAlert();
    }

    private void bind() {
        client_name = findViewById(R.id.client_name);
        location = findViewById(R.id.location);
        hp = findViewById(R.id.hp);
        head = findViewById(R.id.head);
        kw = findViewById(R.id.kw);
        pump_no = findViewById(R.id.pump_no);
        pump_type = findViewById(R.id.pump_type);
        rpm = findViewById(R.id.rpm);
        motor_no = findViewById(R.id.motor_no);
        spare_part = findViewById(R.id.spare_part);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        remarks = findViewById(R.id.remarks);

        continue_btn = findViewById(R.id.continue_btn);

        pump_no_text = findViewById(R.id.pump_no_text);
        hp_text = findViewById(R.id.hp_text);

        client_name_layout = findViewById(R.id.client_name_layout);
        location_layout = findViewById(R.id.location_layout);
        hp_layout = findViewById(R.id.hp_layout);
        head_layout = findViewById(R.id.head_layout);
        kw_layout = findViewById(R.id.kw_layout);
        pump_no_layout = findViewById(R.id.pump_no_layout);
        pump_type_layout = findViewById(R.id.pump_type_layout);
        rpm_layout = findViewById(R.id.rpm_layout);
        motor_no_layout = findViewById(R.id.motor_no_layout);
        spare_part_layout = findViewById(R.id.spare_part_layout);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);
        remarks_layout = findViewById(R.id.remarks_layout);

        client_name_layout.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        hp_layout.setOnClickListener(this);
        head_layout.setOnClickListener(this);
        kw_layout.setOnClickListener(this);
        pump_no_layout.setOnClickListener(this);
        pump_type_layout.setOnClickListener(this);
        rpm_layout.setOnClickListener(this);
        motor_no_layout.setOnClickListener(this);
        spare_part_layout.setOnClickListener(this);
        spare_part_selection_layout.setOnClickListener(this);
        remarks_layout.setOnClickListener(this);

        continue_btn.setOnClickListener(this);

        client_name.setText(clientName);
        pump_type.setText(pumpType);
        if (pumpType.equals("Diesel Engine")) {
            hp_text.setText("BHP");
            pump_no_text.setText("ENGINE NO");
        } else {
            hp_text.setText("HP");
            pump_no_text.setText("PUMP NO");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.client_name_layout:
                //selectClientName();
                break;
            case R.id.location_layout:
                selectLocation();
                break;
            case R.id.hp_layout:
                selectHP();
                break;
            case R.id.head_layout:
                selectHead();
                break;
            case R.id.kw_layout:
                selectKW();
                break;
            case R.id.pump_no_layout:
                selectPumpNo();
                break;
            case R.id.pump_type_layout:
                //selectPumpType();
                break;
            case R.id.rpm_layout:
                selectRPM();
                break;
            case R.id.motor_no_layout:
                selectMotorNo();
                break;
            case R.id.spare_part_layout:
                sparePartsRequired();
                break;
            case R.id.spare_part_selection_layout:
                sparePartsSelection();
                break;
            case R.id.remarks_layout:
                selectRemarks();
                break;
            case R.id.continue_btn:
                uploadDataTask();
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

    private void selectHP() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (pumpType.equals("Diesel Engine")) {
            editText.setHint("BHP");
        } else {
            editText.setHint("HP");
        }

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    hp.setText(editText.getText().toString().trim());
                } else {
                    hp.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectHead() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("HEAD");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    head.setText(editText.getText().toString().trim());
                } else {
                    head.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectKW() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("KW");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    kw.setText(editText.getText().toString().trim());
                } else {
                    kw.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectPumpNo() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (pumpType.equals("Diesel Engine")) {
            editText.setHint("ENGINE NO");
        } else {
            editText.setHint("PUMP NO");
        }

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    pump_no.setText(editText.getText().toString().trim());
                } else {
                    pump_no.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectPumpType() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Pump Type");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    pump_type.setText(editText.getText().toString().trim());
                } else {
                    pump_type.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectRPM() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("RPM");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    rpm.setText(editText.getText().toString().trim());
                } else {
                    rpm.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectMotorNo() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("MOTOR NO");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                Utility.hideKeyBoard(editText, mContext);
                if (editText.getText().toString().trim().length() > 0) {
                    motor_no.setText(editText.getText().toString().trim());
                } else {
                    motor_no.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
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
                Utility.hideKeyBoard(editText, mContext);
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
                Utility.hideKeyBoard(editText, mContext);
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
        String[] listItems;
        if (pumpType.equals("Diesel Engine")) {
            listItems = getResources().getStringArray(R.array.fire_pump_item_2);
        } else {
            listItems = getResources().getStringArray(R.array.fire_pump_item_1);
        }

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
                Utility.hideKeyBoard(editText, mContext);
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
        } else if (hp.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide HP");
        } else if (head.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide HEAD");
        } else if (kw.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide KW");
        } else if (pump_no.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide PUMP NO");
        } else if (pump_type.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide PUMP TYPE");
        } else if (rpm.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide RPM");
        } else if (motor_no.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide MOTOR NO");
        } else if (spare_part.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Spare Parts are required or not");
        } else if (spare_part.getText().toString().trim().equals("Yes") && spare_part_selection.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select required Spare Parts");
        } else if (remarks.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide remark");
        } else {
            Intent intent = new Intent(mContext, FirePumpVerifyActivity.class);
            intent.putExtra(Constant.modelNo, modelNo);
            intent.putExtra(Constant.productId, productId);
            intent.putExtra(Constant.FirePump.clientName, client_name.getText().toString().trim());
            intent.putExtra(Constant.FirePump.location, location.getText().toString().trim());
            intent.putExtra(Constant.FirePump.HP, hp.getText().toString().trim());
            intent.putExtra(Constant.FirePump.Head, head.getText().toString().trim());
            intent.putExtra(Constant.FirePump.KW, kw.getText().toString().trim());
            intent.putExtra(Constant.FirePump.pumpNo, pump_no.getText().toString().trim());
            intent.putExtra(Constant.FirePump.pumpType, pump_type.getText().toString().trim());
            intent.putExtra(Constant.FirePump.RPM, rpm.getText().toString().trim());
            intent.putExtra(Constant.FirePump.motorNo, motor_no.getText().toString().trim());
            intent.putExtra(Constant.FirePump.spare_part_label, spare_part.getText().toString().trim());
            if (remarks.getText().toString().trim().length() > 0) {
                intent.putExtra(Constant.FirePump.remarks, remarks.getText().toString().trim());
            }
            if (spare_part.getText().toString().trim().equals("Yes")) {
                intent.putExtra(Constant.FirePump.spare_part_item_label, spare_part_selection.getText().toString().trim());
            }

            startActivity(intent);
        }
    }
}
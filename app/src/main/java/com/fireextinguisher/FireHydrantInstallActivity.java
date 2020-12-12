package com.fireextinguisher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class FireHydrantInstallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FireHydrantInstallActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    String modelNo = "", productId = "", clientName = "";
    int clientId = 0;
    boolean[] checkedItems = new boolean[]{false, false, false, false, false, false, false, false, false, false};
    TextView remarks, spare_part, location, spare_part_selection, client_name, hose_pipe, hydrant_valve, black_cap, shunt_wheel,
            hose_box, hoses, glasses, branch_pipe, keys, glass_hammer, observation, action;

    LinearLayout location_layout, spare_part_layout, remarks_layout, spare_part_selection_layout, client_name_layout,
            hose_pipe_layout, hydrant_valve_layout, black_cap_layout, shunt_wheel_layout, hose_box_layout, hoses_layout,
            glasses_layout, branch_pipe_layout, keys_layout, glass_hammer_layout, observation_layout, action_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_hydrant_install);

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

        bind();
    }

    private void bind() {
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);

        remarks = findViewById(R.id.remarks);
        spare_part = findViewById(R.id.spare_part);
        location = findViewById(R.id.location);
        client_name = findViewById(R.id.client_name);
        spare_part_selection = findViewById(R.id.spare_part_selection);
        hose_pipe = findViewById(R.id.hose_pipe);
        hydrant_valve = findViewById(R.id.hydrant_valve);
        black_cap = findViewById(R.id.black_cap);
        shunt_wheel = findViewById(R.id.shunt_wheel);
        hose_box = findViewById(R.id.hose_box);
        hoses = findViewById(R.id.hoses);
        glasses = findViewById(R.id.glasses);
        branch_pipe = findViewById(R.id.branch_pipe);
        keys = findViewById(R.id.keys);
        glass_hammer = findViewById(R.id.glass_hammer);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);

        remarks_layout = findViewById(R.id.remarks_layout);
        spare_part_layout = findViewById(R.id.spare_part_layout);
        location_layout = findViewById(R.id.location_layout);
        client_name_layout = findViewById(R.id.client_name_layout);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);
        hose_pipe_layout = findViewById(R.id.hose_pipe_layout);
        hydrant_valve_layout = findViewById(R.id.hydrant_valve_layout);
        black_cap_layout = findViewById(R.id.black_cap_layout);
        shunt_wheel_layout = findViewById(R.id.shunt_wheel_layout);
        hose_box_layout = findViewById(R.id.hose_box_layout);
        hoses_layout = findViewById(R.id.hoses_layout);
        glasses_layout = findViewById(R.id.glasses_layout);
        branch_pipe_layout = findViewById(R.id.branch_pipe_layout);
        keys_layout = findViewById(R.id.keys_layout);
        glass_hammer_layout = findViewById(R.id.glass_hammer_layout);
        observation_layout = findViewById(R.id.observation_layout);
        action_layout = findViewById(R.id.action_layout);

        remarks_layout.setOnClickListener(this);
        spare_part_layout.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        client_name_layout.setOnClickListener(this);
        spare_part_selection_layout.setOnClickListener(this);
        hose_pipe_layout.setOnClickListener(this);
        hydrant_valve_layout.setOnClickListener(this);
        black_cap_layout.setOnClickListener(this);
        shunt_wheel_layout.setOnClickListener(this);
        hose_box_layout.setOnClickListener(this);
        hoses_layout.setOnClickListener(this);
        glasses_layout.setOnClickListener(this);
        branch_pipe_layout.setOnClickListener(this);
        keys_layout.setOnClickListener(this);
        glass_hammer_layout.setOnClickListener(this);
        observation_layout.setOnClickListener(this);
        action_layout.setOnClickListener(this);

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
            case R.id.location_layout:
                selectLocation();
                break;
            case R.id.continue_btn:
                uploadDataTask();
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
            case R.id.hose_pipe_layout:
                selectHosePipe();
                break;
            case R.id.hydrant_valve_layout:
                selectHydrantValve();
                break;
            case R.id.black_cap_layout:
                selectBlackCap();
                break;
            case R.id.shunt_wheel_layout:
                selectShuntWheel();
                break;
            case R.id.hose_box_layout:
                selectHoseBox();
                break;
            case R.id.hoses_layout:
                selectHoses();
                break;
            case R.id.glasses_layout:
                selectGlasses();
                break;
            case R.id.branch_pipe_layout:
                selectBranchPipe();
                break;
            case R.id.keys_layout:
                selectKeys();
                break;
            case R.id.glass_hammer_layout:
                selectGlassHammer();
                break;
            case R.id.observation_layout:
                selectObservation();
                break;
            case R.id.action_layout:
                selectAction();
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
        String[] listItems = getResources().getStringArray(R.array.fire_hydrant_spare_part_item);

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

    private void selectHosePipe() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Hose Pipe");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    hose_pipe.setText(editText.getText().toString().trim());
                } else {
                    hose_pipe.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectHydrantValve() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Hydrant Valve");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    hydrant_valve.setText(editText.getText().toString().trim());
                } else {
                    hydrant_valve.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectBlackCap() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Black Cap");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    black_cap.setText(editText.getText().toString().trim());
                } else {
                    black_cap.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectShuntWheel() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Shunt Wheel");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    shunt_wheel.setText(editText.getText().toString().trim());
                } else {
                    shunt_wheel.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectHoseBox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Hose Box");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    hose_box.setText(editText.getText().toString().trim());
                } else {
                    hose_box.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectHoses() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Hoses");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    hoses.setText(editText.getText().toString().trim());
                } else {
                    hoses.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectGlasses() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Glasses");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    glasses.setText(editText.getText().toString().trim());
                } else {
                    glasses.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectBranchPipe() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Branch Pipe");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    branch_pipe.setText(editText.getText().toString().trim());
                } else {
                    branch_pipe.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectKeys() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Keys");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    keys.setText(editText.getText().toString().trim());
                } else {
                    keys.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectGlassHammer() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Glass Hammer");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    glass_hammer.setText(editText.getText().toString().trim());
                } else {
                    glass_hammer.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectObservation() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Observation");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    observation.setText(editText.getText().toString().trim());
                } else {
                    observation.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectAction() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Action");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    action.setText(editText.getText().toString().trim());
                } else {
                    action.setText("");
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
        } else if (hose_pipe.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Hose Pipe quantity");
        } else if (hydrant_valve.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Hydrant Valve quantity");
        } else if (black_cap.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Black Cap quantity");
        } else if (shunt_wheel.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Shunt Wheel quantity");
        } else if (hose_box.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Hose Box quantity");
        } else if (hoses.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Hoses quantity");
        } else if (glasses.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Glasses quantity");
        } else if (branch_pipe.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Branch Pipe quantity");
        } else if (keys.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Keys quantity");
        } else if (glass_hammer.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Glass Hammer quantity");
        } else if (observation.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide observation");
        } else if (action.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide action");
        } else if (spare_part.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select Spare Parts are required or not");
        } else if (spare_part.getText().toString().trim().equals("Yes") && spare_part_selection.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please select required Spare Parts");
        } else if (remarks.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide remark");
        } else {

            Intent intent = new Intent(mContext, FireHydrantVerifyActivity.class);
            intent.putExtra(Constant.modelNo, modelNo);
            intent.putExtra(Constant.productId, productId);
            intent.putExtra(Constant.FireHydrantInfo.clientName, client_name.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.location, location.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.hose_pipe, hose_pipe.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.hydrant_valve, hydrant_valve.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.black_cap, black_cap.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.shunt_wheel, shunt_wheel.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.hose_box, hose_box.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.hoses, hoses.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.glasses, glasses.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.branch_pipe, branch_pipe.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.keys, keys.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.glass_hammer, glass_hammer.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.observation, observation.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.action, action.getText().toString().trim());
            intent.putExtra(Constant.FireHydrantInfo.spare_part_label, spare_part.getText().toString().trim());
            if (remarks.getText().toString().trim().length() > 0) {
                intent.putExtra(Constant.FireHydrantInfo.remarks, remarks.getText().toString().trim());
            }
            if (spare_part.getText().toString().trim().equals("Yes")) {
                intent.putExtra(Constant.FireHydrantInfo.spare_part_item_label, spare_part_selection.getText().toString().trim());
            }

            startActivity(intent);
        }
    }
}
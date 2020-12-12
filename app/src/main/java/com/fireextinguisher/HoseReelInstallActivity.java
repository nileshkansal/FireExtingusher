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

public class HoseReelInstallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HoseReelInstallActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    String modelNo = "", productId = "", clientName = "";
    int clientId = 0;
    boolean[] checkedItems = new boolean[]{false, false, false, false, false};
    TextView remarks, spare_part, location, spare_part_selection, client_name, hose_reel, shut_off_nozzel, ball_valve,
            jubli_clip, conecting_ruber_hose, observation, action;

    LinearLayout location_layout, spare_part_layout, remarks_layout, spare_part_selection_layout, client_name_layout,
            hose_reel_layout, shut_off_nozzel_layout, ball_valve_layout, jubli_clip_layout, conecting_ruber_hose_layout,
            observation_layout, action_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hose_reel_install);
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
        hose_reel = findViewById(R.id.hose_reel);
        shut_off_nozzel = findViewById(R.id.shut_off_nozzel);
        ball_valve = findViewById(R.id.ball_valve);
        jubli_clip = findViewById(R.id.jubli_clip);
        conecting_ruber_hose = findViewById(R.id.conecting_ruber_hose);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);

        remarks_layout = findViewById(R.id.remarks_layout);
        spare_part_layout = findViewById(R.id.spare_part_layout);
        location_layout = findViewById(R.id.location_layout);
        client_name_layout = findViewById(R.id.client_name_layout);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);
        hose_reel_layout = findViewById(R.id.hose_reel_layout);
        shut_off_nozzel_layout = findViewById(R.id.shut_off_nozzel_layout);
        ball_valve_layout = findViewById(R.id.ball_valve_layout);
        jubli_clip_layout = findViewById(R.id.jubli_clip_layout);
        conecting_ruber_hose_layout = findViewById(R.id.conecting_ruber_hose_layout);
        observation_layout = findViewById(R.id.observation_layout);
        action_layout = findViewById(R.id.action_layout);

        remarks_layout.setOnClickListener(this);
        spare_part_layout.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        client_name_layout.setOnClickListener(this);
        spare_part_selection_layout.setOnClickListener(this);
        hose_reel_layout.setOnClickListener(this);
        shut_off_nozzel_layout.setOnClickListener(this);
        ball_valve_layout.setOnClickListener(this);
        jubli_clip_layout.setOnClickListener(this);
        conecting_ruber_hose_layout.setOnClickListener(this);
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
            case R.id.hose_reel_layout:
                selectHoseReel();
                break;
            case R.id.shut_off_nozzel_layout:
                selectShutOffNozzel();
                break;
            case R.id.ball_valve_layout:
                selectBallValue();
                break;
            case R.id.jubli_clip_layout:
                selectJubliClip();
                break;
            case R.id.conecting_ruber_hose_layout:
                selectConnectingRubberHose();
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
        String[] listItems = getResources().getStringArray(R.array.hose_reel_spare_part_item);

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

    // change karna hai
    private void selectHoseReel() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Hose Reel");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    hose_reel.setText(editText.getText().toString().trim());
                } else {
                    hose_reel.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectShutOffNozzel() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Shunt Off Nozzel");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    shut_off_nozzel.setText(editText.getText().toString().trim());
                } else {
                    shut_off_nozzel.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectBallValue() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Ball Valve");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    ball_valve.setText(editText.getText().toString().trim());
                } else {
                    ball_valve.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectJubliClip() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Jubli Clip");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    jubli_clip.setText(editText.getText().toString().trim());
                } else {
                    jubli_clip.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectConnectingRubberHose() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Connecting Rubber Hose");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    conecting_ruber_hose.setText(editText.getText().toString().trim());
                } else {
                    conecting_ruber_hose.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    // change karna hai

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
        } else if (hose_reel.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Hose Reel quantity");
        } else if (shut_off_nozzel.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Shunt Off Nozzel quantity");
        } else if (ball_valve.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Ball Valve quantity");
        } else if (jubli_clip.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Jubli Clip quantity");
        } else if (conecting_ruber_hose.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Connecting Rubber Hose quantity");
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

            Intent intent = new Intent(mContext, HoseReelVerifyActivity.class);
            intent.putExtra(Constant.modelNo, modelNo);
            intent.putExtra(Constant.productId, productId);
            intent.putExtra(Constant.HoseReelInfo.clientName, client_name.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.location, location.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.hose_reel, hose_reel.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.shut_off_nozzel, shut_off_nozzel.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.ball_valve, ball_valve.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.jubli_clip, jubli_clip.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.conecting_ruber_hose, conecting_ruber_hose.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.observation, observation.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.action, action.getText().toString().trim());
            intent.putExtra(Constant.HoseReelInfo.spare_part_label, spare_part.getText().toString().trim());
            if (remarks.getText().toString().trim().length() > 0) {
                intent.putExtra(Constant.HoseReelInfo.remarks, remarks.getText().toString().trim());
            }
            if (spare_part.getText().toString().trim().equals("Yes")) {
                intent.putExtra(Constant.HoseReelInfo.spare_part_item_label, spare_part_selection.getText().toString().trim());
            }

            startActivity(intent);
        }
    }
}

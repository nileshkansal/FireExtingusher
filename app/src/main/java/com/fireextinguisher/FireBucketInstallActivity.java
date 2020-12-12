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

public class FireBucketInstallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FireBucketInstallActivity.class.getSimpleName();
    Context mContext;
    Button continue_btn;
    Toolbar toolbar;
    String modelNo = "", productId = "", clientName = "";
    int clientId = 0;
    boolean[] checkedItems = new boolean[]{false, false, false, false};
    TextView remarks, spare_part, location, spare_part_selection, client_name, observation, action, number_of_fire_bucket,
            sand, stand, bucket;

    LinearLayout location_layout, spare_part_layout, remarks_layout, spare_part_selection_layout, client_name_layout,
            observation_layout, action_layout, number_of_fire_bucket_layout, bucket_layout, stand_layout, sand_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_bucket);
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
        number_of_fire_bucket = findViewById(R.id.number_of_fire_bucket);
        bucket = findViewById(R.id.bucket);
        stand = findViewById(R.id.stand);
        sand = findViewById(R.id.sand);
        observation = findViewById(R.id.observation);
        action = findViewById(R.id.action);

        remarks_layout = findViewById(R.id.remarks_layout);
        spare_part_layout = findViewById(R.id.spare_part_layout);
        location_layout = findViewById(R.id.location_layout);
        client_name_layout = findViewById(R.id.client_name_layout);
        spare_part_selection_layout = findViewById(R.id.spare_part_selection_layout);
        number_of_fire_bucket_layout = findViewById(R.id.number_of_fire_bucket_layout);
        bucket_layout = findViewById(R.id.bucket_layout);
        stand_layout = findViewById(R.id.stand_layout);
        sand_layout = findViewById(R.id.sand_layout);
        observation_layout = findViewById(R.id.observation_layout);
        action_layout = findViewById(R.id.action_layout);

        remarks_layout.setOnClickListener(this);
        spare_part_layout.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        client_name_layout.setOnClickListener(this);
        spare_part_selection_layout.setOnClickListener(this);
        number_of_fire_bucket_layout.setOnClickListener(this);
        bucket_layout.setOnClickListener(this);
        stand_layout.setOnClickListener(this);
        sand_layout.setOnClickListener(this);
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
            case R.id.number_of_fire_bucket_layout:
                selectNumberOfFireBuckets();
                break;
            case R.id.bucket_layout:
                selectBucket();
                break;
            case R.id.stand_layout:
                selectStand();
                break;
            case R.id.sand_layout:
                selectSand();
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
        String[] listItems = getResources().getStringArray(R.array.fire_bucket_spare_part_item);

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

    private void selectNumberOfFireBuckets() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Numbers of Fire Buckets");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    number_of_fire_bucket.setText(editText.getText().toString().trim());
                } else {
                    number_of_fire_bucket.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectBucket() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Buckets");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    bucket.setText(editText.getText().toString().trim());
                } else {
                    bucket.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectStand() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Stand");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    stand.setText(editText.getText().toString().trim());
                } else {
                    stand.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void selectSand() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        mBuilder.setView(customLayout);
        TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Sand");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                if (editText.getText().toString().trim().length() > 0) {
                    sand.setText(editText.getText().toString().trim());
                } else {
                    sand.setText("");
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
        } else if (number_of_fire_bucket.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Number of Fire Buckets quantity");
        } else if (bucket.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Bucket quantity");
        } else if (stand.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Stand quantity");
        } else if (sand.getText().toString().trim().length() <= 0) {
            Utility.ShowToastMessage(mContext, "Please provide Sand quantity");
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

            Intent intent = new Intent(mContext, FireBucketVerifyActivity.class);
            intent.putExtra(Constant.modelNo, modelNo);
            intent.putExtra(Constant.productId, productId);
            intent.putExtra(Constant.FireBucket.clientName, client_name.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.location, location.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.number_of_fire_bucket, number_of_fire_bucket.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.buckets, bucket.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.stand, stand.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.sand, sand.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.observation, observation.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.action, action.getText().toString().trim());
            intent.putExtra(Constant.FireBucket.spare_part_label, spare_part.getText().toString().trim());
            if (remarks.getText().toString().trim().length() > 0) {
                intent.putExtra(Constant.FireBucket.remarks, remarks.getText().toString().trim());
            }
            if (spare_part.getText().toString().trim().equals("Yes")) {
                intent.putExtra(Constant.FireBucket.spare_part_item_label, spare_part_selection.getText().toString().trim());
            }

            startActivity(intent);
        }
    }

}
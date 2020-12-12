package com.fireextinguisher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fireextinguisher.serverintegration.APIClient;
import com.fireextinguisher.serverintegration.ApiInterface;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    Context mContext;
    Button login_btn;
    Toolbar toolbar;
    TextInputLayout input_layout_empid, input_layout_mobile, input_layout_email, input_layout_password,
            input_layout_confirm_password;
    TextInputEditText empId, mobile, email, password, confirm_password;
    TextView terms_condition;
    LinearLayout login_layout;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.register));

        bind();
    }

    private void bind() {
        login_btn = findViewById(R.id.login_btn);
        login_layout = findViewById(R.id.login_layout);
        terms_condition = findViewById(R.id.terms_condition);
        empId = findViewById(R.id.empId);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);

        input_layout_empid = findViewById(R.id.input_layout_empid);
        input_layout_mobile = findViewById(R.id.input_layout_mobile);
        input_layout_email = findViewById(R.id.input_layout_email);
        input_layout_password = findViewById(R.id.input_layout_password);
        input_layout_confirm_password = findViewById(R.id.input_layout_confirm_password);

        login_btn.setOnClickListener(this);
        login_layout.setOnClickListener(this);
        String termsText = "By registering, I agree to STRAP ";
        String termsLink = "<a href=" + Constant.WEB_T_C + ">T&Cs</a>";
        String allText = termsText + termsLink;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            terms_condition.setMovementMethod(LinkMovementMethod.getInstance());
            terms_condition.setText(Html.fromHtml(allText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            terms_condition.setMovementMethod(LinkMovementMethod.getInstance());
            terms_condition.setText(Html.fromHtml(allText));
        }

        terms_condition.setText(RichTextUtils.replaceAll((Spanned) terms_condition.getText(), URLSpan.class, new URLSpanConverter(), new YourCustomClickableSpan.OnClickListener() {
            @Override
            public void onClick(String url) {
                if (Utility.isNetworkConnected(mContext)) {
                    if (url != null) {
                        Intent i = new Intent(mContext, WebviewActivity.class);
                        i.putExtra(Constant.WEB_URL, Constant.WEB_T_C);
                        if (url.contains("terms-condition"))
                            i.putExtra(Constant.WEB_SUBJECT, "Terms & Conditions");
                        startActivity(i);
                    }
                } else {
                    Utility.ShowToastMessage(mContext, R.string.internetconnection);
                }
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                                    == PackageManager.PERMISSION_GRANTED) {
                        signUpTask();
                    } else {
                        requestSMSPermission();
                    }
                } else {
                    signUpTask();
                }
                break;
            case R.id.login_layout:
                Intent intentLogin = new Intent(mContext, LoginActivity.class);
                startActivity(intentLogin);
                finish();
                break;
        }
    }

    private void requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == 1) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                signUpTask();
                //Displaying a toast
            } else {
                //Displaying another toast if permission is not granted
                Utility.ShowToastMessage(mContext, getResources().getString(R.string.denied_permission));
            }
        }
    }

    private void signUpTask() {

        if (empId.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "Employee id cannot be empty");
        } else if (mobile.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "Mobile number cannot be empty");
        } else if (mobile.getText().toString().trim().length() > 10) {
            Utility.ShowToastMessage(mContext, "Mobile number cannot be greater than 10");
        } else if (mobile.getText().toString().trim().length() < 10) {
            Utility.ShowToastMessage(mContext, "Mobile number cannot be less than 10");
        } else if (email.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "Email cannot be empty");
        } else if (!Utility.isValidEmail(email.getText().toString().trim())) {
            Utility.ShowToastMessage(mContext, "Please provide correct email");
        } else if (password.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "Password cannot be empty");
        } else if (confirm_password.getText().toString().trim().isEmpty()) {
            Utility.ShowToastMessage(mContext, "Confirm Password cannot be empty");
        } else if (password.getText().toString().trim().length() < 4) {
            Utility.ShowToastMessage(mContext, "Minimum 4 characters required for password");
        } else if (password.getText().toString().trim().length() > 8) {
            Utility.ShowToastMessage(mContext, "Password maximum limit is 8 characters");
        } else if (!password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
            Utility.ShowToastMessage(mContext, "Password and Confirm Password not matched");
        } else {

            Intent intent = new Intent(mContext, VerificationCodeActivity.class);
            intent.putExtra(Constant.empid, empId.getText().toString().trim());
            intent.putExtra(Constant.mobile, mobile.getText().toString().trim());
            intent.putExtra(Constant.email, email.getText().toString().trim());
            intent.putExtra(Constant.password, password.getText().toString().trim());
            startActivity(intent);
        }
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

    private static class RichTextUtils {
        public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(Spanned original,
                                                                                                Class<A> sourceType,
                                                                                                SpanConverter<A, B> converter,
                                                                                                final YourCustomClickableSpan.OnClickListener listener) {
            SpannableString result = new SpannableString(original);
            A[] spans = result.getSpans(0, result.length(), sourceType);

            for (A span : spans) {
                int start = result.getSpanStart(span);
                int end = result.getSpanEnd(span);
                int flags = result.getSpanFlags(span);

                result.removeSpan(span);
                result.setSpan(converter.convert(span, listener), start, end, flags);
            }

            return (result);
        }

        public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle> {
            B convert(A span, YourCustomClickableSpan.OnClickListener listener);
        }
    }

    public static class YourCustomClickableSpan extends ClickableSpan {

        private String url;
        private OnClickListener mListener;

        public YourCustomClickableSpan(String url, OnClickListener mListener) {
            this.url = url;
            this.mListener = mListener;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (mListener != null) mListener.onClick(url);
        }

        public interface OnClickListener {
            void onClick(String url);
        }
    }

    class URLSpanConverter implements RichTextUtils.SpanConverter<URLSpan, YourCustomClickableSpan> {

        @Override
        public YourCustomClickableSpan convert(URLSpan span, YourCustomClickableSpan.OnClickListener listener) {
            return (new YourCustomClickableSpan(span.getURL(), listener));
        }
    }
}
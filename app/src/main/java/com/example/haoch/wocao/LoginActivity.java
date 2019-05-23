package com.example.haoch.wocao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haoch.wocao.account_management.DatabaseHelper;
import com.example.haoch.wocao.account_management.InputValidation;

public class LoginActivity extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);
    InputValidation inputValidation = new InputValidation(this);

    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private TextInputEditText etUserName, etUserPassword;
    private Button appLogin, appSignUp;
    private TextView forgetPassword;
    private NestedScrollView nestedScrollView;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private CheckBox remember_me;

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "userpassword";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // find
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        etUserName = (TextInputEditText) findViewById(R.id.etUserName);
        etUserPassword = (TextInputEditText) findViewById(R.id.etUserPassword);
        appLogin = (Button) findViewById(R.id.appLogin);
        appSignUp = (Button) findViewById(R.id.appSignUp);
        forgetPassword = (TextView) findViewById(R.id.etForget);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        remember_me = (CheckBox) findViewById(R.id.remember_me);

        loginPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        boolean isRemeber = loginPreferences.getBoolean("remember_password", false);
        if (isRemeber) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            etUserName.setText("");
            etUserPassword.setText("");
            remember_me.setChecked(false);
        }

        // click events
        appLogin.setOnClickListener(new ButtonListener());
        appSignUp.setOnClickListener(new ButtonListener());

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });
    }


    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.appLogin:
                    verifyFromSQLite();  //
                    break;
                case R.id.appSignUp:
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    break;
            }
        }

        private void verifyFromSQLite() {

            // 是否输入了内容
            if (!inputValidation.isInputEditTextFilled(etUserName, textInputLayoutEmail, getString(R.string.error_message_email))) {
                return;
            }

            if (!inputValidation.isInputEditTextFilled(etUserPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
                return;
            }

            // 是否匹配数据库的内容
            String str = etUserName.getText().toString().trim();
            String passer = etUserPassword.getText().toString().trim();
            String pass_sql = helper.searchPass(str);

            if (remember_me.isChecked()) {
                // remeber
                if (pass_sql.equals(passer)) {
                    // remeber and correct
                    loginPrefsEditor.putBoolean("remember_password", true);
                    loginPrefsEditor.putString("account", str);
                    loginPrefsEditor.putString("password", passer);
                    startNewPage();
                } else {
                    // remeber but wrong
                    remember_me.setChecked(false);
                    emptyInputEditText();
                    loginPrefsEditor.putBoolean("remember_password", false);
                    loginPrefsEditor.putString("account", "");
                    loginPrefsEditor.putString("password", "");
                    Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                }
            } else {
                // not remember
                if (pass_sql.equals(passer)) {
                    // not remeber but correct
                    loginPrefsEditor.putBoolean("remember_password", false);
                    loginPrefsEditor.putString("account", str);
                    loginPrefsEditor.putString("password", passer);
                    startNewPage();
                } else {
                    // not remember and not correct
                    emptyInputEditText();
                    loginPrefsEditor.putBoolean("remember_password", false);
                    loginPrefsEditor.putString("account", "");
                    loginPrefsEditor.putString("password", "");
                    Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                }
            }
            loginPrefsEditor.commit();
        }

        /**
         * This method is to empty all input edit text
         */
        private void emptyInputEditText() {
            etUserName.setText(null);
            etUserPassword.setText(null);
        }

        private void startNewPage() {
            Intent accountsIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(accountsIntent);
        }


    }
}


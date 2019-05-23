package com.example.haoch.wocao;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.haoch.wocao.account_management.DatabaseHelper;
import com.example.haoch.wocao.account_management.InputValidation;
import com.example.haoch.wocao.user_info.User;

public class SignUpActivity extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);
    InputValidation inputValidation = new InputValidation(this);
    User user = new User();

    private TextInputLayout nameLayout, mailLayout, pass1Layout, pass2Layout;
    private TextInputEditText etName, etMail, etPassword, etPassword_confirm;
    private Button register_now;
    private TextView To_login;
    private NestedScrollView SignUpMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDataToSQLite();
            }
        });

        To_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(SignUpActivity.this, LoginActivity.class));
            }
        });


    }


    private void initViews(){
        nameLayout = (TextInputLayout)findViewById(R.id.nameLayout);
        mailLayout = (TextInputLayout)findViewById(R.id.emailLayout);
        pass1Layout = (TextInputLayout)findViewById(R.id.pass1Layout);
        pass2Layout = (TextInputLayout)findViewById(R.id.pass2Layout);

        etName = (TextInputEditText)findViewById(R.id.etName);
        etMail = (TextInputEditText)findViewById(R.id.etEmail);
        etPassword = (TextInputEditText)findViewById(R.id.etPassword);
        etPassword_confirm = (TextInputEditText)findViewById(R.id.etPassword_confirm);

        register_now = (Button)findViewById(R.id.etRegister);
        To_login = (TextView)findViewById(R.id.BackLogin);

        SignUpMain = (NestedScrollView)findViewById(R.id.SignUpMain);
    }

    private void postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(etName, nameLayout, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(etMail, mailLayout, getString(R.string.error_message_email))) {
            return;
        }

        if (!inputValidation.isInputEditTextFilled(etPassword, pass1Layout, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(etPassword, etPassword_confirm,
                pass2Layout, getString(R.string.error_password_match))) {
            return;
        }

        if (!helper.checkUser(etMail.getText().toString().trim())) {

            user.setUser_name(etName.getText().toString().trim());
            user.setUser_email(etMail.getText().toString().trim());
            user.setUser_password(etPassword.getText().toString().trim());

            helper.addUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(SignUpMain, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();

//            startActivity(new Intent().setClass(SignUpActivity.this, LoginActivity.class));


        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(SignUpMain, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }



    }
    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText(){
        etName.setText(null);
        etMail.setText(null);
        etPassword.setText(null);
        etPassword_confirm.setText(null);
    }




}

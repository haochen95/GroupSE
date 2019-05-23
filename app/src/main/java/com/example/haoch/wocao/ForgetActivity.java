package com.example.haoch.wocao;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haoch.wocao.account_management.DatabaseHelper;
import com.example.haoch.wocao.account_management.InputValidation;

public class ForgetActivity extends AppCompatActivity {

    InputValidation is_emial = new InputValidation(this);
    DatabaseHelper find_email = new DatabaseHelper(this);

    private TextInputLayout forget_lay;
    private NestedScrollView forget_view;
    private TextInputEditText forget_mail;
    private Button send_pass;
    private TextView back_log;
    private String f_mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        forget_mail = (TextInputEditText)findViewById(R.id.forget_email);
        send_pass = (Button)findViewById(R.id.asend_pass);
        back_log = (TextView)findViewById(R.id.back_login);
        forget_lay = (TextInputLayout)findViewById(R.id.for1);
        forget_view = (NestedScrollView)findViewById(R.id.forget_view);
        f_mail = forget_mail.getText().toString();

        send_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if email is validated
                if (is_emial.isInputEditTextEmail(forget_mail, forget_lay, "Enter Valida Email")){
                    // it is a email
                    if (find_email.checkUser(f_mail)){
                        // it is in SQL
                        sender_email_Pass(f_mail, find_email.search_mail(f_mail));  // send mail and password
                    } else {
                        Snackbar.make(forget_view, getString(R.string.email_notin_data), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(forget_view, getString(R.string.validate_email), Snackbar.LENGTH_LONG).show();
                }

            }
        });
        back_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(ForgetActivity.this, LoginActivity.class));
            }
        });
    }

    private void sender_email_Pass(String email, String password){

        Intent sender = new Intent(Intent.ACTION_SENDTO);
        sender.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});   // mail address
        sender.putExtra(Intent.EXTRA_SUBJECT, "GroupSE password");  //title
        sender.putExtra(Intent.EXTRA_TEXT, password);  // content

        try {
            startActivity(Intent.createChooser(sender, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ForgetActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}

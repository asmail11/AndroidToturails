package com.issam.drmas.mysqlite.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.issam.drmas.mysqlite.MainActivity;
import com.issam.drmas.mysqlite.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.issam.drmas.mysqlite.helpers.InputValidation;
import com.issam.drmas.mysqlite.sql.DatabaseHelper;
import com.issam.drmas.mysqlite.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;
    private AppCompatTextView textViewLinkForgotPassword;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

       textViewLinkForgotPassword = (AppCompatTextView) findViewById(R.id.forgotPassword);
        PreferenceUtils utils = new PreferenceUtils();

        if (utils.getEmail(this) != null ){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else{

        }

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
        textViewLinkForgotPassword.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
               Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
              startActivity(intentRegister);
                break;

            case R.id.forgotPassword:
                Intent forgetPassword = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(forgetPassword);
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();

        if (databaseHelper.checkUser(email, password)) {
            PreferenceUtils.saveEmail(email, this);
            PreferenceUtils.savePassword(password, this);
            Intent accountsIntent = new Intent(activity, UsersListActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
            finish();
        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }


}
package com.assistance.hashtagapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;

import dmax.dialog.SpotsDialog;
import maes.tech.intentanim.CustomIntent;

public class SignInActivity extends AppCompatActivity {

    ImageView backArrow;
    EditText emailOrMobileField, passwordField;
    MaterialCheckBox showPassword;
    TextView forgotPassword, termOfServices, privacyPolicy, signUp;
    ConstraintLayout signIn;
    CardView emailOrMobileCard, passwordCard, signInCard;
    FirebaseAuth firebaseAuth;

    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.background));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();

        backArrow = findViewById(R.id.arrow_back);
        emailOrMobileField = findViewById(R.id.email_or_mobile);
        passwordField = findViewById(R.id.password);
        showPassword = findViewById(R.id.show_password);
        forgotPassword = findViewById(R.id.forgot_password);
        termOfServices = findViewById(R.id.terms_of_service);
        privacyPolicy = findViewById(R.id.privacy_policy);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        emailOrMobileCard = findViewById(R.id.email_or_mobile_card);
        passwordCard = findViewById(R.id.password_card);
        signInCard = findViewById(R.id.sign_in_card);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new SpotsDialog.Builder().setContext(SignInActivity.this)
                .setMessage("Logging you in..")
                .setCancelable(false)
                .setTheme(R.style.SpotsDialog)
                .build();

        KeyboardVisibilityEvent.setEventListener(SignInActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(!isOpen)
                {
                    emailOrMobileField.clearFocus();
                    passwordField.clearFocus();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                CustomIntent.customType(SignInActivity.this, "left-to-right");
            }
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
                CustomIntent.customType(SignInActivity.this, "left-to-right");
            }
        });

        emailOrMobileField.addTextChangedListener(signinTextWatcher);
        passwordField.addTextChangedListener(signinTextWatcher);
    }

    private TextWatcher signinTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String emailOrMobile = emailOrMobileField.getText().toString().trim();
            final String password = passwordField.getText().toString().trim();

            if(!emailOrMobile.isEmpty() && !password.isEmpty())
            {
                signInCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                signIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(SignInActivity.this);
                        if(Patterns.EMAIL_ADDRESS.matcher(emailOrMobile).matches())
                        {
                            progressDialog.show();
                            login(emailOrMobile);
                        }
                        else if(emailOrMobile.matches("\\d{10}"))
                        {
                            progressDialog.show();
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("Users").whereEqualTo("Phone", emailOrMobile)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                        if(documentSnapshots.isEmpty())
                                        {
                                            progressDialog.dismiss();
                                            YoYo.with(Techniques.Shake)
                                                    .duration(700)
                                                    .repeat(1)
                                                    .playOn(emailOrMobileCard);
                                            Alerter.create(SignInActivity.this)
                                                    .setText("Whoa! There's no account with that mobile number!")
                                                    .setTextAppearance(R.style.ErrorAlert)
                                                    .setBackgroundColorRes(R.color.errorColor)
                                                    .setIcon(R.drawable.error)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .disableOutsideTouch()
                                                    .enableProgress(true)
                                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                                    .show();
                                            return;
                                        }
                                        else
                                        {
                                            String email = documentSnapshots.get(0).get("Email").toString().trim();
                                            login(email);
                                        }
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Alerter.create(SignInActivity.this)
                                                .setText("Whoops! There was some error!")
                                                .setTextAppearance(R.style.ErrorAlert)
                                                .setBackgroundColorRes(R.color.errorColor)
                                                .setIcon(R.drawable.error)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .disableOutsideTouch()
                                                .enableProgress(true)
                                                .setProgressColorInt(getResources().getColor(R.color.white))
                                                .show();
                                        return;
                                    }
                                }
                            });
                        }
                        else
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailOrMobileCard);
                            Alerter.create(SignInActivity.this)
                                    .setText("Please enter a valid Email or Mobile number!")
                                    .setTextAppearance(R.style.ErrorAlert)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .setIcon(R.drawable.error)
                                    .setDuration(3000)
                                    .enableSwipeToDismiss()
                                    .enableIconPulse(true)
                                    .enableVibration(true)
                                    .disableOutsideTouch()
                                    .enableProgress(true)
                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                    .show();
                            return;
                        }
                    }
                });
            }
            else
            {
                signInCard.setCardBackgroundColor(getResources().getColor(R.color.grey));
                signIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(SignInActivity.this);
                        if(emailOrMobile.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailOrMobileCard);
                        }
                        else if(password.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(passwordCard);
                        }
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void login(String email)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, passwordField.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    startActivity(new Intent(SignInActivity.this, NextActivity.class));
                    CustomIntent.customType(SignInActivity.this, "left-to-right");
                }
                else
                {
                    progressDialog.dismiss();
                    Alerter.create(SignInActivity.this)
                            .setText("Whoops! Seems like you've got invalid credentials!")
                            .setTextAppearance(R.style.ErrorAlert)
                            .setBackgroundColorRes(R.color.errorColor)
                            .setIcon(R.drawable.error)
                            .setDuration(3000)
                            .enableSwipeToDismiss()
                            .enableIconPulse(true)
                            .enableVibration(true)
                            .disableOutsideTouch()
                            .enableProgress(true)
                            .setProgressColorInt(getResources().getColor(R.color.white))
                            .show();
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(SignInActivity.this, "right-to-left");
    }
}

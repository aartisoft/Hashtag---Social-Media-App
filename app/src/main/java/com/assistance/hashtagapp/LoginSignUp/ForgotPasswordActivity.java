package com.assistance.hashtagapp.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.assistance.hashtagapp.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import dmax.dialog.SpotsDialog;
import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView backArrow, forgotPassword;
    EditText emailField;
    ConstraintLayout reset;
    CardView emailCard, resetCard;

    FirebaseAuth firebaseAuth;

    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initViews();
        initFirebase();
        setActionOnViews();

        progressDialog = new SpotsDialog.Builder().setContext(ForgotPasswordActivity.this)
                .setMessage("Sending password reset link...")
                .setCancelable(false)
                .setTheme(R.style.SpotsDialog)
                .build();

        KeyboardVisibilityEvent.setEventListener(ForgotPasswordActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(!isOpen)
                {
                    emailField.clearFocus();
                }
            }
        });
    }

    private void initViews() {
        backArrow = findViewById(R.id.arrow_back);
        forgotPassword = findViewById(R.id.forgot_password);
        emailField = findViewById(R.id.email_field);
        reset = findViewById(R.id.reset);
        resetCard = findViewById(R.id.reset_card);
        emailCard = findViewById(R.id.email_card);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setActionOnViews() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        emailField.addTextChangedListener(resetPasswordTextWatcher);
    }

    private TextWatcher resetPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String email = emailField.getText().toString().trim();

            if(!email.isEmpty())
            {
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(ForgotPasswordActivity.this);
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
                            Alerter.create(ForgotPasswordActivity.this)
                                    .setText("Please enter a valid Email!")
                                    .setTextAppearance(R.style.ErrorAlert)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .setIcon(R.drawable.ic_error)
                                    .setDuration(3000)
                                    .enableSwipeToDismiss()
                                    .enableIconPulse(true)
                                    .enableVibration(true)
                                    .disableOutsideTouch()
                                    .enableProgress(true)
                                    .setProgressColorInt(getResources().getColor(android.R.color.white))
                                    .show();
                            return;
                        }
                        else
                        {
                            progressDialog.show();
                            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(!task.getResult().getSignInMethods().isEmpty())
                                        {
                                            firebaseAuth.sendPasswordResetEmail(email)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                progressDialog.dismiss();
                                                                MaterialDialog materialDialog = new MaterialDialog.Builder(ForgotPasswordActivity.this)
                                                                        .setTitle("Reset Password!")
                                                                        .setMessage("A password reset link has been sent to " + email)
                                                                        .setAnimation(R.raw.send_sms)
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("Okay", R.drawable.ic_material_dialog_okay, new MaterialDialog.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                                                dialogInterface.dismiss();
                                                                                onBackPressed();
                                                                            }
                                                                        })
                                                                        .setNegativeButton("Cancel", R.drawable.ic_material_dialog_cancel, new MaterialDialog.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                                                dialogInterface.dismiss();
                                                                                onBackPressed();
                                                                            }
                                                                        })
                                                                        .build();
                                                                materialDialog.show();
                                                            }
                                                            else
                                                            {
                                                                progressDialog.dismiss();
                                                                Alerter.create(ForgotPasswordActivity.this)
                                                                        .setText("Whoa! That ran into some error. Could be a network issue.")
                                                                        .setTextAppearance(R.style.ErrorAlert)
                                                                        .setBackgroundColorRes(R.color.errorColor)
                                                                        .setIcon(R.drawable.ic_error)
                                                                        .setDuration(3000)
                                                                        .enableSwipeToDismiss()
                                                                        .enableIconPulse(true)
                                                                        .enableVibration(true)
                                                                        .disableOutsideTouch()
                                                                        .enableProgress(true)
                                                                        .setProgressColorInt(getResources().getColor(android.R.color.white))
                                                                        .show();
                                                                return;
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Alerter.create(ForgotPasswordActivity.this)
                                                            .setText("Whoa! That ran into some error. Could be a network issue.")
                                                            .setTextAppearance(R.style.ErrorAlert)
                                                            .setBackgroundColorRes(R.color.errorColor)
                                                            .setIcon(R.drawable.ic_error)
                                                            .setDuration(3000)
                                                            .enableSwipeToDismiss()
                                                            .enableIconPulse(true)
                                                            .enableVibration(true)
                                                            .disableOutsideTouch()
                                                            .enableProgress(true)
                                                            .setProgressColorInt(getResources().getColor(android.R.color.white))
                                                            .show();
                                                    return;
                                                }
                                            });
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            YoYo.with(Techniques.Shake)
                                                    .duration(700)
                                                    .repeat(1)
                                                    .playOn(emailCard);
                                            Alerter.create(ForgotPasswordActivity.this)
                                                    .setText("Whoa! We didn't find any account using that Email!")
                                                    .setTextAppearance(R.style.InfoAlert)
                                                    .setBackgroundColorRes(R.color.infoColor)
                                                    .setIcon(R.drawable.ic_info)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .disableOutsideTouch()
                                                    .enableProgress(true)
                                                    .setProgressColorInt(getResources().getColor(android.R.color.white))
                                                    .show();
                                            return;
                                        }
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Alerter.create(ForgotPasswordActivity.this)
                                                .setText("Whoa! That ran into some error. Could be a network issue.")
                                                .setTextAppearance(R.style.ErrorAlert)
                                                .setBackgroundColorRes(R.color.errorColor)
                                                .setIcon(R.drawable.ic_error)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .disableOutsideTouch()
                                                .enableProgress(true)
                                                .setProgressColorInt(getResources().getColor(android.R.color.white))
                                                .show();
                                        return;
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Alerter.create(ForgotPasswordActivity.this)
                                            .setText("Whoa! That ran into some error. Could be a network issue.")
                                            .setTextAppearance(R.style.ErrorAlert)
                                            .setBackgroundColorRes(R.color.errorColor)
                                            .setIcon(R.drawable.ic_error)
                                            .setDuration(3000)
                                            .enableSwipeToDismiss()
                                            .enableIconPulse(true)
                                            .enableVibration(true)
                                            .disableOutsideTouch()
                                            .enableProgress(true)
                                            .setProgressColorInt(getResources().getColor(android.R.color.white))
                                            .show();
                                    return;
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(email.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
                        }
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ForgotPasswordActivity.this, "fadein-to-fadeout");
    }
}

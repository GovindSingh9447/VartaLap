package com.example.vartalap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    TextInputEditText Uname, email,password,name,phone,otp;
    Button go;
    TextView blogin;

LinearLayout linearLayout;
    private String checkker="",phoneNumber="";
    private CountryCodePicker ccp;
    FirebaseAuth auth;



    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private ProgressDialog loadingbar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();


        linearLayout=findViewById(R.id.DT);
        Uname=findViewById(R.id.Uname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        otp=findViewById(R.id.otp);


        go=findViewById(R.id.go);
        blogin=findViewById(R.id.blogin);


        auth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);
        ccp=(CountryCodePicker) findViewById(R.id.ccp);




        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,Login.class);
                startActivity(intent);
            }
        });




        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(go.getText().equals("Submit") || checkker.equals("Code Sent"))
                {

                    String verificationCode=otp.getText().toString();
                    if (verificationCode.equals(""))
                    {
                        Toast.makeText(SignUp.this, "Please Write the Verification code...", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        loadingbar.setTitle("Code Verification...");
                        loadingbar.setMessage("Please Wait While Verification");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }

                }
                else
                {
                    phoneNumber=ccp.getFullNumberWithPlus();
                    if(!phoneNumber.equals(""))
                    {

                        loadingbar.setTitle("Phone Number Verification...");
                        loadingbar.setMessage("Please Wait While Verification...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();


                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(auth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(SignUp.this)                 // Activity (for callback binding)
                                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    }
                    else
                    {
                        Toast.makeText(SignUp.this,"Please Check Your Phone Number..",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingbar.dismiss();
                Toast.makeText(SignUp.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);

                go.setText("Continue");
                otp.setVisibility(View.GONE);

            }


            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId=s;
                mResendToken=forceResendingToken;


                linearLayout.setVisibility(View.GONE);
                checkker="Code Sent";
                go.setText("Submit");
                otp.setVisibility(View.VISIBLE);

                Toast.makeText(SignUp.this, "Code has been Send...", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();

            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
            Intent honeIntent = new Intent(SignUp.this, ContactsActivity.class);
            startActivity(honeIntent);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Toast.makeText(SignUp.this, "Congratulation...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingbar.dismiss();

                            String e=task.getException().toString();
                            Toast.makeText(SignUp.this, "Error:"+e, Toast.LENGTH_SHORT).show();



                        }
                    }
                });
    }


    private void sendUserToMainActivity(){
        Intent intent=new Intent(SignUp.this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

}

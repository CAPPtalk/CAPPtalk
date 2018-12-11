package com.example.jangheejun.capptalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;
import android.support.annotation.NonNull;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private CheckBox autoLogin;

    private String uid;
    private boolean firstLogin;
    private boolean autoLoginChecked;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        autoLogin = findViewById(R.id.cb_autologin);

        if(SingleSharedPreference.getInstance(this).getBooleanExtra("autoLogin"))
        {
            editTextEmail.setText(SingleSharedPreference.getInstance(this).getStringExtra("email"));
            editTextPassword.setText(SingleSharedPreference.getInstance(this).getStringExtra("password"));
            autoLogin.setChecked(true);

            signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
        }

        Button mEmailSignUpButton = (Button) findViewById(R.id.btn_signUp);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_signIn);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidEmail(editTextEmail.getText().toString()) && isValidPassword(editTextPassword.getText().toString())) {
                    signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
            }
        });

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                    autoLoginChecked = true;
                else {
                    autoLoginChecked = false;
                    SingleSharedPreference.getInstance(LoginActivity.this).clearExtra();
                }
            }
        });

    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!checkSKKUEmail(email)) {
            Toast.makeText(LoginActivity.this, "성균관대 이메일만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(LoginActivity.this, "잘못된 비밀번호 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private boolean checkSKKUEmail(String email)
    {
        return email.endsWith("skku.edu");
    }

    private boolean checkEmailVerified()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user.isEmailVerified())
            return true;

        else
            return false;
    }

    private void signIn(final String email, final String password   ) {
        final LoadingDialog dialog = new LoadingDialog(LoginActivity.this);
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(checkEmailVerified()) {
                                //로그인 성공 시 다음 액티비티로 전환
                                mDatabase = FirebaseDatabase.getInstance().getReference();

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        uid = user.getUid();

                                        if(autoLoginChecked == true)
                                        {
                                            SingleSharedPreference.getInstance(LoginActivity.this).putStringExtra("email", email);
                                            SingleSharedPreference.getInstance(LoginActivity.this).putStringExtra("password", password);
                                            SingleSharedPreference.getInstance(LoginActivity.this).putBooleanExtra("autoLogin", true);
                                        }

                                        firstLogin = (boolean) dataSnapshot.child("Users").child(uid).child("firstLogin").getValue();

                                        if(firstLogin == true){
//                                            dialog.dismiss();
                                            mDatabase.child("Users").child(uid).child("firstLogin").setValue(false);
                                            Intent intent = new Intent(LoginActivity.this, FindSubjectActivity.class);
                                            intent.putExtra("myUid", user.getUid());
                                            startActivity(intent);
                                            finish();
                                        }

                                        else if(firstLogin == false){
                                            boolean report_flag=false;
                                            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Users").child(uid).getChildren())
                                            {
                                                if(dataSnapshot1.getKey().equals("report_count"))
                                                    report_flag=true;
                                            }
                                            if(report_flag&&dataSnapshot.child("Users").child(uid).child("report_count").getValue().toString().equals("2")) {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "신고를 2번이상 받으셔서 로그인하실 수 없습니다.", Toast.LENGTH_LONG).show();
                                            }
                                            else{
//                                                dialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, sample_activity_1.class);
                                                //Intent intent = new Intent(LoginActivity.this, RankingActivity.class);
                                                intent.putExtra("myUid", user.getUid());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                            else {
                                //로그인 실패
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "로그인 실패. 이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}

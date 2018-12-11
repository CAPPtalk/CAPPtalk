package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jangheejun.capptalk.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;


public class SignUpActivity  extends Activity {

    private FirebaseAuth mAuth;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private EditText editTextName;

    public static Activity SignUp;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^*?_~]{6,16}$");

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        editTextPasswordConfirm = findViewById(R.id.et_passwordConfirm);
        editTextName = findViewById(R.id.et_name);

        SignUp = SignUpActivity.this;

        Button btn = (Button) findViewById(R.id.btn_OK);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValidEmail(editTextEmail.getText().toString()) && isValidPassword(editTextPassword.getText().toString()) && checkPasswordConfirm(editTextPassword.getText().toString(), editTextPasswordConfirm.getText().toString()) && isValidNickname(editTextName.getText().toString())) {
                    signUp(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!checkSKKUEmail(email)) {
            Toast.makeText(SignUpActivity.this, "성균관대 이메일만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(SignUpActivity.this, "잘못된 비밀번호 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private boolean checkPasswordConfirm(String password, String passwordConfirm){
        if(password.equals(passwordConfirm))
            return true;
        else{
            Toast.makeText(SignUpActivity.this, "비밀번호 확인이 틀렸습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidNickname(String name) {
        if (name.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private boolean checkSKKUEmail(String email)
    {
        return email.endsWith("skku.edu");
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(SignUpActivity.this, "이메일 전송 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getUserId()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.getUid();
    }

    private void signUp(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            sendVerificationEmail();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

                            String uid = getUserId();

                            UserModel current_user = new UserModel(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextName.getText().toString());
                            mDatabase.child("Users").child(uid).setValue(current_user);
                            //mDatabase.child("Users").child(uid).child("ProfilePhoto").child("default").setValue(true);
                            //mDatabase.child("Users").child(myUid).child("ProfilePhoto").child("url").setValue(null);

                            Intent intent = new Intent(SignUpActivity.this, SignUpCompleteActivity.class);

                            if(email.endsWith("g.skku.edu"))
                                intent.putExtra("host", "GOOGLE");
                            else
                                intent.putExtra("host", "SKKU");

                            startActivity(intent);

                        } else {
                            Toast.makeText(SignUpActivity.this, "이미 가입한 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}


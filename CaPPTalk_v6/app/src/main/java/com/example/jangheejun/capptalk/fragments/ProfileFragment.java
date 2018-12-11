package com.example.jangheejun.capptalk.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jangheejun.capptalk.LoginActivity;
import com.example.jangheejun.capptalk.R;
import com.example.jangheejun.capptalk.RankingActivity;
import com.example.jangheejun.capptalk.Reported;
import com.example.jangheejun.capptalk.SingleSharedPreference;
import com.example.jangheejun.capptalk.sample_activity_1;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    String email;
    String name;
    String point;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String url;

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    boolean check_default;

    int num;

    ImageView ProfileImage;
    Bitmap bm;

    sample_activity_1 Previous;

    public  ProfileFragment(){
    }

    public String getName(){
        return this.name;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPoint(){
        return this.point;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_profile,container,false);

        ((sample_activity_1)getActivity()).click = 0;
        ProfileImage = (ImageView) view.findViewById(R.id.image);

        mAuth = FirebaseAuth.getInstance();
        Previous = (sample_activity_1) sample_activity_1.SA;

        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String Case = snapshot.getKey();
                    switch(Case){
                        case "url":

                            //check_default = (boolean) snapshot.child("default").getValue();
                            url = snapshot.getValue().toString();

                            if(url == null) {

                                ProfileImage.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.user));

                            }

                            else {
                                Picasso.with(getActivity())
                                        .load(url)
                                        .fit()
                                        .centerInside()
                                        .into(ProfileImage, new Callback.EmptyCallback() {
                                            public void onSuccess() {
                                            }
                                        });
                            }
                            break;
                        case "email":
                            email = snapshot.getValue().toString();
                            break;
                        case "userName":
                            name = snapshot.getValue().toString();
                            break;
                        case "report_flag":
                            if(snapshot.getValue().toString().equals("true"))
                            {
                                Intent temp = new Intent(getActivity(), Reported.class);
                                temp.putExtra("text", dataSnapshot.child("report").child("report_text").getValue().toString());
                                startActivity(temp);
                                mDatabase.child("Users").child(uid).child("report_flag").setValue("false");
                            }
                            break;
                        case "points":
                            point = snapshot.getValue().toString();
                            break;
                        default:
                            url = snapshot.getValue().toString();
                                ProfileImage.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.user));
                            break;
                    }
                }

                TextView textemail = (TextView)view.findViewById(R.id.email);
                TextView textname = (TextView)view.findViewById(R.id.Nickname);
                TextView textpoint = (TextView)view.findViewById(R.id.Point);
                textemail.setText(email);
                textname.setText(name);
                textpoint.setText(point);
                ImageView tierImg = (ImageView)view.findViewById(R.id.tier_image);
                TextView tierText = (TextView)view.findViewById(R.id.tier_text);
                String tier,tierImgStr;

                if(Integer.parseInt(point)>40){
                    tier="Diamond";
                    tierImgStr="diamond";
                }
                else if(Integer.parseInt(point)>30){
                    tier="Platinum";
                    tierImgStr="platinum";
                }
                else if(Integer.parseInt(point)>20){
                    tier="Gold";
                    tierImgStr="gold";

                }else if(Integer.parseInt(point)>10){
                    tier="Silver";
                    tierImgStr="silver";
                }
                else{
                    tier="Bronze";
                    tierImgStr="bronze";
                }
                int id = getActivity().getResources().getIdentifier(tierImgStr, "drawable", getActivity().getApplicationContext().getPackageName());

                Picasso.with(getActivity())
                        .load(id).fit().centerCrop().into(tierImg);
                tierText.setText(tier);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        Button btn = (Button)view.findViewById(R.id.btn_signOut);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(),LoginActivity.class);
                SingleSharedPreference.getInstance(view.getContext()).clearExtra();
                startActivity(intent);
                Previous.finish();
            }
        });

        ImageView rankingimage = (ImageView) view.findViewById(R.id.ranking);
        rankingimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Users").child(uid).child("gotolist").setValue(0);
                Intent intent = new Intent(view.getContext(),RankingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri image = data.getData();

            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);

                final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] baosData = baos.toByteArray();

                final StorageReference profileRef = mStorageRef.child("ProfilePhoto/" + uid + ".jpg");

                //ProfileImage.setImageBitmap(bm);

                UploadTask uploadTask = profileRef.putBytes(baosData);
                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                //String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                //Toast.makeText(SignUpActivity.this, downloadUrl, Toast.LENGTH_SHORT).show();

                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        //mDatabase.child("Users").child(uid).child("ProfilePhoto").child("default").setValue(false);
                                        //mDatabase.child("Users").child(uid).child("ProfilePhoto").child("url").setValue(uri.toString());
                                        mDatabase.child("Users").child(uid).child("url").setValue(uri.toString());

                                        Picasso.with(getActivity())
                                                .load(uri.toString())
                                                .fit()
                                                .centerInside()
                                                .into(ProfileImage, new Callback.EmptyCallback() {
                                                    public void onSuccess() {
                                                    }
                                                });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Toast.makeText(sample_activity_1.this, "URL 획득 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                //mDatabase.child("Users").child(getUserId()).child("ProfilePhoto").setValue(downloadUrl);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                //Toast.makeText(sample_activity_1.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

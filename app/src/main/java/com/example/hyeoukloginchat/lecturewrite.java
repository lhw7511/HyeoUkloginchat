package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hyeoukloginchat.model.LectureModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class lecturewrite extends AppCompatActivity {

    String uid  = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturewrite);
       final EditText title = (EditText)findViewById(R.id.lecturewrite_title);
        final EditText content = (EditText)findViewById(R.id.lecturewrite_content);

       Button button  = (Button)findViewById(R.id.lecturewrite_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final   RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
                loderlayout.setVisibility(View.VISIBLE);
                final LectureModel lectureModel = new LectureModel();
                final String title2  = title.getText().toString();
                final String content2 = content.getText().toString();
                lectureModel.title1=title2;
                lectureModel.content=content2;
               FirebaseDatabase.getInstance().getReference().child("lecture").child(uid).setValue(lectureModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       loderlayout.setVisibility(View.GONE);
                       lecturewrite.this.finish();

                     //  startActivity(new Intent(lecturewrite.this,loginmain.class));
                   }
               });
            }
        });

    }


}

package com.example.hyeoukloginchat.loginmainfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hyeoukloginchat.R;
import com.example.hyeoukloginchat.boardwriteex;
import com.example.hyeoukloginchat.lecturewrite;
import com.example.hyeoukloginchat.model.LectureModel;
import com.example.hyeoukloginchat.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class lecturefragment extends Fragment {
    Spinner spinner;
    FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lecturefragment,container,false);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.lecturefragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), lecturewrite.class));
            }
        });
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.lecturerecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setAdapter(new lecturefragmentadapter());
        return view;
    }


    class lecturefragmentadapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
           List<LectureModel> lectureModels;

        public lecturefragmentadapter() {
            lectureModels  = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("lecture").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lectureModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        LectureModel lectureModel = snapshot.getValue(LectureModel.class);
                        //친구목록에서 자신의 이름과사진 안보이게하는코드
                        lectureModels.add(lectureModel);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlecture,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).title.setText(lectureModels.get(position).title1);
            ((CustomViewHolder)holder).content.setText(lectureModels.get(position).content);

        }

        @Override
        public int getItemCount() {
            return lectureModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
                   public TextView title;
            public TextView content;
             public CustomViewHolder(View view) {
                super(view);
                  title = (TextView) view.findViewById(R.id.lecture_title);
                 content =(TextView) view.findViewById(R.id.lecture_content);

            }
        }
    }
}

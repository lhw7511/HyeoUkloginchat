package com.example.hyeoukloginchat.loginmainfragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hyeoukloginchat.R;
import com.example.hyeoukloginchat.chat.message;
import com.example.hyeoukloginchat.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;

public class peoplefragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.peoplefragment,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setAdapter(new peoplefragmentrecyclerviewadapter());


        FloatingActionButton floatingActionButton =(FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),selectpeople.class));
            }
        });
        setHasOptionsMenu(true);
        return view;
    }
    //친구목록이 database를 통해서 쌓임
    class peoplefragmentrecyclerviewadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
      List<UserModel> userModels;
      public  peoplefragmentrecyclerviewadapter(){
          userModels = new ArrayList<>();

          final String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
          FirebaseDatabase.getInstance().getReference().child("users").orderByChild("UserName").addValueEventListener(new ValueEventListener() {
                      @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //누적데이터없앰
                   userModels.clear();

                  for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                      UserModel userModel = snapshot.getValue(UserModel.class);
                      //친구목록에서 자신의 이름과사진 안보이게하는코드
                      if(userModel.uid.equals(myuid)){
                          continue;
                      }
                      userModels.add(userModel);
                  }
                  notifyDataSetChanged();
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          }) ;
      }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {            //아이템xml을통해 디자인
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itempeople,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {      //친구목록리스트 디자인
          Glide.with(holder.itemView.getContext())
                         .load(userModels.get(position).profileImageUrl)
                         .apply(new RequestOptions().circleCrop())
                         .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).UserName);
             holder.itemView.setOnClickListener(new View.OnClickListener() {
                     @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                     @Override
                     public void onClick(View view) {
                         Intent intent = new Intent(view.getContext(), message.class);
                         intent.putExtra("destinationuid",userModels.get(position).uid);//친구목록에서 친구누를시 그 상대방의 uid가져옴
                         //화면전환 애니메이션
                         ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                         startActivity(intent,activityOptions.toBundle());
                     }
             });
             //상태메세지가 널값이아니면 상태메세지를 텍스트로받아옴
            if (userModels.get(position).comment != null) {
                ((CustomViewHolder) holder).comment.setText(userModels.get(position).comment);
            }else{
                //널이면 텍스트를 띄우지않음
                ((CustomViewHolder) holder).comment.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
          public ImageView imageView;
          public TextView textView;
          public  TextView comment;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.itempeople_imageview);
                textView =(TextView) view.findViewById(R.id.itempeople_textview);
                comment = (TextView)view.findViewById(R.id.itempeople_texview_comment);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


}

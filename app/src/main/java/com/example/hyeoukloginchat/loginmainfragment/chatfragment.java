package com.example.hyeoukloginchat.loginmainfragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.hyeoukloginchat.chat.chatmodel;
import com.example.hyeoukloginchat.chat.groupmessage;
import com.example.hyeoukloginchat.chat.message;
import com.example.hyeoukloginchat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class chatfragment extends Fragment {

     //마지막 메세지에대한 타임스탬프를 표기하는 포멧
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.chatfragment,container,false);


        RecyclerView recyclerView = view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        setHasOptionsMenu(true);
        return view;
    }
    class  ChatRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<chatmodel> chatmodels = new ArrayList<>();
        private  List<String> keys  = new ArrayList<>();
       private  ArrayList<String>  destinationUsers  = new ArrayList<>();
        private  String uid;
        public ChatRecyclerViewAdapter() {  //채팅목록 가져오기
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();  //유저이름
            //소속된방 가져오기
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatmodels.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren()){
                        chatmodels.add(item.getValue(chatmodel.class));
                        keys.add(item.getKey());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemchat,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destinationuid = null;
            for (String user : chatmodels.get(position).users.keySet()) {
                if (!user.equals(uid)) {
                    destinationuid = user;
                    destinationUsers.add(destinationuid);//상대방 데이터가져옴  챗팅리스트에서 누르면 바로 챗팅방들어가지는 기능
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationuid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);    //상대 이미지를 방 이미지에 띄움
                    customViewHolder.textView_title.setText(userModel.UserName);  //유저이름을 방이름에 띄움
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져와서 띄움
            Map<String, chatmodel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatmodels.get(position).comments);
            if (commentMap.keySet().toArray().length > 0) {
                String lastmessagekey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_lastmessage.setText(chatmodels.get(position).comments.get(lastmessagekey).message);

                //타임스탬프를 마지막메세지로 받아와 아시아 서울지역시간으로 설정한다.
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) chatmodels.get(position).comments.get(lastmessagekey).timestamp;
                Date date = new Date(unixTime);
                customViewHolder.texview_timestamp.setText(simpleDateFormat.format(date));
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {  //채팅방리스트에서 누르면 챗팅방으로 넘어가는 인텐트
                    Intent intent = null;
                    if (chatmodels.get(position).users.size()>2){
                        intent = new Intent(view.getContext(), groupmessage.class);
                        intent.putExtra("destinationroom",keys.get(position));
                    }else {


                        intent = new Intent(view.getContext(), message.class);
                        intent.putExtra("destinationuid", destinationUsers.get(position));
                    }

                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }
        @Override
        public int getItemCount() {
            return chatmodels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView  textView_title;
            public  TextView textView_lastmessage;
            public  TextView texview_timestamp;
            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.itemchat_imageview);
                textView_title = view.findViewById(R.id.itemchat_textview);
                textView_lastmessage = view.findViewById(R.id.itemchat_textview_lastmessage);
                texview_timestamp = view.findViewById(R.id.itemchat_textview_timestamp);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

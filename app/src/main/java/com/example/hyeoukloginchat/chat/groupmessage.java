package com.example.hyeoukloginchat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hyeoukloginchat.R;
import com.example.hyeoukloginchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class groupmessage extends AppCompatActivity {
Map<String, UserModel> users  = new HashMap<>();
String destinaionroom;
String uid;
EditText editText;
    private DatabaseReference databaseReference;
    private  ValueEventListener valueEventListener;
    private  RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");   //시간표현 포멧방식
    List<chatmodel.Comment> comments  =new ArrayList<>();
    int peoplecounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmessage);
        destinaionroom  =getIntent().getStringExtra("destinationroom");
        uid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText  =(EditText) findViewById(R.id.groupmessage_edittext);
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot item: dataSnapshot.getChildren()){
                  users.put(item.getKey(),item.getValue(UserModel.class));
              }
                init();
                recyclerView =(RecyclerView)findViewById(R.id.groupmessage_recyclerview);
                recyclerView.setAdapter(new Groupmessagerecylceradapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(groupmessage.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void init(){
        Button button =(Button)findViewById(R.id.groupmessage_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatmodel.Comment comment   = new chatmodel.Comment();
                comment.uid  =uid;
                comment.message  =editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinaionroom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       editText.setText("");
                    }
                });
            }
        });
    }
    class Groupmessagerecylceradapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public Groupmessagerecylceradapter() {
            getMessageList();
        }
        void getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinaionroom).child("comments");
            valueEventListener= databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();//데이터가쌓이는걸 방지 누적되서 초기부터계속 쌓이지않게함
                    //메세지를 읽었는지 파이어베이스 디비 내에서 확인함.
                    Map<String,Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        chatmodel.Comment comment_origin  = item.getValue(chatmodel.Comment.class);
                        chatmodel.Comment comment_motify  = item.getValue(chatmodel.Comment.class);
                        comment_motify.readUsers.put(uid,true);
                        readUsersMap.put(key,comment_motify);
                        comments.add(comment_origin);
                    }
                    if (comments.size()==0){
                        return;
                    }
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)) {
                        //디비에 챗룸에 코멘트에서 readusers를 통해확인
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinaionroom).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //메시지 갱신
                                notifyDataSetChanged();
                                //메시지대화위치를 최신위치(마지막)으로내림
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    }else{
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size()-1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemmessage,parent,false);
            return  new Groupmessageviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Groupmessageviewholder messageViewHolder = ((Groupmessageviewholder)holder);
            //내가 보낸 메세지


            if(comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                //첫번 uid = comments안에 uid , 두번쨰 uid = 나의 uid
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);//내 대화는 오른쪽
                //내가보낸메세지는 읽음표시숫자가 왼쪽에뜨게함
                setReadCounter(position,messageViewHolder.texview_readcounterleft);

                //상대방이 보낸메세지
            }else{
                Glide.with(holder.itemView.getContext())

                        .load(users.get(comments.get(position).uid).profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).UserName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                //상대방이 보낸 메세지 읽음표시숫자는 오른쪽에뜨게함
                setReadCounter(position,messageViewHolder.texview_readcounterright);

            }
            //타임스탬프텍스트에 아시아의 서울지역을 포맷으로하여 설정.
            long  unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.texview_timestamp.setText(time);
        }
        //메세지수만큼  인원수를 서버에 물어보는 메소드 처음에만 물어보게해야 서버에 무리가없음
        void setReadCounter(final int position, final TextView textView){
            if (peoplecounter==0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinaionroom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peoplecounter =users.size();
                        //읽지않은사람 수 = 전체유저 - 읽은사람수
                        int count = peoplecounter - comments.get(position).readUsers.size();
                        //읽지않은사람 수가 0보다크면 표시 아니면 표시하지않음음
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));

                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{  int count = peoplecounter - comments.get(position).readUsers.size();
                //읽지않은사람 수가 0보다크면 표시 아니면 표시하지않음음
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));

                } else {
                    textView.setVisibility(View.INVISIBLE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class Groupmessageviewholder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public  TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public  TextView texview_timestamp;
            public TextView texview_readcounterleft;
            public TextView texview_readcounterright;
            public Groupmessageviewholder(View view) {
                super(view);
                textView_message = (TextView)view.findViewById(R.id.itemmessage_textview);
                textView_name = (TextView)view.findViewById(R.id.itemmessage_textview_name);
                imageView_profile =(ImageView)view.findViewById(R.id.itemmessage_imageview_profile);
                linearLayout_destination =(LinearLayout)view.findViewById(R.id.itemmessage_linearlayout_destination);
                linearLayout_main =(LinearLayout)view.findViewById(R.id.itemmessage_linearlayout_main);
                texview_timestamp = (TextView)view.findViewById(R.id.itemmessage_textview_timestamp);
                texview_readcounterleft =(TextView)view.findViewById(R.id.itemmessage_textview_readcounterleft);
                texview_readcounterright =(TextView)view.findViewById(R.id.itemmessage_textview_readcounterright);
            }
        }
    }
}

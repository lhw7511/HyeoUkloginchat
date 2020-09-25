package com.example.hyeoukloginchat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
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
import com.google.android.gms.tasks.OnSuccessListener;
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
//1:1 대화 액티비티
public class message extends AppCompatActivity {
private String destinationuid;
private Button button;
private EditText editText;
private  String uid;
private String chatroomuid;
private RecyclerView recyclerView;
private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");   //시간표현 포멧방식
    private DatabaseReference databaseReference;
    private  ValueEventListener valueEventListener;
    int peoplecounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅요구 하는 아이디
        //상대방의uid
        destinationuid =getIntent().getStringExtra("destinationuid");
   button=(Button)findViewById(R.id.message_button);
   editText=(EditText)findViewById(R.id.message_edittext);
   recyclerView =(RecyclerView) findViewById(R.id.message_recyclerview);
   button.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           chatmodel chatmodel = new chatmodel();
           chatmodel.users.put(uid,true);
           chatmodel.users.put(destinationuid,true);

           //챗팅데이터베이스 push를 넣어야 챗팅방이름이생김 프라이머리키개념
           if(chatroomuid ==null){
               //채팅방의 uid가없으면 생성
               button.setEnabled(false);//챗팅방디비가 서버에 들어올때까지 버튼기능 꺼줘서 버그방지
               FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                                checkchatroom();
                   }
               });
           }else{  //이미있을경우 대화만 집어넣음
               chatmodel.Comment comment = new chatmodel.Comment();
               comment.uid = uid;
               comment.message = editText.getText().toString();
               comment.timestamp = ServerValue.TIMESTAMP;//메세지시간 보내서출력하게하는 파이어베이스 메소드
               FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                      //보내면 글 초기화
                        editText.setText("");
                   }
               });
           }

       }
   });
        checkchatroom();
    }
    void checkchatroom(){
        //같은 상대방과 사용자가 채팅해서 보낼시 중복해서 챗팅룸이 만들어지는것을방지
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item: dataSnapshot.getChildren()){
                    chatmodel chatmodel = item.getValue(chatmodel.class);
                    if(chatmodel.users.containsKey(destinationuid)&& chatmodel.users.size() ==2){
                        chatroomuid = item.getKey();
                        button.setEnabled(true);//채팅방이 디비에 들어가면 버튼 정상작동
                        recyclerView.setLayoutManager(new LinearLayoutManager(message.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

   class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<chatmodel.Comment> comments;
        UserModel userModel;
        public RecyclerViewAdapter() {
         comments = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(destinationuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             userModel = dataSnapshot.getValue(UserModel.class);
                getMessageList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }
        //메세지를 보여주는 메소드
        void getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments");
          valueEventListener= databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();//데이터가쌓이는걸 방지 누적되서 초기부터계속 쌓이지않게함
                    //메세지를 읽었는지 파이어베이스 디비 내에서 확인함.
                    Map<String,Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        //chatmodel.Comment comment_origin  = item.getValue(chatmodel.Comment.class);
                        chatmodel.Comment comment_modify  = item.getValue(chatmodel.Comment.class);
                        comment_modify.readUsers.put(uid,true);
                        readUsersMap.put(key,comment_modify);
                        comments.add(item.getValue(chatmodel.Comment.class));
                    }
                    if (comments.size()==0){
                        return;
                    }
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)) {
                        //디비에 챗룸에 코멘트에서 readusers를 통해확인
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        //메세지를 보여주게하는 리사이클러뷰
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemmessage,parent,false);
            return new MessageViewHolder(view);
        }
     //나의 이름과 사진은 보이지않고 말풍선에 대화내용만보이게하고 상대방의 사진과 이름은 보여지게함.
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
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

                        .load(userModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
               messageViewHolder.textView_name.setText(userModel.UserName);
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
          FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
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
            return  comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public  TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public  TextView texview_timestamp;
            public TextView texview_readcounterleft;
            public TextView texview_readcounterright;
            public MessageViewHolder(View view) {
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
    //챗팅방에서 다시 리스트로 갈때에도 애니메이션 효과줌
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(valueEventListener!= null) {


            //챗팅방에서 뒤로가기를 눌러도 메세지를 계속 읽어들이는 버그를 잡는코드.
            databaseReference.removeEventListener(valueEventListener);
        }
            finish();
            overridePendingTransition(R.anim.fromleft, R.anim.toright);

    }
}

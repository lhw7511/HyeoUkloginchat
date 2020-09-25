package com.example.hyeoukloginchat.loginmainfragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hyeoukloginchat.Major;
import com.example.hyeoukloginchat.R;
import com.example.hyeoukloginchat.RecyclerViewItemClickListener;
import com.example.hyeoukloginchat.Schedule;
import com.example.hyeoukloginchat.account;
import com.example.hyeoukloginchat.boardwriteex;
import com.example.hyeoukloginchat.login;
import com.example.hyeoukloginchat.model.BoardModel;
import com.example.hyeoukloginchat.noticecontents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

public class homefragment extends Fragment  implements RecyclerViewItemClickListener.OnItemClickListener{
    private static final String TAG = "boardex";
       BoardwriteAdapter boardwriteAdapter;

      ArrayList<BoardModel> boardModels;
    private ImageButton homepage;
    private  ImageButton schedule;
    private  ImageButton account;
    private  ImageButton logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homefragment,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.boardex_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(),recyclerView,this));
       setHasOptionsMenu(true);
        boardModels   =new ArrayList<>();

        boardwriteAdapter  = new BoardwriteAdapter(boardModels);

        recyclerView.setAdapter(boardwriteAdapter);

        FloatingActionButton floatingActionButton =(FloatingActionButton)view.findViewById(R.id.homefragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getActivity().finish();
                startActivity(new Intent(view.getContext(),boardwriteex.class));
            }
        });

        homepage=(ImageButton)view.findViewById(R.id.homepage);
        schedule=(ImageButton)view.findViewById(R.id.schedule);
        account=(ImageButton)view.findViewById(R.id.account);
        logout=(ImageButton)view.findViewById(R.id.logout);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Schedule.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder alert_confirm = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();

                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(getContext(), login.class));

                    }
                });
                alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert_confirm.show();
            }
        });
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Major.class);
                startActivity(intent);
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), account.class);
                startActivity(intent);

            }
        });
        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("notice");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            boardModels.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String title  = (String) document.getData().get("title");
                                String name  = (String) document.getData().get("name");
                                String time  = (String) document.getData().get("time");
                                String imageurl  = (String) document.getData().get("imageurl");
                                String id   = (String)document.getData().get("id");
                                String uid  =(String)document.getData().get("uid");
                                BoardModel data = new BoardModel(title,name,time,imageurl,id,uid);
                                boardModels.add(data);
                            }
                            boardwriteAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });


        return  view;

    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent( view.getContext(), noticecontents.class);
        intent.putExtra("destinationuids",boardModels.get(position).id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        if (boardModels.get(position).uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            AlertDialog.Builder  builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(false);
            builder.setMessage("삭제 하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    FirebaseFirestore.getInstance().collection("notice").document(boardModels.get(position).id).delete();
                    Toast.makeText(getContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();


                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setTitle("게시글 삭제");
            builder.show();
        }
        else{
            return;
        }


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.BLACK);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                boardwriteAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private  class BoardwriteAdapter extends RecyclerView.Adapter<homefragment.BoardwriteAdapter.BoardwriteViewHolder>implements Filterable  {
        ArrayList<BoardModel> boardModels;


        public BoardwriteAdapter(ArrayList<BoardModel> boardModels) {
            this.boardModels= boardModels;


        }


        @NonNull
        @Override
        public BoardwriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BoardwriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemboardlist,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull homefragment.BoardwriteAdapter.BoardwriteViewHolder holder, final int position) {
            BoardModel data = boardModels.get(position);
            holder.title.setText(data.getTitle());
            holder.username.setText(data.getName());
            holder.time.setText(data.getTime());

            Glide.with(holder.itemView.getContext())
                    .load(boardModels.get(position).imageurl)
                    .into(holder.image);


        }

        @Override
        public int getItemCount() {
            return boardModels.size();
        }

        @Override
        public Filter getFilter() {
            return searchItemListFilter;
        }
        private Filter searchItemListFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<BoardModel> filteredList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(boardModels);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();

                    for (BoardModel model : boardModels) {
                        if(model.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(model);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                boardModels.clear();
                boardModels.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        };


        class BoardwriteViewHolder extends  RecyclerView.ViewHolder{
            public TextView title;
            public TextView username;
            public TextView time;
            public ImageView image;

            public BoardwriteViewHolder(@NonNull View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.itemboardlist_title);
                username = (TextView)itemView. findViewById(R.id.itemboardlist_name);
                time = (TextView) itemView.findViewById(R.id.itemboardlist_time);
                image = (ImageView) itemView.findViewById(R.id.itemboardlist_image);

            }
        }




    }

}

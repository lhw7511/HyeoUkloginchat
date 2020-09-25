package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyeoukloginchat.model.freeboardmodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class freeboard extends AppCompatActivity  implements RecyclerViewItemClickListener.OnItemClickListener{
    final ArrayList<freeboardmodel>  freeboardmodels   = new ArrayList<>();
    private static final String TAG = "freeboard";
    FreeboardAdapter freeboardAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard);
        Toolbar toolbar =(Toolbar)findViewById(R.id.freeboardtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.freeboard_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeboard.this.finish();
                startActivity(new Intent(view.getContext(),Freeboard_write.class));
            }
        });
        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("freeboard");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            freeboardmodels.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String title  = (String) document.getData().get("title");
                                String name  = (String) document.getData().get("name");
                                String time  = (String) document.getData().get("time");
                                String imageurl  = (String) document.getData().get("imageurl");
                                String id   = (String)document.getData().get("id");
                                String uid  =(String)document.getData().get("uid");
                                freeboardmodel data = new freeboardmodel(title,name,time,imageurl,id,uid);
                                freeboardmodels.add(data);
                            }
                            freeboardAdpater.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        freeboardAdpater  = new FreeboardAdapter(freeboardmodels);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.freeboard_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getApplicationContext(),recyclerView,this));
        recyclerView.setAdapter(freeboardAdpater);

    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent( view.getContext(), freeboardcontents.class);
        intent.putExtra("freeboarduid",freeboardmodels.get(position).id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, final int position) {

        if (freeboardmodels.get(position).uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            AlertDialog.Builder  builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(false);
            builder.setMessage("삭제 하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    FirebaseFirestore.getInstance().collection("freeboard").document(freeboardmodels.get(position).id).delete();
                    Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();


                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        //SearchView.SearchAutoComplete searchAutoComplete =
               // (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        //searchAutoComplete.setHintTextColor(Color.BLACK);
       // searchAutoComplete.setTextColor(Color.BLACK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              freeboardAdpater.getFilter().filter(newText);
                return false;
            }
        });
       // return super.onCreateOptionsMenu(menu);
        return true;
    }
/*
  public  class FreeboardAdpater extends  RecyclerView.Adapter<FreeboardAdpater.FreeboardViewHolder> implements  Filterable{

      ArrayList<freeboardmodel> freeboardmodelListfull;
        ArrayList<freeboardmodel> freeboardmodelList;

        public FreeboardAdpater(   ArrayList<freeboardmodel> freeboardmodels) {
            freeboardmodelList=freeboardmodels;
            freeboardmodelListfull = new ArrayList<>(freeboardmodels) ;
        }

        @NonNull
        @Override
        public FreeboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FreeboardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_freeboard,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FreeboardAdpater.FreeboardViewHolder holder,  final int position) {
            freeboardmodel data = freeboardmodelList.get(position);
            holder.title.setText(data.getTitle());
            holder.username.setText(data.getName());
            holder.time.setText(data.getTime());

            Glide.with(holder.itemView.getContext())
                    .load(freeboardmodelList.get(position).imageurl)
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return freeboardmodelList.size();
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }
       private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
               List<freeboardmodel> filteredList   = new ArrayList<>();
               if (charSequence==null || charSequence.length()==0){
                   filteredList.addAll(freeboardmodelListfull);
               }else
               {
                   String filterPattern = charSequence.toString().toLowerCase().trim();
                   for (freeboardmodel model:freeboardmodelListfull ){
                       if (model.getTitle().toLowerCase().contains(filterPattern)){
                           filteredList.add(model);
                       }
                   }
               }
               FilterResults filterResults   = new FilterResults();
                filterResults.values = filteredList;
               return  filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                freeboardmodelList.clear();
                freeboardmodelList.addAll((List)filterResults.values);
                     notifyDataSetChanged();
            }
        };

        public  class FreeboardViewHolder extends  RecyclerView.ViewHolder {
            public TextView title;
            public TextView username;
            public TextView time;
            public ImageView image;
            public FreeboardViewHolder(@NonNull View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.freeboard_title);
                username = (TextView)itemView. findViewById(R.id.freeboard_name);
                time = (TextView) itemView.findViewById(R.id.freeboard_time);
                image = (ImageView) itemView.findViewById(R.id.freeboard_image);
            }
        }
    }
    */
}

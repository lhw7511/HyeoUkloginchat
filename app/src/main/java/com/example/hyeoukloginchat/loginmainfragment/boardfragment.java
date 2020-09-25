package com.example.hyeoukloginchat.loginmainfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyeoukloginchat.R;
import com.example.hyeoukloginchat.clubboard;
import com.example.hyeoukloginchat.fourboard;
import com.example.hyeoukloginchat.freeboard;
import com.example.hyeoukloginchat.lostboard;
import com.example.hyeoukloginchat.model.freeboardmodel;
import com.example.hyeoukloginchat.oneboard;
import com.example.hyeoukloginchat.threeboard;
import com.example.hyeoukloginchat.twoboard;

import java.util.ArrayList;
import java.util.List;

public class boardfragment extends Fragment {
    BoardfragmentAdapter  itemsAdapter ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.boardfragment,container,false);
       RecyclerView recyclerView  = (RecyclerView)view.findViewById(R.id.boardfragment_recyclerview);
        ArrayList<BoardFragmentDTO> boardFragmentDTOLists =new ArrayList<>();
        boardFragmentDTOLists.add(new BoardFragmentDTO("자유게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("동아리게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("1학년게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("2학년게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("3학년게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("4학년게시판"));
        boardFragmentDTOLists.add(new BoardFragmentDTO("분실물게시판"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setHasFixedSize(true);
        itemsAdapter =  new BoardfragmentAdapter(boardFragmentDTOLists);
        recyclerView.setAdapter(itemsAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class BoardfragmentAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>implements  Filterable {
        ArrayList<BoardFragmentDTO> mitems;
        ArrayList<BoardFragmentDTO> searchItemListFull;

      public BoardfragmentAdapter(ArrayList<BoardFragmentDTO> program) {
          this.mitems = program;
          searchItemListFull= new ArrayList<>(program);


      }

      @NonNull
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemboard,parent,false);

          return new CustomViewHolder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
          ((CustomViewHolder)holder).textView.setText(mitems.get(position).board);
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  switch (position){
                      case 0:
                          Intent intent  = new Intent(getActivity(), freeboard.class);
                          startActivity(intent);
                          break;
                      case 1:
                          Intent intent1  = new Intent(getContext(), clubboard.class);
                          startActivity(intent1);
                          break;
                      case 2:
                          Intent intent2  = new Intent(getContext(), oneboard.class);
                          startActivity(intent2);
                          break;
                      case 3:
                          Intent intent3  = new Intent(getContext(), twoboard.class);
                          startActivity(intent3);
                          break;
                      case 4:
                          Intent intent4 = new Intent(getContext(), threeboard.class);
                          startActivity(intent4);
                          break;
                      case 5:
                          Intent intent5  = new Intent(getContext(), fourboard.class);
                          startActivity(intent5);
                          break;
                      case 6:
                          Intent intent6  = new Intent(getContext(), lostboard.class);
                          startActivity(intent6);
                          break;
                  }
              }
          });
      }

      @Override
      public int getItemCount() {
          return mitems.size();
      }

        @Override
        public  Filter getFilter() {
            return searchItemListFilter;
        }
        private Filter searchItemListFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<BoardFragmentDTO> filteredList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(searchItemListFull);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();

                    for (BoardFragmentDTO model : searchItemListFull) {
                        if(model.getBoard().toLowerCase().contains(filterPattern)) {
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
                mitems.clear();
                mitems.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        };


        private  class CustomViewHolder extends   RecyclerView.ViewHolder{
               public TextView textView;
          public CustomViewHolder(@NonNull View view) {
              super(view);
              textView  =(TextView)view.findViewById(R.id.board);
          }
      }
  }
}

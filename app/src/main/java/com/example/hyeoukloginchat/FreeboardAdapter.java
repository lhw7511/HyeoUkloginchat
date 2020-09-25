package com.example.hyeoukloginchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hyeoukloginchat.model.freeboardmodel;

import java.util.ArrayList;
import java.util.List;

public class FreeboardAdapter extends  RecyclerView.Adapter<FreeboardAdapter.ViewHolder> implements Filterable {

    ArrayList<freeboardmodel> freeboardmodelListfull;
    ArrayList<freeboardmodel> freeboardmodelList;

    public FreeboardAdapter(ArrayList<freeboardmodel> freeboardmodels) {
        freeboardmodelList=freeboardmodels;
        freeboardmodelListfull = new ArrayList<>(freeboardmodels) ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // return new freeboard.FreeboardAdpater.FreeboardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_freeboard,parent,false));
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_freeboard,parent,false);
        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            FilterResults results   = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            freeboardmodelList.clear();
            freeboardmodelList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public  class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView title;
        public TextView username;
        public TextView time;
        public ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.freeboard_title);
            username = (TextView)itemView. findViewById(R.id.freeboard_name);
            time = (TextView) itemView.findViewById(R.id.freeboard_time);
            image = (ImageView) itemView.findViewById(R.id.freeboard_image);
        }
    }
}
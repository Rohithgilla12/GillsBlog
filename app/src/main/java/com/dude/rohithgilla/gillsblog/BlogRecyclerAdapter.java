package com.dude.rohithgilla.gillsblog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Blogpost> blogpostList;
    public BlogRecyclerAdapter(List<Blogpost> blogpostList){
        this.blogpostList = blogpostList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String descData = blogpostList.get(position).getDesc();
        holder.setDescText(descData);
    }

    @Override
    public int getItemCount() {
        return blogpostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView descView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blogPostDescription);
            descView.setText(descText);
        }
    }
}

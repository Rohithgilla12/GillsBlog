package com.dude.rohithgilla.gillsblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Blogpost> blogpostList;
    public Context context;
    public BlogRecyclerAdapter(List<Blogpost> blogpostList){
        this.blogpostList = blogpostList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String descData = blogpostList.get(position).getDesc();
        holder.setDescText(descData);

        String image_url =blogpostList.get(position).getImage_url();
        holder.setBlogImage(image_url);

        String user_id = blogpostList.get(position).getUser_id();
        holder.setUserID(user_id);


    }

    @Override
    public int getItemCount() {
        return blogpostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView descView;
        private ImageView blogImageView;
        private TextView userName;
        private ImageView profilePicture;
        FirebaseFirestore firebaseFirestore;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blogPostDescription);
            descView.setText(descText);
        }
        public void setBlogImage(String downloadUri){
            blogImageView = mView.findViewById(R.id.blogImage);
            Glide.with(context).load(downloadUri).into(blogImageView);
        }

        public void setUserID(final String userCode){
           firebaseFirestore = FirebaseFirestore.getInstance();
           firebaseFirestore.collection("Users").document(userCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if(task.isSuccessful()){
                       String userNameText = task.getResult().getString("Name");
//                       Toast.makeText(context,userNameText,Toast.LENGTH_LONG).show();
                       userName = mView.findViewById(R.id.blogUserName);
                       userName.setText(userNameText);
                       String profileImage = task.getResult().getString("Image");
                       profilePicture = mView.findViewById(R.id.blogUserImage);
                       Glide.with(context).load(profileImage).into(profilePicture);
                   }

               }
           });
        }
    }
}

package com.dude.rohithgilla.gillsblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
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
        String thumbUri = blogpostList.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumbUri);

        String user_id = blogpostList.get(position).getUser_id();
        holder.setUserID(user_id);

        long milliSecs = blogpostList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliSecs)).toString();
        holder.setTime(dateString);

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
        private TextView timePost;
        FirebaseFirestore firebaseFirestore;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blogPostDescription);
            descView.setText(descText);
        }
        public void setBlogImage(String downloadUri, String thumbUri ){
            blogImageView = mView.findViewById(R.id.blogImage);
            RequestOptions postPlaceholder = new RequestOptions();
            postPlaceholder.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(postPlaceholder).load(downloadUri).thumbnail(Glide.with(context).load(thumbUri)).into(blogImageView);
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

                       RequestOptions profilePlaceholder = new RequestOptions();
                       profilePlaceholder.placeholder(R.drawable.profile_placeholder);
                       Glide.with(context).applyDefaultRequestOptions(profilePlaceholder).load(profileImage).into(profilePicture);
                   }

               }
           });
        }

        public void setTime(String time){
            timePost = mView.findViewById(R.id.blogPostDate);
            timePost.setText(time);
        }
    }
}

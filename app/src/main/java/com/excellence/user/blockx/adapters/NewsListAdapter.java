package com.excellence.user.blockx.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.excellence.user.blockx.R;
import com.excellence.user.blockx.models.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/19/2017.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.PostHolder> {
    private List<Post> posts=new ArrayList<Post>();
    private Context context;
    private ImageLoader imageLoader;


    public NewsListAdapter(Context context){
        for(int i = 0; i<Post.count(Post.class, null, null); i++) {
            Post post = Post.findById(Post.class, i);
            this.posts.add(0, post);
        }
        this.context = context;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        Post post = posts.get(holder.getAdapterPosition());


        /*CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timestamp.setText(timeAgo);*/

        /**
         * POSTED IMAGE
         */

        if(!TextUtils.isEmpty(post.getPostedImageUrl())) {
            /*
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            imageLoader.get(post.getPostedImageUrl(), ImageLoader.getImageListener(holder.postedImage, R.mipmap.ic_launcher,
                    android.R.drawable.ic_dialog_alert));
            holder.postedImage.setImageUrl(post.getPostedImageUrl(), imageLoader);*/
            Picasso.with(context).load(post.getPostedImageUrl()).into(holder.postedImage);
            //Picasso.with(context).load(post.getPostedImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.postedImage);
            holder.postedImage.setVisibility(View.VISIBLE);

        }else{
            holder.postedImage.setVisibility(View.GONE);
        }
        /**
         * POSTED VIDEO
         */
        if(!TextUtils.isEmpty(post.getPostedVideoUrl())){
            Uri vidUri = Uri.parse(post.getPostedVideoUrl());

            MediaController vidControl = new MediaController(context);
            vidControl.setAnchorView(holder.vidView);
            vidControl.setMediaPlayer(holder.vidView);
            holder.vidView.setMediaController(vidControl);
            holder.vidView.setVideoURI(vidUri);
            holder.vidView.start();
            holder.vidView.setVisibility(View.VISIBLE);


        }else{
            holder.vidView.setVisibility(View.GONE);
        }

        /**
         * PROFILE PICTURE
         */

        if(!TextUtils.isEmpty(post.getMemberImageUrl())) {

            //imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            //imageLoader.get(post.getMemberImageUrl(), ImageLoader.getImageListener(holder.profilePic, R.mipmap.ic_launcher,
            //        android.R.drawable.ic_dialog_alert));
            //holder.profilePic.setImageUrl(post.getMemberImageUrl(), imageLoader);
            Picasso.with(context).load(post.getMemberImageUrl()).into(holder.profilePic);

            //Picasso.with(context).load(post.getMemberImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profilePic);

            holder.profilePic.setVisibility(View.VISIBLE);
        }else{
            holder.profilePic.setVisibility(View.GONE);
        }

        /**
         * DISPLAY NAME
         */
        String strippedName = post.getPosterFullName().replace("\n", "").replace(" ", "");
        holder.fullname.setText(strippedName);

        /**
         * DISPLAY POSITION
         */
        holder.position.setText(post.getPosterPosition());

        /**
         * MESSAGE
         */

        if(!(post.getMessage()).equalsIgnoreCase("n/a")) {
            holder.postedText.setText(post.getMessage());
            holder.postedText.setVisibility(View.VISIBLE);
        } else {
            holder.postedText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public void update(){
        this.posts = Post.listAll(Post.class);
        notifyDataSetChanged();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        TextView fullname, position, postedText;
        ImageView postedImage;
        ImageView profilePic;
        VideoView vidView;
        PostHolder(View itemView) {
            super(itemView);
            postedImage = (ImageView) itemView.findViewById(R.id.postedImage);
            fullname = (TextView) itemView.findViewById(R.id.fullname);
            position = (TextView) itemView.findViewById(R.id.position);
            postedText = (TextView) itemView.findViewById(R.id.postedText);
            profilePic = (ImageView)itemView.findViewById(R.id.memberpic);
            vidView = (VideoView)itemView.findViewById(R.id.videoView);

        }
    }
}
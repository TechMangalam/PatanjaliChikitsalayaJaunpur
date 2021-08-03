package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.bitaam.patanjalichikitsalayajaunpur.utility.FullscreenActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bitaam.patanjalichikitsalayajaunpur.R;

public class DoubtAnsAdapter extends RecyclerView.Adapter<DoubtAnsAdapter.ViewHolder> {

    RecyclerView recyclerView;
    List<DoubtQuestionModel> modelList;
    Context context;
    OnItemClickListener mListener;
    ArrayList<Long> nLikes = new ArrayList<Long>();
    //AdRequest adRequest;

    public  interface OnItemClickListener{
        void onLiked(int position);

    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public DoubtAnsAdapter(RecyclerView recyclerView, Context context, List<DoubtQuestionModel> objects) {
        this.recyclerView = recyclerView;
        this.context = context;
        modelList = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comm_ans_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.profName.setText(modelList.get(position).getName());
        holder.postDate.setText(modelList.get(position).getPostDate());
        holder.question.setText(modelList.get(position).getQue());
        holder.likeCounter.setText(String.valueOf(nLikes.get(position)));
        if (!modelList.get(position).getQueImgUrl().equalsIgnoreCase("na")){
            holder.queImg.setVisibility(View.VISIBLE);
            holder.queImg.setImageURI(Uri.parse(modelList.get(position).getQueImgUrl()));
        }
        Picasso.get().load(modelList.get(position).getProfileImgUrl()).into(holder.profImg);

        holder.queImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("imgUrl",modelList.get(position).getQueImgUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void update(DoubtQuestionModel m,long likes){
        modelList.add(m);
        nLikes.add(likes);
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView profName,postDate,question;
        TextView likeCounter;
        ImageView like;
        CircleImageView profImg;
        SimpleDraweeView queImg;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Fresco.initialize(context);

            profName = itemView.findViewById(R.id.profileName);
            postDate = itemView.findViewById(R.id.date);
            question = itemView.findViewById(R.id.question);
            like = itemView.findViewById(R.id.like);
            likeCounter = itemView.findViewById(R.id.likeCounter);
            profImg = itemView.findViewById(R.id.profile_image);
            queImg = itemView.findViewById(R.id.queImg);




            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mListener.onLiked(position);
                        }
                    }
                }
            });

          }


    }
}


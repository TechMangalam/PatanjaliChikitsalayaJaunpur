package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.bitaam.patanjalichikitsalayajaunpur.utility.FullscreenActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bitaam.patanjalichikitsalayajaunpur.R;


public class DoubtQueAdapter extends RecyclerView.Adapter<DoubtQueAdapter.ViewHolder> {

    RecyclerView recyclerView;
    List<DoubtQuestionModel> modelList;
    ArrayList<Long> nComment = new ArrayList<Long>();
    ArrayList<Long> nUpvote = new ArrayList<Long>();
    Context context;
    OnItemClickListener mListener;
    //AdRequest adRequest;

    public  interface OnItemClickListener{
        void onUpvoted(int position);
        void onCommented(int position);
        void onDeleted(DoubtQuestionModel model);
        void onShowHide(DoubtQuestionModel model);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public DoubtQueAdapter(RecyclerView recyclerView, Context context, List<DoubtQuestionModel> objects) {
        this.recyclerView = recyclerView;
        this.context = context;
        modelList = objects;

    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        Fresco.initialize(context);
        View view = LayoutInflater.from(context).inflate(R.layout.comm_que_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.profName.setText(modelList.get(position).getName());
        holder.postDate.setText(modelList.get(position).getPostDate());
        holder.question.setText(modelList.get(position).getQue());
        holder.commentC.setText(String.valueOf(nComment.get(position)));
        holder.upvoteC.setText(String.valueOf(nUpvote.get(position)));

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(modelList.get(position).getAuth())){
            holder.moreMenuQue.setVisibility(View.VISIBLE);
        }else {
            holder.moreMenuQue.setVisibility(View.GONE);
        }

        if (!modelList.get(position).getQueImgUrl().equalsIgnoreCase("na")){
            holder.queImg.setVisibility(View.VISIBLE);
            holder.queImg.setImageURI(Uri.parse(modelList.get(position).getQueImgUrl()));
        }else{
            holder.queImg.setImageURI(Uri.parse(""));
            holder.queImg.setVisibility(View.GONE);
        }
        if (!modelList.get(position).getProfileImgUrl().equals("na")){
            Picasso.get().load(modelList.get(position).getProfileImgUrl()).placeholder(Objects.requireNonNull(context.getDrawable(R.drawable.profile_icon)))
                    .error(Objects.requireNonNull(context.getDrawable(R.drawable.profile_icon)))
                    .into(holder.profImg);

        }

        holder.queImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("imgUrl",modelList.get(position).getQueImgUrl());
                context.startActivity(intent);
            }
        });

        holder.moreMenuQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.moreMenuQue);
                //inflating menu from xml resource
                popup.inflate(R.menu.more_menu_question);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.showHideQueM:
                                if (modelList.get(position).isVisibility()){
                                    modelList.get(position).setVisibility(false);
                                }else{
                                    modelList.get(position).setVisibility(true);
                                }
                                if (mListener!=null){
                                    if (position!=RecyclerView.NO_POSITION){
                                        mListener.onShowHide(modelList.get(position));
                                        notifyDataSetChanged();
                                    }
                                }
                                break;

                            case R.id.deleteQueM:
                                if (mListener!=null){
                                    if (position != RecyclerView.NO_POSITION){
                                        mListener.onDeleted(modelList.get(position));
                                        modelList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }
                                break;

                        }

                        return false;
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void update(DoubtQuestionModel m,Long nCom,Long nUpv){
        modelList.add(m);
        nComment.add(nCom);
        nUpvote.add(nUpv);
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView profName,postDate,question,giveAns,upvote;
        TextView commentC,upvoteC;
        CircleImageView profImg;
        SimpleDraweeView queImg;
        ImageView moreMenuQue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            profName = itemView.findViewById(R.id.profileName);
            postDate = itemView.findViewById(R.id.date);
            question = itemView.findViewById(R.id.question);
            giveAns = itemView.findViewById(R.id.answerQue);
            upvote = itemView.findViewById(R.id.upvote);
            profImg = itemView.findViewById(R.id.profile_image);
            queImg = itemView.findViewById(R.id.queImg);
            commentC = itemView.findViewById(R.id.commentCounterTv);
            upvoteC = itemView.findViewById(R.id.upvoteCounterTv);
            moreMenuQue = itemView.findViewById(R.id.moreMenuQue);

            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mListener.onUpvoted(position);
                        }
                    }
                }
            });

            giveAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mListener.onCommented(position);
                        }
                    }
                }
            });



        }


    }
}

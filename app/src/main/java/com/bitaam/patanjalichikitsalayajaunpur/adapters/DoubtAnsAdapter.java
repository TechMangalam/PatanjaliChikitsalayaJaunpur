package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.bitaam.patanjalichikitsalayajaunpur.utility.FullscreenActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bitaam.patanjalichikitsalayajaunpur.R;

public class DoubtAnsAdapter extends RecyclerView.Adapter<DoubtAnsAdapter.ViewHolder> {

    RecyclerView recyclerView;
    List<DoubtQuestionModel> modelList;
    Context context;

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
        if (modelList.get(position).isVerified()){
            holder.verifiedBadgeIv.setVisibility(View.VISIBLE);
        }else{
            holder.verifiedBadgeIv.setVisibility(View.GONE);
        }
        if (!modelList.get(position).getQueImgUrl().equalsIgnoreCase("na")){
            holder.queImg.setVisibility(View.VISIBLE);
            holder.queImg.setImageURI(Uri.parse(modelList.get(position).getQueImgUrl()));
        }else{
            holder.queImg.setVisibility(View.GONE);
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

    public void update(DoubtQuestionModel m){
        modelList.add(m);
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView profName,postDate,question;
        CircleImageView profImg;
        SimpleDraweeView queImg;
        ImageView verifiedBadgeIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Fresco.initialize(context);

            profName = itemView.findViewById(R.id.profileName);
            postDate = itemView.findViewById(R.id.date);
            question = itemView.findViewById(R.id.question);
            profImg = itemView.findViewById(R.id.profile_image);
            queImg = itemView.findViewById(R.id.queImg);
            verifiedBadgeIv = itemView.findViewById(R.id.verification_badge_iv);

          }


    }
}


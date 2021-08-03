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

import com.bitaam.patanjalichikitsalayajaunpur.AddNuskeActivity;
import com.bitaam.patanjalichikitsalayajaunpur.AddYogaActivity;
import com.bitaam.patanjalichikitsalayajaunpur.NuskeDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.YogaDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.YogaTrackingActivity;
import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaSessionModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.util.ArrayList;

public class YogaSessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<YogaSessionModel> yogaSessionModels;
    String userRole="";
    Context context;


    public YogaSessionAdapter(RecyclerView recyclerView, Context context,ArrayList<YogaSessionModel> yogaSessionModels) {
        this.recyclerView = recyclerView;
        this.yogaSessionModels = yogaSessionModels;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.yoga_session_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.nameofSessionTv.setText(yogaSessionModels.get(position).getSessionName());
        String duration = "Duration : "+yogaSessionModels.get(position).getDt()+" minutes";
        viewHolder.durationTv.setText(duration);

    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    @Override
    public int getItemCount() {
        return yogaSessionModels.size();
    }

    public void update(YogaSessionModel yogaSessionModel,String role){

        yogaSessionModels.add(yogaSessionModel);
        userRole = role;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameofSessionTv,durationTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameofSessionTv = itemView.findViewById(R.id.yogaSessionTitle);
            durationTv = itemView.findViewById(R.id.yoga_session_duration);

//            if(userRole.equals("Developer")){
//                yogaEditImgView.setVisibility(View.VISIBLE);
//            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent(context, YogaTrackingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("sessionInfo", (Serializable) yogaSessionModels.get(position));
                    context.startActivity(intent);

                }
            });
        }


    }


}



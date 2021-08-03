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
import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.util.ArrayList;

public class NuskeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<NuskeModal> nuskeModals;
    String userRole="";
    Context context;
    AdRequest adRequest;


    public NuskeAdapter(RecyclerView recyclerView, Context context,ArrayList<NuskeModal> nuskeModals) {
        this.recyclerView = recyclerView;
        this.nuskeModals = nuskeModals;
        this.context = context;
        adRequest = new AdRequest.Builder().build();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        Fresco.initialize(context);
        MobileAds.initialize(context);
        View view = LayoutInflater.from(context).inflate(R.layout.yoga_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.nameofFile.setText(nuskeModals.get(position).getNuskeName());

        if (position%2==0){
            viewHolder.yogalistAds.setVisibility(View.VISIBLE);
            viewHolder.yogalistAds.loadAd(adRequest);
        }else{
            viewHolder.yogalistAds.setVisibility(View.GONE);
        }
            viewHolder.webIcon.setImageURI(Uri.parse(nuskeModals.get(position).getIconUrl()));

        viewHolder.yogaEditImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNuskeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("role","edit");
                intent.putExtra("nuskeInfo", (Serializable) nuskeModals.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    @Override
    public int getItemCount() {
        return nuskeModals.size();
    }

    public void update(NuskeModal nuskeModal,String role){

        nuskeModals.add(nuskeModal);
        userRole = role;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameofFile;
        SimpleDraweeView webIcon;
        AdView yogalistAds;
        ImageView yogaEditImgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameofFile = itemView.findViewById(R.id.webName);
            yogalistAds = itemView.findViewById(R.id.itemBanAds);
            webIcon = itemView.findViewById(R.id.webIconImg);
            yogaEditImgView = itemView.findViewById(R.id.editYogaUpcharImgView);

            if(userRole.equals("Developer")){
                yogaEditImgView.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent(context, NuskeDisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("nuskeInfo", (Serializable) nuskeModals.get(position));
                    context.startActivity(intent);

                }
            });
        }


    }


}


package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.AddUpcharActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.UpcharDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.YogaDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.util.ArrayList;

public class UpcharAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<String> upcharName = new ArrayList<>();
    ArrayList<String> upcharIconUrls = new ArrayList<>();
    ArrayList<UpcharModal> upcharModals = new ArrayList<>();
    String userRole="";
    Context context;


    public UpcharAdapter(RecyclerView recyclerView, ArrayList<String> upcharName, ArrayList<String> upcharIconUrls, ArrayList<UpcharModal> upcharModals, Context context) {
        this.recyclerView = recyclerView;
        this.upcharName = upcharName;
        this.upcharIconUrls = upcharIconUrls;
        this.upcharModals = upcharModals;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        Fresco.initialize(context);

        View view = LayoutInflater.from(context).inflate(R.layout.yoga_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        MobileAds.initialize(context);


        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.nameofFile.setText(upcharModals.get(position).getUpcharName());

        if (position%2==0){
            viewHolder.yogalistAds.setVisibility(View.VISIBLE);
            viewHolder.yogalistAds.loadAd(new AdRequest.Builder().build());
        }else{
            viewHolder.yogalistAds.setVisibility(View.GONE);
        }

        viewHolder.webIcon.setImageURI(Uri.parse(upcharIconUrls.get(position)));

        viewHolder.upcharEditImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddUpcharActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("role","edit");
                intent.putExtra("upcharInfo", (Serializable) upcharModals.get(position));
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
        return upcharName.size();
    }

    public void update(String name ,String iconUrl,UpcharModal upcharModal,String role){

        upcharName.add(name);
        upcharIconUrls.add(iconUrl);
        upcharModals.add(upcharModal);
        userRole = role;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameofFile,weburl;
        SimpleDraweeView webIcon;
        AdView yogalistAds;
        ImageView upcharEditImgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Fresco.initialize(context);

            nameofFile = itemView.findViewById(R.id.webName);
            yogalistAds = itemView.findViewById(R.id.itemBanAds);
            webIcon = itemView.findViewById(R.id.webIconImg);
            upcharEditImgView = itemView.findViewById(R.id.editYogaUpcharImgView);

            if(userRole.equals("Developer")){
                upcharEditImgView.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent(context, UpcharDisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("upcharInfo", (Serializable) upcharModals.get(position));
                    context.startActivity(intent);

                }
            });
        }


    }


}


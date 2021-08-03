package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class GeneralUpcharAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<String> yogaName = new ArrayList<>();
    ArrayList<String> yogaIconUrls = new ArrayList<>();
    ArrayList<UpcharModal> upcharModals = new ArrayList<>();
    Context context;


    public GeneralUpcharAdapter(RecyclerView recyclerView, Context context, ArrayList<String> yogaItems, ArrayList<String> yogaIconUrls,ArrayList<UpcharModal> upcharModals) {
        this.recyclerView = recyclerView;
        this.yogaName = yogaItems;
        this.yogaIconUrls = yogaIconUrls;
        this.upcharModals = upcharModals;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        Fresco.initialize(context);
        View view = LayoutInflater.from(context).inflate(R.layout.general_upchar_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MobileAds.initialize(context);


        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.nameofFile.setText(upcharModals.get(position).getUpcharName());

        viewHolder.webIcon.setImageURI(Uri.parse(yogaIconUrls.get(position)));

    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    @Override
    public int getItemCount() {
        return yogaName.size();
    }

    public void update(String name ,String iconUrl,UpcharModal upcharModal){

        yogaName.add(name);
        yogaIconUrls.add(iconUrl);
        upcharModals.add(upcharModal);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameofFile;
        SimpleDraweeView webIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Fresco.initialize(context);

            nameofFile = itemView.findViewById(R.id.webName);
            webIcon = itemView.findViewById(R.id.webIconImg);
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


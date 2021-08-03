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
import com.bitaam.patanjalichikitsalayajaunpur.YogaDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.util.ArrayList;

public class DailyYogaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<String> yogaName = new ArrayList<>();
    ArrayList<String> yogaIconUrls = new ArrayList<>();
    ArrayList<YogaModal> yogaModals = new ArrayList<>();
    Context context;


    public DailyYogaAdapter(RecyclerView recyclerView, Context context, ArrayList<String> yogaItems, ArrayList<String> yogaIconUrls,ArrayList<YogaModal> yogaModals) {
        this.recyclerView = recyclerView;
        this.yogaName = yogaItems;
        this.yogaIconUrls = yogaIconUrls;
        this.yogaModals = yogaModals;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Fresco.initialize(context);
        View view = LayoutInflater.from(context).inflate(R.layout.daily_yoga_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MobileAds.initialize(context);


        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.nameofFile.setText(yogaModals.get(position).getYogaName());

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

    public void update(String name ,String iconUrl,YogaModal yogaModal){

        yogaName.add(name);
        yogaIconUrls.add(iconUrl);
        yogaModals.add(yogaModal);
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
                    Intent intent = new Intent(context, YogaDisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("yogaInfo", (Serializable) yogaModals.get(position));
                    context.startActivity(intent);

                }
            });
        }


    }


}


package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.ShowPatientDetailActivity;
import com.bitaam.patanjalichikitsalayajaunpur.UpcharDisplayActivity;
import com.bitaam.patanjalichikitsalayajaunpur.modals.PatientModel;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;

import java.io.Serializable;
import java.util.ArrayList;

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<PatientModel> patientModels;
    ArrayList<String> keys;
    String userRole="";
    Context context;

    public PatientListAdapter(RecyclerView recyclerView, ArrayList<PatientModel> patientModels,ArrayList<String> keys, String userRole, Context context) {
        this.recyclerView = recyclerView;
        this.patientModels = patientModels;
        this.keys = keys;
        this.userRole = userRole;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.patNameTv.setText("Patient Name : "+patientModels.get(position).getName());
        viewHolder.checkupDateTv.setText("Checkup Date : "+patientModels.get(position).getDateTime());
        viewHolder.diseaseTv.setText("Disease : "+patientModels.get(position).getDisease());
        viewHolder.hospitalTv.setText("Hospital : "+patientModels.get(position).getHospital());

    }

    @Override
    public int getItemCount() {
        return patientModels.size();
    }

    public void update(PatientModel patientModel,String key){

        patientModels.add(patientModel);
        keys.add(key);
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView patNameTv,checkupDateTv,diseaseTv,hospitalTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            patNameTv = itemView.findViewById(R.id.pat_name_tv);
            checkupDateTv = itemView.findViewById(R.id.checkup_date_tv);
            diseaseTv = itemView.findViewById(R.id.disease_tv);
            hospitalTv = itemView.findViewById(R.id.hospital_tv);

            if(userRole.equals("Developer")){
                //upcharEditImgView.setVisibility(View.VISIBLE);
            }

           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent(context, ShowPatientDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("patientInfo", patientModels.get(position));
                    context.startActivity(intent);

                }
            });

        }
    }


}

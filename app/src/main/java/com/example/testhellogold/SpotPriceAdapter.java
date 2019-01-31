package com.example.testhellogold;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SpotPriceAdapter  extends RecyclerView.Adapter<SpotPriceAdapter.ViewHolder>{

    private ArrayList<SpotPrice> mDataSet;
    private Context mContext;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public SpotPriceAdapter(ArrayList<SpotPrice> mDataSet, Context mContext) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_card,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int positionTemp = position;


        try {
            Date date = format.parse(((SpotPrice) mDataSet.get(position)).getTimestamp());
            String hour = (date.getHours() < 10) ? "0"+date.getHours() : date.getHours()+"";
            String mins = (date.getMinutes() < 10) ? "0"+date.getMinutes() : date.getMinutes()+"";
            String seconds = (date.getSeconds() < 10) ? "0"+date.getSeconds() : date.getSeconds()+"";
            holder.timeStampView.setText(hour + ":" + mins + ":" + seconds);
            holder.spotPriceView.setText(((SpotPrice) mDataSet.get(position)).getSpot_price() + "");
            holder.buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, ((SpotPrice) mDataSet.get(positionTemp)).getTimestamp(), Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView timeStampView;
        public TextView spotPriceView;
        public Button buyButton;
        public ViewHolder(View v){
            super(v);
            timeStampView = (TextView) v.findViewById(R.id.timeStampTxt);
            spotPriceView = (TextView) v.findViewById(R.id.spotPriceTxt);
            buyButton = (Button) v.findViewById(R.id.buyButton);
        }
    }
}

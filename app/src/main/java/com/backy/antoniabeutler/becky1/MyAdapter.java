package com.backy.antoniabeutler.becky1;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.backy.antoniabeutler.becky1.fragment.MapFragment;

import java.text.DecimalFormat;
import java.util.List;

import static com.backy.antoniabeutler.becky1.MainActivity.mPois;
import static com.backy.antoniabeutler.becky1.MainActivity.mapF;
import static com.backy.antoniabeutler.becky1.MainActivity.navigation;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static List<Tile> mDataset;
    public static Context context;
    public static Double longitude, latitude;
    public static RecyclerView recView;
    public static Fragment mFrag;
    public static FragmentManager fragManager;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView info_text, distance;
        public Button delete;
        public  CardView cardView;
        public ImageView mImage;

        public MyViewHolder(CardView v) {
            super(v);
            cardView = v;
            info_text = v.findViewById(R.id.info_text);
            distance = v.findViewById(R.id.distance_text);
            mImage = v.findViewById(R.id.image_holder);
            delete = v.findViewById(R.id.deleteCard);

            v.setOnClickListener(new CardView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info_text.getText().equals("Add POI")){
                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(context, mImage);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                for (Tile t : mDataset){
                                    if (t.getTile_name().equals(item.getTitle().toString())){
                                        return true;
                                    }
                                }
                                mDataset.add(new Tile(item.getTitle().toString()));

                                MyAdapter mAdapter = new MyAdapter(context, mDataset, latitude, longitude, recView, fragManager);
                                recView.setAdapter(mAdapter);

                                return true;
                            }
                        });
                        popup.show(); //showing popup menu
                    } else {
                        navigation.setSelectedItemId(R.id.map_side);
                        if (mapF == null){
                            mapF = new MapFragment();
                        }
                        Bundle args = new Bundle();
                        if (latitude != null || longitude != null){
                            args.putDouble("latitude", latitude);
                            args.putDouble("longitude", longitude);
                            args.putBoolean("locationAvailable", false);
                        } else {
                            args.putBoolean("locationAvailable", true);
                        }
                        args.putString("poiType", info_text.getText().toString());
                        mapF.setArguments(args);

                        FragmentTransaction transaction = fragManager.beginTransaction();
                        transaction.replace(R.id.frame_container, mapF);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, List<Tile> myDataset, Double latitude, Double longitude, RecyclerView recView, FragmentManager fragManager) {
        mDataset = myDataset;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recView = recView;
        this.fragManager = fragManager;
    }

    public void setLocation(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;

        for(Tile t : mDataset){
            Double d = MainActivity.shortestdistance.get(t.getTile_name());
            if(d != null)
                t.setDistance(d);

        }
        notifyDataSetChanged();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView c = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent, false);

        MyViewHolder vh = new MyViewHolder(c);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position, List<Object> payload) {
        if (payload.isEmpty()){
            onBindViewHolder(holder, position);
        }

        holder.mImage.setImageResource(mDataset.get(position).getImg_src());
        holder.info_text.setText(mDataset.get(position).getTile_name());
        if (mDataset.get(position).getTile_name().equals("Add POI")){
        holder.delete.setVisibility(View.GONE);
        holder.info_text.setVisibility(View.INVISIBLE);
        holder.distance.setVisibility(View.INVISIBLE);
    } else {
        holder.info_text.setText(mDataset.get(position).getTile_name());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
        String s;
        if(!MainActivity.shortestdistance.containsKey(mDataset.get(position).getTile_name())){
            s = "Nothing found!";
        }else{
            DecimalFormat df = new DecimalFormat("###.##");
            s  = df.format(MainActivity.shortestdistance.get(mDataset.get(position).getTile_name()))+ " m";
        }
        holder.distance.setText(s);
    }

}




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

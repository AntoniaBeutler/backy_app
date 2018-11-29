package com.backy.antoniabeutler.becky1;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.Toast;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static List<Tile> mDataset;
    public static Context context;
    public static Double longitude, latitude;
    public static RecyclerView recView;

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

                                MyAdapter mAdapter = new MyAdapter(context, mDataset, latitude, longitude, recView);
                                recView.setAdapter(mAdapter);

                                return true;
                            }
                        });
                        popup.show(); //showing popup menu
                    } else {
                        Intent i = new Intent(context, MapActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("type", info_text.getText().toString());
                        if ((longitude != null)||(latitude != null)){
                            i.putExtra("longitudeLocation", longitude);
                            i.putExtra("latitudeLocation", latitude);
                            i.putExtra("noLocation", false);
                        } else  {
                            Toast.makeText(context, "No GPS signal found. Try it later.", Toast.LENGTH_LONG).show();
                            i.putExtra("noLocation", true);
                        }
                        context.startActivity(i);
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, List<Tile> myDataset, Double latitude, Double longitude, RecyclerView recView) {
        mDataset = myDataset;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recView = recView;
    }

    public void setLocation(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView c = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent, false);

        MyViewHolder vh = new MyViewHolder(c);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
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
            holder.distance.setText(latitude + " + " + longitude);
        }

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

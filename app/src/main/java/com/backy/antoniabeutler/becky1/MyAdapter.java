package com.backy.antoniabeutler.becky1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Tile> mDataset;
    public static Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView info_text;
        public CardView mTextView;


        public MyViewHolder(CardView v) {
            super(v);
            mTextView = v;
            info_text = v.findViewById(R.id.info_text);

            v.setOnClickListener(new CardView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MapActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("type", info_text.getText().toString());
                    context.startActivity(i);
                    // item clicked
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, List<Tile> myDataset) {
        mDataset = myDataset;
        this.context = context;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.info_text.setText(mDataset.get(position).tile_name);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

package com.backy.antoniabeutler.becky1;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton button;
    private Context context;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Tile> tile_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTiles();
    }

    private void addTiles(){
        context = getApplicationContext();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        for (Tile t : tile_List){
                            if (t.getTile_name().equals(item.getTitle().toString())){
                                return true;
                            }
                        }
                        tile_List.add(new Tile("0", item.getTitle().toString(), item.getItemId()));

                        mRecyclerView = findViewById(R.id.my_recycler_view);
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new GridLayoutManager(context,2);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new MyAdapter(context, tile_List);
                        mRecyclerView.setAdapter(mAdapter);

                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
    }

}
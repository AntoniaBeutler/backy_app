package com.backy.antoniabeutler.becky1.fragment;


import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backy.antoniabeutler.becky1.MainActivity;
import com.backy.antoniabeutler.becky1.MyAdapter;
import com.backy.antoniabeutler.becky1.R;
import com.backy.antoniabeutler.becky1.Tile;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private Context context;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Tile> tile_List = new ArrayList<>();

    private Location location;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Double mLatitude, mLongitude;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTiles();

    }

    private void loadTiles(){
        String lTile;
        Cursor cursor = MainActivity.sqLiteHelper.getLoadedTiles(1);

        cursor.moveToFirst();
        lTile = cursor.getString(cursor.getColumnIndex("poi"));
        tile_List.add(new Tile(lTile));
        while (cursor.moveToNext()){
            lTile = cursor.getString(cursor.getColumnIndex("poi"));
            if (lTile.equals("Add POI")){
                tile_List.add(0, new Tile(cursor.getString(cursor.getColumnIndex("poi"))));
            } else {
                tile_List.add(new Tile(lTile));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (getArguments() != null) {
            mLatitude = getArguments().getDouble("latitude");
            mLongitude = getArguments().getDouble("longitude");
        }

        context = getContext();

        //if(tile_List.isEmpty()) tile_List.add(new Tile("Add POI"));

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(context,2);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //mAdapter.notifyDataSetChanged();

        mAdapter = new MyAdapter(context, tile_List, mLatitude, mLongitude , mRecyclerView, MainActivity.fragManager);
        try{
            ((OnFragmentInteractionListener) context).giveAdapter(mAdapter);
        } catch (ClassCastException e){ }
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void giveAdapter(MyAdapter adapter);
    }
}

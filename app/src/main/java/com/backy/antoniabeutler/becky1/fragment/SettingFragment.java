package com.backy.antoniabeutler.becky1.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TabWidget;
import android.widget.Toast;

import com.backy.antoniabeutler.becky1.MainActivity;
import com.backy.antoniabeutler.becky1.R;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private GeocoderNominatim geocoder = new GeocoderNominatim("mobile");
    private CheckBox powerSaveCheckBox, mapCheckBox;
    private Button radiusButton, amountButton, locationButton;
    private EditText radiusEdit, amountEdit, locationEdit;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Cursor cursor;

        // TODO: Implement the setOnClickListener for the Location with request for longitude and latitude of desired destination
        locationEdit = view.findViewById(R.id.editLocation);

        locationButton = view.findViewById(R.id.buttonLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Location changed");
            }
        });

        radiusEdit = view.findViewById(R.id.editRadius);
        cursor = MainActivity.sqLiteHelper.getPOIRadius();
        cursor.moveToFirst();
        int radius = Integer.parseInt(cursor.getString(cursor.getColumnIndex("poi_radius")));
        radiusEdit.setHint(Integer.toString(radius));
        cursor.close();

        radiusButton = view.findViewById(R.id.buttonRadius);
        radiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 0, Integer.parseInt(radiusEdit.getText().toString()), 0, 0, 2);
                    radiusEdit.setHint(radiusEdit.getText().toString());
                } catch (NumberFormatException e) {
                    radiusEdit.setHint("Not a valid number!");
                }
                radiusEdit.setText("");
                radiusEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
                System.out.println("Radius changed");
            }
        });

        amountEdit = view.findViewById(R.id.editAmount);
        cursor = MainActivity.sqLiteHelper.getPOIAmount();
        cursor.moveToFirst();
        int amount = Integer.parseInt(cursor.getString(cursor.getColumnIndex("poi_amount")));
        amountEdit.setHint(Integer.toString(amount));
        cursor.close();

        amountButton = view.findViewById(R.id.buttonAmount);
        amountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 0, 0, Integer.parseInt(amountEdit.getText().toString()), 0, 3);
                    amountEdit.setHint(amountEdit.getText().toString());
                } catch (NumberFormatException e) {
                    amountEdit.setHint("Not a valid number!");
                }
                amountEdit.setText("");
                amountEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
                System.out.println("Amount changed");
            }
        });

        mapCheckBox = view.findViewById(R.id.checkBoxMap);
        cursor = MainActivity.sqLiteHelper.getMapDownload();
        cursor.moveToFirst();
        int map = Integer.parseInt(cursor.getString(cursor.getColumnIndex("map_download")));
        if (map == 1){
            mapCheckBox.setChecked(true);
        } else if (map == 0){
            mapCheckBox.setChecked(false);
        }
        cursor.close();

        mapCheckBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mapCheckBox.isChecked()) {
                    //MapFragment.mapDownLoad(getContext());
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 1, 0, 0, 0, 1);
                    System.out.println("Checked");
                } else {
                    //MapFragment.deleteMap(getContext());
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 0, 0, 0, 0, 1);
                    System.out.println("Un-Checked");
                }
            }
        });

        powerSaveCheckBox = view.findViewById(R.id.checkBoxPowerSave);
        cursor = MainActivity.sqLiteHelper.getPowerSaving();
        cursor.moveToFirst();
        int power_saving = Integer.parseInt(cursor.getString(cursor.getColumnIndex("power_saving")));
        if (power_saving == 1){
            powerSaveCheckBox.setChecked(true);
        } else if (power_saving == 0){
            powerSaveCheckBox.setChecked(false);
        }
        cursor.close();

        powerSaveCheckBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (powerSaveCheckBox.isChecked()) {
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 0, 0, 0, 1, 4);
                    System.out.println("Checked");
                } else {
                    MainActivity.sqLiteHelper.updateSettings("", 0.0, 0.0, 1, 0, 0, 0, 4);
                    System.out.println("Un-Checked");
                }
            }
        });

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
    }
}

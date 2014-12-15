package com.pin.event.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.commonsware.cwac.merge.MergeAdapter;
import com.pin.event.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileFragment extends SherlockFragment {
	
    private static final String STATE_USERNAME = "userName";
    private static final String STATE_USERID = "userId";
    private static final String STATE_DETAILS = "details";

    private LayoutInflater inflater;
    private ListView userView;

    private ImageView imageView;
    private TextView nameText, locationText, dates, favStyleText, recentratingslabel;
    private Button beersratedButton, cellarButton;

    protected String userName;
    protected int userId;
    
    public ProfileFragment() {
        this(null, -1);
    }
    
    public ProfileFragment(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    // Inflate the layout for this fragment
	    this.inflater = inflater;
	    return inflater.inflate(R.layout.fragment_userview, container, false);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userView = (ListView) getView().findViewById(R.id.userview);
        if (userView != null) {
            // Phone
            userView.setAdapter(new UserViewAdapter());
            userView.setItemsCanFocus(true);
        } else {
            // Tablet
            
        }
    }
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
	private class UserViewAdapter extends MergeAdapter {

        public UserViewAdapter() {

            // Set the user detail fields
            View fields = getActivity().getLayoutInflater().inflate(R.layout.fragment_userdetails, null);
            addView(fields);

            imageView = (ImageView) fields.findViewById(R.id.image);
            nameText = (TextView) fields.findViewById(R.id.name);
            locationText = (TextView) fields.findViewById(R.id.location);
            dates = (TextView) fields.findViewById(R.id.dates);
            favStyleText = (TextView) fields.findViewById(R.id.favStyle);
            recentratingslabel = (TextView) fields.findViewById(R.id.recentratingslabel);
            beersratedButton = (Button) fields.findViewById(R.id.beersrated);
            //beersratedButton.setOnClickListener(onShowAllRatingsClick);
            cellarButton = (Button) fields.findViewById(R.id.cellar);
            //cellarButton.setOnClickListener(onViewCellarClick);
            
        }

    }
	
}

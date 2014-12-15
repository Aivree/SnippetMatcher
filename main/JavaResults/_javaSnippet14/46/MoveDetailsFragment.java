package com.lib4.picmove.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appbase.androidquery.AQuery;
import com.lib4.picmove.R;
import com.lib4.picmove.datastorage.DBManager;

public class MoveDetailsFragment extends BaseFragment{

	ScrollView movesDetailsLayout;
	String moveId;
	Cursor moveDetails,bigItemDetails;
	TextView smallBoxCount,mediumBoxcount,largeBoxcount,bigItemsCount,itemsRequiresDiassemblyCount,itemsWontfitInelevatorCount;
	LinearLayout bigItemLayout,requiresDiassemblyLayout,wontfitInelevatorLayout;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		movesDetailsLayout = (ScrollView) inflater.inflate(
				R.layout.move_details_fragment, container, false);
		init();
		
		return movesDetailsLayout;
	}
	
	
	private void init(){
		
		
		smallBoxCount	=	(TextView) movesDetailsLayout.findViewById(R.id.smallBoxCount);
		mediumBoxcount	=	(TextView) movesDetailsLayout.findViewById(R.id.mediumBoxCount);
		largeBoxcount	=	(TextView) movesDetailsLayout.findViewById(R.id.largeBoxCount);
		bigItemsCount	=	(TextView) movesDetailsLayout.findViewById(R.id.bigItemsCount);
		itemsRequiresDiassemblyCount	=	(TextView) movesDetailsLayout.findViewById(R.id.itemsRequiresDiassemblyCount);
		itemsWontfitInelevatorCount	=	(TextView) movesDetailsLayout.findViewById(R.id.itemsWontfitInelevatorCount);
		
		
		moveDetails.moveToFirst();
		smallBoxCount.setText(""+moveDetails.getInt(1));
		mediumBoxcount.setText(""+moveDetails.getInt(2));
		largeBoxcount.setText(""+moveDetails.getInt(3));
		bigItemsCount.setText(""+moveDetails.getInt(7));
		
		bigItemLayout	=	(LinearLayout) movesDetailsLayout.findViewById(R.id.bigItemsImageLayout);
		requiresDiassemblyLayout	=	(LinearLayout) movesDetailsLayout.findViewById(R.id.itemsRequiresDiassemblyImageLayout);
		wontfitInelevatorLayout	=	(LinearLayout) movesDetailsLayout.findViewById(R.id.itemsWontFitInBoxImageLayout);
		int count 	=	bigItemDetails.getCount();
		int index	=	0;
		int itemRequiresDiassemblyCount	=	0;
		int itemWontFitInelevator	=	0;
		
		while(index<count){
			bigItemDetails.moveToPosition(index);
			
			
			
			
			
			Log.e("URL",""+bigItemDetails.getString(5));
			Log.e("URL TYPE ",""+bigItemDetails.getType(6));
			Log.e("URL",""+bigItemDetails.getString(6));
			Log.e("URL",""+bigItemDetails.getString(7));
			setImage(bigItemLayout,getActivity().getLayoutInflater().inflate(R.layout.grid_item, null));
			
			
			
			if(bigItemDetails.getString(6).compareTo("1")==0){
				
				//requiresDiassemblyLayout.addView(mInflater);
				setImage(requiresDiassemblyLayout,getActivity().getLayoutInflater().inflate(R.layout.grid_item, null));
				itemRequiresDiassemblyCount++;
			}
	
			if(bigItemDetails.getString(7).compareTo("1")==0){
				//wontfitInelevatorLayout.addView(mInflater);
				setImage(wontfitInelevatorLayout,getActivity().getLayoutInflater().inflate(R.layout.grid_item, null));
				itemWontFitInelevator++;
			}
			
			index++;
		}
		
		
		
		itemsRequiresDiassemblyCount.setText(""+itemRequiresDiassemblyCount);
		itemsWontfitInelevatorCount.setText(""+itemWontFitInelevator);
		
		
	}
	
	private void setImage(LinearLayout mParent,View mView){
		
		ImageView mImageView	=	(ImageView) mView.findViewById(R.id.itempic);
		AQuery aq = new AQuery(mImageView);
		aq.id(mImageView).image(bigItemDetails.getString(5));
		mParent.addView(mView);
	}
	
	public void setMoveId(String moveId){
		
		this.moveId	=	moveId;
		moveDetails	=	new DBManager(getActivity()).fetchMove(moveId);
		bigItemDetails	=	new DBManager(getActivity()).fetchBigItems(moveId);
		
		Log.e("MoveId ","MoveId"+moveId);
	}
	
}
package com.login.recscores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PlayerAdapter extends BaseAdapter {

	private Context mContext;
	LayoutInflater inflater;

	// Keep all Images in array
	public Integer[] mThumbIds = {};
		/*	R.drawable.gridview_createteam, R.drawable.pic2,
			R.drawable.pic3, R.drawable.pic4,
			R.drawable.pic5, R.drawable.pic6,
			R.drawable.pic7, R.drawable.pic8,
			R.drawable.pic9, R.drawable.pic10,
			R.drawable.pic11, R.drawable.pic12,
			R.drawable.pic13, R.drawable.pic14,
			R.drawable.pic15
	};
*/
	// Constructor
	public PlayerAdapter(Context c){
		mContext = c;
		
	}

	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return mThumbIds[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

   public View getView(int position, View convertView, ViewGroup parent) {
		View MyView = convertView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            //Inflate the layout
        	LayoutInflater li= (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
     //       LayoutInflater li = getLayoutInflater();
        	//View view = inflater.inflate( R.layout.gv_createplayer, null );
            MyView = li.inflate(R.layout.gv_createplayer, null);
            Button myButton = new Button(mContext);
            myButton.setText("Push Me");
            //do stuff like add text and listeners.

            ((ViewGroup) MyView).addView(myButton);

            // Add The Image!!!           
      //      ImageView iv = (ImageView)MyView.findViewById(R.id.gridviewplayer);
      //      iv.setImageResource(mThumbIds[position]);

            // Add The Text!!!
        //    TextView tv = (TextView)MyView.findViewById(R.id.grid_item_text);
         //   tv.setText(names[position] );


        }
        return MyView;
    
	}

	}



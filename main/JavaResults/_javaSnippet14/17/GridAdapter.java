package mobile.learning;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {
	private Context context;
	private String[] reviewdata;

	public GridAdapter(Context context, String[] data) 
	{
	    this.context = context;
	    reviewdata=data;
	}
	static class ViewHolder {
		public RatingBar rate;
		public TextView textView;
	}
	public int getCount() 
	{
	    return reviewdata.length;
	}

	public Object getItem(int position) 
	{
	    return null;
	}

	public long getItemId(int position) 
	{
	    return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View v=convertView;
		/*
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		v = inflater.inflate(R.layout.viewreviewfile, null, true);
		GridView gridview = (GridView) v.findViewById(R.id.gridview);
	  */
	    TextView tv = new TextView(context);
	    
	    ViewHolder holder=new ViewHolder();
	    //RatingBar rb = null;
	  /*  if((v == null)&&(position==2))
	    {
	    	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(R.layout.viewreviewfile, null, true);
			GridView gridview = (GridView) v.findViewById(R.id.gridview);
	    	float rating_value = Float.valueOf(reviewdata[position].trim()).floatValue();
	    	RatingBar rb = new RatingBar(context,null, android.R.attr.ratingBarStyleSmall); 
	      // ViewHolder holder=new ViewHolder();
           v.setTag(holder);
	       holder.rate=rb;
	       holder.rate.setRating(rating_value);
	    	// rb.setRating(f);
	    	// gridview.addView(rb);
	    	//tv = new TextView(context);
	    	//tv.setLayoutParams(new GridView.LayoutParams(85, 85));     
	    }
	    */
	    //else if((v== null)&&(position!=2))
	    if(v== null)
	    {
	    	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(R.layout.viewreviewfile, null, true);
			GridView gridview = (GridView) v.findViewById(R.id.gridview);
	    	tv = new TextView(context);
	    	//ViewHolder holder=new ViewHolder();
	           v.setTag(holder);
		       holder.textView=tv;
		       holder.textView.setLayoutParams(new GridView.LayoutParams(85, 85));
		       holder.textView.setText(reviewdata[position]);
		  /*
		   tv.setLayoutParams(new GridView.LayoutParams(85, 85));
	       tv.setText(reviewdata[position]);
	       */
	    }
	  
	    else 
	    {
	       tv = (TextView) convertView;
	       tv.setText(reviewdata[position]);
	        holder = (ViewHolder) v.getTag();
	    }

	   // tv.setText(reviewdata[position]);
	    return v;
	}


}

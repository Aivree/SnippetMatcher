package org.cnc.nsv;

import org.cnc.nsv.activity.AgentActivity;
import org.cnc.nsv.news.Activity_news;
import org.cnc.nsv.product.Activity_production;
import org.cnc.nsv.search.Activity_mainsearch;
import org.cnc.nsv.setting.Activity_setting;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class createmenu implements OnClickListener{
	
	
	Activity activity;
	public void create(Activity view) {
		this.activity=view;
		ImageView menu_home=(ImageView)  activity.findViewById(R.id.menu_home);
		menu_home.setOnClickListener(createmenu.this);
		Button menu_product=(Button) view.findViewById(R.id.menu_product);
		Button menu_news=(Button) view.findViewById(R.id.menu_news);
		Button menu_search=(Button) view.findViewById(R.id.menu_search);
		Button menu_more=(Button) view.findViewById(R.id.menu_more);
		menu_product.setOnClickListener(createmenu.this);
		menu_news.setOnClickListener(createmenu.this);
		menu_search.setOnClickListener(createmenu.this);
		menu_more.setOnClickListener(createmenu.this);
		
		
	}
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.menu_home:
			i = new Intent(activity, Activity_main.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(i);
			break;
		case R.id.menu_product:
			i = new Intent(activity, Activity_production.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			activity.startActivity(i);
			break;
		case R.id.menu_news:
			i = new Intent(activity, Activity_news.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			activity.startActivity(i);
			break;
		case R.id.menu_search:
			i = new Intent(activity, AgentActivity.class);
			i.putExtra(Constant.OPEN_ACTIVITY, Constant.KEY_SEARCH);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			activity.startActivity(i);
			break;
		case R.id.menu_more:
			i = new Intent(activity, Activity_setting.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			activity.startActivity(i);
			break;

		default:
			break;
		}
		
	}
	
}
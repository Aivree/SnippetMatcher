package org.cnc.nsv;

import org.cnc.nsv.activity.AgentActivity;
import org.cnc.nsv.news.Activity_news;
import org.cnc.nsv.otheractivity.Activity_contact;
import org.cnc.nsv.otheractivity.Activity_info;
import org.cnc.nsv.product.Activity_production;
import org.cnc.nsv.setting.Activity_setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.cnc.nsv.order.MyConstant;
import com.cnc.nsv.order.OrderActivity;

public class Activity_main extends Activity implements Constant, OnClickListener
{
	ImageView			info, product, news, contact, contractor, agent, search, setting;
	private ImageView	orderc;
	
	@Override
	public void onClick( View v )
	{
		v.startAnimation( new Animation( )
		{
			@Override
			protected void applyTransformation( float interpolatedTime,
					Transformation t )
			{
				t.setTransformationType( Transformation.TYPE_ALPHA );
				t.setAlpha( ( float ) 0.6 );
				super.applyTransformation( interpolatedTime, t );
			}
		} );
		Intent i;
		switch ( v.getId( ) )
		{
			case R.id.main_info:
				i = new Intent( this, Activity_info.class );
				i.putExtra( Constant.OPEN_ACTIVITY, "info" );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_product:
				i = new Intent( this, Activity_production.class );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_news:
				i = new Intent( this, Activity_news.class );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_contact:
				i = new Intent( this, Activity_contact.class );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_contractor:
				i = new Intent( this, AgentActivity.class );
				i.putExtra( Constant.OPEN_ACTIVITY, KEY_RETAILER );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_agent:
				i = new Intent( this, AgentActivity.class );
				i.putExtra( Constant.OPEN_ACTIVITY, KEY_DISTRIBUTOR );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_search:
				i = new Intent( this, AgentActivity.class );
				i.putExtra( Constant.OPEN_ACTIVITY, KEY_SEARCH );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.main_setting:
				i = new Intent( this, Activity_setting.class );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			case R.id.orderc:
				MyConstant.ORDER_FIRST = true;
				i = new Intent( this, OrderActivity.class );
				i.putExtra( "type_activity", 0 );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
				startActivity( i );
				break;
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		info = ( ImageView ) findViewById( R.id.main_info );
		product = ( ImageView ) findViewById( R.id.main_product );
		news = ( ImageView ) findViewById( R.id.main_news );
		contact = ( ImageView ) findViewById( R.id.main_contact );
		contractor = ( ImageView ) findViewById( R.id.main_contractor );
		agent = ( ImageView ) findViewById( R.id.main_agent );
		search = ( ImageView ) findViewById( R.id.main_search );
		setting = ( ImageView ) findViewById( R.id.main_setting );
		orderc = ( ImageView ) findViewById( R.id.orderc );
		info.setOnClickListener( this );
		product.setOnClickListener( this );
		news.setOnClickListener( this );
		contact.setOnClickListener( this );
		contractor.setOnClickListener( this );
		agent.setOnClickListener( this );
		search.setOnClickListener( this );
		setting.setOnClickListener( this );
		orderc.setOnClickListener( this );
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		MyConstant.ORDER_FIRST = false;
	}
	
}

package example.flockers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class SendIntent {
	
	private Context context; 
	static String message[]=new String[8];
	public SendIntent(Context context){
		this.context = context;
	}
	public void signin()
    {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }	
	public void login()
	{
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
	public void signup(){
		Intent intent = new Intent(context, SignUp.class);
		context.startActivity(intent);		
	}
	public void eventClick(View view){
		Intent intent = new Intent(context, CreateEvent.class);
		String message[] = new String[2];
		message[0] = Variables.verb;
		message[1]=Variables.selectedActivity;
		intent.putExtra(MainActivity.EXTRA_MESSAGE, message);
		context.startActivity(intent);
		}
	public void eventDetail(String ename,String date,String time,String place,String fees,String prize,String description,int id){
		Intent intent = new Intent(context, EventDetail.class);		
		message[0]=ename;
		message[1]=date;
		message[2]=time;
		message[3]=place;
		message[4]=fees;
		message[5]=prize;
		message[6]=description;
		message[7]=""+id;
		intent.putExtra(MainActivity.EXTRA_MESSAGE, message);
		context.startActivity(intent);
	}
	public void eventSearch(String ename,String date,String time,String place,String fees,String prize,String description,int id){
		Intent intent = new Intent(context, EventSearch.class);
		message[0]=ename;
		message[1]=date;
		message[2]=time;
		message[3]=place;
		message[4]=fees;
		message[5]=prize;
		message[6]=description;
		message[7]=""+id;
		intent.putExtra(MainActivity.EXTRA_MESSAGE, message);
		context.startActivity(intent);
	}
	public void participate(){
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(MainActivity.EXTRA_MESSAGE, "1");
		context.startActivity(intent);
	}
	public void redirectlogin(){
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(MainActivity.EXTRA_MESSAGE, "1");
		context.startActivity(intent);
	}
	public void redirectMyEvents(){
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(MainActivity.EXTRA_MESSAGE, "1");
		context.startActivity(intent);
	}
}
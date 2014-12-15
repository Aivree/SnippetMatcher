package pro.mindtek.gomoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;

public class GomokuActivity extends Activity {
	private static int FIELD_SIZE = 15;// game field size

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gomoku);
		
		GridLayout gw = (GridLayout)findViewById(R.id.grid_layout);
		gw.setRowCount(FIELD_SIZE);
		gw.setColumnCount(FIELD_SIZE);
		
		LayoutInflater inflater = getLayoutInflater();
		//ArrayList<View> btns = new ArrayList<View>(225);
		for(int i = 0; i < FIELD_SIZE; i++)
			for(int j = 0; j < FIELD_SIZE; j++){
				View v = inflater.inflate(R.layout.gomoku_item, null);
				v.setTag(new int[]{i, j});// coords in grid
				gw.addView(v);
			}
		
		
	}
}

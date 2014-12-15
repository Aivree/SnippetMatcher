package app.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import app.sql.ExerciseController.ExerciseEntry;
import app.workout.Exercise;
import app.workout.R;

public class CustomActivity extends Activity {
	protected static CustomActivity previousActivity;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.options_menu_manage_exercises:
			intent = new Intent(this, ManageExercises.class);
			startActivity(intent);
			break;

		case R.id.options_menu_manager_focuses:
			intent = new Intent(this, ManageFocuses.class);
			startActivity(intent);
			break;

		default:

			break;

		}
		return true;
	}
	
	@Override
	public void startActivity(Intent intent){
		previousActivity = this;
		super.startActivity(intent);
	}

	public void cancelButtonOnClick(View view) {
		Intent intent = null;
		if(previousActivity == null){
			intent = new Intent(this, Schedule.class);
		}else{
			intent = new Intent(this, previousActivity.getClass());
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
}

package ayrat.salavatovich.gmail.com.day_56.layoutinflaterlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String[] name = { "Иван", "Марья", "Петр", "Антон", "Даша",
			"Борис", "Костя", "Игорь" };
	private String[] position = { "Программер", "Бухгалтер", "Программер",
			"Программер", "Бухгалтер", "Директор", "Программер", "Охранник" };
	private int salary[] = { 13000, 10000, 13000, 13000, 10000, 15000, 13000,
			8000 };

	int[] colors = new int[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		colors[0] = getResources().getColor(R.color.DarkOliveGreen);
		colors[1] = getResources().getColor(R.color.CadetBlue);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

		LayoutInflater layoutInflater = getLayoutInflater();

		for (int i = 0; i < name.length; i++) {
			View item = layoutInflater.inflate(R.layout.item, linearLayout,
					false);
			TextView textViewName = (TextView) item
					.findViewById(R.id.textViewName);
			textViewName.setText(name[i]);
			TextView textViewPosition = (TextView) item
					.findViewById(R.id.textViewPosition);
			textViewPosition.setText("Должность: " + position[i]);
			TextView textViewSalary = (TextView) item
					.findViewById(R.id.textViewSalary);
			textViewSalary.setText("Оклад: " + String.valueOf(salary[i]));
			item.getLayoutParams().width = LayoutParams.MATCH_PARENT;
			item.setBackgroundColor(colors[i % 2]);
			linearLayout.addView(item);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

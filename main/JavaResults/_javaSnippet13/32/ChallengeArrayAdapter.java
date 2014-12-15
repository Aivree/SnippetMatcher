package ca.myhealthsphere.challenger;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ca.myhealthsphere.model.Challenge;

public class ChallengeArrayAdapter extends ArrayAdapter<Challenge> {

	public ChallengeArrayAdapter(Context context, List<Challenge> challenges) {
		super(context, R.layout.fragment_challenge_item, challenges);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.fragment_challenge_item, parent, false);
		
		Challenge challenge = getItem(position);
		TextView title = (TextView) rowView.findViewById(R.id.challenge_item_title);
		title.setText(challenge.getTitle());
		TextView description = (TextView) rowView.findViewById(R.id.challenge_item_description);
		description.setText(challenge.getDescription());
		
		return rowView;
	}

}

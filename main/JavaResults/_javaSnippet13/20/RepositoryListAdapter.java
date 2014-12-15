package org.burgers.github_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.eclipse.egit.github.core.Repository;

import java.util.List;

public class RepositoryListAdapter extends ArrayAdapter<Repository> {
    public RepositoryListAdapter(Context context, int textViewResourceId, List<Repository> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Repository repo = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView name= (TextView) rowView.findViewById(R.id.repository_label);
        name.setText(repo.getName());

        TextView description = (TextView) rowView.findViewById(R.id.repository_description);
        description.setText(repo.getDescription());

        return rowView;
    }
}

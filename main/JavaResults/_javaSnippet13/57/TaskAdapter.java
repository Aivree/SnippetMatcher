package ge.adg.todolist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskAdapter extends ArrayAdapter<Task> {
	Context context;
	    
	public TaskAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.task_item, null);
	        }
	        Task o = getItem(position);
	        if (o != null) {
	        	TextView desc = (TextView) v.findViewById(R.id.taskItemText);
	        	ImageView img = (ImageView) v.findViewById(R.id.taskItemImage);
	        	
	        	desc.setText(o.desc);
	        	img.setImageDrawable(context.getResources().getDrawable(R.drawable.file_edit));
	        }
	        return v;
	}
}

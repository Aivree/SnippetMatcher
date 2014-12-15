package dayoung.taegyu.kakaodialogstat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<FileData>{
	private LayoutInflater inflater;
	public FileListAdapter(Context context, ArrayList<FileData> object)
	{
		super(context, 0, object);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup parent)
	{
		View view = null;
		if(v==null)
		{
			view = inflater.inflate(R.layout.list_row, null);
		}
		else
		{
			view = v;
		}
		final FileData data = this.getItem(pos);
		if(data != null)
		{
			TextView tv = (TextView)view.findViewById(R.id.file_path_id);
			ImageView iv = (ImageView)view.findViewById(R.id.file_img_id);
			
			tv.setText(data.path);
			iv.setImageResource(data.img_id);
		}
		return view;
	}
}

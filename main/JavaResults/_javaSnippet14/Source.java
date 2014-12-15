@Override
public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = activity.getLayoutInflater();
    View lst_item_view = inflater.inflate(R.layout.list_layout, null);
    TextView t1 = (TextView) lst_item_view.findViewById(R.id.field1);
    TextView t2 = (TextView) lst_item_view.findViewById(R.id.field2);
    t1.setText("some value");
    t2.setText("another value");

    // dinamically add TextViews for each item in ArrayList list_schedule
    for(int i = 0; i < list_schedule.size(); i++){
        View schedule_view = inflater.inflate(R.layout.schedule_layout, (ViewGroup) lst_item_view, false);
        ((TextView)schedule_view).setText(list_schedule.get(i));
        ((ViewGroup) lst_item_view).addView(schedule_view);
    }
    return lst_item_view;
}
public MyAdapter(Context context, List<MyObject> objects) extends ArrayAdapter {
  super(context, 1, objects);
  /* We get the inflator in the constructor */
  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
  View view;
  /* We inflate the xml which gives us a view */
  view = mInflater.inflate(R.layout.my_list_custom_row, parent, false);

  /* Get the item in the adapter */
  MyObject myObject = getItem(position);

  /* Get the widget with id name which is defined in the xml of the row */
  TextView name = (TextView) view.findViewById(R.id.name);

  /* Populate the row's xml with info from the item */
  name.setText(myObject.getName());

  /* Return the generated view */
  return view;
}
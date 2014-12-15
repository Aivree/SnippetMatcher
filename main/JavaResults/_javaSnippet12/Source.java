Intent intent = new Intent();
intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
intent.setClassName(this,"com.mainscreen.activity2");
startActivity(intent);
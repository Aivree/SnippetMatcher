package cz.csas.cashninja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AskNinja extends Activity {

    private TextView mTextView;
    public static int requiredAmount;
    GridViewPager gridViewPager;

    private TextView amountTextView;
    private View amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_ninja);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final LayoutInflater inflater = getLayoutInflater();
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                displaySpeechRecognizer();

                gridViewPager = (GridViewPager) findViewById(R.id.pager);
                gridViewPager.setAdapter(new MyGridViewPagerAdapter());
//                gridViewPager.addView(inflater.inflate(R.layout.selector_generic, stub));;
            }
        });


    }

    private class MyGridViewPagerAdapter extends GridPagerAdapter {
        @Override
        public int getColumnCount(int arg0) {
            return 1;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        protected Object instantiateItem(ViewGroup container, int row, int col) {

            final View view;


            if (col == 0 ) {
                amountView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.check_balance, container, false);
                container.addView(amountView);
                return amountView;
            } else if (col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.insufficient_funds, container, false);
                final TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(String.format("dvojka", row, col));
                container.addView(view);
                return view;
            } else if (col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_layout_item, container, false);
                final TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(String.format("trojka", row, col));
                container.addView(view);
                return view;
            } else if (col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_layout_item, container, false);
                final TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(String.format("ctyrka", row, col));
                container.addView(view);
                return view;
            } else if (col == 4) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_layout_item, container, false);
                final TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(String.format("ctyrka", row, col));
                container.addView(view);
                return view;
            }

            return null;


        }

        @Override
        protected void destroyItem(ViewGroup container, int row, int col, Object view) {
            container.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }

    private void vibrateWatch() {
//        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds v.vibrate(500);
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "How much do you need?");
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            Log.v("Ninja", spokenText);

            try {
                requiredAmount = Integer.parseInt(spokenText);

                if (amountView != null) {

                    final DelayedConfirmationView dcv = (DelayedConfirmationView) amountView.findViewById(R.id.delayed_confirmation);
                    dcv.setTotalTimeMs(3000);
                    dcv.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
                        @Override
                        public void onTimerFinished(View view) {
                            Intent intent = new Intent(AskNinja.this, FundsLowActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onTimerSelected(View view) {
                            Intent intent = new Intent(AskNinja.this, FundsLowActivity.class);
                            startActivity(intent);
                        }
                    });
                    dcv.start();

                    amountTextView = (TextView) amountView.findViewById(R.id.asked_amount);
                    amountTextView.setText(requiredAmount + "CZK");
                    amountView.invalidate();
                }

                Log.v("Ninja", requiredAmount + "CZK");
            } catch (NumberFormatException e) {
                Log.v("Ninja", "spoken text is not integer");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}

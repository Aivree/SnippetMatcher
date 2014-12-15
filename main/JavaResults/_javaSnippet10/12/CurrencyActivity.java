    package ak.logbook;



    import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


    public class CurrencyActivity extends ActionBarActivity implements TextWatcher {

        EditText eT_currency;
        EditText eT_price;
        Spinner CurrencySpinner;
        String currency = null;
        int id, icon, tmpIcon;
        double price;
        boolean isDirty, add = false;
        ActionBar bar;
        MenuItem btnSave;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.a_currency);
            bar = getSupportActionBar();
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayHomeAsUpEnabled(true);
            eT_currency = (EditText) findViewById(R.id.CurrencyName);
            eT_price = (EditText) findViewById(R.id.CurrencyPrice);
            CurrencySpinner = (Spinner) findViewById(R.id.CurrencySpinner);
            Intent intent = getIntent();
            IconAdapter mIconAdapter = new IconAdapter(this,R.layout.l_icon,Icon.getArrayList());
            CurrencySpinner.setAdapter(mIconAdapter);
            CurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position!=icon){
                        tmpIcon = position;
                        if (!isDirty) {
                            isDirty=true;
                            supportInvalidateOptionsMenu();
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            if (Main.enableLog) Log.w("ak.Log", "CurrencyActivity received the Intent with action " +
                    intent.getAction());
            if (intent.getAction().equals(Main.ACTION_EDIT)){
                id = intent.getIntExtra(Table_Currency.ID,0);
                currency = intent.getStringExtra(Table_Currency.CURRENCY);
                icon = intent.getIntExtra(Table_Currency.ICON, 0);
                price = intent.getDoubleExtra(Table_Currency.PRICE, 0);
                eT_currency.setText(currency);
                eT_price.setText(price+"");
                CurrencySpinner.setSelection(icon);
                bar.setTitle(currency);
            }
            if (intent.getAction().equals(Main.ACTION_ADD)){
                add =true;
                bar.setTitle(R.string.newcurrency);
            }
            eT_currency.addTextChangedListener(this);
            eT_price.addTextChangedListener(this);
            if (Main.enableLog) Log.w("ak.Log", "onCreate CurrencyActivity");

        }

        @Override
        public boolean onOptionsItemSelected (MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case android.R.id.home:
                    finish();
                    return true;
                case R.id.menu_save:
            if (isDirty) {
                if (Main.enableLog)
                    Log.w("ak.Log", "isDirty="+isDirty);
                icon = tmpIcon;
                if ((eT_currency.getText().toString().length()==0)|(eT_price.getText().toString().length()==0)) {
                    Toast.makeText(this, "Некорректные данные", Toast.LENGTH_LONG).show();
                    if (eT_currency.getText().toString().length()==0) { eT_currency.setBackgroundResource(R.color.redColor);}
                    if (eT_price.getText().toString().length()==0) { eT_price.setBackgroundResource(R.color.redColor);}
                }
                else {
                    if (Main.enableLog)
                        Log.w("ak.Log", "CurrencyActivity c изменениями");
                    Intent answerIntent = new Intent ();
                    answerIntent.putExtra (Table_Currency.ID, id);
                    answerIntent.putExtra (Table_Currency.CURRENCY, eT_currency.getText().toString());
                    answerIntent.putExtra (Table_Currency.PRICE, Double.parseDouble(eT_price.getText().toString()));
                    answerIntent.putExtra (Table_Currency.ICON, icon);
                    if (add){
                        answerIntent.setAction(Main.ACTION_ADD);
                        if (Main.enableLog)
                            Log.w("ak.Log", "CurrencyActivity ACTION_ADD");
                    } else {
                        answerIntent.setAction(Main.ACTION_EDIT);
                    }
                    setResult(RESULT_OK, answerIntent);
                    finish();
                }
            } else {
                if (Main.enableLog)
                    Log.w("ak.Log", "CurrencyActivity без изменений");
                setResult(RESULT_CANCELED);
                finish();
            }
                return true;
                default:
                    return super.onOptionsItemSelected(menuItem);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isDirty) {
                isDirty=true;
                supportInvalidateOptionsMenu();
            }
            if (Main.enableLog)
                Log.w("ak.Log", "afterTextChanged "+s);

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (Main.enableLog)
                Log.w("ak.Log", "beforeTextChanged "+s);

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Main.enableLog)
                Log.w("ak.Log", "onTextChanged "+s);

        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            id=savedInstanceState.getInt("id");
            icon=savedInstanceState.getInt("icon");
            currency=savedInstanceState.getString("currency");
            price=savedInstanceState.getDouble("price");
            isDirty=savedInstanceState.getBoolean("isDirty");
            add=savedInstanceState.getBoolean("add");
            if (Main.enableLog)
                Log.d("ak.Log", "onRestoreInstanceState CurrencyActivity");
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("id", id);
            outState.putInt("icon", icon);
            outState.putString("currency", currency);
            outState.putDouble("price", price);
            outState.putBoolean("isDirty", isDirty);
            outState.putBoolean("add", add);
            if (Main.enableLog)
                Log.d("ak.Log", "onSaveInstanceState CurrencyActivity");
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.itemmenu, menu);
            btnSave = menu.findItem(R.id.menu_save);
            if (!isDirty) btnSave.setVisible(false);
            return true;
        }

    }

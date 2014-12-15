package com.sadeghsalamy.badansazi;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import com.sadeghsalamy.badansazi.Utils.Farsi;

/**
 * User: ayhan
 * Date: 11/5/13
 * Time: 2:13 PM
 */
public class Main extends FragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        Typeface persianFont = Typeface.createFromAsset(getAssets(), "BBCNasim.ttf");
        Button exerciseButton = (Button) findViewById(R.id.main_exercise);
        Button managementButton = (Button) findViewById(R.id.main_management);
        Button rankingButton = (Button) findViewById(R.id.main_ranking);
        Button buyCreditButton = (Button) findViewById(R.id.main_buyCredit);
        Button proudsButton = (Button) findViewById(R.id.main_prouds);
        Button newsButton = (Button) findViewById(R.id.main_news);
        exerciseButton.setText(Farsi.Convert(getString(R.string.main_exercise)));
        managementButton.setText(Farsi.Convert(getString(R.string.main_management)));
        rankingButton.setText(Farsi.Convert(getString(R.string.main_ranking)));
        buyCreditButton.setText(Farsi.Convert(getString(R.string.main_buyCredit)));
        proudsButton.setText(Farsi.Convert(getString(R.string.main_prouds)));
        newsButton.setText(Farsi.Convert(getString(R.string.main_news)));
        exerciseButton.setOnClickListener(this);
        managementButton.setOnClickListener(this);
        rankingButton.setOnClickListener(this);
        buyCreditButton.setOnClickListener(this);
        proudsButton.setOnClickListener(this);
        newsButton.setOnClickListener(this);
        exerciseButton.setTypeface(persianFont);
        managementButton.setTypeface(persianFont);
        rankingButton.setTypeface(persianFont);
        buyCreditButton.setTypeface(persianFont);
        proudsButton.setTypeface(persianFont);
        newsButton.setTypeface(persianFont);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_exercise:
                Intent gymActivity = new Intent(this, Gym.class);
                gymActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(gymActivity);
                break;
            case R.id.main_management:
                Intent managementActivity = new Intent(this, Management.class);
                managementActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(managementActivity);
                break;
            case R.id.main_ranking:
                Intent rankingActivity = new Intent(this, Ranking.class);
                rankingActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(rankingActivity);
                break;
            case R.id.main_buyCredit:
                Intent buyCreditActivity = new Intent(this, Credit.class);
                buyCreditActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(buyCreditActivity);
                break;
            case R.id.main_news:
                Intent newsActivity = new Intent(this, NewsList.class);
                newsActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(newsActivity);
                break;
            case R.id.main_prouds:
                Intent proudsActivity = new Intent(this, Prouds.class);
                proudsActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(proudsActivity);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
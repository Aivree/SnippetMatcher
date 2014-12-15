package com.example.projectweek;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by svirch_n on 29/01/14.
 * nicolas.svirchevsky@epitech.eu
 */
public class FragmentQuestionnaire extends Fragment implements View.OnClickListener, IPictureTaker {

    private int numberQuestion = 0;

    private String[] mQuestions = {
            "Que pensez vous de la nouvelle collection printemps/été ?",
            "Que pensez vous de la qualité du service ?",
            "Avez vous acheté un article ?",
    };

    private String[][] mResponses = {
            {"Génial", "Cool", "Ca passe", "Médiocre"},
            {"Génial", "Cool", "Ca passe", "Médiocre"},
            {"Oui", "Non"}
    };

    public FragmentQuestionnaire() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);

        return (view);
    }

    @Override
    public void onResume() {
        super.onResume();
        fillView();
    }

    private void fillView() {
        View view = getView();

        if (numberQuestion >= mQuestions.length)
            return;
        if (view == null)
            return;

        ((TextView) view.findViewById(R.id.text_number)).setText((numberQuestion + 1) + "");
        ((TextView) view.findViewById(R.id.text_max)).setText((mQuestions.length + 1) + "");
        ((TextView) view.findViewById(R.id.question)).setText(mQuestions[numberQuestion]);

        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.container);
        viewGroup.removeAllViews();
        View newView;

        if (viewGroup != null)
            for (int i = 0; i < mResponses[numberQuestion].length; i++) {
                newView = getActivity().getLayoutInflater().inflate(R.layout.item_quesitonnaire, viewGroup, false);
                if (newView != null) {
                    ((TextView) newView.findViewById(R.id.text)).setText(mResponses[numberQuestion][i]);
                    newView.setOnClickListener(this);
                    viewGroup.addView(newView);
                }
            }
    }

    private void fillViewAvis() {
        View view = getView();

        ((TextView) view.findViewById(R.id.text_number)).setText((mQuestions.length + 1) + "");
        ((TextView) view.findViewById(R.id.text_max)).setText((mQuestions.length + 1) + "");
        ((TextView) view.findViewById(R.id.question)).setText("Donnez votre avis général");

        ViewGroup parent = (ViewGroup) view.findViewById(R.id.container);

        parent.removeAllViews();


        View newView = getActivity().getLayoutInflater().inflate(R.layout.question_textview, null);

        if (newView != null) {
            parent.addView(newView);
            view.findViewById(R.id.validate).setOnClickListener(this);
        }
    }

    private void getPoint() {
        View view = getView();

        ((ViewGroup) view).removeAllViews();

        View newView = getActivity().getLayoutInflater().inflate(R.layout.fragment_questionnaire_thanks, null);

        if (newView != null)
            ((ViewGroup) view).addView(newView);

        view.findViewById(R.id.validate).setOnClickListener(this);
    }

    public boolean backPressed() {
        if (numberQuestion <= 0 || numberQuestion > mQuestions.length)
            return (true);
        numberQuestion--;
        fillView();
        return false;
    }

    @Override
    public void onClick(View view) {
        numberQuestion++;

        if (numberQuestion < mQuestions.length)
            fillView();
        else if (numberQuestion == mQuestions.length)
            fillViewAvis();
        else
            getPoint();
    }

    public void setImage(Bitmap bitmap) {
        ((ImageView) getView().findViewById(R.id.image)).setImageBitmap(bitmap);
    }

    @Override
    public void getPicture(Bitmap bitmap) {
        setImage(bitmap);
    }

}

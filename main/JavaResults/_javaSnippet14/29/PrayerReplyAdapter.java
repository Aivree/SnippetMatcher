package com.princeton.prayforme.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.princeton.prayforme.GlobalConstants;
import com.princeton.prayforme.R;
import com.princeton.prayforme.list.PrayerReplyMainItem;
import com.princeton.prayforme.list.ReplyItem;
import com.princeton.prayforme.model.PrayerReply;
import com.princeton.prayforme.model.Reply;

import java.util.ArrayList;
import java.util.List;

public class PrayerReplyAdapter extends PagerAdapter {

    private List<PrayerReply> prayerReplies;
    Activity context;
    LayoutInflater layoutInflater;

    public PrayerReplyAdapter(Activity context) {
        this.context = context;
        layoutInflater = context.getLayoutInflater();
        prayerReplies = new ArrayList<PrayerReply>();
    }

    @Override
    public int getCount() {
        return prayerReplies.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup view = (ViewGroup) layoutInflater.inflate(R.layout.item_prayerview, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        ListAdapter listAdapter = new ListAdapter(context);
        listView.setAdapter(listAdapter);


        PrayerReplyMainItem prayerItem = new PrayerReplyMainItem(context, prayerReplies.get(position).getPrayer());
        listAdapter.add(prayerItem);
//        ViewGroup prayerView = (ViewGroup) layoutInflater.inflate(prayerItem.getResourceId(), view, false);
//        prayerItem.populate(prayerView);
//        listView.addView(prayerView);


        for (Reply reply : prayerReplies.get(position).getReplies()) {
            ReplyItem replyItem = new ReplyItem(reply);
//            View replyView = layoutInflater.inflate(replyItem.getResourceId(), view, false);
//            replyItem.populate(replyView);
//            listView.addView(replyView);
            listAdapter.add(replyItem);
        }

        final EditText editText = (EditText) view.findViewById(R.id.send_edit);

        ProgressDialog loadingDialog;

        container.addView(view);
        return view;
//     return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//        super.destroyItem(container, position, object);
    }

    public void addItem(PrayerReply prayerReply) {
        prayerReplies.add(prayerReply);
    }

    public PrayerReply getItem(int pos) {
        return prayerReplies.get(pos);
    }

    public void removeItem(int pos) {
        prayerReplies.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}

package com.lafourchette.lafourchette.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lafourchette.lafourchette.LF_MyApplication;
import com.lafourchette.lafourchette.R;
import com.lafourchette.lafourchette.model.RestaurantTagsDetailed;
import com.lafourchette.lafourchette.model.TagList;

public class LF_PracticalInfo_Fragment extends Fragment {
    private View _layout = null;
    private Context _context = null;
    private LinearLayout _rootView;
    private LF_MyApplication _gbl;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _context = activity;
        // GLOBAL
        _gbl = LF_MyApplication.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = inflater.inflate(R.layout.fragment_practicalinfo, container, false);
        _rootView = (LinearLayout) _layout.findViewById(R.id.practicalinfo_root_container);
        if (_gbl._restaurantGetInfo_api != null && _gbl._restaurantGetInfo_api.data != null
                && _gbl._restaurantGetInfo_api.data.restaurantTagsDetailed != null
                && _gbl._restaurantGetInfo_api.data.restaurantTagsDetailed.size() > 0) {
            initUiWithData();
        }
        return (_layout);
    }

    private void initUiWithData() {
        LayoutInflater layoutInflater = ((Activity) _context).getLayoutInflater();
        LinearLayout itemView;
        String txt;

        for (RestaurantTagsDetailed restTagDtls : _gbl._restaurantGetInfo_api.data.restaurantTagsDetailed) {
            if (restTagDtls.id_restaurant_tag_category == 14) {
                continue;
            }
            itemView = (LinearLayout) layoutInflater.inflate(R.layout.item_practicalinfo, _rootView, false);
            ((TextView) itemView.getChildAt(0)).setText(restTagDtls.category_name);
            if (restTagDtls.tagList != null) {
                txt = "";
                for (TagList tag : restTagDtls.tagList) {
                    txt += ((txt.isEmpty()) ? "" : ", ") + tag.tag_name;
                }
                ((TextView) itemView.getChildAt(1)).setText(txt);
            }
            _rootView.addView(itemView);
        }
    }
}

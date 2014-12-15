package com.funo.provincialtourism.fragments;

import com.funo.antennatestsystem.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment3 extends Fragment {
	private int[] images = { R.drawable.yuding_0201, R.drawable.yuding_0202,
			R.drawable.yuding_0203, R.drawable.yuding_0204,
			R.drawable.yuding_0205, R.drawable.yuding_0206,
			R.drawable.yuding_0301, R.drawable.yuding_0302,
			R.drawable.yuding_0303, R.drawable.yuding_0304,
			R.drawable.yuding_0105 };
	private String date[][] = { { "厦门鼓浪屿", "位于日光岩莲花庵前的巨石上有概述题字‘岩石’" },
			{ "厦门鼓浪屿", "位于日光岩莲花庵前的巨石上有概述题字‘岩石’" }, { "日光岩", "登日光岩俯瞰鼓浪屿" },
			{ "万国建筑", "这里是鼓浪屿中西文化交流的精萃景观。中国传统建筑，如宗教建筑..." },
			{ "小店", "逛小店感受小清新" }, { "钢琴博物馆", "在音乐之岛聆听琴声飘飘" },
			{ "厦门海底世界", "融生态保护，海洋知识、海洋水产、科教，观光于一体的大型海洋水..." },
			{ "皓月园", "无论是球星、政客，还是影视巨星，你都能在这里发现，馆中塑像都..." },
			{ "菽庄花园", "曾为私人别墅的海滨花园，远眺可见海浪、园中可见秀丽园林。" },
			{ "郑成功纪念馆", "郑成功纪念馆是1962年为纪念郑成功收复台湾300周年而建立..." },
			{ "钢琴码头", "如同一架三角钢琴的钢琴码头，是钢琴之岛迎接游人的起点，踏上码..." } };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.activity_zijiqu, null);
		RelativeLayout rl = (RelativeLayout) layout
				.findViewById(R.id.rel_view_rellayout);
		int height = dip2Px(getActivity(), 150);
		for (int i = 0; i < images.length; i++) {
			if (i % 2 == 0) {// 左边
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, height);
				param.topMargin = (i / 2) * height;
				View view = getActivity().getLayoutInflater().inflate(
						R.layout.scenerylistleft, null);
				initLeftWidget(view, i);
				rl.addView(view, param);

			} else {// 右边
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, height);
				param.topMargin = ((i - 1) / 2) * height + dip2Px(getActivity(), 50);
				View view = getActivity().getLayoutInflater().inflate(
						R.layout.scenerylistright, null);
				initRightWidget(view, i);
				rl.addView(view, param);
			}
		}
		return layout;
	}

	// 左边控件赋值
	private void initLeftWidget(View view, int i) {
		ImageView img = (ImageView) view
				.findViewById(R.id.iv_view_sceneryleft_image);
		TextView title = (TextView) view.findViewById(R.id.tv_view_left_title);
		TextView info = (TextView) view.findViewById(R.id.tv_view_left_info);
		img.setImageResource(images[i]);
		title.setText(date[i][0]);
		info.setText(date[i][1]);
	}

	// 右边控件赋值
	private void initRightWidget(View view, int i) {
		ImageView img = (ImageView) view
				.findViewById(R.id.iv_view_sceneryright_image);
		TextView title = (TextView) view.findViewById(R.id.tv_view_right_title);
		TextView info = (TextView) view.findViewById(R.id.tv_view_right_info);
		img.setImageResource(images[i]);
		title.setText(date[i][0]);
		info.setText(date[i][1]);
	}

	// dp转换成px
	public static int dip2Px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}

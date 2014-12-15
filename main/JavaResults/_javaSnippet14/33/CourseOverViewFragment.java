package com.boha.cmauthor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.boha.cmauthor.R;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.listeners.PageInterface;

public class CourseOverViewFragment extends Fragment implements PageInterface {

	public CourseOverViewFragment() {
		// TODO Auto-generated constructor stub
	}

	View view;
	Context ctx;
	CourseDTO course;

	@Override
	public void onAttach(Activity a) {
		/*if (a instanceof ActivityListener) {
			activityListener = (ActivityListener) a;
		} else {
			throw new UnsupportedOperationException();
		}*/
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	LayoutInflater mInflater;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		mInflater = getActivity().getLayoutInflater();
		view = mInflater.inflate(R.layout.course_overview_item, container,
				false);
		Bundle b = getArguments();
		course = (CourseDTO)b.getSerializable("course");
		setFields();
		return view;
	}
	private LinearLayout lessonsLayout;
	
	private void setFields() {
		
		lessonsLayout = (LinearLayout)view.findViewById(R.id.CRS_lessonsLayout);
//		for (LessonDTO lesson : course.getLessonList()) {
//			View lsnView = mInflater.inflate(R.layout.lesson_overview_item, null);
//			TextView name = (TextView)lsnView.findViewById(R.id.LSN_name);
//			TextView pr = (TextView)lsnView.findViewById(R.id.LSN_priority);
//			name.setText(lesson.getLessonName());
//			ZeroUtil.formatPadWithZero(pr, lesson.getPriorityFlag());
//			LinearLayout activitiesLayout = (LinearLayout) lsnView.findViewById(R.id.LSN_activitiesLayout);
//			for (ActivityDTO act : lesson.getActivityList()) {
//				View actView = mInflater.inflate(R.layout.act_overview_item, null);
//				TextView nameA = (TextView)actView.findViewById(R.id.ACT_name);
//				TextView prA = (TextView)actView.findViewById(R.id.ACT_priority);
//				nameA.setText(act.getActivityName());
//				ZeroUtil.formatPadWithZero(prA, act.getPriorityFlag());
//				activitiesLayout.addView(actView);
//			}
//
//
//			lessonsLayout.addView(lsnView);
//		}
	}

	public CourseDTO getCourse() {
		return course;
	}

}

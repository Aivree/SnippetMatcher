/**
 * Copyright 2012-2013 Mairie de Paris
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
**/

package com.ui;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.containers.User;
import com.ui.takemydata.R;

public class TmdUi extends SherlockActivity implements AuthentInterface {

	public static final int CLASSEMENT = 10;
	public static final int CHALLENGES = 11;
	public static final int DECORATION = 12;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i;
		switch (item.getItemId()) {

		case R.id.map:
			i = new Intent(this, Map.class);
			i.putExtra("from", Map.ACTION_BARRE);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			break;
		case R.id.decorations:
			User.connect(this, DECORATION);
			break;
		case R.id.help:
			i = new Intent(this, Help.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			break;
		case R.id.about:
			i = new Intent(this, About.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			break;
		case R.id.grade:
			User.connect(this, CLASSEMENT);
			break;
		case R.id.challenges:
			User.connect(this, CHALLENGES);
			break;
		}
		return true;
	}

	public void authentAction(boolean isAuthenticated, int keyCode) {
		Intent i = null;
		if (!isAuthenticated) {
			return;
		} else if (keyCode == CHALLENGES) {
			i = new Intent(this, Challenges.class);
		} else if (keyCode == CLASSEMENT) {
			i = new Intent(this, Grades.class);
		} else if (keyCode == DECORATION) {
			i = new Intent(this, Decorations.class);
		}
		if (i != null) {
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
	}
}

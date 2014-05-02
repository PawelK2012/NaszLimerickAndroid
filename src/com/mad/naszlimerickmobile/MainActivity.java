package com.mad.naszlimerickmobile;

import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class MainActivity extends Activity {

	Button buttonNe;
	Button buttonJo;
	Button buttonRe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#009900"));
		ab.setBackgroundDrawable(colorDrawable);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_home:
			intent = new Intent(MainActivity.this, MainActivity.class);
			startActivity(intent);
			break;

		case R.id.action_news:
			intent = new Intent(this, NewsActivity.class);
			startActivity(intent);
			break;

		case R.id.action_note:
			intent = new Intent(this, TodosOverviewActivity.class);
			startActivity(intent);
			break;

		case R.id.action_point_interest:
			intent = new Intent(this, PointInterestActivity.class);
			startActivity(intent);
			break;

		case R.id.action_accommondation:
			intent = new Intent(this, AccommondationActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	public void goToNews(View v) {

		Intent intent = new Intent(this, NewsActivity.class);
		startActivity(intent);

	}

	public void goToNotes(View v) {
		Intent intent = new Intent(this, TodosOverviewActivity.class);
		startActivity(intent);
	}

	public void goToPointInterest(View v) {
		Intent intent = new Intent(this, PointInterestActivity.class);
		startActivity(intent);
	}

	public void goToAccommondation(View v) {
		Intent intent = new Intent(this, AccommondationActivity.class);
		startActivity(intent);
	}

}

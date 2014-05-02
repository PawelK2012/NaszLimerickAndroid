package com.mad.naszlimerickmobile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

@SuppressLint("HandlerLeak")
public class NewsActivity extends Activity {

	static String LIST_URL = "http://www.naszlimerick.pl/feed/";

	ArrayList<HashMap<String, String>> packageList;

	Handler mHandler;
	PackageDownloadTask mTask;
	ProgressDialog mProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#009900"));
		ab.setBackgroundDrawable(colorDrawable);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				displayNlPackages();
				mProgress.dismiss();
			}
		};

		startBackgroundTask();

	}

	private void displayNlPackages() {
		String from[] = { "title", "link", "pubDate", "category", "description" };
		int to[] = { R.id.pTitle, R.id.pLink, R.id.pPubDate, R.id.pCategory,
				R.id.pDescription };

		SimpleAdapter a = new SimpleAdapter(this, packageList,
				R.layout.news_list_item, from, to);
		ListView l = (ListView) findViewById(R.id.listViewTest);
		l.setAdapter(a);
	}

	public class PackageDownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.v("XML", "Loading data from AsyncTask");
			loadNlPackages();
			if (!isCancelled()) {
				mHandler.sendEmptyMessage(0);
			}

			return null;
		}

		private void loadNlPackages() {
			try {
				HttpParams params = new BasicHttpParams();
				DefaultHttpClient client = new DefaultHttpClient(params);
				HttpGet get = new HttpGet(LIST_URL);
				HttpResponse res = client.execute(get);
				int code = res.getStatusLine().getStatusCode();

				if (code != 200) {
					throw new Exception("Invalid status code: " + code);
				}

				Log.v("XML", "Successfully fetched XML file");

				InputStream is = res.getEntity().getContent();

				packageList = new ArrayList<HashMap<String, String>>();

				XmlPullParserFactory parserCreator = XmlPullParserFactory
						.newInstance();
				XmlPullParser parser = parserCreator.newPullParser();

				parser.setInput(is, null);

				int event;
				String title = null;
				String link = null;
				String pubDate = null;
				String category = null;
				String description = null;
				String tag = null;

				while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
					if (isCancelled()) {
						Log.v("Xml", "Canceling xml processing");
						break;
					}
					if (event == XmlPullParser.START_TAG) {
						tag = parser.getName();
						if ("item".equals(tag)) {
							title = link = pubDate = description = ""; // Clear
																		// values
						}
					} else if (event == XmlPullParser.TEXT) {
						if ("link".equals(tag)) {
							link = parser.getText();
						} else if ("pubDate".equals(tag)) {
							pubDate = parser.getText();
						} else if ("category".equals(tag)) {
							category = parser.getText();
						} else if ("description".equals(tag)) {
							description = parser.getText();
						} else if ("title".equals(tag)) {
							title = parser.getText();
						}
					} else if (event == XmlPullParser.END_TAG) {
						if ("item".equals(parser.getName())) {
							HashMap<String, String> map = new HashMap<String, String>();

							map.put("title", title);
							map.put("link", link);
							map.put("pubDate", pubDate);
							map.put("category", category);
							map.put("description", description);
							packageList.add(map);
							Log.v("XML", "Loaded package: " + title);
						}
						tag = null;
					}
				}

			} catch (Exception e) {
				Log.v("XML", "Error fetching packages.xml", e);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_home:
			intent = new Intent(this, MainActivity.class);
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

	private void startBackgroundTask() {
		mProgress = ProgressDialog.show(this, "Loading Nasz Limerick",
				"Please wait...", true);
		mProgress.setCancelable(true);
		mProgress.show();
		mTask = new PackageDownloadTask();
		mTask.execute();

	}

	@Override
	protected void onDestroy() {
		mTask.cancel(false);
		super.onDestroy();
	}

}

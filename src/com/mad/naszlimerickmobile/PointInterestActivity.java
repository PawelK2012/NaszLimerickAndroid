package com.mad.naszlimerickmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PointInterestActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	GoogleMap mMap;

	@SuppressWarnings("unused")
	private static final double LIMERICK_LAT = 52.6653, LIMERICK_LNG = -8.6238;

	static final LatLng ST_MICHAEL_CHURCH = new LatLng(52.663915, -8.624037),
			WISLA_GROCERY = new LatLng(52.662768, -8.622605),
			SEV_MOTORS = new LatLng(52.627648, -8.577332),
			JOANNA_ACCOUNTING = new LatLng(52.631855, -8.646608),
			POLISH_SCHOOL = new LatLng(52.655905, -8.638280),
			CASH_CARRY = new LatLng(52.652366, -8.586780),
			DOMINATOR_GYM = new LatLng(52.661524, -8.619192);

	private static final float DEFAULTZOOM = 12;
	private static final String LOGTAG = "Maps";

	LocationClient mLocationClient;

	//
	ArrayList<LatLng> markerPoints;
	TextView tvDistanceDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#009900"));
		ab.setBackgroundDrawable(colorDrawable);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (servicesOK()) {

			setContentView(R.layout.activity_point_interest);
			if (initMap()) {
				Toast.makeText(this, "Map ready", Toast.LENGTH_SHORT).show();
				gotoLocation(LIMERICK_LAT, LIMERICK_LNG, DEFAULTZOOM);
				mMap.setMyLocationEnabled(true);
				mLocationClient = new LocationClient(this, this, this);
				mLocationClient.connect();

				Marker michaelchurch = mMap.addMarker(new MarkerOptions()
						.position(ST_MICHAEL_CHURCH)
						.title("St Michael's Church - Polskie Duszpasterstwo")
						.snippet("Denmark St, Co. Limerick"));

				Marker wislagrocery = mMap.addMarker(new MarkerOptions()
						.position(WISLA_GROCERY)
						.title("Wisla - Polish Grocery")
						.snippet("Mon-Sat 10am-8pm Sun 12pm-6pm"));

				Marker sevmotors = mMap.addMarker(new MarkerOptions()
						.position(SEV_MOTORS)
						.title("SEV MOTORS - Polish Car Mechanic")
						.snippet("Tel: 085 743 9054"));

				Marker joannaaccounting = mMap
						.addMarker(new MarkerOptions()
								.position(JOANNA_ACCOUNTING)
								.title("Joanna Accounting - Polish Accounting Services")
								.snippet("Tel: 085 136 7592"));

				Marker polishschool = mMap.addMarker(new MarkerOptions()
						.position(POLISH_SCHOOL)
						.title("Polish School in Limerick")
						.snippet("Tel:085 703 6127 "));

				Marker cashcarry = mMap
						.addMarker(new MarkerOptions()
								.position(CASH_CARRY)
								.title("Cash & Carry")
								.snippet(
										"Eastlink Business Park Unit 20, Ballysimon Road"));

				Marker dominator = mMap.addMarker(new MarkerOptions()
						.position(DOMINATOR_GYM).title("Dominator Gym")
						.snippet("Tel: 087 750 7498"));

			} else {
				Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT)
						.show();

			}
		} else {
			setContentView(R.layout.activity_main);
		}

		this.tvDistanceDuration = (TextView) this
				.findViewById(R.id.tv_distance_time);

		this.markerPoints = new ArrayList<LatLng>();

		SupportMapFragment fm = (SupportMapFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.map);

		this.mMap = fm.getMap();

		this.mMap.setMyLocationEnabled(true);

		this.mMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {

				if (PointInterestActivity.this.markerPoints.size() > 1) {
					PointInterestActivity.this.markerPoints.clear();
					PointInterestActivity.this.mMap.clear();
				}

				PointInterestActivity.this.markerPoints.add(point);

				MarkerOptions options = new MarkerOptions();

				options.position(point);

				/**
				 * For the start location, the color of marker is GREEN and for
				 * the end location, the color of marker is RED.
				 */
				if (PointInterestActivity.this.markerPoints.size() == 1) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				} else if (PointInterestActivity.this.markerPoints.size() == 2) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				}

				PointInterestActivity.this.mMap.addMarker(options);

				if (PointInterestActivity.this.markerPoints.size() >= 2) {
					LatLng origin = PointInterestActivity.this.markerPoints
							.get(0);
					LatLng dest = PointInterestActivity.this.markerPoints
							.get(1);

					String url = PointInterestActivity.this.getDirectionsUrl(
							origin, dest);

					DownloadTask downloadTask = new DownloadTask();

					downloadTask.execute(url);
				}
			}
		});

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String sensor = "sensor=false";

		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		String output = "json";

		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... url) {

			String data = "";

			try {

				data = PointInterestActivity.this.downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			if (result.size() < 1) {
				Toast.makeText(PointInterestActivity.this.getBaseContext(),
						"No Points", Toast.LENGTH_SHORT).show();
				return;
			}

			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				List<HashMap<String, String>> path = result.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) {
						distance = point.get("distance");
						continue;
					} else if (j == 1) {
						duration = point.get("duration");
						continue;
					}
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}

				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);
			}

			PointInterestActivity.this.tvDistanceDuration.setText("Distance:"
					+ distance + ", Duration:" + duration);

			PointInterestActivity.this.mMap.addPolyline(lineOptions);
		}
	}

	private void gotoLocation(double lat, double lng, float zoom) {

		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_map, menu);
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
		case R.id.action_currentlocation:
			gotoCurrentLocation();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Can't connect to Google Play serices",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMap = mapFrag.getMap();
		}
		return (mMap != null);
	}

	protected void gotoCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available",
					Toast.LENGTH_SHORT).show();
		} else {
			LatLng ll = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
					DEFAULTZOOM);
			mMap.animateCamera(update);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Connected to location service",
				Toast.LENGTH_SHORT).show();

		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// request checking location every 5 sec.
		request.setInterval(5000);
		request.setFastestInterval(1000);
		mLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String msg = "Location: " + location.getLatitude() + ","
				+ location.getLongitude();

	}

}

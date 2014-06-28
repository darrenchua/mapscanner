
package com.example.mapscanner;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class MainActivity extends Activity implements SensorEventListener, LocationListener, OnMarkerClickListener {

	// global variables
	private GoogleMap googleMap;
	private SensorManager mSensorManager;
	private Sensor magSensor;
	private Sensor accSensor;
	private double latitude,longitude;
	float[] mGravity;
	float[] mGeomagnetic;
	float azimut;
	ArrayList<LatLng> allLocations = new ArrayList<LatLng>();
	Polyline joinLine;

	ArrayList<String> markerInfoArray; // {markerName/markerTitle/markerDetails}

	/*
	 *  Auto-generated method stubs for implemented protocols(non-Javadoc)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


	/*
	 *  Event Listeners
	 */

	// listen for marker click
	@Override
	public boolean onMarkerClick(final Marker marker){
		marker.showInfoWindow();
		Log.v("title: ",marker.getTitle());
		Log.v("hc: ",Integer.toString(marker.hashCode()));


		for(String s : markerInfoArray){
			String placeHash = s.split("/")[0];
			if(marker.getTitle().equals(placeHash)){
				String placeDetails = s.split("/")[2];
				String placeName = s.split("/")[1];
				/*
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(placeName);
				builder.setMessage(placeDetails);
				builder.setCancelable(true);
				builder.setPositiveButton("dismiss", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//do things
					}
				});

				AlertDialog dlg = builder.create();
				dlg.show();
				 */
				Intent detailsScreen = new Intent(getApplicationContext(),detailsView.class);
				detailsScreen.putExtra("placeDetails",placeDetails);
				detailsScreen.putExtra("placeTitle",placeName);
				startActivity(detailsScreen);
			}
		}
		return true;
	}

	public double calcBearing(LatLng from, LatLng to){

		double dLon = (to.longitude - from.longitude);
		double y = Math.sin(dLon)*Math.cos(to.latitude);
		double x = Math.cos(from.latitude)*Math.sin(to.latitude) - Math.sin(from.latitude)*Math.cos(to.latitude)*Math.cos(dLon);
		double bearing = Math.atan2(y, x);

		return bearing;
	}

	// listen for compass change event
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0]; // orientation contains: azimut, pitch and roll
				//Log.v("orientation",Float.toString(azimut));
				CameraPosition pos = CameraPosition.builder().target(new LatLng(latitude,longitude)).bearing((float)Math.toDegrees(azimut)).zoom(18).build();
				googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
				
				drawPolyLine();
			}
		}
	}
	
	public void drawPolyLine(){
		// this function will draw the polyline
		LatLng myLocation = new LatLng(latitude,longitude);
		LatLng pointedLocation = new LatLng(latitude,longitude);
		double closestBearing = 9999; // sentinel value
		for(int i = 0 ; i < allLocations.size() ; i++){
			double currentBearing = calcBearing(myLocation,allLocations.get(i));
			Log.v("currentBearing",Double.toString(currentBearing));
			Log.v("azimut",Float.toString(azimut));
			if(Math.abs((currentBearing-azimut)%3.14) < closestBearing){
				closestBearing = Math.abs(currentBearing-azimut);
				pointedLocation = new LatLng(allLocations.get(i).latitude,allLocations.get(i).longitude);
			}
			Log.v("closestBearing",Double.toString(closestBearing));
		}
		if(joinLine != null){
			ArrayList<LatLng>points = new ArrayList<LatLng>();
			points.add(new LatLng(latitude,longitude));
			points.add(pointedLocation);
			joinLine.setPoints(points);
		}
		else{
			joinLine = googleMap.addPolyline(new PolylineOptions()
			.add(new LatLng(latitude,longitude),pointedLocation).width(5).color(Color.RED));
		}
	}

	// listen for when GPS coord change
	public void onLocationChanged(Location location){
		// Getting latitude of the current location
		latitude = location.getLatitude();

		// Getting longitude of the current location
		longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				latLng).zoom(18).build();

		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		drawPolyLine();
	}

	/*
	 * Initialization methods
	 */
	public void initSensor(){
		Log.v("sensor","init");
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	/*
	 * Networking methods
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnNextScreen = (Button) findViewById(R.id.btnNextScreen);
		btnNextScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				//Starting a new Intent
				Intent nextScreen = new Intent(getApplicationContext(), poiView.class);
				nextScreen.putExtra("locationCoords", "?locationCoords="+Double.toString(latitude)+","+Double.toString(longitude));
				startActivity(nextScreen);

			}
		});

		markerInfoArray = new ArrayList<String>();
		initSensor();

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initilizeMap() {

		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setOnMarkerClickListener(this);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);

			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,10, this);

			getLocations();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	public void getLocations(){
		new sendDataAsync().execute();
	}

	private class sendDataAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... arg0) {
			String responseString = "";
			String url = "http://192.168.1.2/~Ben/mapscanner/getLocations.php?locationCoords="+Double.toString(latitude)+","+Double.toString(longitude); // add loc coords
			HttpResponse response = null;
			try {
				HttpClient client = new DefaultHttpClient();
				response = client.execute(new HttpGet(url));
				responseString = EntityUtils.toString(response.getEntity());
				Log.v("resp: ",responseString);

			} catch(IOException e) {

			}
			return responseString;
		}
		protected void onPostExecute(String result) {
			Log.v("result",result);
			try {
				JSONArray locationsArray = (JSONArray) new JSONTokener(result).nextValue();
				Log.d("json",locationsArray.toString(4));
				/*
				LatLng closestPlace = new LatLng(1,1);
				double closestBearing = 100;
				boolean gotPlace = false;
				 */
				for(int i = 0 ; i < locationsArray.length() ; i++){
					JSONObject locationObject = locationsArray.getJSONObject(i);
					String placeName = locationObject.getString("placeName");
					String placeCoord = locationObject.getString("placeCoord");
					String placeTitle = locationObject.getString("placeTitle");
					String placeSnippet = locationObject.getString("placeSnippet");
					String placeDetails = locationObject.getString("placeDetails");

					double placeLat = Double.parseDouble(placeCoord.split(",")[0]);
					double placeLon = Double.parseDouble(placeCoord.split(",")[1]);

					allLocations.add(new LatLng(placeLat,placeLon));

					/*
					double currentBearing = Math.atan((latitude-placeLat)/(longitude/placeLon));
					if(currentBearing < closestBearing){
						closestBearing = currentBearing;
						closestPlace = new LatLng(placeLat,placeLon);
						gotPlace = true;
					}

					if(gotPlace){
						joinLine = googleMap.addPolyline(new PolylineOptions()
						.add(new LatLng(latitude,longitude),closestPlace).width(5).color(Color.RED));
					}
					 */
					MarkerOptions marker = new MarkerOptions().position(new LatLng(placeLat,placeLon)).title(placeTitle).snippet(placeSnippet);
					googleMap.addMarker(marker).showInfoWindow();
					markerInfoArray.add(placeTitle+"/"+placeName+"/"+placeDetails);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
		initilizeMap();
	}

}
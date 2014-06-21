
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class MainActivity extends Activity implements SensorEventListener, LocationListener, OnMarkerClickListener {
	
	// testing for compass
	 
    // Google Map
    private GoogleMap googleMap;
	private SensorManager mSensorManager;
	private Sensor magSensor;
	private Sensor accSensor;
	private double latitude,longitude;
	
	ArrayList<String> markerInfoArray;
	
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
	    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	        builder.setTitle(placeName);
	    	        builder.setMessage(placeDetails);
	    	        builder.setCancelable(true);
	    	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	            public void onClick(DialogInterface dialog, int id) {
	    	                //do things
	    	            }
	    	           });

	    	        AlertDialog dlg = builder.create();
	    	        dlg.show();
	    		}
	    	}
	    	return true;
	    }
	
	public void initSensor(){
		Log.v("sensor","init");
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	float[] mGravity;
	float[] mGeomagnetic;
	float azimut;
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
	       // Log.v("orientation",Float.toString(azimut));
	        CameraPosition pos = CameraPosition.builder().target(new LatLng(latitude,longitude)).bearing((float)Math.toDegrees(azimut)).zoom(18).build();
	        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
	        }
	    }
	  }
	
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
 	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
    
    public void getLocations(){
		new sendDataAsync().execute();
	}
    
    private class sendDataAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... arg0) {
			String responseString = "";
			String url = "http://192.168.1.2/~Ben/mapscanner/getLocations.php";
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
				for(int i = 0 ; i < locationsArray.length() ; i++){
					JSONObject locationObject = locationsArray.getJSONObject(i);
					String placeName = locationObject.getString("placeName");
					String placeCoord = locationObject.getString("placeCoord");
					String placeTitle = locationObject.getString("placeTitle");
					String placeSnippet = locationObject.getString("placeSnippet");
					String placeDetails = locationObject.getString("placeDetails");
					
					double placeLat = Double.parseDouble(placeCoord.split(",")[0]);
					double placeLon = Double.parseDouble(placeCoord.split(",")[1]);
					
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        markerInfoArray = new ArrayList<String>();
        initSensor();
 
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
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
    

 
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
        initilizeMap();
    }
 
}
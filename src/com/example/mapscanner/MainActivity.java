package com.example.mapscanner;
 
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
 
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;


public class MainActivity extends Activity implements SensorEventListener {
	
	// testing for compass
	
	private SensorManager mSensorManager;
	private Sensor magSensor;
	private Sensor accSensor;
	
	
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
	        Log.v("orientation: ", Float.toString(azimut));
	      }
	    }
	  }
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
	 
    // Google Map
    private GoogleMap googleMap;
    
    public void getLocations(){
		new sendDataAsync().execute();
	}
    
    private class sendDataAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... arg0) {
			String responseString = "";
			String url = "http://benappdev.com/others/test/getLocations.php";
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
	         
	     }
	}
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lon)).zoom(18).build();
     
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
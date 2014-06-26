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


import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class poiView extends ListActivity{
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_view);	 
		adapter=new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            listItems);
	        setListAdapter(adapter);	
	        getLocations();
	}

	public void getLocations(){
		new sendDataAsync().execute();
	}

	private class sendDataAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... arg0) {
			String responseString = "";
			String url = "http://192.168.1.2/~Ben/mapscanner/getLocations.php"; // add loc coords
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
				for(int i = 0 ; i < locationsArray.length() ; i++){
					JSONObject poiObject = locationsArray.getJSONObject(i);
					listItems.add(poiObject.getString("placeName"));
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

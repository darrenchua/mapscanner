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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class poiView extends ListActivity{
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayList<ArrayList<String>> poiArray = new ArrayList<ArrayList<String>>();
    
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        String selectedPOI = (String)getListAdapter().getItem(position);
        for(int i = 0 ; i < poiArray.size() ; i++){
        	if(selectedPOI.equals(poiArray.get(i).get(0))){
        		Intent detailsScreen = new Intent(getApplicationContext(),detailsView.class);
        		detailsScreen.putExtra("placeDetails",poiArray.get(i).get(4));
        		detailsScreen.putExtra("placeTitle",poiArray.get(i).get(1));
        		startActivity(detailsScreen);
        	}
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_view);	 
		adapter=new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            listItems);
	        setListAdapter(adapter);	
	        Intent i = getIntent();
	        String locationCoords = i.getStringExtra("locationCoords");
	        getLocations(locationCoords);
	}

	public void getLocations(String locationCoords){
		new sendDataAsync().execute(locationCoords);
	}

	private class sendDataAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String...arg0) {
			String responseString = "";
			String url = "http://192.168.1.2/~Ben/mapscanner/getLocations.php"+arg0[0]; // add loc coords
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
					
					ArrayList<String> poiInfo = new ArrayList<String>();
					poiInfo.add(poiObject.getString("placeName"));
					poiInfo.add(poiObject.getString("placeTitle"));
					poiInfo.add(poiObject.getString("placeCoord"));
					poiInfo.add(poiObject.getString("placeSnippet"));
					poiInfo.add(poiObject.getString("placeDetails"));
					poiArray.add(poiInfo);
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

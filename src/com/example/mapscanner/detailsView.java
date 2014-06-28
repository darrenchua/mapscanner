package com.example.mapscanner;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class detailsView extends Activity {
	String placeTitle,placeDetails,placeSnippet;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_view);
		
		Intent extraDetails = getIntent();
		placeTitle = extraDetails.getStringExtra("placeTitle");
		placeDetails = extraDetails.getStringExtra("placeDetails");
		
		TextView detailsTitle = (TextView)findViewById(R.id.detailsTitle);
		detailsTitle.setText(placeTitle);
		
		TextView detailsDetails = (TextView)findViewById(R.id.detailsDetails);
		detailsDetails.setText(placeDetails);
		
		Button prevButton = (Button) findViewById(R.id.backButton);
		prevButton.setOnClickListener(new View.OnClickListener() {
			 
            public void onClick(View arg0) {
                finish();
            }
        });
	}
}

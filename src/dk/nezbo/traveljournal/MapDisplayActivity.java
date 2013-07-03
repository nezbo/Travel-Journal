package dk.nezbo.traveljournal;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapDisplayActivity extends FragmentActivity {
	
	private GoogleMap mMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullmap);
		init();
	}
	
	private void init(){
		Bundle bundle = getIntent().getExtras();
		double lon = bundle.getDouble("long");
		double lat = bundle.getDouble("lat");
		
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lon))); // place at target
	}
}

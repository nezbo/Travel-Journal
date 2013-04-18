package dk.nezbo.traveljournal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class TravelsActivity extends Activity{
	
	private DatabaseHelper db;
	private TravelsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.travels);
		
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.update();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
		db = null;
	}

	private void init(){
		db = new DatabaseHelper(this);
		
		ListView lv = (ListView) findViewById(R.id.lvTravelsList);
		adapter = new TravelsListAdapter(this,db.getTravels());
		lv.setAdapter(adapter);
	}
}

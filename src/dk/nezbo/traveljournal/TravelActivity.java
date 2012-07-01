package dk.nezbo.traveljournal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class TravelActivity extends Activity {

	private Travel travel;
	private DateTime focus;
	private DatabaseHelper db;

	private CalendarAdapter adapter;
	private Handler handler;

	private TextView title;
	private Button previous;
	private Button next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		init();
	}

	private void init() {
		db = new DatabaseHelper(this);
		int id = getIntent().getExtras().getInt("id");
		travel = db.getTravel(id);

		// show start of travel or today?
		DateTime now = new DateTime();
		if (now.between(travel.getStart(), travel.getEnd())) {
			focus = now;
		} else {
			focus = travel.getStart();
		}

		// derping
		GridView gridview = (GridView) findViewById(R.id.gvCalGrid);
		
		adapter = new CalendarAdapter(this, focus);
		gridview.setAdapter(adapter);

		handler = new Handler();
		//handler.post(calendarUpdater);

		title = (TextView) findViewById(R.id.tvCalMonthYear);
		title.setText(focus.asStringMonthYear());

		previous = (Button) findViewById(R.id.bCalPrevious);
		next = (Button) findViewById(R.id.bCalNext);

		previous.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				focus.addMonths(-1);
				refreshCalendar();
			}

		});

		next.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				focus.addMonths(1);
				refreshCalendar();
			}

		});

		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				//TODO
			}

		});
	}
	
	public void refreshCalendar(){
		title.setText(focus.asStringMonthYear());
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		//handler.post(calendarUpdater);
	}

}

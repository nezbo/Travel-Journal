package dk.nezbo.traveljournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class TravelActivity extends Activity implements OnClickListener {

	private Travel travel;
	private DateTime focus;
	private DatabaseHelper db;

	private CalendarAdapter adapter;
	private Handler handler;

	private TextView monthYear;
	private Button previous;
	private Button next;
	private TextView title;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onCreateOptionsMenu(menu);
		MenuInflater blow = this.getMenuInflater();
		blow.inflate(R.menu.travel_menu, menu);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.travelChangeStart:
			DateTime start = travel.getStart();
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Select Start Date");

			final DatePicker date = new DatePicker(this);
			date.init(start.getYear(), start.getMonth() - 1, start.getDate(),
					null);

			alert.setView(date);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							DateTime result = new DateTime(date.getYear(), date
									.getMonth() + 1, date.getDayOfMonth(), 0, 0);
							travel.setStart(result);
							refreshCalendar();
						}
					});

			alert.show();

			break;
		case R.id.travelChangeEnd:
			DateTime end = travel.getEnd();
			AlertDialog.Builder a = new AlertDialog.Builder(this);
			a.setTitle("Select End Date");

			final DatePicker d = new DatePicker(this);
			d.init(end.getYear(), end.getMonth() - 1, end.getDate(), null);

			a.setView(d);
			a.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					DateTime result = new DateTime(d.getYear(),
							d.getMonth() + 1, d.getDayOfMonth(), 0, 0);
					travel.setEnd(result);
					refreshCalendar();
				}
			});

			a.show();

			break;
		case R.id.travelDelete:
			db.deleteTravel(travel);
			finish();
			break;
		}

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.saveTravel(travel);

		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshCalendar();
	}

	private void init() {
		db = new DatabaseHelper(this);
		int id = getIntent().getExtras().getInt("id");
		travel = db.getTravel(id);

		// show start of travel or today?
		DateTime now = new DateTime();
		if (now.dayBetween(travel.getStart(), travel.getEnd())) {
			focus = now;
		} else {
			focus = (DateTime) travel.getStart().clone();
		}

		// days
		TextView[] days = new TextView[] {
				(TextView) findViewById(R.id.calMonday),
				(TextView) findViewById(R.id.calTuesday),
				(TextView) findViewById(R.id.calWednesday),
				(TextView) findViewById(R.id.calThursday),
				(TextView) findViewById(R.id.calFriday),
				(TextView) findViewById(R.id.calSaturday),
				(TextView) findViewById(R.id.calSunday) };
		
		for(int i = 0; i < days.length; i++){
			String result = DateTime.getDayName(i);
			System.out.println(i+":"+result);
			days[i].setText(result);
		}

		// derping
		GridView gridview = (GridView) findViewById(R.id.gvCalGrid);

		adapter = new CalendarAdapter(this, focus, travel);
		gridview.setAdapter(adapter);

		handler = new Handler();
		// handler.post(calendarUpdater);

		monthYear = (TextView) findViewById(R.id.tvCalMonthYear);
		monthYear.setText(focus.asStringMonthYear());

		title = (TextView) findViewById(R.id.tvTravelTitle);
		updateTitle();

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

		title.setOnClickListener(this);
	}

	public void refreshCalendar() {
		monthYear.setText(focus.asStringMonthYear());

		adapter.refreshDays();
		adapter.getContentFromDB();
		adapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		if (v == title) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Travel Title");

			final EditText input = new EditText(this);
			input.setText(travel.getTitle());
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							travel.setTitle(input.getText().toString());
							updateTitle();
						}
					});

			alert.show();
		}
	}
	
	private void updateTitle(){
		String titleS = travel.getTitle();
		title.setText(title.equals("") ? Travel.NO_TITLE : titleS);
	}
}

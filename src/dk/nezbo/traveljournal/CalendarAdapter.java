package dk.nezbo.traveljournal;

import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	private DateTime focus;
	private Context context;

	private Travel travel;
	private TravelDay[] items;
	private DatabaseHelper db;

	private String[] days;
	private int firstDay;

	public CalendarAdapter(Context c, DateTime focus, Travel travel) {
		this.focus = focus;
		context = c;
		this.travel = travel;

		// get content
		refreshDays();

		db = new DatabaseHelper(c);
		getContentFromDB();
	}

	private TravelDay traveldayForPosition(int position, DateTime target) {
		if (position < firstDay)
			return null;

		for (TravelDay t : items) {
			if (t != null && t.getDateTime().sameDay(target))
				return t;
		}
		return null;
	}

	public void getContentFromDB() {
		this.items = db.getTravelDays(travel);
	}

	public int getCount() {
		return days.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		boolean start = false;
		boolean end = false;
		boolean text = false;
		boolean images = false;

		if (position >= firstDay /* && !days[position].equals("") */) {
			int day = Integer.parseInt(days[position]);
			DateTime target = new DateTime(focus.getYear(), focus.getMonth(),
					day, 0, 0);

			TravelDay travelday = traveldayForPosition(position, target);

			start = travel.getStart().sameDay(target);
			end = travel.getEnd().sameDay(target);
			if (travelday != null) {
				text = !travelday.getText().equals("");
				images = db.hasImages(travelday.getId());
			}
		}

		View v = convertView;
		TextView dayView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);
		}
		dayView = (TextView) v.findViewById(R.id.tvCalDate);

		// disable items outside month
		if (days[position].equals("")) {
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			v.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					System.out.println("Calendar Day Clicked!");

					int day = Integer.parseInt(days[position]);
					DateTime target = new DateTime(focus.getYear(), focus
							.getMonth(), day, 0, 0);

					if (target.dayBetween(travel.getStart(), travel.getEnd())) {
						TravelDay travelday = traveldayForPosition(position,
								target);
						if (travelday != null) {
							NezboUtils.goToTravelDay(context, travelday.getId());
						} else { // not created yet
							TravelDay current = db.findOrCreateTravelDay(
									travel, target);
							NezboUtils.goToTravelDay(context, current.getId());
						}
					} else {
						System.out
								.println("Calendar Day outside of travel clicked!, nothing will happen...");
					}
				}
			});
		}
		dayView.setText(days[position]);

		v.findViewById(R.id.ivCalDateImage1).setVisibility(
				start ? View.VISIBLE : View.INVISIBLE);
		v.findViewById(R.id.ivCalDateImage2).setVisibility(
				end ? View.VISIBLE : View.INVISIBLE);
		v.findViewById(R.id.ivCalDateImage3).setVisibility(
				text ? View.VISIBLE : View.INVISIBLE);
		v.findViewById(R.id.ivCalDateImage4).setVisibility(
				images ? View.VISIBLE : View.INVISIBLE);

		return v;
	}

	public void refreshDays() {
		Calendar cal = focus.extractCalendar();
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);

		if (firstDay == 1) { // sunday
			firstDay = 6;
		} else {
			firstDay -= 2;
		}

		days = new String[lastDay + firstDay];

		for (int i = 0; i < firstDay; i++) {
			days[i] = "";
		}

		int dayNumber = 1;
		for (int j = firstDay; dayNumber <= lastDay; j++) {
			days[j] = "" + dayNumber;
			dayNumber++;
		}
	}

	public void close() {
		db.close();
	}
}

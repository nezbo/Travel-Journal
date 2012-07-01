package dk.nezbo.traveljournal;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	private DateTime focus;
	private Context context;
	private ArrayList<String> items;
	private String[] days;

	public CalendarAdapter(Context c, DateTime focus) {
		this.focus = focus;
		context = c;
		this.items = new ArrayList<String>();

		refreshDays();
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
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

	public View getView(int position, View convertView, ViewGroup parent) {
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
			// TODO: DO SOMETHING
		}
		dayView.setText(days[position]);

		// TODO: show custom stuff it something on day (R.id.ivCalDateImage)
		return v;
	}

	public void refreshDays() {
		System.out.println("CALENDAR REFRESH: " + focus.toString());
		items.clear();

		Calendar cal = focus.extractCalendar();
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);

		if (firstDay == 1) { // sunday
			firstDay = 6;
		} else {
			firstDay -= 2;
		}

		System.out.println("lastDay=" + lastDay);
		System.out.println("firstDay=" + firstDay);

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
}

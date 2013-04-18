package dk.nezbo.traveljournal;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TravelsListAdapter extends BaseAdapter {

	private DatabaseHelper db;
	private ArrayList<Travel> travels;
	private Context context;

	public TravelsListAdapter(Context c, ArrayList<Travel> travels) {
		context = c;
		this.travels = travels;
		db = new DatabaseHelper(c);
	}

	public int getCount() {
		return travels.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView title, start, end;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.travel_item, null);
		}
		title = (TextView) v.findViewById(R.id.travelItemTitle);
		start = (TextView) v.findViewById(R.id.travelItemStart);
		end = (TextView) v.findViewById(R.id.travelItemEnd);
		
		// info and click
		//travels.set(position, db.getTravel(travels.get(position).getId())); // update
		Travel current = travels.get(position);
		title.setText(current.getTitle());
		start.setText(current.getStart().asStringDay());
		end.setText(current.getEnd().asStringDay());
		
		v.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				NezboUtils.goToTravel(context, travels.get(position).getId());
			}
			
		});
		
		return v;
	}

	public void update(){
		travels = db.getTravels();
		notifyDataSetChanged();
	}
}

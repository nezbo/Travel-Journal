package dk.nezbo.traveljournal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

public class TravelDayActivity extends Activity implements OnItemClickListener {

	private TravelDay today;
	private DatabaseHelper db;

	private TextView title;
	private EditText text;
	private Gallery gallery;
	private ArrayList<AdvImage> images;

	private static final int ACTIVITY_SELECT_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_day);
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		save();
		db.close();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		db.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// db = new DatabaseHelper(this);
		init();
	}

	private void save() {
		// values to TravelDay
		today.setText(text.getText().toString());

		db.saveTravelDay(today);

		for (AdvImage image : images) {
			image.recycle();
		}
	}

	private void init() {
		db = new DatabaseHelper(this);
		int traveldayId = getIntent().getExtras().getInt("id");
		today = db.getTravelDay(traveldayId);
		this.images = db.getImages(today.getId());

		title = (TextView) findViewById(R.id.tvDayTitle);
		text = (EditText) findViewById(R.id.etDayText);
		gallery = (Gallery) findViewById(R.id.galDayImages);
		gallery.setAdapter(new GalleryImageAdapter(this, images));
		gallery.setOnItemClickListener(this);

		// testing
		title.setText(today.getDateTime().asStringDay());
		text.setText(today.getText());
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		NezboUtils.goToImage(this, images.get(position).getId());
		// Toast.makeText(TravelDayActivity.this, "" + position,
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onCreateOptionsMenu(menu);
		MenuInflater blow = this.getMenuInflater();
		blow.inflate(R.menu.travelday_menu, menu);

		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case ACTIVITY_SELECT_IMAGE:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cur = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				if (!cur.moveToFirst())
					break;

				String filePath = cur.getString(0);
				cur.close();

				// copy image and save //

				DateTime now = new DateTime();
				AdvImage image = new AdvImage(0, today.getId(), null, now, "",
						"");

				File source = new File(filePath);
				File destination = NezboUtils.generateFilePath(this, image);

				try {
					// copy
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(destination);

					NezboUtils.copyFile(in, out);

					// save
					image.setFilename(destination.getAbsolutePath());
					db.createImage(image);

				} catch (IOException e) {
					System.err.println("ERROR: While moving imported image!");
				}

				// update this context
				image.recycle();
				save();
				init();
			}
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.dayMenuImportImage:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			break;
		case R.id.dayMenuShare:
			save();

			Travel travel = db.getTravel(today.getTravelId());

			Intent j = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
			j.setType("text/plain");
			j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			int dayNo = DateTime.getDaysBetween(travel.getStart(),
					today.getDateTime()) + 1;
			int noDays = DateTime.getDaysBetween(travel.getStart(),
					travel.getEnd()) + 1;

			j.putExtra(Intent.EXTRA_SUBJECT, travel.getTitle()
					+ " - Travel Journey Day: "
					+ today.getDateTime().asStringDay() + " (Day " + dayNo
					+ " of " + noDays + ")");
			j.putExtra(Intent.EXTRA_TEXT, createShareText());

			// URI's for all the images of that day
			ArrayList<Uri> uris = new ArrayList<Uri>();
			for (AdvImage image : images) {
				uris.add(Uri.fromFile(new File(image.getFilename())));
			}

			j.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			startActivity(Intent.createChooser(j, "Share via:"));
			break;
		}

		return true;
	}

	private String createShareText() {
		StringBuilder builder = new StringBuilder();
		builder.append("Journal Entry:");
		builder.append(today.getText());
		builder.append("\n");
		builder.append("\nHere's information about each picture in order:\n\n");
		for (AdvImage image : images) {
			String desc = image.getDescription();
			String title = image.getTitle();

			builder.append(image.getTitle().equals("") ? "No Image Title"
					: title);
			builder.append(" (" + image.getCaptureTime().asStringTime() + ")\n");
			builder.append(desc.equals("") ? "No Description" : desc);
			builder.append("\n\n");
		}

		return builder.toString();
	}
}

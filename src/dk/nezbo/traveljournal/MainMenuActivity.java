package dk.nezbo.traveljournal;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainMenuActivity extends Activity implements OnClickListener {

	private DatabaseHelper db;
	private Travel currentTravel = null;

	private ImageButton bToday, bCamera, bCurrent, bAllTravels;
	private TextView tvCurrent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	private void init() {
		if(db == null) db = new DatabaseHelper(this);

		bToday = (ImageButton) findViewById(R.id.ib01);
		bCamera = (ImageButton) findViewById(R.id.ib02);
		bCurrent = (ImageButton) findViewById(R.id.ib03);
		bAllTravels = (ImageButton) findViewById(R.id.ib04);
		tvCurrent = (TextView) findViewById(R.id.tvCurrent);

		// See if we have a current travel
		currentTravel = db.getCurrentTravel();
		
		// set varying content
		bToday.setEnabled(currentTravel != null);
		bCamera.setEnabled(currentTravel != null);
		tvCurrent.setText(currentTravel == null ? R.string.menu_option_create_new : R.string.menu_option_three);

		// set listeners
		bToday.setOnClickListener(this);
		bCamera.setOnClickListener(this);
		bCurrent.setOnClickListener(this);
		bAllTravels.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: // image captured
			if (resultCode == RESULT_OK) {
				final File file = getTempFile(this);

				DateTime time = new DateTime();
				TravelDay today = db.findOrCreateTravelDay(currentTravel, time);
				double[] location = NezboUtils.getLastLocation(this);
				
				AdvImage image = new AdvImage(0, today.getId(), null, time, "",
						"", location);
				
				File file2 = NezboUtils.generateFilePath(this, image);

				boolean success = file.renameTo(file2);

				if (success) {
					image.setFilename(file2.getAbsolutePath());
					db.createImage(image);

					if (image.getId() != -1) {
						NezboUtils.goToImage(this, image.getId());
					}
				} else {
					System.err.println("Image renaming failed");
				}
				image.recycle();
			}
			System.out.println("Camera save completed");
			break;
		}
	}

	private File getTempFile(Context context) {
		// it will return /sdcard/image.png
		final File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, "image.png");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib01: // bToday
			TravelDay day = db.findOrCreateTravelDay(currentTravel,
					new DateTime());
			NezboUtils.goToTravelDay(this, day.getId());
			break;
		case R.id.ib02: // bCamera
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(getTempFile(this)));
			startActivityForResult(intent, 1);
			break;
		case R.id.ib03: // bCurrent
			if(currentTravel != null){
				NezboUtils.goToTravel(this, currentTravel.getId());
			}else{
				DateTime nextWeek = new DateTime();
				nextWeek.addDays(7);
				currentTravel = new Travel(0,"Untitled Travel","",new DateTime(),nextWeek);
				db.createTravel(currentTravel);
				
				NezboUtils.goToTravel(this, currentTravel.getId());
			}

			break;
		case R.id.ib04: // bAllTravels
			NezboUtils.goToTravels(this);
			break;
		}
	}
}
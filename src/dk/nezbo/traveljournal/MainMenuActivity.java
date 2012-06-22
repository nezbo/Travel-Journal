package dk.nezbo.traveljournal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainMenuActivity extends Activity implements OnClickListener {

	private DatabaseHelper db;
	private Travel testTravel;

	private ImageButton bToday, bCamera, bCurrent, bAllTravels;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		db = new DatabaseHelper(this);

		// TEST
		DateTime nextWeek = new DateTime();
		nextWeek.addDays(7);
		testTravel = db.getCurrentTravel();
		if (testTravel == null) {
			testTravel = new Travel(0, "", new DateTime(), nextWeek);
			db.saveTravel(testTravel);
		}

		bToday = (ImageButton) findViewById(R.id.ib01);
		bCamera = (ImageButton) findViewById(R.id.ib02);
		bCurrent = (ImageButton) findViewById(R.id.ib03);
		bAllTravels = (ImageButton) findViewById(R.id.ib04);

		bToday.setOnClickListener(this);
		bCamera.setOnClickListener(this);
		bCurrent.setOnClickListener(this);
		bAllTravels.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 1:
			if (resultCode == RESULT_OK) {
				final File file = getTempFile(this);
				
				DateTime time = new DateTime();
				TravelDay today = db.findOrCreateTravelDay(testTravel, time);
				AdvImage image = new AdvImage(0,today.getId(), null, time, "", "");
				//ImageLocationFinder.initiate(this, image, 10); // get gps location in 10 seconds
				File file2 = NezboUtils.generateFilePath(this, image);
				
				boolean success = file.renameTo(file2);

				if(success){
					image.setFilename(file2.getAbsolutePath());
					int createdId = db.createImage(image);
					
					if(createdId != -1){
						NezboUtils.goToImage(this,createdId);
					}
				}else{
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
			TravelDay day = db
					.findOrCreateTravelDay(testTravel, new DateTime());
			NezboUtils.goToTravelDay(this,day.getId());
			break;
		case R.id.ib02: // bCamera
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(getTempFile(this)));
			startActivityForResult(intent, 1);
			break;
		case R.id.ib03: // bCurrent

			break;
		case R.id.ib04: // bAllTravels

			break;
		}
	}
}
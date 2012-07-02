package dk.nezbo.traveljournal;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageActivity extends Activity implements OnClickListener {

	private DatabaseHelper db;
	private AdvImage thisImage;

	private ImageView imageView;
	private TextView title;
	private TextView time;
	private EditText description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		save();
		db.close();
		thisImage.recycle();
	}

	private void save() {
		thisImage.setDescription(description.getText().toString());
		if (!title.getText().toString().equals("Photo Title"))
			thisImage.setTitle(title.getText().toString());

		db.saveImage(thisImage);
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
		db = new DatabaseHelper(this);
	}

	private void init() {
		db = new DatabaseHelper(this);

		// bundle
		int imageId = getIntent().getExtras().getInt("id");
		thisImage = db.getImage(imageId);

		// objects
		imageView = (ImageView) findViewById(R.id.ivImageBig);
		title = (TextView) findViewById(R.id.tvImageTitle);
		time = (TextView) findViewById(R.id.tvImageTime);
		description = (EditText) findViewById(R.id.etImageText);

		// content
		imageView.setImageBitmap(thisImage.getBitmap(this));
		String imageTitle = thisImage.getTitle();
		if (!imageTitle.equals(""))
			title.setText(imageTitle);
		description.setText(thisImage.getDescription());
		time.setText(thisImage.getCaptureTime().asStringLong());

		title.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean result = super.onOptionsItemSelected(item);
		File imageFile = new File(thisImage.getFilename());
		
		switch (item.getItemId()) {
		case R.id.imageMenuDelete:
			
			boolean deleted = imageFile.delete();
			if(deleted){
				db.deleteImage(thisImage);
				finish();
			}else{
				System.out.println("WARNING: Was unable to delete image file");
			}
			break;
		case R.id.imageMenuOpen:
			Intent i = new Intent();
			i.setAction(android.content.Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(imageFile), "image/png");
			startActivity(i);
			break;
		case R.id.imageMenuShare:
			save();
			
			Intent j = new Intent(android.content.Intent.ACTION_SEND);
			j.setType("text/plain");
			j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			
			j.putExtra(Intent.EXTRA_SUBJECT, "Travel Journey Picture: "+thisImage.getTitle());
			j.putExtra(Intent.EXTRA_TEXT, thisImage.getDescription());
			j.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
			startActivity(Intent.createChooser(j, "Share via:"));
			break;
		}

		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onCreateOptionsMenu(menu);

		MenuInflater blow = this.getMenuInflater();
		blow.inflate(R.menu.image_menu, menu);

		return result;
	}

	public void onClick(View v) {
		if (v == title) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Photo Title");
			// alert.setMessage("Message");

			final EditText input = new EditText(this);
			input.setText(thisImage.getTitle());
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							thisImage.setTitle(input.getText().toString());
							title.setText(thisImage.getTitle());
						}
					});

			alert.show();
		}
	}

}

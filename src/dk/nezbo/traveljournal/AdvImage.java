package dk.nezbo.traveljournal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

public class AdvImage {

	private static final int IMAGE_SIZE = 1024;
	private static final int THUMBNAIL_SIZE = 256;
	
	private int id;
	private int dayId;
	private String file;
	private String title;
	private String description;
	private DateTime captureTime;
	private double[] loc;
	
	private Bitmap bitmap;
	private Bitmap thumbnail;

	public AdvImage(int id, int dayId, String file, DateTime captureTime,
			String title, String desc, double[] location) {
		this.id = id;
		this.dayId = dayId;
		this.file = file;
		this.captureTime = captureTime;
		this.title = title;
		this.description = desc;
		this.loc = location;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new AdvImage(id, dayId, file, captureTime, title, description, new double[]{loc[0],loc[1]});
	}

	public int getId() {
		return id;
	}

	public int getTravelDayId() {
		return dayId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DateTime getCaptureTime() {
		return captureTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return description;
	}

	public double[] getLocation() {
		return loc;
	}

	public void setLocation(double[] loc) {
		this.loc = loc;
	}
	
	private Bitmap loadScaledBitmap(final Context c, int maxWidth, int maxHeight, boolean asyncRotate){
		final int rotate = necessaryRotation(c, file);
		// if(rotate != 0) rotateImageFile(c, rotate);

		// Get scaled version
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, options);
		options.inSampleSize = calcInSampleSize(options, maxWidth, maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(file, options);
		
		System.out.println(getFilename());

		// rotate?
		bitmap = rotateImage(c, bitmap, rotate);

		if(bitmap != null) System.out.println("Bitmap loaded from file: size=" + bitmap.getWidth()
				+ "," + bitmap.getHeight());

		System.gc();

		// if rotation is needed, do it in worker thread for next time
		if (rotate != 0 && bitmap != null && asyncRotate) {
			Thread t = new Thread(new Runnable() {

				public void run() {
					// load entire image
					try {
						File imageFile = new File(getFilename());
						Bitmap huge = Media.getBitmap(c.getContentResolver(),
								Uri.fromFile(imageFile));

						huge = rotateImage(c, huge, rotate);

						// save bitmap properly
						FileOutputStream out = new FileOutputStream(imageFile);
						huge.compress(Bitmap.CompressFormat.PNG, 100, out);

						out.flush();
						out.close();
						huge.recycle();
						huge = null;
						out = null;
						System.gc();

						System.out
								.println("Full Image successfully rotated and saved");

					} catch (IOException e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						System.err
								.println("ERROR: Full Image rotation failed because of memory");
					}
				}

			});
			t.start();
		}
		return bitmap;
	}
	
	public Bitmap getThumbnail(Context c){
		if(thumbnail != null){
			return thumbnail;
		}
		
		thumbnail = loadScaledBitmap(c,AdvImage.THUMBNAIL_SIZE, AdvImage.THUMBNAIL_SIZE, false);
		
		return thumbnail;
	}

	public Bitmap getBitmap(Context c) {
		if (bitmap != null)
			return bitmap;

		bitmap = loadScaledBitmap(c,AdvImage.IMAGE_SIZE,AdvImage.IMAGE_SIZE, true);

		return bitmap;
	}

	private Bitmap rotateImage(Context c, Bitmap bitmap, int rotate) {
		if (rotate != 0) {
			// rotate
			Matrix m = new Matrix();
			m.postRotate(rotate);
			Bitmap rotImage = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), m, true);
			bitmap.recycle();

			System.out.println("Image (id=" + getId()
					+ ") rotated successfully");

			System.gc();

			return rotImage;
		}
		return bitmap;
	}

	private int necessaryRotation(Context c, String imageFile) {
		int rotate = 0;
		ExifInterface exif;
		try {
			exif = new ExifInterface(imageFile);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rotate;
	}

	private int calcInSampleSize(BitmapFactory.Options options, int reqWidth,
			int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		while (height > reqHeight || width > reqWidth) {
			height /= 2;
			width /= 2;
			inSampleSize *= 2;
		}
		//System.out.println("Size going to be: w="+width+", h="+height);
		return inSampleSize;
	}

	public String getFilename() {
		return file;
	}

	public void setFilename(String filename) {
		file = filename;
	}

	public String toString() {
		return "[AdvImage: id=" + id + " dayId=" + dayId + " time="
				+ captureTime.asStringDay() + " filename=" + file + " title="
				+ title + " desc=" + description + " loc=" + loc[0] + "," +loc[1] + "]";
	}

	public void recycle() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
			
			System.out.println("Image Bitmap recycled.");
		}
	}
}

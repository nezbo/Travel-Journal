package dk.nezbo.traveljournal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NezboUtils {

	public static void goToTravelDay(Context c, int id) {
		Intent next = new Intent("dk.nezbo.traveljournal.TRAVELDAY");
		Bundle travelday = new Bundle();
		travelday.putInt("id", id);
		next.putExtras(travelday);
		c.startActivity(next);
	}
	
	public static void goToImage(Context c, int id){
		Intent next = new Intent("dk.nezbo.traveljournal.IMAGE");
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		next.putExtras(bundle);
		c.startActivity(next);
	}
	
	public static void goToTravel(Context c, int id) {
		Intent next = new Intent("dk.nezbo.traveljournal.TRAVEL");
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		next.putExtras(bundle);
		c.startActivity(next);
	}
	
	public static void goToTravels(Context c){
		Intent next = new Intent("dk.nezbo.traveljournal.TRAVELS");
		c.startActivity(next);
	}
	
	public static void goToMap(Context c, double[] target){
		Intent next = new Intent("dk.nezbo.traveljournal.MAP");
		Bundle bundle = new Bundle();
		bundle.putDouble("long", target[0]);
		bundle.putDouble("lat", target[1]);
		next.putExtras(bundle);
		c.startActivity(next);
	}
	
	public static void goToGoogleMaps(Context c, double[] location){
		String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location[0], location[1]);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		c.startActivity(intent);
	}
	
	public static void copyFile(InputStream in, OutputStream out) throws IOException{
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer,0,read);
		}
	}
	
	public static File generateFilePath(Context c, AdvImage image){
		File path = new File(Environment.getExternalStorageDirectory(),
				c.getPackageName());
		return new File(path,image.getCaptureTime().toString().replace(':', '-') + ".png");
	}
	
	public static DateTime getImageCaptureTime(String filepath){
		try {
			ExifInterface exif = new ExifInterface(filepath);
			String time = exif.getAttribute(ExifInterface.TAG_DATETIME);
			return DateTime.fromExifFormat(time);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static double[] getImageLocation(String filepath){
		ExifInterface exif;
		try {
			exif = new ExifInterface(filepath);
			String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			
			double[] result = new double[2];
			
			// in case of no data
			if(LATITUDE == null || LATITUDE_REF == null || LONGITUDE == null || LONGITUDE_REF == null) return new double[]{0.0,0.0};
			
			if(LATITUDE_REF.equals("N")){
				result[1] = convertToDegree(LATITUDE);
			}else{
				result[1] = 0 - convertToDegree(LATITUDE);
			}
			
			if(LONGITUDE_REF.equals("E")){
				result[0] = convertToDegree(LONGITUDE);
			}else{
				result[0] = 0 - convertToDegree(LONGITUDE);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static double convertToDegree(String stringDMS){
		Double result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = Double.valueOf(stringD[0]);
		Double D1 = Double.valueOf(stringD[1]);
		Double FloatD = D0/D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = Double.valueOf(stringM[0]);
		Double M1 = Double.valueOf(stringM[1]);
		Double FloatM = M0/M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = Double.valueOf(stringS[0]);
		Double S1 = Double.valueOf(stringS[1]);
		Double FloatS = S0/S1;

		result = Double.valueOf(FloatD + (FloatM/60) + (FloatS/3600));

		return result.doubleValue();
	}
	
	public static double[] getLastLocation(Context c){
		LocationManager lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);
		
		Location l = null;
		for(int i = providers.size()-1; i >= 0; i--){
			l = lm.getLastKnownLocation(providers.get(i));
			if(l != null) break; // existing provider found
		}
		
		double[] gps = new double[2];
		if(l != null){
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}
	
	public static void showKeyboard(final EditText editText, final Context c){
	    editText.setOnFocusChangeListener(new OnFocusChangeListener() {
	        public void onFocusChange(View v, boolean hasFocus) {
	            editText.post(new Runnable() {
	                public void run() {
	                    InputMethodManager inputMethodManager= (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
	                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	                }
	            });
	        }
	    });
	    editText.requestFocus();
	}
}

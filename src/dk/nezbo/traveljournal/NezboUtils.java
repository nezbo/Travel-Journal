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
	
	public static DateTime getCaptureTime(String filepath){
		try {
			ExifInterface exif = new ExifInterface(filepath);
			String time = exif.getAttribute(ExifInterface.TAG_DATETIME);
			return DateTime.fromExifFormat(time);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
}

package dk.nezbo.traveljournal;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ImageLocationFinder implements LocationListener{
	
	private static final long TIMEFRAME = 1000*60*5; // 5 minutes
	private static final int TWO_MINUTES = 1000 * 60 * 2; // 2 minutes
	
	private final AdvImage image;
	private final LocationManager manager;
	private final Context context;
	private Location currentBest = null;

	private long end;
	
	public static void initiate(Context c, AdvImage image, int seconds){
		new ImageLocationFinder(c,image,seconds);
	}

	private ImageLocationFinder(Context c, AdvImage image, final int seconds){
		this.image = image;
		this.context = c;
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		Thread t = new Thread(){
			public Handler mHandler;
			public void run(){
				Looper.prepare();
				mHandler = new Handler(){
					public void handleMessage(Message msg){
						//derp
					}
				};
				Looper.loop();
				
				init(seconds);
			}
		};
		t.start();
	}

	private void init(int seconds) {
		// try last known location
		Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if(loc != null){
			long now = System.currentTimeMillis();
			long time = loc.getTime();
			
			if((now - time) < TIMEFRAME){
				image.setLocation(loc);
			}
		}
		
		// setting up timer
		end = System.currentTimeMillis() + 1000*seconds;
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		System.out.println("GPS init complete");
	}

	public void onLocationChanged(Location location) {
		System.out.println("GPS location recieved");
		if(currentBest == null) currentBest = location;
		
		// check for better
		if(this.isBetterLocation(location, currentBest)) currentBest = location;
		
		// end if necessary
		if(System.currentTimeMillis() > end){
			image.setLocation(currentBest);
			manager.removeUpdates(this);
			System.out.println("GPS position determined");
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}

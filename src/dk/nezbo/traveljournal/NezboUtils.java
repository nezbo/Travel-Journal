package dk.nezbo.traveljournal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
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
}

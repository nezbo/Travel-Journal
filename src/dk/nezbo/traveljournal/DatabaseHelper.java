package dk.nezbo.traveljournal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final int VERSION = 12;
	private static final String dbName = "traveljournal";
	
	public DatabaseHelper(Context context) {
		super(context,dbName, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Travel (id INTEGER PRIMARY KEY AUTOINCREMENT, desc TEXT, start DATETIME, end DATETIME)");
		db.execSQL("CREATE TABLE TravelDay (id INTEGER PRIMARY KEY AUTOINCREMENT, travelId INTEGER, day DATETIME, text TEXT)");
		db.execSQL("CREATE TABLE Image (id INTEGER PRIMARY KEY AUTOINCREMENT, dayId INTEGER, file TEXT, title TEXT, desc TEXT, time DATETIME)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Travel");
		db.execSQL("DROP TABLE IF EXISTS TravelDay");
		db.execSQL("DROP TABLE IF EXISTS Image");
		
		onCreate(db);
	}
	
	public Travel getTravel(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT desc, start, end FROM Travel WHERE id=?", new String[]{String.valueOf(id)});
		if(!cur.moveToFirst()) return null;
		
		String text = cur.getString(0);
		DateTime start = new DateTime(cur.getString(1));
		DateTime end = new DateTime(cur.getString(2));
		
		Travel travel = new Travel(id,text,start,end);
		
		/*
		// put ids for days
		ArrayList<Integer> days = travel.getDays();
		cur = db.rawQuery("SELECT id FROM TravelDay WHERE travelId=?", new String[]{String.valueOf(travel.getId())});
		boolean lastAnswer = cur.moveToFirst();
		while(lastAnswer){
			days.add(cur.getInt(0));
			cur.moveToNext();
		}*/
		
		cur.close();
		db.close();
		return travel;
	}
	
	public ArrayList<Travel> getTravels(){
		ArrayList<Travel> travels = new ArrayList<Travel>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		// get all travels
		Cursor cur = db.rawQuery("SELECT id FROM Travel", new String[]{});
		boolean lastAnswer = cur.moveToFirst();
		int[] ids = new int[cur.getCount()];
		int index = 0;
		while(lastAnswer){
			ids[index] = cur.getInt(0);
			
			lastAnswer = cur.moveToNext();
			index++;
		}
		
		// get them all
		for(int i : ids){
			travels.add(getTravel(i));
		}
		
		cur.close();
		db.close();
		return travels;
	}
	
	public void saveTravel(Travel existing){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("desc", existing.getDescription());
		cv.put("start", existing.getStart().toString());
		cv.put("end", existing.getEnd().toString());
		db.update("Travel", cv, "id=?", new String[]{String.valueOf(existing.getId())});
		System.out.println("Travel saved: "+existing.toString());
		db.close();
	}
	
	public void createTravel(Travel newTravel){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("desc", newTravel.getDescription());
		cv.put("start", newTravel.getStart().toString());
		cv.put("end", newTravel.getEnd().toString());
		db.insert("Travel", null, cv);
		db.close();
		
		// placing id
		db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id FROM Travel WHERE desc=? AND start=? AND end=?", new String[]{newTravel.getDescription(),newTravel.getStart().toString(), newTravel.getEnd().toString()});
		cur.moveToFirst();
		newTravel.setId(cur.getInt(0));
		
		db.close();
	}
	
	public Travel getCurrentTravel(){
		DateTime now = new DateTime();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id FROM Travel WHERE start <= ? AND end >= ?", new String[]{now.toString(), now.toString()});
		System.out.println(""+cur.getCount()+" matches for current Travel");
		
		if(!cur.moveToFirst()) return null; // no current travel
		
		int id = cur.getInt(0);
		cur.close();
		db.close();
		return getTravel(id); 
	}

	public TravelDay findOrCreateTravelDay(Travel travel, DateTime date) {
		// check for existence
		TravelDay match = findTravelDay(travel,date);
		if(match != null) return match;
		
		// didn't exist yet, create and return
		
		SQLiteDatabase db = this.getWritableDatabase();
		match = new TravelDay(-1, travel.getId(), date, "");
		ContentValues cv = new ContentValues();
		cv.put("travelId", match.getTravelId());
		cv.put("day", match.getDateTime().toString());
		cv.put("text", match.getText());
		db.insert("TravelDay", null, cv);
		System.out.println("TravelDay created: "+match.toString());
		
		// call same method because it can now be found
		db.close();
		return findTravelDay(travel,date);
	}
	
	public TravelDay findTravelDay(Travel travel, DateTime date){
		DateTime beginning = date.getBeginning();
		DateTime end = date.getNextDay();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id, travelId, day, text FROM TravelDay WHERE travelId=? AND day >= ? AND day < ?", new String[]{String.valueOf(travel.getId()),beginning.toString(),end.toString()});
		System.out.println(""+cur.getCount()+" results for TravelDay");
		TravelDay result = null;
		boolean lastAnswer = cur.moveToFirst();
		if(lastAnswer){
			result = new TravelDay(cur.getInt(0),cur.getInt(1),new DateTime(cur.getString(2)),cur.getString(3));
			System.out.println("TravelDay found: "+result.toString());
		}
		cur.close();
		db.close();
		return result;
	}

	public TravelDay getTravelDay(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id, travelId, day, text FROM TravelDay WHERE id=?", new String[]{String.valueOf(id)});
		if(!cur.moveToFirst()){
			System.out.println("TravelDay not found by id");
			return null;
		}
		TravelDay result = new TravelDay(cur.getInt(0),cur.getInt(1),new DateTime(cur.getString(2)),cur.getString(3));
		System.out.println("TravelDay found by id: "+result.toString());
		cur.close();
		db.close();
		return result;
	}

	public void saveTravelDay(TravelDay today) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("text", today.getText());
		db.update("TravelDay", cv, "id=?", new String[]{String.valueOf(today.getId())});
		System.out.println("TravelDay saved: "+today.toString());
		db.close();
	}

	public int createImage(AdvImage image) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("dayId", image.getTravelDayId());
		cv.put("file", image.getFilename());
		cv.put("title", image.getTitle());
		cv.put("desc", image.getDescription());
		cv.put("time", image.getCaptureTime().toString());
		db.insert("Image", null, cv);
		System.out.println("New Image saved: "+image.toString());
		db.close();
		
		return getImageId(image);
	}
	
	public void saveImage(AdvImage image) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("dayId", image.getTravelDayId());
		cv.put("file", image.getFilename());
		cv.put("title", image.getTitle());
		cv.put("desc", image.getDescription());
		cv.put("time", image.getCaptureTime().toString());
		int rows = db.update("Image", cv, "id=?", new String[]{String.valueOf(image.getId())});
		System.out.println("Image saved: "+image.toString()+" ("+rows+" affected)");
		db.close();
	}
	
	public void deleteImage(AdvImage image){
		SQLiteDatabase db = this.getWritableDatabase();
		int rows = db.delete("Image", "id=?", new String[]{String.valueOf(image.getId())});
		
		db.close();
		System.out.println("Image deleted: "+image.toString()+ "("+rows+" rows affected)");
	}
	
	private int getImageId(AdvImage image){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id FROM Image WHERE dayId=? AND file=? AND title=? AND desc=? AND time=?", new String[]{String.valueOf(image.getTravelDayId()),image.getFilename(),image.getTitle(),image.getDescription(),image.getCaptureTime().toString()});
		if(!cur.moveToFirst()){
			System.out.println("Image not found by content");
			cur.close();
			db.close();
			return -1;
		}
		if(cur.getCount() > 1){
			System.out.println("WARNING: "+cur.getCount()+" rows matching Image content");
		}
		int id = cur.getInt(0);
		cur.close();
		db.close();
		return id;
	}
	
	public AdvImage getImage(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id, dayId, file, title, desc, time FROM Image WHERE id=?", new String[]{String.valueOf(id)});
		if(!cur.moveToFirst()){
			System.out.println("Image not found by id");
			cur.close();
			db.close();
			return null;
		}
		AdvImage result = new AdvImage(cur.getInt(0),cur.getInt(1),cur.getString(2),new DateTime(cur.getString(5)),cur.getString(3),cur.getString(4));
		System.out.println("Image found by id: "+result.toString());
		cur.close();
		db.close();
		return result;
	}

	public ArrayList<AdvImage> getImages(int dayId) {
		ArrayList<AdvImage> images = new ArrayList<AdvImage>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		System.out.println("db created");
		Cursor cur = db.rawQuery("SELECT id FROM Image WHERE dayId=?", new String[]{String.valueOf(dayId)});
		System.out.println(""+cur.getCount()+" images found for dayId="+dayId);
		
		// gather ids
		int ids[] = new int[cur.getCount()];
		int index = 0;
		
		if(!cur.moveToFirst()) return images;
		boolean answer = true;
		while(answer){
			ids[index] = cur.getInt(0);
			
			answer = cur.moveToNext();
			index++;
		}
		cur.close();
		db.close();
		
		// load 
		for(int i : ids){
			images.add(this.getImage(i));
		}
		
		return images;
	}
	
	public boolean hasImages(int dayId){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id FROM Image WHERE dayId=?", new String[]{String.valueOf(dayId)});
		
		boolean result = cur.moveToFirst();
		cur.close();
		db.close();
		
		return result;
	}
	
	public TravelDay[] getTravelDays(Travel t){
		ArrayList<TravelDay> days = new ArrayList<TravelDay>();
		
		DateTime current = (DateTime) t.getStart().clone();
		while(current.before(t.getEnd()) || current.sameDay(t.getEnd())){
			days.add(this.findTravelDay(t, current));
			
			current.addDays(1);
		}
		
		return days.toArray(new TravelDay[days.size()]);
	}
}

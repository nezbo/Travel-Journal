package dk.nezbo.traveljournal;

import java.util.ArrayList;

public class Travel{
	
	private int id;
	private DateTime start;
	private DateTime end;
	private ArrayList<Integer> days;
	private String description;
	
	public Travel(int id, String description, DateTime start, DateTime end){
		this.id = id;
		this.start = start;
		this.end = end;
		this.description = description;
		days = new ArrayList<Integer>();
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setStart(DateTime newStart){
		start = newStart;
	}
	
	public DateTime getStart(){
		return start;
	}
	
	public void setEnd(DateTime newEnd){
		end = newEnd;
	}
	
	public DateTime getEnd(){
		return end;
	}
	
	public void setDescription(String desc){
		description = desc;
	}
	
	public String getDescription(){
		return description;
	}
	
	public ArrayList<Integer> getDays(){
		return days;
	}
	
	public TravelDay getDate(DateTime date, DatabaseHelper db){
		// return null if outside travel limits
		if(!date.between(start, end) || end.sameDay(date)) return null;
		
		// get matching day
		return db.findOrCreateTravelDay(this, date);
	}
	
	public String toString(){
		return "[Travel: id="+id+" start="+start.asStringDay()+" end="+end.asStringDay()+" desc="+description+"]";
	}
	/*
	public ArrayList<AdvImage> deleteDay(DateTime date){
		TravelDay toDelete = null;
		for(TravelDay day : days){

			if(date.sameDay(day.getDateTime())){
				toDelete = day;
				break;
			}
		}
		
		if(toDelete != null){
			days.remove(toDelete);
			return toDelete.getImages();
		}
		return null;
	}
	
	public ArrayList<AdvImage> deleteDay(TravelDay day){
		return deleteDay(day.getDateTime());
	}
	
	public ArrayList<AdvImage> getAllImages(){
		ArrayList<AdvImage> all = new ArrayList<AdvImage>();
		
		for(TravelDay day : days){
			all.addAll(day.getImages());
		}
		
		return all;
	}*/
	
	public boolean isCurrent(){
		return new DateTime().between(start, end);
	}


}
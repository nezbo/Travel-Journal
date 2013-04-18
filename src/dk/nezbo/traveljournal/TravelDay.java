package dk.nezbo.traveljournal;

public class TravelDay {

	private int id;
	private int travelId;
	private final DateTime day;
	private String text;
	//TODO: Geo location
	
	public TravelDay(int id, int travelId, DateTime day, String text){
		this.id = id;
		this.travelId = travelId;
		this.day = day;
		this.text = text;
	}
	
	public int getId(){
		return id;
	}
	
	public int getTravelId(){
		return travelId;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public DateTime getDateTime(){
		return day;
	}
	
	public String toString(){
		return "[TravelDay: id="+(id > 0 ? ""+id : "?")+" tId="+travelId+" day="+day.asStringDay()+" text="+text+"]";
	}
}

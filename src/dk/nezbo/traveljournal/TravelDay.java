package dk.nezbo.traveljournal;

public class TravelDay {

	private int id;
	private int travelId;
	private final DateTime day;
	private String text;
	private double[] loc;
	
	public TravelDay(int id, int travelId, DateTime day, String text, double[] location){
		this.id = id;
		this.travelId = travelId;
		this.day = day;
		this.text = text;
		this.loc = location;
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
	
	public void setLocation(double[] location){
		this.loc = location;
	}
	
	public double[] getLocation(){
		return loc;
	}
	
	public String toString(){
		return "[TravelDay: id="+(id > 0 ? ""+id : "?")+" tId="+travelId+" day="+day.asStringDay()+" text="+text + " loc=" + loc[0] + "," +loc[1] +"]";
	}
}

package dk.nezbo.traveljournal;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime implements Comparable<DateTime> {

	private static final DateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final DateFormat month = new SimpleDateFormat("MMMM");
	private static final DateFormat time = new SimpleDateFormat("HH:mm");
	private static final String[] daynames = new DateFormatSymbols().getShortWeekdays();

	private Calendar cal = null;

	public DateTime(String datetime) {
		cal = Calendar.getInstance();
		try {
			cal.setTime(formatter.parse(datetime));
		} catch (ParseException e) {
			System.err.println("ERROR: DateTime couldn't be created from faulty string: "+datetime);
		}
	}

	public DateTime(int year, int month, int date, int hour, int minute) {
		cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(year, month - 1, date, hour, minute);
	}

	public DateTime() {
		cal = Calendar.getInstance();
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	public void setYear(int year) {
		cal.set(Calendar.YEAR, year);
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	public void setMonth(int month) {
		cal.set(Calendar.MONTH, month - 1);
	}

	public String getMonthText() {
		String result = month.format(cal.getTime());
		
		return result.substring(0,1).toUpperCase() + result.substring(1, result.length());
	}

	public int getDate() {
		return cal.get(Calendar.DATE);
	}

	public String getDateText() {
		String number = String.valueOf(getDate());
		switch (number.charAt(number.length() - 1)) {
		case '1':
			number += "st";
			break;
		case '2':
			number += "nd";
			break;
		case '3':
			number += "rd";
			break;
		default:
			number += "th";
			break;
		}

		return number;
	}

	public void setDate(int date) {
		cal.set(Calendar.DATE, date);
	}

	public int getHour() {
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public void setHour(int hour) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
	}

	public int getMinute() {
		return cal.get(Calendar.MINUTE);
	}

	public void setMinute(int minute) {
		cal.set(Calendar.MINUTE, minute);
	}
	
	public void addYears(int number){
		cal.add(Calendar.YEAR, number);
	}
	
	public void addMonths(int number){
		cal.add(Calendar.MONTH, number);
	}

	public void addDays(int number) {
		cal.add(Calendar.DATE, number);
	}

	public void addHours(int number) {
		cal.add(Calendar.HOUR_OF_DAY, number);
	}

	public void addMinutes(int number) {
		cal.add(Calendar.MINUTE, number);
	}

	public boolean before(DateTime that) {
		return this.compareTo(that) == -1;
	}

	public boolean after(DateTime that) {
		return this.compareTo(that) == 1;
	}
	
	public boolean between(DateTime first, DateTime second) {
		if (first.compareTo(second) == 1) {
			DateTime temp = first;
			first = second;
			second = temp;
		}

		return this.after(first) && this.before(second);
	}

	public boolean dayBetween(DateTime first, DateTime second) {
		if (first.compareTo(second) == 1) {
			DateTime temp = first;
			first = second;
			second = temp;
		}

		return this.after(first) && this.before(second) || this.sameDay(first) || this.sameDay(second);
	}

	public int compareTo(DateTime that) {
		return cal.compareTo(that.cal);
	}

	public boolean sameDay(DateTime that) {
		return this.getYear() == that.getYear()
				&& this.getMonth() == that.getMonth()
				&& this.getDate() == that.getDate();
	}

	public boolean isToday() {
		return this.sameDay(new DateTime());
	}

	public DateTime getBeginning() {
		return new DateTime(getYear(), getMonth(), getDate(), 0, 0);
	}
	
	public void setBeginning(){
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
	}
	
	public DateTime getNextDay(){
		DateTime next = new DateTime(getYear(),getMonth(),getDate(),0,0);
		next.addDays(1);
		return next;
	}
	
	@Override
	public String toString() {
		return formatter.format(cal.getTime());
	}
	
	@Override
	public Object clone(){
		return new DateTime(getYear(),getMonth(),getDate(),getHour(),getMinute());
	}

	public String asStringDay() {
		return getMonthText()+" "+getDateText();
	}
	
	public String asStringMonthYear(){
		return getMonthText() +" ("+getYear()+")";
	}
	
	public String asStringTime(){
		return time.format(cal.getTime());
	}
	
	public String asStringLong(){
		return asStringDay() + " " + asStringTime();
	}
	
	public Calendar extractCalendar(){
		return (Calendar) cal.clone();
	}
	
	public int getDaysOfMonth(){
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public int getDayOfWeek(){
		int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);

		if (firstDay == 1) { // sunday
			firstDay = 6;
		} else {
			firstDay -= 2;
		}
		return firstDay;
	}
	
	public static int getDaysBetween(DateTime first, DateTime second){
		first = (DateTime) first.clone();

		if(first.after(second)){
			int counter = 0;
			while(!first.sameDay(second)){
				first.addDays(-1);
				counter--;
			}
			return counter;
		}else{
			int counter = 0;
			while(!first.sameDay(second)){
				first.addDays(1);
				counter++;
			}
			return counter;
		}
	}
	
	// static methods

	public static String getDayName(int index) {
		if(index == 6) return daynames[1];
		return daynames[index+2];
	}
}

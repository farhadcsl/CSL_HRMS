package org.ofbiz.humanres;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ofbiz.base.util.UtilValidate;

import com.ibm.icu.util.Calendar;

public class ProfileWorker {

	public static final String module = ProfileWorker.class.getName();

	public static String getYearFromDate(Timestamp time) {

		Date date = new Date(time.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return String.valueOf(cal.get(Calendar.YEAR));
	}
	
	public static String getDifferenceBetweenTwoDate(Timestamp startTime,Timestamp stopTime){
		
		Date startDate = new Date(startTime.getTime());
		Date endDate = null;
		
		if(stopTime != null){
			endDate = new Date(stopTime.getTime());
		}else{
			endDate = new Date();
		}
		 int difInDays = (int)((endDate.getTime() - startDate.getTime())/(1000*60*60*24));
		 return ""+difInDays;
	}
	
	public static String FormatDate(Date time) {

		Date date = new Date(time.getTime());
		
		SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String returnDate = oSimpleDateFormat.format(date);
		
		return returnDate;
	
	}
	
	public static String getFormatedDateFromDate(Timestamp time) {

		if(UtilValidate.isEmpty(time)){
			return "";
		}
		Date date = new Date(time.getTime());
		
		SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String returnDate = oSimpleDateFormat.format(date);
		
		return returnDate;
	
	}
	
	
	public static String getFormattedStatus(String status) {

		
		if (status.equals("Active/Open")){
			 return "Active";
		}else if(status.equals("Inactive/Closed")){
			return "Inactive";
		}
		
		
		return status;
		
	}
	
	
	public static boolean validateTwoDate(Date fromDate,Date toDate){
		
		
		Date endDate = null;
		
		if(toDate == null){
			toDate =  new Date();
		}
		 int difInDays = (int)((endDate.getTime() - fromDate.getTime())/(1000*60*60*24));
		 if (difInDays >= 0)return true;
		 
		 return false;
	}
	/**
	 * 
	 * @param birthTime
	 * @return year,month,days
	 */
	public static String getAgeFromTheBirthDate(Timestamp birthTime){
		
		if(UtilValidate.isEmpty(birthTime)){
			return "";
		}
		Date birthDate = new Date(birthTime.getTime());
		Date endDate = new Date();
		
		double totalDay = ((endDate.getTime() - birthDate.getTime())/(365.25 * 86400 * 1000));
		int years = (int) totalDay;
		double mm =  ((totalDay - years) * 12);
		int mon = (int)mm;
		int dd = (int) ((mm-mon)*30);
		
		return years+" years "+mon+" months "+dd+" days";
	}
	
}

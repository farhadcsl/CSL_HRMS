package org.ofbiz.humanres.attendance;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

/**
 * 
 * @author zzz
 *
 */

public class FileUploadThread extends Thread {
	private static Logger logger=Logger.getLogger("FileUploadThread");
	GenericDelegator delegator;
    String fileName;
    InputStream stream;
    BufferedReader br;
   
    public FileUploadThread(GenericDelegator delegator,InputStream stream, String filename) {
        this.delegator=delegator;
        this.stream = stream;
        this.br = new BufferedReader(new InputStreamReader(stream));
        this.fileName = filename;
    }

    public void run() {
        try {
        	      	
        	StringBuilder errorMassage=new StringBuilder();
            errorMassage.append("File Upload Fail for Duplicate Entry ");
            StringBuilder successMassage=new StringBuilder();
            successMassage.append("File Uploaded Successfully ");
            int entityCoun=0;
            String file_name="";
            StringBuilder warningMassage=new StringBuilder();
            warningMassage.append("These Duplicate Entry can not be store ");
            boolean flag=false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));
        	
        	String strLine;
        	
        	// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
							
					if (strLine.length() > 41) {
				
						String pegasusAddress=strLine.substring(0,2);
						String cardNumber = strLine.substring(12, 20);
						try{
							
									cardNumber=String.valueOf(Integer.valueOf(cardNumber));
									
									String dateTime = "20" + strLine.substring(24, 26)
											+ "-" + strLine.substring(26, 28) + "-"
											+ strLine.substring(28, 30) + " "
											+ strLine.substring(32, 34) + ":"
											+ strLine.substring(34, 36);
								
									Date date = new Date();
									try {
										date = sdf.parse(dateTime);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Timestamp currentDate=new Timestamp(date.getTime());				
									GenericValue person=AttendanceEvents.checkEmpExistence(delegator,cardNumber,currentDate);
									if (person != null&&UtilValidate.isNotEmpty(person)) {
										String partyId=person.getString("partyId");
										String userLoginId=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
										
										Map<String, String> paramMap = UtilMisc.toMap( 
								    			"logID", String.valueOf(System.currentTimeMillis()), 
								    			"userID", userLoginId,
								    			"partyId", partyId,
								    			"timestamp",String.valueOf(date.getTime()),
								    			"trigger", "In",
								    			"terminalSN", pegasusAddress,
								    			"sender", pegasusAddress
								    		);
										
										
										if(!AttendanceEvents.saveEmployeeLog(delegator, paramMap)){
											errorMassage.append("Employee: "+String.valueOf(person.get("partyId")));
											errorMassage.append(" Timestamp: "+sdf.format(date));
											flag=true;
											warningMassage.append(",");
											warningMassage.append("Employee: "+String.valueOf(person.get("partyId")));
											warningMassage.append(" Timestamp: "+sdf.format(date));
											
										}else{
											entityCoun++;
										}
										
									}
									
						}catch(Exception ex){
							logger.debug("Line "+strLine+" can not process for illegal text format.");
						}	
					}
				
			}

        } catch (Exception e) {
            System.out.println("In the run method of the thread");
            e.printStackTrace();
        } finally {
            try {
            	stream.close();
            } catch (Exception e) {
            }
        }
    }

}
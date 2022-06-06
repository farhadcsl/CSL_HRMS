/**
 * 
 */
package org.ofbiz.humanres.attendance;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.EntityJoinOperator;

/** 
 * @category Compare ArrayList<Map>
 * @author 
 */
public class ListOfMapComparator implements Comparator<Map <String, String>>{
	
	@Override
	public int compare(Map<String, String> o1, Map<String, String> o2) {
		// TODO Auto-generated method stub
		
		/*compare function return 0 if both of them are same.
		return 1 if first is greater than second
		return -1 if first is less than second*/
		
		String employeeId1 = o1.get("EmployeeId");
		String employeeId2 = o2.get("EmployeeId");
		//checking employeeId null or not
		if(UtilValidate.isEmpty(employeeId1)){
			employeeId1 = o1.get("employeeId");
			if(UtilValidate.isEmpty(employeeId1)){
				employeeId1 = o1.get("PIN");
				if(UtilValidate.isEmpty(employeeId1)){
					employeeId1 = "";
				}
			}
			
		}
		//checking employeeId null or not
		if(UtilValidate.isEmpty(employeeId2)){
			employeeId2 = o2.get("employeeId");
			if(UtilValidate.isEmpty(employeeId2)){
				employeeId2 = o2.get("PIN");
				if(UtilValidate.isEmpty(employeeId2)){
					employeeId2 = "";
				}
			}
		}
		
		String department1 = o1.get("Department");
		String department2 = o2.get("Department");
		
		//checking department null or not
		if(UtilValidate.isEmpty(department1)){
			department1 = o1.get("department");
			if(UtilValidate.isEmpty(department1)){
				department1 = "";
			}
		}
		//checking department null or not
		if(UtilValidate.isEmpty(department2)){
			department2 = o2.get("department");
			if(UtilValidate.isEmpty(department2)){
				department2 = "";
			}
		}
		 

		// First sort department wise...
		int deptCom = (department1.trim()).compareTo(department2.trim());
		if(deptCom!=0){
			return deptCom;
		}
		
		employeeId1 = employeeId1.trim();
		employeeId2 = employeeId2.trim();
		
		// Second sort employeeId wise...
		if(employeeId1.length() == employeeId2.length()){
			return employeeId1.compareTo(employeeId2);
		}
		if(employeeId1.length()>employeeId2.length()){
			return 1;
		}return -1;	
	}

}
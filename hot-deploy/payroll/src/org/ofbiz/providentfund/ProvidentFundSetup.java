package org.ofbiz.providentfund;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.salaryprocess.NewSalaryServices;
import org.ofbiz.salaryprocess.SalaryUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


public class ProvidentFundSetup {
	 public static String createProvidentFund(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        

	        String employeeID = "";
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }

	        if (employeeID.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
	            return "error";
	        }
	        List<GenericValue> partyList = null;
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", employeeID));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	partyId = partyList.get(0).get("partyId").toString();
	            }
	        }
	       

			String companyProFundPercent = request.getParameter("companyProFundPercent");
	        if (companyProFundPercent.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage can not be empty");
	            return "error";
	        }
	        else{	        	
	        	try  
	        	  {  
	        	    double numberValue = Double.parseDouble(companyProFundPercent);  
	        	  }  
	        	  catch(NumberFormatException nfe)  
	        	  {  
	  	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage must be number value");
		            return "error";
	        	  } 
	        }
			String selfProFundPercent = request.getParameter("selfProFundPercent");
	        if (selfProFundPercent.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee Percentage can not be empty");
	            return "error";
	        }
	        else{	        	
	        	try  
	        	  {  
	        	    double numberValue = Double.parseDouble(selfProFundPercent);  
	        	  }  
	        	  catch(NumberFormatException nfe)  
	        	  {  
	  	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage must be number value");
		            return "error";
	        	  } 
	        }
			BigDecimal companyProFundPercentFloat=new BigDecimal(0);
			BigDecimal selfProFundPercentFloat=new BigDecimal(0);
			try{
				companyProFundPercentFloat=new BigDecimal(companyProFundPercent);
				selfProFundPercentFloat=new BigDecimal(selfProFundPercent);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			String comments = request.getParameter("comments");
			
			Map<String, Object> partyPFIn = FastMap.newInstance();
			partyPFIn.put("partyId", partyId);
			partyPFIn.put("employeeId", employeeID);
			partyPFIn.put("companyProFundPercent", companyProFundPercentFloat);
			partyPFIn.put("selfProFundPercent", selfProFundPercentFloat);
			partyPFIn.put("comments", comments);
			partyPFIn.put("isActive", "N");
			partyPFIn.put("dateAdded", currentDatetime);
    	    try {
    			GenericValue partyPFInSetup = delegator
    				.makeValue("PartyProFundRules",
    						UtilMisc.toMap(partyPFIn));
    			partyPFInSetup.create();
    	    } catch (Exception e) { }

 			//--------------Store Data in History Table----------------
			Map<String, Object> partyPFHistoryIn = FastMap.newInstance();
			partyPFHistoryIn.put("partyId", partyId);
			partyPFHistoryIn.put("employeeId", employeeID);
			partyPFHistoryIn.put("companyProFundPercent", companyProFundPercentFloat);
			partyPFHistoryIn.put("selfProFundPercent", selfProFundPercentFloat);
			partyPFHistoryIn.put("comments", comments+"(New Rules Create)");
			partyPFHistoryIn.put("isActive", "N");
			partyPFHistoryIn.put("dateAdded", currentDatetime);
			try {
	 			GenericValue partyPFInHistory = delegator
		 				.makeValue("PartyProFundRulesHis",
		 						UtilMisc.toMap(partyPFHistoryIn));
	 			partyPFInHistory.create();
		 	} catch (Exception e) { }
 			//---------------------------------------------------------------
			
			
	        request.setAttribute("_EVENT_MESSAGE_", "Provident Fund Setup Successfully");
	        return "success";
	    }
	 public static String updateProvidentFund(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

	        String partyId = request.getParameter("partyId");
	        String companyProFundPercent = request.getParameter("companyProFundPercent");
	        if (companyProFundPercent.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage can not be empty");
	            return "error";
	        }
	        else{	        	
	        	try  
	        	  {  
	        	    double numberValue = Double.parseDouble(companyProFundPercent);  
	        	  }  
	        	  catch(NumberFormatException nfe)  
	        	  {  
	  	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage must be number value");
		            return "error";
	        	  } 
	        }
			String selfProFundPercent = request.getParameter("selfProFundPercent");
	        if (selfProFundPercent.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee Percentage can not be empty");
	            return "error";
	        }
	        else{	        	
	        	try  
	        	  {  
	        	    double numberValue = Double.parseDouble(selfProFundPercent);  
	        	  }  
	        	  catch(NumberFormatException nfe)  
	        	  {  
	  	            request.setAttribute("_ERROR_MESSAGE_", "Company percentage must be number value");
		            return "error";
	        	  } 
	        }
			BigDecimal companyProFundPercentFloat=new BigDecimal(0);
			BigDecimal selfProFundPercentFloat=new BigDecimal(0);
			try{
				companyProFundPercentFloat=new BigDecimal(companyProFundPercent);
				selfProFundPercentFloat=new BigDecimal(selfProFundPercent);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	        String dateAddedStr = request.getParameter("dateAdded");
	        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
			String comments = request.getParameter("comments");
			String isActive = request.getParameter("isActive");
			String employeeID = request.getParameter("employeeId");
			
			Map<String, Object> partyPFIn = FastMap.newInstance();
			partyPFIn.put("partyId", partyId);
			partyPFIn.put("employeeId", employeeID);
			partyPFIn.put("companyProFundPercent", companyProFundPercentFloat);
			partyPFIn.put("selfProFundPercent", selfProFundPercentFloat);
			partyPFIn.put("comments", comments);
			partyPFIn.put("isActive", isActive);
			partyPFIn.put("dateAdded", dateAdded);
	 	    try {
	 			GenericValue partyPFInSetup = delegator
	 				.makeValue("PartyProFundRules",
	 						UtilMisc.toMap(partyPFIn));
	 			partyPFInSetup.store();
	 			
	 			//--------------Store Data in History Table----------------
				Map<String, Object> partyPFHistoryIn = FastMap.newInstance();
				partyPFHistoryIn.put("partyId", partyId);
				partyPFHistoryIn.put("employeeId", employeeID);
				partyPFHistoryIn.put("companyProFundPercent", companyProFundPercentFloat);
				partyPFHistoryIn.put("selfProFundPercent", selfProFundPercentFloat);
				partyPFHistoryIn.put("comments", comments+"(Data Update)");
				partyPFHistoryIn.put("isActive", isActive);
				partyPFHistoryIn.put("dateAdded", currentDatetime);
	 			GenericValue partyPFInHistory = delegator
		 				.makeValue("PartyProFundRulesHis",
		 						UtilMisc.toMap(partyPFHistoryIn));
	 			partyPFInHistory.create();
	 			//---------------------------------------------------------------
	 			
	 			
	 	    } catch (Exception e) { }
	        request.setAttribute("_EVENT_MESSAGE_", "Provident Fund Setup Successfully Updated");
	        return "success";
	    }
	 public static String statusChangeProvidentFund(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        String partyId = request.getParameter("partyId");
	      //------------ Find Employee Basic Salary--------------------------//
	        String basicSalaryStr="0";
			BigDecimal basicSalary=new BigDecimal(0);
			BigDecimal companyInvestmentAmount=new BigDecimal(0);
			BigDecimal employeeInvestmentAmount=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBasic = FastList.newInstance();
	 	    exprsForBasic.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
	 	    exprsForBasic.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));        
			List<GenericValue> presentSalaryListForBasic = null;		
			
			try {
				presentSalaryListForBasic = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasic, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : presentSalaryListForBasic){	
					basicSalaryStr=presentSalaryListForBasic.get(0).get("amount").toString();
					basicSalary=new BigDecimal(basicSalaryStr);
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			
			if(basicSalaryStr.equals("0")){
		        request.setAttribute("_EVENT_MESSAGE_", "Basic Salary Not Set For THis Employee");
			}
			else
			{
				String companyProFundPercent = request.getParameter("companyProFundPercent");
				String selfProFundPercent = request.getParameter("selfProFundPercent");
				BigDecimal companyProFundPercentFloat=new BigDecimal(0);
				BigDecimal selfProFundPercentFloat=new BigDecimal(0);
				try{
					companyProFundPercentFloat=new BigDecimal(companyProFundPercent);
					selfProFundPercentFloat=new BigDecimal(selfProFundPercent);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				String comments = request.getParameter("comments");
				String isActive = request.getParameter("isActive");
		        String dateAddedStr = request.getParameter("dateAdded");
		        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
				if(isActive.equals("Y")){
					isActive="N";
				}
				else{
					isActive="Y";
				}
				
				Map<String, Object> partyPFIn = FastMap.newInstance();
				partyPFIn.put("partyId", partyId);
				partyPFIn.put("companyProFundPercent", companyProFundPercentFloat);
				partyPFIn.put("selfProFundPercent", selfProFundPercentFloat);
				partyPFIn.put("comments", comments);
				partyPFIn.put("isActive", isActive);
				partyPFIn.put("dateAdded", dateAdded);
		 	    try {
		 			GenericValue partyPFInSetup = delegator
		 				.makeValue("PartyProFundRules",
		 						UtilMisc.toMap(partyPFIn));
		 			partyPFInSetup.store();
		 	    } catch (Exception e) { }
	
	 			//--------------Store Data in History Table----------------
				Map<String, Object> partyPFHistoryIn = FastMap.newInstance();
				partyPFHistoryIn.put("partyId", partyId);
				partyPFHistoryIn.put("companyProFundPercent", companyProFundPercentFloat);
				partyPFHistoryIn.put("selfProFundPercent", selfProFundPercentFloat);
				partyPFHistoryIn.put("comments", comments+"(Status Change)");
				partyPFHistoryIn.put("isActive", isActive);
				partyPFHistoryIn.put("dateAdded", currentDatetime);
				try {
		 			GenericValue partyPFInHistory = delegator
			 				.makeValue("PartyProFundRulesHis",
			 						UtilMisc.toMap(partyPFHistoryIn));
		 			partyPFInHistory.create();
			 	} catch (Exception e) { }
	 			//-----------------------------------------------------------------------//
				
				//------------ Company PF For EmplPresentSalary--------------------------//
				
				try{
					float floatBasicAmount=basicSalary.floatValue();
					float floatcompanyAmount= Float.parseFloat(companyProFundPercent);				
					float floatAmount =  floatBasicAmount * floatcompanyAmount / 100;
					companyInvestmentAmount=BigDecimal.valueOf(floatAmount);
				}catch (Exception e) { }
		 	    List<EntityExpr> exprsForComnapy = FastList.newInstance();
		 	    exprsForComnapy.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
		 	    exprsForComnapy.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "COMP_PF_INV "));        
				List<GenericValue> presentSalaryListForCompany = null;		
				
				try {
					presentSalaryListForCompany = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForComnapy, EntityOperator.AND), null, null, null, false);
					for (Map aParsed : presentSalaryListForCompany){	
						try {
				 			GenericValue emplPresentSalarySetup = delegator
					 				.makeValue("EmplPresentSalary",
					 						UtilMisc.toMap(aParsed));
				 			emplPresentSalarySetup.remove();
				 			
				 			//==============================Remove Company Provident Fund Earning ============================//
				 			List<EntityExpr> exprsForCOMP_CPF_INV = FastList.newInstance();
				 			exprsForCOMP_CPF_INV.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
				 			exprsForCOMP_CPF_INV.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "COMP_CPF_INV "));        
							List<GenericValue> presentSalaryListForCOMP_CPF_INV = null;		
							try {
								presentSalaryListForCOMP_CPF_INV = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForCOMP_CPF_INV, EntityOperator.AND), null, null, null, false);
								for (Map aaParsed : presentSalaryListForCOMP_CPF_INV){	
									try {
							 			GenericValue emplPresentSalaryCOMP_CPF_INVSetup = delegator
								 				.makeValue("EmplPresentSalary",
								 						UtilMisc.toMap(aaParsed));
							 			emplPresentSalaryCOMP_CPF_INVSetup.remove();
									}catch (Exception e) { }
								}
							} catch (Exception e) { }
							//==========================================================================
				 			
					 	} catch (Exception e) { }
					}
					
					if(isActive.equals("Y") && presentSalaryListForCompany.isEmpty()){
						Map<String, Object> employeePersentSalaryIn = FastMap.newInstance();
						employeePersentSalaryIn.put("partyId", partyId);
						employeePersentSalaryIn.put("payrollItemTypeId", "COMP_PF_INV");
						employeePersentSalaryIn.put("calculationType", "B");
						employeePersentSalaryIn.put("calculationMode", "Fixed");
						employeePersentSalaryIn.put("optValue", companyInvestmentAmount.toString());
						employeePersentSalaryIn.put("amount", companyInvestmentAmount);
						employeePersentSalaryIn.put("comments", "");
						employeePersentSalaryIn.put("fromDate", currentDatetime);
			    	    try {
			    			GenericValue emplPresentSalarySetup = delegator
			    				.makeValue("EmplPresentSalary",
			    						UtilMisc.toMap(employeePersentSalaryIn));
			    			emplPresentSalarySetup.create();
			    	    } catch (Exception e) { }
			    	    
			    	    
			    	    Map<String, Object> employeePersentSalaryCom_CPFIn = FastMap.newInstance();
			    	    employeePersentSalaryCom_CPFIn.put("partyId", partyId);
			    	    employeePersentSalaryCom_CPFIn.put("payrollItemTypeId", "COMP_CPF_INV");
			    	    employeePersentSalaryCom_CPFIn.put("calculationType", "D");
			    	    employeePersentSalaryCom_CPFIn.put("calculationMode", "Fixed");
			    	    employeePersentSalaryCom_CPFIn.put("optValue", companyInvestmentAmount.toString());
			    	    employeePersentSalaryCom_CPFIn.put("amount", companyInvestmentAmount);
			    	    employeePersentSalaryCom_CPFIn.put("comments", "");
			    	    employeePersentSalaryCom_CPFIn.put("fromDate", currentDatetime);
			    	    try {
			    			GenericValue emplPresentSalaryCom_CPFSetup = delegator
			    				.makeValue("EmplPresentSalary",
			    						UtilMisc.toMap(employeePersentSalaryCom_CPFIn));
			    			emplPresentSalaryCom_CPFSetup.create();
			    	    } catch (Exception e) { }
					}
				} 
				catch (GenericEntityException e) {
					e.printStackTrace();
				}
	 			//-----------------------------------------------------------------------//
	
				//------------ Sale PF For EmplPresentSalary--------------------------//

				try{
					float floatBasicAmount=basicSalary.floatValue();
					float floatselfAmount= Float.parseFloat(selfProFundPercent);				
					float floatAmount =  floatBasicAmount * floatselfAmount / 100;
					employeeInvestmentAmount=BigDecimal.valueOf(floatAmount);
				}catch (Exception e) { }
		 	    List<EntityExpr> exprsForSelf = FastList.newInstance();
		 	    exprsForSelf.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
		 	    exprsForSelf.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "EMPL_PF_INV"));        
				List<GenericValue> presentSalaryListForSelt = null;		
				
				try {
					presentSalaryListForSelt = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForSelf, EntityOperator.AND), null, null, null, false);
					for (Map aParsed : presentSalaryListForSelt){	
						try {
				 			GenericValue emplPresentSalarySetup = delegator
					 				.makeValue("EmplPresentSalary",
					 						UtilMisc.toMap(aParsed));
				 			emplPresentSalarySetup.remove();
					 	} catch (Exception e) { }
					}
					
					if(isActive.equals("Y") && presentSalaryListForSelt.isEmpty()){
						Map<String, Object> employeePersentSalaryIn = FastMap.newInstance();
						employeePersentSalaryIn.put("partyId", partyId);
						employeePersentSalaryIn.put("payrollItemTypeId", "EMPL_PF_INV");
						employeePersentSalaryIn.put("calculationType", "D");
						employeePersentSalaryIn.put("calculationMode", "Fixed");
						employeePersentSalaryIn.put("optValue", employeeInvestmentAmount.toString());
						employeePersentSalaryIn.put("amount", employeeInvestmentAmount);
						employeePersentSalaryIn.put("comments", "");
						employeePersentSalaryIn.put("fromDate", currentDatetime);
			    	    try {
			    			GenericValue emplPresentSalarySetup = delegator
			    				.makeValue("EmplPresentSalary",
			    						UtilMisc.toMap(employeePersentSalaryIn));
			    			emplPresentSalarySetup.create();
			    	    } catch (Exception e) { }
					}
			        request.setAttribute("_EVENT_MESSAGE_", "Provident Fund Successfully Status Change");
				} 
				catch (GenericEntityException e) {
					e.printStackTrace();
			        request.setAttribute("_EVENT_MESSAGE_", "Provident Fund Status Not Change");
				}

 			//-----------------------------------------------------------------------//
			}
	 	    
	        return "success";
	    }
 
}
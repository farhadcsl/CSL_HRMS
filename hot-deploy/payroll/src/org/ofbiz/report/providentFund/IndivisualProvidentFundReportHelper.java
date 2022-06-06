package org.ofbiz.report.providentFund;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.awt.geom.Arc2D.Double;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

public class IndivisualProvidentFundReportHelper{
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);

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
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx,partyId);

        HashMap jrParameters = new HashMap();

        String reportsDirPath = request.getRealPath("/reports/providentFund/");
        jrParameters.put("reportsDirPath", reportsDirPath);

        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
    }
	public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx,String partyId) {
        JRMapCollectionDataSource dataSource;
        Collection reportRows = initializeMapCollection(delegator, ctx,partyId);
        dataSource = new JRMapCollectionDataSource(reportRows);
        return dataSource;
    }
	 
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx,String partyId) {

       ArrayList reportRows = new ArrayList();
       HashMap rowMap = new HashMap();

	  
       List<GenericValue> dataList = null;
       GenericValue personPayrollInfo = null;
	   GenericValue employee = null;
	   
	   try {
		   personPayrollInfo = delegator.findByPrimaryKey("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
   		String employeeName=getAnEmployeeName(delegator, partyId);
  		String employeeId = personPayrollInfo.get("employeeId").toString();
  		String departmentName = getAnDepartment(delegator, partyId);
  		String designationName=getAnEmployeeDesignation(delegator, partyId);
	    rowMap.put("employeeId", employeeId);
	    rowMap.put("employeeName", employeeName);
	    rowMap.put("departmentName", departmentName);
	    rowMap.put("designationName", designationName);
  		String monthlySelfInvestmentAmount="";
  		String monthlyCompanyInvestmentAmount="";
    	List<GenericValue> emplPresentSalaryItemList = null;
  	    
  	    try {
  		 emplPresentSalaryItemList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","EMPL_PF_INV"));
  	      } catch (GenericEntityException e) {
  	        e.printStackTrace();
  	    }
	    for (GenericValue salInfoRowData : emplPresentSalaryItemList) {
	    	monthlySelfInvestmentAmount=salInfoRowData.get("amount").toString();
	    }

  	    try {
  		 emplPresentSalaryItemList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","COMP_CPF_INV"));
  	      } catch (GenericEntityException e) {
  	        e.printStackTrace();
  	    }
	    for (GenericValue salInfoRowData : emplPresentSalaryItemList) {
	    	monthlyCompanyInvestmentAmount=salInfoRowData.get("amount").toString();
	    }
	    rowMap.put("monthlySelfInvestmentAmount", monthlySelfInvestmentAmount);
	    rowMap.put("monthlyCompanyInvestmentAmount", monthlyCompanyInvestmentAmount);

    	double totalAmount=0;
    	List<GenericValue> emplSelfPFPaymentList = null;
  	    
  	    try {
  	    	emplSelfPFPaymentList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","EMPL_PF_INV"));
  	      } catch (GenericEntityException e) {
  	        e.printStackTrace();
  	    }

        ArrayList monthlyPfDeductionInfoList = new ArrayList();
	    for (GenericValue salInfoRowData : emplSelfPFPaymentList) {
	    	HashMap monthlyPDDeductionRowMap = new HashMap();
	    	String monthlyCompanyInvestment="0";
	    	String monthlySelfInvestment=salInfoRowData.get("amount").toString();
	    	double monthlyTotal=0;
	    	String monthNumber=salInfoRowData.get("monthNumber").toString();
	    	String monthName=getMonthName(monthNumber);
	    	String fiscalYear=salInfoRowData.get("fiscalYear").toString();
	    	totalAmount=totalAmount+java.lang.Double.parseDouble(monthlySelfInvestment);

		    List<GenericValue> emplCompPFPaymentList = null;
	  	    
	  	    try {
	  	    	emplCompPFPaymentList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","COMP_CPF_INV","monthNumber",monthNumber));
	  	      } catch (GenericEntityException e) {
	  	        e.printStackTrace();
	  	    }
		    for (GenericValue emplCompPFPayment : emplCompPFPaymentList) {
		    	monthlyCompanyInvestment=emplCompPFPayment.get("amount").toString();
		    	totalAmount=totalAmount+java.lang.Double.parseDouble(monthlyCompanyInvestment);
		    }
		    monthlyTotal=java.lang.Double.parseDouble(monthlySelfInvestment) + java.lang.Double.parseDouble(monthlyCompanyInvestment);
		    monthlyPDDeductionRowMap.put("fiscalYear", fiscalYear);
		    monthlyPDDeductionRowMap.put("monthNumber", monthNumber);
		    monthlyPDDeductionRowMap.put("monthName", monthName);
		    monthlyPDDeductionRowMap.put("monthlySelfInvestment", monthlySelfInvestment);
		    monthlyPDDeductionRowMap.put("monthlyCompanyInvestment", monthlyCompanyInvestment);
		    monthlyPDDeductionRowMap.put("monthlyTotal", String.valueOf(monthlyTotal));
		    monthlyPfDeductionInfoList.add(monthlyPDDeductionRowMap);  
	    }
    	rowMap.put("totalAmount",String.valueOf(totalAmount));
    	rowMap.put("monthlyPfDeductionInfoList",monthlyPfDeductionInfoList);
	    reportRows.add(rowMap);
       return reportRows;
   }

	 public static String getAnEmployeeName(Delegator delegator, String partyId) {
	        List<GenericValue> partyName = null;
	        try {
	        	partyName = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String firstName = "";
	        String lastName= "";
	        if (UtilValidate.isNotEmpty(partyName)) {
	            if (UtilValidate.isNotEmpty(partyName.get(0).get("firstName"))) {
	            	firstName = partyName.get(0).get("firstName").toString();
	            }
	        }
	        if (UtilValidate.isNotEmpty(partyName)) {
	            if (UtilValidate.isNotEmpty(partyName.get(0).get("lastName"))) {
	            	lastName = partyName.get(0).get("lastName").toString();
	            }
	        }
	        String Name = firstName +" " +lastName;
	        return Name;
	    }

		public static String getAnEmployeeDesignation(Delegator delegator, String partyId) {
	        List<GenericValue> partyDesignation = null;
	        List<GenericValue> partyDesignation1 = null;
	        List<GenericValue> partyDesignation2 = null;
	        try {
	        	partyDesignation = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String Designation = "";
	      
	        if (UtilValidate.isNotEmpty(partyDesignation)) {
	            if (UtilValidate.isNotEmpty(partyDesignation.get(0).get("emplPositionId"))) {
	            	Designation = partyDesignation.get(0).get("emplPositionId").toString();
	            }
	        }
	        
	        try {
	        	partyDesignation1 = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", Designation));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String Designation1 = "";
	      
	        if (UtilValidate.isNotEmpty(partyDesignation1)) {
	            if (UtilValidate.isNotEmpty(partyDesignation1.get(0).get("emplPositionTypeId"))) {
	            	Designation1 = partyDesignation1.get(0).get("emplPositionTypeId").toString();
	            }
	        }
	        
	        try {
	        	partyDesignation2 = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", Designation1));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String Designation2 = "";
	      
	        if (UtilValidate.isNotEmpty(partyDesignation2)) {
	            if (UtilValidate.isNotEmpty(partyDesignation2.get(0).get("description"))) {
	            	Designation2 = partyDesignation2.get(0).get("description").toString();
	            }
	        }
	        return Designation2;
	    }
		public static String getAnDepartment(Delegator delegator,String partyId){
			String departmentName="";
			String sectionId="";
			String partyUnitId="";
			String partyTypeId="";
			String partyUnitName="";
			String roleTypeIdFrom="";
			 List<GenericValue> party = null;
				try {
					party = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GenericValue partys = EntityUtil.getFirst(party);
				if(UtilValidate.isNotEmpty(partys)){
					partyUnitId=partys.getString("partyId");
					
				}
				List<GenericValue> partyRel = null;
				try {
					partyRel = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", partyUnitId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GenericValue partyRels = EntityUtil.getFirst(partyRel);
				if(UtilValidate.isNotEmpty(partyRels)){
					partyTypeId=partyRels.getString("partyTypeId");
					
				}
				if (partyTypeId.equalsIgnoreCase("PARTY_DEPARTMENT")){
					departmentName=partyRels.getString("toGroupName");
				} else if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")) {
					departmentName=partyRels.getString("fromGroupName");
				}
				else if (partyTypeId.equalsIgnoreCase("PARTY_UNIT")){
					sectionId=partyRels.getString("partyIdFrom");
					List<GenericValue> partyRell = null;
					try {
						partyRell = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", sectionId));
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					GenericValue partyRells = EntityUtil.getFirst(partyRell);
					if(UtilValidate.isNotEmpty(partyRells)){
						departmentName=partyRells.getString("fromGroupName");
						
					}
				}
			return departmentName;
		}

		public static String getMonthName(String bonusMonth) {
		     String monthName = "";
		     if (bonusMonth.equals("0")) monthName = "January";
		     if (bonusMonth.equals("1")) monthName = "February";
		     if (bonusMonth.equals("2")) monthName = "March";
		     if (bonusMonth.equals("3")) monthName = "April";
		     if (bonusMonth.equals("4")) monthName = "May";
		     if (bonusMonth.equals("5")) monthName = "June";
		     if (bonusMonth.equals("6")) monthName = "July";
		     if (bonusMonth.equals("7")) monthName = "August";
		     if (bonusMonth.equals("8")) monthName = "September";
		     if (bonusMonth.equals("9")) monthName = "October";
		     if (bonusMonth.equals("10")) monthName = "November";
		     if (bonusMonth.equals("11")) monthName = "December";

		     return monthName;
		 }
		
     
}
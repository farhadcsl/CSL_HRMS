package org.ofbiz.report.salarySheet;
import javolution.util.FastList;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.report.PayrollReportHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SalarySheetService {
    public static final String module = SalarySheetService.class.getName();


    public static JRDataSource createReportDataSource(Delegator delegator, String companyId, String monthNumber, String year, Collection parsed) {
        JRMapCollectionDataSource dataSource;
        Collection reportRows = initializeMapCollection(delegator, companyId, monthNumber, year, parsed);
        dataSource = new JRMapCollectionDataSource(reportRows);
        return dataSource;
    }
    
    private static Collection initializeMapCollection(Delegator delegator, String companyId, String monthNumber, String year, Collection parsed) {
    	
    
    List partyIds = FastList.newInstance();

    for (Object aParsed : parsed) {
        Map record = (Map) aParsed;
        partyIds.add(record.get("employeePartyId"));
    }
    
    EntityFindOptions findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);

    
    List<GenericValue> eligibleList = null;
    GenericValue employee = null;
    try {
    	eligibleList = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("monthNumber", monthNumber, "fiscalYear", "2015"));
      } catch (GenericEntityException e) {
        e.printStackTrace();
    }
    ArrayList reportRows = new ArrayList();
    HashMap rowMap = new HashMap();
    String monthNameYear = null;
    assert eligibleList != null;
    
    for (GenericValue employeeValue : eligibleList) {
    	BigDecimal basic = BigDecimal.ZERO;
        BigDecimal HouseRent = BigDecimal.ZERO;
        BigDecimal medical = BigDecimal.ZERO;
        BigDecimal mobile = BigDecimal.ZERO;
        BigDecimal OtherAllow = BigDecimal.ZERO;
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal otherDeduction = BigDecimal.ZERO;
        BigDecimal grossben = BigDecimal.ZERO;
        BigDecimal grossDed = BigDecimal.ZERO;
        BigDecimal CompanyCon = BigDecimal.ZERO;
        BigDecimal employCon = BigDecimal.ZERO;
        BigDecimal totalpay = BigDecimal.ZERO;
        BigDecimal emplTax = BigDecimal.ZERO;
    	rowMap = new HashMap();
        String employeePartyId = null;
        if (UtilValidate.isNotEmpty(employeeValue)) {
            employeePartyId = employeeValue.getString("partyId");
        }
        List<GenericValue> singleEmployeeBenefitList = PayrollReportHelper.getAnEmployeeBenefitFromDatabase(delegator, employeePartyId, monthNumber, year);
        String employeeName = PayrollReportHelper.getAnEmployeeName(delegator, employeePartyId);
        String employeeId = PayrollReportHelper.getAnEmployeeId(delegator, employeePartyId);
        rowMap.put("empId", employeeId);
        rowMap.put("name", employeeName);
        for (GenericValue employeeBenefit : singleEmployeeBenefitList) {
        	String payrollItemTypeId = employeeBenefit.getString("payrollItemTypeId");
        	String payrollType = PayrollReportHelper.getpayrollType(delegator,payrollItemTypeId);
            if (payrollType.equals("basic")) {
            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
                basic = (BigDecimal) employeeBenefit.get("amount");
            }
            if (payrollType.equals("houseRent")) {
            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
                HouseRent = (BigDecimal) employeeBenefit.get("amount");
            }
            if (payrollType.equals("medical")) {
            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
                medical = (BigDecimal) employeeBenefit.get("amount");
            }
            if (payrollType.equals("mobile")) {
            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
                mobile = (BigDecimal) employeeBenefit.get("amount");
            }
            if (payrollType.equals("otherAllowance")) {
            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
            	OtherAllow = (BigDecimal) employeeBenefit.get("amount");
            }
            if (payrollType.equals("CompanyCon")) {
            	
            	CompanyCon = (BigDecimal) employeeBenefit.get("amount");
            }
            
        }
        rowMap.put("basic", basic);
        rowMap.put("houseRent", HouseRent);
        rowMap.put("medical", medical);
        rowMap.put("mobile", mobile);
        rowMap.put("otherAllow", OtherAllow);
        rowMap.put("gross", grossben);
        rowMap.put("CompanyCon", CompanyCon);
        List<GenericValue> singleEmployeeDeductionList = PayrollReportHelper.getAnEmployeeDeductionsFromDatabase(delegator, employeePartyId, monthNumber, year);
        for (GenericValue employeeDeduction : singleEmployeeDeductionList) {
        	String payrollItemTypeId = employeeDeduction.getString("payrollItemTypeId");
        	String payrollType = PayrollReportHelper.getpayrollType(delegator,payrollItemTypeId);
            if (payrollType.equals("otherded")) {
            	grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
            	otherDeduction = (BigDecimal) employeeDeduction.get("amount");
            } 
            if (payrollType.equals("employCon")) {
            	grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
            	employCon = (BigDecimal) employeeDeduction.get("amount");
            } 
            if (payrollType.equals("emplTax")) {
            	//grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
            	emplTax = (BigDecimal) employeeDeduction.get("amount");
            } 
    }
        rowMap.put("emplTax", emplTax);
        rowMap.put("otherDeduct", otherDeduction);
        rowMap.put("deductionTotal", grossDed);
        totalpay = grossben.subtract(grossDed);
       
        totalpay = totalpay.subtract(emplTax);
        rowMap.put("netPay", totalpay);
        reportRows.add(rowMap);
    }
        
       
    
    	
    	return reportRows;
    }
    
}  
    

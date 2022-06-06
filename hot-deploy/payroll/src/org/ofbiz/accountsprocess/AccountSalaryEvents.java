package org.ofbiz.accountsprocess;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.xmlrpc.metadata.Util;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.salaryprocess.SalaryUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class AccountSalaryEvents {
	public static String newAccountSalaryList(HttpServletRequest request, HttpServletResponse response) {
		 	Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String monthNumber = "";
	        String companyId = "";
	      
	        String year = "";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (null != request.getParameter("partyIdFrom")) {
	            companyId = request.getParameter("partyIdFrom");
	        }
	        if (null != request.getParameter("fiscalYear")) {
	        	year = request.getParameter("fiscalYear");
	        }
	       
	        if (monthNumber.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        List<GenericValue> employeeSalaryFinal = null;
	        try {
	        	employeeSalaryFinal = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("monthNumber", monthNumber,"fiscalYear",year, "isActive","Y"));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String processId = null;
	        if (UtilValidate.isNotEmpty(employeeSalaryFinal)) {
	            if (UtilValidate.isNotEmpty(employeeSalaryFinal.get(0).get("processId"))) {
	            	processId = employeeSalaryFinal.get(0).get("processId").toString();
	            }
	        }
	        
	        if (employeeSalaryFinal.isEmpty()){
	        	request.setAttribute("_ERROR_MESSAGE_", "There is no pending salary Process for Accounting Transaction");
	            return "error";
	        }
	        int totalEmployee = employeeSalaryFinal.size();
	        double MonthNetPay = 0;
	        for (GenericValue totalSalaryEmplList : employeeSalaryFinal) {
	        	MonthNetPay += Double.parseDouble(totalSalaryEmplList.get("totalSalary").toString());
	        }
	        BigDecimal finalMonthlyNetPay = new BigDecimal(MonthNetPay);
	        request.setAttribute("companyId", companyId);
	        request.setAttribute("year", year);
	        request.setAttribute("processId", processId);
            request.setAttribute("monthName", SalaryUtils.getMonthName(monthNumber) );
            request.setAttribute("finalMonthlyNetPay", finalMonthlyNetPay);
            request.setAttribute("totalListEmplint", totalEmployee);
	return "success";
	}
	
	
	public static Map<String, Object> updateSalaryAccountsList(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

       String isSalary = (String) context.get("isSalary");
        String companyId = (String) context.get("companyId");
        String monthNumber = (String) context.get("monthNumber");
        String partyId = (String) context.get("partyId");
        
        String processId = (String) context.get("processId");
	 if (isSalary == null){
		 isSalary = "N";
	 }
	 if (isSalary.equalsIgnoreCase("N")){
		 isSalary = "N";
	 }
        List<GenericValue> salaryList = null;
        GenericValue employee = null;
        try {
        	salaryList = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId, "partyId", partyId));
            employee = EntityUtil.getFirst(salaryList);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        employee.set("isSalary", isSalary);
        try {
            delegator.store(employee);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        
        return ServiceUtil.returnSuccess();
    }
	  public static String finalAccountingProcess(HttpServletRequest request, HttpServletResponse response) {
		  Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        HttpSession session = request.getSession();
	       // Delegator delegator = dctx.getDelegator();
	        String year = "";
	        String monthNumber="";
	        String companyId ="";
	        String processId = "";
	        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (null != request.getParameter("companyId")) {
	            companyId = request.getParameter("companyId");
	        }
	        if (null != request.getParameter("fiscalYear")) {
	        	year = request.getParameter("fiscalYear");
	        }
	        if (null != request.getParameter("processId")) {
	        	processId = request.getParameter("processId");
	        }
	        List<GenericValue> salaryList = null;
	        GenericValue employee = null;
	        try {
	        	salaryList = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId, "isSalary", "Y"));
	            //employee = EntityUtil.getFirst(salaryList);

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = null;
	        if (UtilValidate.isNotEmpty(salaryList)) {
	            if (UtilValidate.isNotEmpty(salaryList.get(0).get("partyId"))) {
	            	partyId = salaryList.get(0).get("partyId").toString();
	            }
	        }
	        
	       
	            
	        for (GenericValue value : salaryList) {
	        	Map<String, Object> record = FastMap.newInstance();
	            record.put("monthNumber", monthNumber);
	            record.put("fiscalYear", year);
	            record.put("statusId", "INVOICE_PAID");
	            record.put("invoiceTypeId", "PAYROL_INVOICE");
	            record.put("partyIdFrom", partyId);
	            record.put("partyId", companyId);
	            record.put("currencyUomId", "BDT");
	            //record.put("userLogin", userLogin);
	            String invoiceId = delegator.getNextSeqId("Invoice");
	            record.put("invoiceId", invoiceId);
	            try {
	          		GenericValue emplTemsalaryopt = delegator
	          				.makeValue("Invoice",
	          						UtilMisc.toMap(record));
	          		emplTemsalaryopt.create();
	              } catch (Exception e) {

	              	e.printStackTrace();
	              }
	        	
	        	 List<GenericValue> invoicePrepareList = null;
	        	 try {
	        		 invoicePrepareList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber,"processId",processId, "fiscalYear", year, "isAccounting", "N"));
	 	            //employee = EntityUtil.getFirst(salaryList);

	 	        } catch (GenericEntityException e) {
	 	            e.printStackTrace();
	 	        }
	        	 int i = 1;
	        	 BigDecimal quantity = new BigDecimal(i);
	        	 for (GenericValue finalamn : invoicePrepareList) {
	        		 String j = Integer.toString(i);
	        		 List<GenericValue> payrollstatus = null;
	        	        try {
	        	        	payrollstatus = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", finalamn.get("payrollItemTypeId")));
	        	        } catch (GenericEntityException e) {
	        	            e.printStackTrace();
	        	        }
	        	        String invoiceItemTypeId = null;
	        	        if (UtilValidate.isNotEmpty(payrollstatus)) {
	        	            if (UtilValidate.isNotEmpty(payrollstatus.get(0).get("invoiceItemTypeId"))) {
	        	            	invoiceItemTypeId = payrollstatus.get(0).get("invoiceItemTypeId").toString();
	        	            }
	        	        }
	        	        BigDecimal amount = new BigDecimal(finalamn.get("amount").toString());
	        	        
	        	        Map<String, Object> invoiceItemMap = FastMap.newInstance();
	        	        invoiceItemMap.put("invoiceId", invoiceId);
	        	        /*invoiceItemMap.put("invoiceItemSeqId", delegator.getNextSeqId("InvoiceItem") );*/
	        	        invoiceItemMap.put("invoiceItemSeqId", j );
	        	        invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
	        	        invoiceItemMap.put("quantity", quantity);
	        	        invoiceItemMap.put("amount", amount);
	        	        try {
	    	          		GenericValue emplInvoiceItem = delegator
	    	          				.makeValue("InvoiceItem",
	    	          						UtilMisc.toMap(invoiceItemMap));
	    	          		emplInvoiceItem.create();
	    	              } catch (Exception e) {

	    	              	e.printStackTrace();
	    	              }
	        	        
	        	       List<GenericValue> invoiceItemtype = null;
	        	        try {
	        	        	invoiceItemtype = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
	        	        } catch (GenericEntityException e) {
	        	            e.printStackTrace();
	        	        }
	        	        String invoiceCreditId = null;
	        	        if (UtilValidate.isNotEmpty(invoiceItemtype)) {
	        	            if (UtilValidate.isNotEmpty(invoiceItemtype.get(0).get("defaultGlAccountId"))) {
	        	            	invoiceCreditId = invoiceItemtype.get(0).get("defaultGlAccountId").toString();
	        	            }
	        	        }
	        	        List<GenericValue> invoiceItemtypeGl = null;
	        	        try {
	        	        	invoiceItemtypeGl = delegator.findByAnd("InvoiceItemTypeGlAccount", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
	        	        } catch (GenericEntityException e) {
	        	            e.printStackTrace();
	        	        }
	        	        String invoiceDebitId = null;
	        	        if (UtilValidate.isNotEmpty(invoiceItemtypeGl)) {
	        	            if (UtilValidate.isNotEmpty(invoiceItemtypeGl.get(0).get("glAccountId"))) {
	        	            	invoiceDebitId = invoiceItemtypeGl.get(0).get("glAccountId").toString();
	        	            }
	        	        }
	        	        Timestamp stamp = new Timestamp(System.currentTimeMillis());
	        	        Map<String, Object> paymenttrans = FastMap.newInstance();
	        	        String paymentId = delegator.getNextSeqId("Payment");
	        	        paymenttrans.put("paymentId",paymentId);
	        	        paymenttrans.put("paymentTypeId","PAYROL_PAYMENT");
	        	        paymenttrans.put("partyIdFrom",companyId);
	        	        paymenttrans.put("paymentMethodTypeId","CASH");
	        	        paymenttrans.put("paymentMethodId","CASH");
	        	        paymenttrans.put("partyIdTo",partyId);
	        	        paymenttrans.put("statusId","PMNT_SENT");
	        	        paymenttrans.put("amount",amount);
	        	        paymenttrans.put("currencyUomId","BDT");
	        	        paymenttrans.put("overrideGlAccountId",invoiceCreditId);
	        	        paymenttrans.put("effectiveDate",stamp);
	        	        try {
	    	          		GenericValue paymrranc = delegator
	    	          				.makeValue("Payment",
	    	          						UtilMisc.toMap(paymenttrans));
	    	          		paymrranc.create();
	    	              } catch (Exception e) {

	    	              	e.printStackTrace();
	    	              }
	        	        createAccountingTransaction(context, userLogin, companyId, invoiceDebitId, invoiceCreditId, amount,stamp,invoiceId,paymentId);  
	        	     /*   Map<String, Object> acctgtrans = FastMap.newInstance();
	        	        String acctgTransId = delegator.getNextSeqId("AcctgTrans");
	        	        acctgtrans.put("acctgTransId",acctgTransId);
	        	        acctgtrans.put("acctgTransTypeId","DISBURSEMENT");
	        	        
	        	        acctgtrans.put("isPosted", "Y");
	        	        acctgtrans.put("glFiscalTypeId", "ACTUAL");
	        	        acctgtrans.put("voucherDate", stamp);
	        	        acctgtrans.put("invoiceId",invoiceId);
	        	        acctgtrans.put("roleTypeId","EMPLOYEE");
	        	        acctgtrans.put("partyId",partyId);
	        	        acctgtrans.put("paymentId",paymentId);
	        	        
	        	        try {
	    	          		GenericValue acctranc = delegator
	    	          				.makeValue("AcctgTrans",
	    	          						UtilMisc.toMap(acctgtrans));
	    	          		acctranc.create();
	    	              } catch (Exception e) {

	    	              	e.printStackTrace();
	    	              }
	        	       
	        	       
	        	        //for credit Entry
	        	        Map<String, Object> transaction = FastMap.newInstance();
	        	        transaction.put("acctgTransId", acctgTransId);
	        	        transaction.put("acctgTransEntrySeqId", "00001");
	        	        transaction.put("acctgTransEntryTypeId", "_NA_");
	        	        transaction.put("partyId", partyId);
	        	        
	        	        transaction.put("reconcileStatusId", "AES_NOT_RECONCILED");
	        	        transaction.put("amount", amount);
	        	        transaction.put("debitCreditFlag", "C");
	        	        transaction.put("organizationPartyId",companyId);
	        	        //transaction.put("glAccountTypeId", "ACCOUNTS_PAYABLE");
	        	        transaction.put("glAccountId", invoiceCreditId);
	        	        transaction.put("origCurrencyUomId", "BDT");
	        	        
	        	        //transaction.put("debitGlAccountId", invoiceDebitId);
	        	       
	        	        
	        	        
	        	       // transaction.put("userLogin", userLogin);
	        	        try {
	    	          		GenericValue acctrancentryC = delegator
	    	          				.makeValue("AcctgTransEntry",
	    	          						UtilMisc.toMap(transaction));
	    	          		acctrancentryC.create();
	    	              } catch (Exception e) {

	    	              	e.printStackTrace();
	    	              }
	        	        //for Debit Entry
	        	        
	        	        Map<String, Object> transactionD = FastMap.newInstance();
	        	        transactionD.put("acctgTransId", acctgTransId);
	        	        transactionD.put("acctgTransEntrySeqId", "00002");
	        	        transaction.put("acctgTransEntryTypeId", "_NA_");
	        	        transactionD.put("partyId", partyId);
	        	        transaction.put("reconcileStatusId", "AES_NOT_RECONCILED");
	        	        transactionD.put("amount", amount);
	        	        transactionD.put("debitCreditFlag", "D");
	        	        transactionD.put("organizationPartyId",companyId);
	        	        //transactionD.put("glAccountTypeId", "PAYROL_INVOICE");
	        	        transactionD.put("glAccountId", invoiceDebitId);
	        	        transactionD.put("origCurrencyUomId", "BDT");
	        	        
	        	        //transaction.put("debitGlAccountId", );
	        	       // transactionD.put("glFiscalTypeId", "ACTUAL");
	        	        
	        	        //transactionD.put("voucherDate", stamp);
	        	        //transactionD.put("userLogin", userLogin);
	        	        try {
	    	          		GenericValue acctrancentryD = delegator
	    	          				.makeValue("AcctgTransEntry",
	    	          						UtilMisc.toMap(transactionD));
	    	          		acctrancentryD.create();
	    	              } catch (Exception e) {

	    	              	e.printStackTrace();
	    	              }*/
	        	        
	        	        
	        	        GenericValue updateEemplSalary = null; 
	        	        try {
	        	        	updateEemplSalary = delegator.findByPrimaryKey("EmplFinalSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId, "payrollItemTypeId", finalamn.get("payrollItemTypeId")));
	        	        	updateEemplSalary.set("isAccounting", "Y");
	        	        	updateEemplSalary.store();
	        	        } catch (GenericEntityException e) {
	        	            e.printStackTrace();
	        	        }
	        	        
	        	        
	        	        
	        	 }
	        	 GenericValue employeeSalaryFinalup = null; 
     	        try {
     	        	employeeSalaryFinalup = delegator.findByPrimaryKey("EmployeeSalaryFinal", UtilMisc.toMap("processId", processId,"partyId", partyId));
     	        	employeeSalaryFinalup.set("isActive", "N");
     	        	employeeSalaryFinalup.store();
     	        } catch (GenericEntityException e) {
     	            e.printStackTrace();
     	        }
	        }
		  
		  
		  
		  return "success";
	  }
	  public static String createAccountingTransaction(DispatchContext ctx, GenericValue userLogin, String organizationPartyId, String debitGlAccountId, String creditGlAccountId, BigDecimal amount, Timestamp monthYear, String paymentId, String invoiceId ) {
	        LocalDispatcher dispatcher = ctx.getDispatcher();

	        Map<String, Object> transaction = FastMap.newInstance();
	        transaction.put("acctgTransTypeId", "OUTGOING_PAYMENT");
	        transaction.put("isPosted", "Y");
	        transaction.put("userLogin", userLogin);
	        transaction.put("amount", amount);
	        transaction.put("creditGlAccountId", creditGlAccountId);
	        transaction.put("debitGlAccountId", debitGlAccountId);
	        transaction.put("glFiscalTypeId", "ACTUAL");
	        transaction.put("organizationPartyId", organizationPartyId);
	        transaction.put("voucherDate", monthYear);
	        transaction.put("paymentId", paymentId);
	        transaction.put("invoiceId", invoiceId);
	        Map<String, Object> payments;

	        try {
	            payments = dispatcher.runSync("XXXX:p", transaction);
	            if (ServiceUtil.isError(payments)) {
	                return "error";
	            }

	        } catch (ServiceValidationException e1) {
	            e1.printStackTrace();
	        } catch (GenericServiceException e1) {
	            e1.printStackTrace();
	        }
	        return "success";

	    }

	  public static String indBonusProcessAcctg(Delegator delegator,DispatchContext ctx,GenericValue userLogin, String partyId, String bonusId, String month, String year,BigDecimal amount){
		  LocalDispatcher dispatcher = ctx.getDispatcher();
		  DispatchContext context = dispatcher.getDispatchContext();
		  
		  List<GenericValue> payrtyList = null;
	       
	        try {
	        	payrtyList = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "EMPLOYEE"));
	            //employee = EntityUtil.getFirst(salaryList);

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String companyId = null;
	        if (UtilValidate.isNotEmpty(payrtyList)) {
	            if (UtilValidate.isNotEmpty(payrtyList.get(0).get("partyIdFrom"))) {
	            	companyId = payrtyList.get(0).get("partyIdFrom").toString();
	            }
	        }
	        
	        List<GenericValue> payrollItem = null;
       	 try {
       		payrollItem = delegator.findByAnd("BonusType", UtilMisc.toMap("bonusId", bonusId));
	            //employee = EntityUtil.getFirst(salaryList);

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
       	String payrollItemTypeId = null;
        if (UtilValidate.isNotEmpty(payrollItem)) {
            if (UtilValidate.isNotEmpty(payrollItem.get(0).get("payrollItemTypeId"))) {
            	payrollItemTypeId = payrollItem.get(0).get("payrollItemTypeId").toString();
            }
        }
	        
		  Map<String, Object> record = FastMap.newInstance();
		  	record.put("month", month);
          record.put("fiscalYear", year);
          record.put("statusId", "INVOICE_PAID");
          record.put("invoiceTypeId", "PAYROL_INVOICE");
          record.put("partyIdFrom", partyId);
          record.put("partyId", companyId);
          record.put("currencyUomId", "BDT");
          //record.put("userLogin", userLogin);
          String invoiceId = delegator.getNextSeqId("Invoice");
          record.put("invoiceId", invoiceId);
          try {
        		GenericValue emplTemsalaryopt = delegator
        				.makeValue("Invoice",
        						UtilMisc.toMap(record));
        		emplTemsalaryopt.create();
            } catch (Exception e) {

            	e.printStackTrace();
            }
         int i = 1;
         BigDecimal quantity = new BigDecimal(i);
         String j = Integer.toString(i);
 		 List<GenericValue> payrollstatus = null;
 	        try {
 	        	payrollstatus = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId ));
 	        } catch (GenericEntityException e) {
 	            e.printStackTrace();
 	        }
 	        String invoiceItemTypeId = null;
 	        if (UtilValidate.isNotEmpty(payrollstatus)) {
 	            if (UtilValidate.isNotEmpty(payrollstatus.get(0).get("invoiceItemTypeId"))) {
 	            	invoiceItemTypeId = payrollstatus.get(0).get("invoiceItemTypeId").toString();
 	            }
 	        }
 	        //BigDecimal amount = new BigDecimal(finalamn.get("amount").toString());
 	        
 	        Map<String, Object> invoiceItemMap = FastMap.newInstance();
 	        invoiceItemMap.put("invoiceId", invoiceId);
 	        /*invoiceItemMap.put("invoiceItemSeqId", delegator.getNextSeqId("InvoiceItem") );*/
 	        invoiceItemMap.put("invoiceItemSeqId", j );
 	        invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
 	        invoiceItemMap.put("quantity", quantity);
 	        invoiceItemMap.put("amount", amount);
 	        try {
	          		GenericValue emplInvoiceItem = delegator
	          				.makeValue("InvoiceItem",
	          						UtilMisc.toMap(invoiceItemMap));
	          		emplInvoiceItem.create();
	              } catch (Exception e) {

	              	e.printStackTrace();
	              }
 	        
 	       List<GenericValue> invoiceItemtype = null;
 	        try {
 	        	invoiceItemtype = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
 	        } catch (GenericEntityException e) {
 	            e.printStackTrace();
 	        }
 	        String invoiceCreditId = null;
 	        if (UtilValidate.isNotEmpty(invoiceItemtype)) {
 	            if (UtilValidate.isNotEmpty(invoiceItemtype.get(0).get("defaultGlAccountId"))) {
 	            	invoiceCreditId = invoiceItemtype.get(0).get("defaultGlAccountId").toString();
 	            }
 	        }
 	        List<GenericValue> invoiceItemtypeGl = null;
 	        try {
 	        	invoiceItemtypeGl = delegator.findByAnd("InvoiceItemTypeGlAccount", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
 	        } catch (GenericEntityException e) {
 	            e.printStackTrace();
 	        }
 	        String invoiceDebitId = null;
 	        if (UtilValidate.isNotEmpty(invoiceItemtypeGl)) {
 	            if (UtilValidate.isNotEmpty(invoiceItemtypeGl.get(0).get("glAccountId"))) {
 	            	invoiceDebitId = invoiceItemtypeGl.get(0).get("glAccountId").toString();
 	            }
 	        }
 	        Timestamp stamp = new Timestamp(System.currentTimeMillis());
 	        Map<String, Object> paymenttrans = FastMap.newInstance();
 	        String paymentId = delegator.getNextSeqId("Payment");
 	        paymenttrans.put("paymentId",paymentId);
 	        paymenttrans.put("paymentTypeId","PAYROL_PAYMENT");
 	        paymenttrans.put("partyIdFrom",companyId);
 	        paymenttrans.put("paymentMethodTypeId","CASH");
 	        paymenttrans.put("paymentMethodId","CASH");
 	        paymenttrans.put("partyIdTo",partyId);
 	        paymenttrans.put("statusId","PMNT_SENT");
 	        paymenttrans.put("amount",amount);
 	        paymenttrans.put("currencyUomId","BDT");
 	        paymenttrans.put("overrideGlAccountId",invoiceCreditId);
 	        paymenttrans.put("effectiveDate",stamp);
 	        try {
	          		GenericValue paymrranc = delegator
	          				.makeValue("Payment",
	          						UtilMisc.toMap(paymenttrans));
	          		paymrranc.create();
	              } catch (Exception e) {

	              	e.printStackTrace();
	              }
 	        createAccountingTransaction(context, userLogin, companyId, invoiceDebitId, invoiceCreditId, amount,stamp,invoiceId,paymentId);  
 	   
          
      	
		  return "success";
	  }
	  
}
package org.ofbiz.humanres.excelexport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.ContessaPartyServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilCommon;

public class ExcelExportEvents {

	private static final String MODULE = ExcelExportEvents.class.getName();

	/**
	 * @author zzz
	 * The Method exportToExcelEmployeesDetails  method to export employees details into excel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static String exportToExcelEmployeesDetails(HttpServletRequest request,	HttpServletResponse response) {
		
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String orgPartyId = request.getParameter("orgPartyId");
        //added for test
       
        Map<String, Object> withdrawCtx = FastMap.newInstance();
        
        withdrawCtx.put("extInfo", request.getParameter("extInfo"));
        withdrawCtx.put("roleTypeId", request.getParameter("roleTypeId"));
        if(request.getParameter("statusId")==""){
        	withdrawCtx.put("statusId", null);
        }else{
        	 withdrawCtx.put("statusId", request.getParameter("statusId"));
        }
       
        withdrawCtx.put("VIEW_INDEX", "0");
        withdrawCtx.put("VIEW_SIZE", request.getParameter("partyListSize"));
        withdrawCtx.put("lookupFlag", request.getParameter("lookupFlag"));
        withdrawCtx.put("showAll", request.getParameter("showAll"));
        withdrawCtx.put("partyId", request.getParameter("partyId"));
        if(request.getParameter("partyTypeId")==""){
        	withdrawCtx.put("partyTypeId", null);
        }else{
        	 withdrawCtx.put("partyTypeId", request.getParameter("partyTypeId"));
        }
      
        withdrawCtx.put("defaultOrganizationPartyId", request.getParameter("defaultOrganizationPartyId"));
        
        withdrawCtx.put("userLoginId", request.getParameter("userLoginId"));
        withdrawCtx.put("groupName", request.getParameter("groupName"));
        withdrawCtx.put("firstName", request.getParameter("firstName"));
        withdrawCtx.put("lastName", request.getParameter("lastName"));
        withdrawCtx.put("address1", request.getParameter("address1"));
        withdrawCtx.put("address2", request.getParameter("address2"));
        withdrawCtx.put("city", request.getParameter("city"));
        if(request.getParameter("stateProvinceGeoId")==""){
        	withdrawCtx.put("stateProvinceGeoId", null);
        }else{
        	 withdrawCtx.put("stateProvinceGeoId", request.getParameter("stateProvinceGeoId"));
        }
        withdrawCtx.put("postalCode", request.getParameter("postalCode"));
        withdrawCtx.put("infoString", request.getParameter("infoString"));        
        withdrawCtx.put("countryCode", request.getParameter("countryCode"));
        withdrawCtx.put("areaCode", request.getParameter("areaCode"));
        withdrawCtx.put("contactNumber", request.getParameter("contactNumber"));
        withdrawCtx.put("inventoryItemId", request.getParameter("inventoryItemId"));
        withdrawCtx.put("serialNumber", request.getParameter("serialNumber"));
        withdrawCtx.put("softIdentifier", request.getParameter("softIdentifier"));
        withdrawCtx.put("partyRelationshipTypeId", request.getParameter("partyRelationshipTypeId"));
        withdrawCtx.put("ownerPartyIds", request.getParameter("ownerPartyIds"));
        withdrawCtx.put("sortField", request.getParameter("sortField"));
        withdrawCtx.put("excelExportSize", request.getParameter("partyListSize"));
        withdrawCtx.put("excelExport", "excelExport");

        Map<String, Object> serviceResults = null;
		try {
			
			serviceResults = ContessaPartyServices.findContessaParty(context, withdrawCtx);
		} catch (Exception e1) {			
			e1.printStackTrace();
		}
        if (ServiceUtil.isError(serviceResults)) {
            return "error";
        }
        @SuppressWarnings("unchecked")
		List<GenericValue> partyList = (List<GenericValue>) serviceResults.get("partyList");
        
		// prepare for excel
        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeesDetails(delegator,partyList,orgPartyId);
		// create file Name with random number
		String fileName = "EmployeesInfo_"
				+ String.valueOf((int) (Math.random() * 100000)) + ".xls";
		
		// create path for excel file
		String excelFilePath = UtilCommon
				.getAbsoluteFilePath(request, fileName);
		
		try {
			
			ExcelUtils.saveToExcel(excelFilePath, "eligibleEmployeeList", data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ExcelUtils.downloadExcel(fileName, request, response);
		
		
	}	

}

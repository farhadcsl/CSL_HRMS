package org.ofbiz.humanres.graphicalChart;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
//import org.apache.poi.hslf.util.SystemTimeUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.HumanResEvents;
import org.ofbiz.minilang.method.entityops.FindByAnd;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.humanres.attendance.AttendanceUtils;
//import org.opentaps.common.util.UtilCommon;
//import org.opentaps.foundation.entity.EntityException;
import org.ofbiz.humanres.HrUtils;

import com.ibm.icu.util.StringTokenizer;
import org.ofbiz.humanres.organization.OrgChart;

public class barChart {

    public static Map<String, Object> findBusinessUnitItem(DispatchContext dctx, Map<String, ? extends Object> context) {

	    Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();
	    String businessUnit = (String) context.get("businessUnit");	    
	    List<EntityExpr> exprs = FastList.newInstance();
	    exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, businessUnit));
	    exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.NOT_EQUAL, null));
		List<GenericValue> businessUnitList = null;
		try { 
			businessUnitList = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	    int count=0;
        List exprsForLevel = FastList.newInstance();
		List<Map> cfMap = FastList.newInstance();
		List<Integer> totalEmplooyeeCountList = new ArrayList<Integer>();
		for (Map aParsed : businessUnitList){
			Map record = FastMap.newInstance();
			record.putAll(aParsed);
			cfMap.add(record);
			String partyId=aParsed.get("partyId").toString();
			int unitTotalEmployee=OrgChart.getTotalEmployee(delegator,0, partyId);
			totalEmplooyeeCountList.add(unitTotalEmployee);
		}
	    result.put("businessUnitList", cfMap);
	    result.put("businessUnitEmployeeCountList", totalEmplooyeeCountList);
	    return result;
	} 
	
}

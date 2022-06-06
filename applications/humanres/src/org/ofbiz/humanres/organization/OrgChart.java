package org.ofbiz.humanres.organization;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class OrgChart {

	private static Logger logger=Logger.getLogger("OrgChart");
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createOrgChart(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();

		String partyId = (String) context.get("partyId");
		String partyUnit = (String) context.get("partyUnit");
		String selectionMode = (String) context.get("selectionMode");
		
		if(selectionMode == null || selectionMode.isEmpty()){
			selectionMode = "1";
		}
		
		int unit=0;
		try{
			unit=Integer.valueOf(partyUnit);
			if(unit>0){
				partyId=partyUnit;
			}
			
		}catch(NumberFormatException e){
			
		}
		int count=0;
		List<OrgChartDto> orgChartDtoList=FastList.newInstance();
		List<Map<String,String>> employeeList=FastList.newInstance();
	
		count=populateChart(delegator, partyId, orgChartDtoList,count,employeeList);
		
		Collections.reverse(employeeList);
	
	
		JSONArray results = new JSONArray();
	
		JSONObject firstItem =new JSONObject();
		firstItem.put("id", 0);
		firstItem.put("parent","null");
		firstItem.put("groupTitleColor","#99EE11");
		firstItem.put("itemTitleColor","#99EE11");
		int totalEmployee=0;
		totalEmployee=	getTotalEmployee(delegator,totalEmployee,partyId);
		firstItem.put("description", getGroupName(delegator,partyId).trim()+"("+totalEmployee+")");
		results.add(firstItem);
		
		/*Set Total Employee */
		margeChildName(delegator,employeeList,orgChartDtoList);
		
		for(OrgChartDto dto:orgChartDtoList){
			JSONObject chartdto =new JSONObject();
			chartdto.put("id", dto.getId());
			chartdto.put("parent",dto.getParent());
			String description= dto.getTitle()!=null ? dto.getTitle() : "description";
			chartdto.put("description", description.trim());
			chartdto.put("groupTitleColor",dto.getGroupTitleColor());
			chartdto.put("itemTitleColor",dto.getItemTitleColor());
			results.add(chartdto);
		}
		/*System.out.println(results);*/
		Map<String, Object> result = FastMap.newInstance();
		result.put("results", results);
		result.put("selectionMode", selectionMode);
		
		return result;
	}
	
	public static String getGroupName(Delegator delegator,String partyId){
		try{
			GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
			if(UtilValidate.isNotEmpty(group)){
				String groupName = group.getString("groupName");
				return groupName;
			}
		
		}catch(GenericEntityException e){
			return null;
		}
		return null;
	}
	
	/**
	 * Get Child Division/Department/Section/Line
	 * @param delegator
	 * @param positionId
	 * @return
	 */
	
	public static int populateChart(Delegator delegator, String partyId,List<OrgChartDto> orgChartDtoList,int count,List<Map<String,String>> employeeList){
		int parent=count;
		List<Map<String,String>> children=	getChildren(delegator, partyId);
		
		List<Map<String,String>> positions=getPositions(delegator, partyId);
			
			for(Map<String,String> child:children){
				count++;
				OrgChartDto orgChartDto=new OrgChartDto();
				orgChartDto.setTitle(child.get("name"));
				orgChartDto.setGroupTitleColor("#99EE11");
				orgChartDto.setItemTitleColor("#B800E6");
				orgChartDto.setPartyId(child.get("id"));
				orgChartDto.setId(count);
				orgChartDto.setParent(parent);
				orgChartDtoList.add(orgChartDto);
				count=populateChart(delegator,child.get("id"),orgChartDtoList,count,employeeList);
			}

			/*if(positions!=null){
				for(Map<String,String> position:positions){
					count++;
					OrgChartDto orgChartDto=new OrgChartDto();
					orgChartDto.setTitle(position.get("description"));
					orgChartDto.setGroupTitleColor("#99EE11");
					orgChartDto.setItemTitleColor("#4169E1");
					orgChartDto.setId(count);
					orgChartDto.setParent(parent);
					orgChartDtoList.add(orgChartDto);
					
				}
			}*/
		Map<String, String> empMap=FastMap.newInstance();
		empMap.put("id", partyId);
		employeeList.add(empMap);
			
		return count;	
	}
	/*
	 * This method brings all valid and invalid employee(terminated employee)
	 */
	public static int getTotalEmployee(Delegator delegator,int totalEmployee,String partyId){
		List<Map<String,String>> children=	getChildren(delegator, partyId);
		
		List<Map<String,String>> positions=getPositions(delegator, partyId);
		
		for(Map<String,String> child:children){
	
			totalEmployee=getTotalEmployee(delegator,totalEmployee,child.get("id"));
		}
		
		if(positions!=null){
			for(Map<String,String> position:positions){
				totalEmployee=totalEmployee+Integer.valueOf(position.get("totalEmployee"));
			}
		}
		return totalEmployee;
		
	}
	public static List<OrgChartDto> margeChildName(Delegator delegator,List<Map<String,String>> empList,List<OrgChartDto> orgChartList){
		
		for(Map<String,String> emp:empList){
			String id=emp.get("id");
		
			for(OrgChartDto dto:orgChartList){
				if(dto.getPartyId()!=null&&dto.getPartyId().equalsIgnoreCase(id)){
					String title=dto.getTitle();
					int totalEmployee=0;
					totalEmployee=getTotalEmployee(delegator, totalEmployee, dto.getPartyId());
					dto.setTitle(title+"("+totalEmployee+")");
				}
			}
		}
		return orgChartList;
	}

	public static List<Map<String,String>> getChildren(Delegator delegator, String partyId){
		
		List<Map<String,String>> childrenPartyIds=FastList.newInstance();
		List<GenericValue> children=FastList.newInstance();
		   	
		try {
				List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
		        children = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		        if(children!=null&&children.size()>0){
		        	for(GenericValue child:children){
		        		String childrenPartyId=child.getString("partyIdTo");
		        		String groupName= getGroupName(delegator,childrenPartyId);
		        		Map<String,String> childdto=FastMap.newInstance();
		        		childdto.put("id", childrenPartyId);
		        		childdto.put("name", groupName);
		        		childrenPartyIds.add(childdto);
		        	}
		        	
		        }else{
		        	return childrenPartyIds;	
		        }
		
			}catch (GenericEntityException e) {
				return childrenPartyIds;
			}
		
		return childrenPartyIds;
	}
	
	public static List<Map<String,String>> getPositions(Delegator delegator, String partyId){
		List<Map<String,String>> positionids=FastList.newInstance();
		List<GenericValue> positions=FastList.newInstance();
		try {
		
			positions= delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
		if(positions!=null&&positions.size()>0){
			for(GenericValue position:positions){
				String positionId=position.getString("emplPositionId");
				int total=	getTotalEmployeeByPosition(delegator,positionId);
				String description=position.getString("description");
				Map<String,String> positionMap=FastMap.newInstance();
				positionMap.put("description", description+"("+String.valueOf(total)+")");
				positionMap.put("totalEmployee", String.valueOf(total));
				positionMap.put("positionId", positionId);
				positionids.add(positionMap);
			}			
		}else{
			return positionids;
		}
		}catch (GenericEntityException e) {
			return positionids;
		}
		return positionids;
	}
	public static List<Map<String,String>> getPositionsWithoutNumberOfEmployee(Delegator delegator, String partyId){
		List<Map<String,String>> positionids=FastList.newInstance();
		List<GenericValue> positions=FastList.newInstance();
		try {
		
			positions= delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
		if(positions!=null&&positions.size()>0){
			for(GenericValue position:positions){
				String positionId=position.getString("emplPositionId");
				int total=	getTotalEmployeeByPosition(delegator,positionId);
				String description=position.getString("description");
				Map<String,String> positionMap=FastMap.newInstance();
				positionMap.put("description", description);
				positionMap.put("totalEmployee", String.valueOf(total));
				positionMap.put("positionId", positionId);
				positionids.add(positionMap);
			}
			
		}else{
			return positionids;
		}
		}catch (GenericEntityException e) {
			return positionids;
		}
		return positionids;
	}
	
	public static int getTotalEmployeeByPosition(Delegator delegator, String positionId){
    	
		try {
			List<GenericValue> emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), null, null, null, false);
			return emplPositionFulfillmentList.size();

		} catch (GenericEntityException e) {
			return 0;
		}
    }
	
	public static List<Map<String,String>> getTotalEmployeeAndBudgetOfChild(Delegator delegator,String partyId) {
		List<Map<String,String>> getTotalEmployeeOfChild=FastList.newInstance(); 
		List<Map<String,String>> getChildren=getChildren(delegator, partyId);
		for(Map<String,String> children:getChildren){
			Map<String,String> totalEmployeeInMap=FastMap.newInstance();
			int totalEmployee=0;
			int budgetEmployee=0;
			totalEmployee=getTotalEmployee(delegator, totalEmployee, children.get("id"));
			budgetEmployee=getBudgetEmployee(delegator, budgetEmployee, children.get("id"));
			totalEmployeeInMap.put("name", children.get("name"));
			totalEmployeeInMap.put("totalEmployee", String.valueOf(totalEmployee));
			totalEmployeeInMap.put("budgetEmployee", String.valueOf(budgetEmployee));
			totalEmployeeInMap.put("needOrOver", String.valueOf(budgetEmployee-totalEmployee));
			getTotalEmployeeOfChild.add(totalEmployeeInMap);
		}
		return getTotalEmployeeOfChild;
	}
	
	
	public static int getBudgetEmployee(Delegator delegator,int budgetEmployee,String partyId){
		List<Map<String,String>> children=	getChildren(delegator, partyId);
		
		List<Map<String,String>> positions=getPositionsWithBudget(delegator, partyId);
		
		for(Map<String,String> child:children){
	
			budgetEmployee=getBudgetEmployee(delegator,budgetEmployee,child.get("id"));
		}
		
		if(positions!=null){
			for(Map<String,String> position:positions){
				budgetEmployee=budgetEmployee+Integer.valueOf(position.get("totalEmployee"));
			}
		}
		return budgetEmployee;
		
	}
	
	
	public static List<Map<String,String>> getPositionsWithBudget(Delegator delegator, String partyId){
		List<Map<String,String>> positionids=FastList.newInstance();
		List<GenericValue> positions=FastList.newInstance();
		try {
		
			positions= delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
		if(positions!=null&&positions.size()>0){
			for(GenericValue position:positions){
				String positionid=position.getString("emplPositionId");
				int total=	getTotalBudgetEmployeeByPosition(delegator,positionid);
				String description=position.getString("description");
				Map<String,String> positionMap=FastMap.newInstance();
				positionMap.put("description", description+"("+String.valueOf(total)+")");
				positionMap.put("totalEmployee", String.valueOf(total));
				positionids.add(positionMap);
			}
			
		}else{
			return positionids;
		}
		}catch (GenericEntityException e) {
			return positionids;
		}
		return positionids;
	}
	
	public static int getTotalBudgetEmployeeByPosition(Delegator delegator, String positionId){
    	
		try {
			List<GenericValue> employeePositions = delegator.findList("EmplPosition", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), null, null, null, false);
			for(GenericValue employeePosition:employeePositions){
				List<GenericValue> budgetItems = delegator.findList("BudgetItem", EntityCondition.makeCondition("budgetId", EntityOperator.EQUALS, employeePosition.get("budgetId")), null, null, null, false);
				for(GenericValue budgetItem:budgetItems){
					if(budgetItem.get("amount")!=null){
						try{
							BigDecimal bigValue=(BigDecimal)budgetItem.get("amount");
							return bigValue.intValue();
						}catch(Exception ex){
							logger.debug("Exception to parse in getTotalBudgetEmployeeByPosition---->"+ex);
							return 0;
						}
					}
					return 0;
				}
			}

		} catch (GenericEntityException e) {
			logger.debug("Exception to getTotalBudgetEmployeeByPosition---->"+e);
			return 0;
		}
		return 0;
    }
	
	public static int getTotalBudgetByOrganizationLevel(Delegator delegator,String OrganizationLevelPartyId,int totalBudget){
		List<Map<String,String>> getPosition=getPositions(delegator,OrganizationLevelPartyId);
		for(Map<String,String> positionMap:getPosition){
			totalBudget+=getTotalBudgetEmployeeByPosition(delegator,(String)positionMap.get("positionId"));
		}

		List<Map<String,String>> children=	getChildren(delegator, OrganizationLevelPartyId);
		for(Map<String,String> child:children){
			totalBudget=getTotalBudgetByOrganizationLevel(delegator, (String)child.get("id"), totalBudget);
		}
		return totalBudget;
	}
	
	public static int getTotalRecruitedByOrganizationLevel(Delegator delegator,String OrganizationLevelPartyId,int totalRecruited){
		List<Map<String,String>> getPosition=getPositions(delegator,OrganizationLevelPartyId);
		for(Map<String,String> positionMap:getPosition){
			totalRecruited+=getTotalEmployeeByPosition(delegator,(String)positionMap.get("positionId"));
		}

		List<Map<String,String>> children=	getChildren(delegator, OrganizationLevelPartyId);
		for(Map<String,String> child:children){
			totalRecruited=getTotalRecruitedByOrganizationLevel(delegator, (String)child.get("id"), totalRecruited);
		}
		return totalRecruited;
	}
	
	public static int getNeedOrOverByOrganizationLevel(Delegator delegator,String OrganizationLevelPartyId,int needOrOver){
		int totalBudget=0;
		int totalRecruited=0;
		totalBudget=getTotalBudgetByOrganizationLevel(delegator, OrganizationLevelPartyId, totalBudget);
		totalRecruited=getTotalRecruitedByOrganizationLevel(delegator, OrganizationLevelPartyId, totalRecruited);
		needOrOver=totalBudget-totalRecruited;
		return needOrOver;
	}
	
	public static List<Map<String,String>> getPositionAndBudgetByOrgLevel(Delegator delegator,String OrganizationLevelPartyId,List<Map<String,String>> listEmplBudget){
		List<Map<String,String>> getPosition=getPositionsWithoutNumberOfEmployee(delegator,OrganizationLevelPartyId);
		Integer positionBudget=0;				
		for(Map<String,String> positionMap:getPosition){
			Map<String,String> positionBudgetMap=FastMap.newInstance();
			positionBudget=getTotalBudgetEmployeeByPosition(delegator,(String)positionMap.get("positionId"));
			positionBudgetMap.put("positionId", (String)positionMap.get("positionId"));
			positionBudgetMap.put("description", (String)positionMap.get("description"));
			positionBudgetMap.put("totalEmployee", (String)positionMap.get("totalEmployee"));
			positionBudgetMap.put("budget", positionBudget.toString());
			listEmplBudget.add(positionBudgetMap);
		}

		List<Map<String,String>> children=	getChildren(delegator, OrganizationLevelPartyId);
		for(Map<String,String> child:children){
			getPositionAndBudgetByOrgLevel(delegator, (String)child.get("id"), listEmplBudget);
		}
		return listEmplBudget;
	}
}

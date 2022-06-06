import org.ofbiz.entity.util.EntityFindOptions;		
import java.util.List;		
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;		 
import org.ofbiz.base.util.UtilMisc;	
import org.apache.log4j.Logger;	
	/*	employeeId=0;
		String employeeIdInString=null;
		 EntityFindOptions opts = new EntityFindOptions();
		    opts.setMaxRows(1);
		    opts.setFetchSize(1);
		    List<String> orderBy = UtilMisc.toList("-userLoginId");
		    
		    List<GenericValue> employeeList = null;
		    
		 try {
			 employeeList = delegator.findList("UserLogin", null, null, null, null, false);
		} catch (Exception e) {			
			e.printStackTrace();
		}

		 for(GenericValue lastEmployeeEntry:employeeList){
		try {
			//lastEmployeeEntry = EntityUtil.getFirst(employeeList);
			if(lastEmployeeEntry!=null){
				tempEmployeeId=Long.parseLong((String)lastEmployeeEntry.get("userLoginId"));
				if(tempEmployeeId>employeeId){
					employeeId=tempEmployeeId;
					employeeIdInString=	String.valueOf(employeeId+1);
				}				
			}
		} catch (Exception e) {		
				employeeList.remove(lastEmployeeEntry);				
			}		
		}*/


		 Map<String, String> sequenceValueItemCondition = UtilMisc.toMap("seqName", "UserLogin");
		 GenericValue sequenceValueItem = delegator.findByPrimaryKey("SequenceValueItem",sequenceValueItemCondition); 
		 long userLoginInSequenceValueItem=0; 
		 try{
			 userLoginInSequenceValueItem=(Long)sequenceValueItem.get("seqId");
			 userLoginInSequenceValueItem++;
		 }catch(Exception ex){
			 logger.debug("userLoginInSequenceValueItem can not process due to--------->"+ex);
		 }		
		
		
		
		context.employeeId=String.valueOf(userLoginInSequenceValueItem);
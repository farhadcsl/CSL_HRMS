import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

Logger logger=Logger.getLogger("Groovy");
List<Map<String,String>> retdesignationPositionList=FastList.newInstance();
DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId,"partyContentTypeId","INTERNAL"));
if(partyContentList.size()>0){
String contentId=partyContentList[partyContentList.size()-1].contentId;
GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId",contentId),false);
GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId",content.get("dataResourceId")),false);
context.proPicImageUrl=dataResource.get("objectInfo");
}
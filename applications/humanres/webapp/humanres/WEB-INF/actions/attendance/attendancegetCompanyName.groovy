 import javolution.util.FastMap
import javolution.util.FastList.*

import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.product.catalog.*
import org.ofbiz.product.category.*


company = delegator.findByPrimaryKey("PartyGroup", [partyId : orgId.partyId]);
context.companyName = company.groupName;





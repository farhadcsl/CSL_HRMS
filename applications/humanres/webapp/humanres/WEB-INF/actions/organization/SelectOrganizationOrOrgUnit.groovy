import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;

import freemarker.core.ReturnInstruction.Return;

import java.awt.GraphicsConfiguration.DefaultBufferCapabilities;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.List;

unit=partyUnit;
party=partyId;
if(unit!="0") {
	context.partyId=unit;
}else{
	context.partyId=party;
}

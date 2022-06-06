import java.security.KeyPairGenerator.Delegate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanres.HumanResEvents;

import org.ofbiz.humanres.recruitment.RecruitmentServices;
import org.ofbiz.humanres.attendance.AttendanceUtils;


String partyId = partyId;
String confimDate = RecruitmentServices.getConfirmationDate(delegator,partyId,partyIdTo,emplPositionId);
confimDate = AttendanceUtils.getFormatedDate(confimDate,"dd/MM/yyyy");
context.confimDate = confimDate;
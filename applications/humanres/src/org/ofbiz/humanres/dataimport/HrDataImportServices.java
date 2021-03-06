package org.ofbiz.humanres.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.fortuna.ical4j.model.DateTime;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HrDataImportServices {
	public static final String module = HrDataImportServices.class.getName();

	public static List<List<String>> getDataFromExcelFile2003(File uploadedFile) {
		List<List<String>> sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			/*
			 * Create a FileInputStream that will be use to read the excel file.
			 */
			fis = new FileInputStream(uploadedFile);
			/* Create an excel workbook from the file system. */
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			/* Get the first sheet on the workbook. */
			// HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				HSSFSheet sheet = workbook.getSheetAt(i);
				Iterator rows = sheet.rowIterator();
				while (rows.hasNext()) {
					HSSFRow row = (HSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					List<String> data = new ArrayList();
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						String celDataStr = filterDataAndFormatToString(cell);
						data.add(celDataStr);
					}
					sheetData.add(data);
				}
			}
			delAllFiles(uploadedFile);
		} catch (IOException e) {
			delAllFiles(uploadedFile);
			e.printStackTrace();
		} finally {
			delAllFiles(uploadedFile);
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sheetData;

	}

	public static List<List<String>> getDataFromExcelFile2007(File uploadedFile) {
		List<List<String>> sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			/*
			 * Create a FileInputStream that will be use to read the excel file.
			 */
			fis = new FileInputStream(uploadedFile);
			/* Create an excel workbook from the file system. */
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			/* Get the first sheet on the workbook. */

			// XSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);

				Iterator rows = sheet.rowIterator();
				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					Iterator cells = row.cellIterator();

					List<String> data = new ArrayList();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						String celDataStr = filterDataAndFormatToString(cell);
						data.add(celDataStr);
					}
					sheetData.add(data);
				}

			}

			delAllFiles(uploadedFile);
		} catch (IOException e) {
			delAllFiles(uploadedFile);
			e.printStackTrace();
		} finally {
			delAllFiles(uploadedFile);
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sheetData;
	}

	public static String filterDataAndFormatToString(Object objDataForFilter) {
		String cellDataStr = "";
		Object objCellData = objDataForFilter;

		if (objCellData != null) {
			if (objCellData instanceof BigDecimal) {
				String dataDouble = ((BigDecimal) objCellData).toString();
				cellDataStr = dataDouble;
			} else if (objCellData instanceof Double) {
				String dataDouble = ((Double) objCellData).toString();
				cellDataStr = dataDouble;
			} else if (objCellData instanceof Integer) {
				String dataInteger = ((Integer) objCellData).toString();
				cellDataStr = dataInteger;
			} else if (objCellData instanceof BigInteger) {
				String dataInteger = ((BigInteger) objCellData).toString();
				cellDataStr = dataInteger;
			} else {
				cellDataStr = objCellData.toString();
			}
		} else
			cellDataStr = "  ";

		return cellDataStr;
	}

	public static void delAllFiles(File uploadedFile) {
		String filePath = uploadedFile.getParent();
		String fileName = uploadedFile.getName();
		String fileExt = FilenameUtils.getExtension(fileName);
		new HrDataImportServices().deleteFile(filePath, "." + fileExt);
	}

	public void deleteFile(String folder, String ext) {

		GenericExtFilter filter = new GenericExtFilter(ext);
		File dir = new File(folder);

		// list out all the file name with .txt extension
		String[] list = dir.list(filter);

		if (list.length == 0)
			return;

		File fileDelete;

		for (String file : list) {
			String temp = new StringBuffer(folder).append(File.separator).append(file).toString();
			fileDelete = new File(temp);
			boolean isdeleted = fileDelete.delete();
			System.out.println("file : " + temp + " is deleted : " + isdeleted);
		}
	}

	public class GenericExtFilter implements FilenameFilter {
		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}

	/*
	 * public static Date convertDateOneFormatToAnother(String dateinString) {
	 * final String OLD_FORMAT = "dd-MMM-yyyy"; final String NEW_FORMAT =
	 * "yyyy-MM-dd";
	 * 
	 * String newDateString;
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT); Date dateOld =
	 * null; try { dateOld = sdf.parse(dateinString); } catch (ParseException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * sdf.applyPattern(NEW_FORMAT); newDateString = sdf.format(dateOld); Date
	 * shipmentDate = java.sql.Date.valueOf(newDateString.toString()); return
	 * shipmentDate; }
	 */

	/*
	 * public static Date convertDateOneFormatToAnother(String dateinString,
	 * String dateOldStr, String dateNewStr) { final String OLD_FORMAT =
	 * dateOldStr; final String NEW_FORMAT = dateNewStr;
	 * 
	 * String newDateString; Date newDateFormat = null; if
	 * (!OLD_FORMAT.isEmpty() && !NEW_FORMAT.isEmpty()) { SimpleDateFormat sdf =
	 * new SimpleDateFormat(OLD_FORMAT); Date dateOld = null; try { dateOld =
	 * sdf.parse(dateinString); } catch (ParseException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * sdf.applyPattern(NEW_FORMAT); newDateString = sdf.format(dateOld);
	 * newDateFormat = java.sql.Date.valueOf(newDateString.toString()); }
	 * 
	 * if (!OLD_FORMAT.isEmpty() || !NEW_FORMAT.isEmpty()) { SimpleDateFormat
	 * sdf = new SimpleDateFormat(OLD_FORMAT); Date dateOld = null; try {
	 * dateOld = sdf.parse(dateinString); } catch (ParseException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * sdf.applyPattern(NEW_FORMAT); newDateString = sdf.format(dateOld);
	 * newDateFormat = java.sql.Date.valueOf(newDateString.toString()); }
	 * 
	 * return newDateFormat; }
	 */

	public static BigDecimal getStringToBigDecimal(String strData) {
		BigDecimal strToBigDecimal = new BigDecimal(0);
		if (UtilValidate.isNotEmpty(strData)) {
			strToBigDecimal = new BigDecimal(strData);
		}
		return strToBigDecimal;
	}

	public static Map<String, Object> setEmployeeDataImportReq(List<String> objStr) {
		Map<String, Object> dataMap = FastMap.newInstance();

		/*
		 * dataMap.put("nationalid", objStr.get(0)); dataMap.put("bloodgroup",
		 * objStr.get(1)); dataMap.put("address", objStr.get(2));
		 * dataMap.put("firstName", objStr.get(3)); dataMap.put("middleName",
		 * objStr.get(4)); dataMap.put("lastName", objStr.get(5));
		 * dataMap.put("gender", objStr.get(6)); dataMap.put("fatherName",
		 * objStr.get(7)); dataMap.put("mothersMaidenName", objStr.get(8));
		 * dataMap.put("employmentType", objStr.get(9));
		 */

		dataMap.put("firstName", objStr.get(0));
		dataMap.put("middleName", objStr.get(1));
		dataMap.put("lastName", objStr.get(2));
		dataMap.put("emplPositionId", objStr.get(3));
		dataMap.put("employmentType", objStr.get(4));
		dataMap.put("userLoginId", objStr.get(5));
		dataMap.put("joiningDate", objStr.get(6));
		dataMap.put("emailAddress", objStr.get(7));
		dataMap.put("bloodGroup", objStr.get(8));
		dataMap.put("phoneNumber", objStr.get(9));
		dataMap.put("presentAddress", objStr.get(10));
		dataMap.put("presentCity", objStr.get(11));
		dataMap.put("permanentAddress", objStr.get(12));
		dataMap.put("permanentCity", objStr.get(13));
		dataMap.put("gender", objStr.get(14));
		dataMap.put("fatherName", objStr.get(15));
		dataMap.put("motherName", objStr.get(16));

		return dataMap;
	}

	public static void createOrUpdateUser(Delegator delegator, Map<String, Object> mapData) {

		/* Party */
		/*
		 * String nationalid=mapData.get("nationalid").toString(); String
		 * bloodgroup=mapData.get("bloodgroup").toString(); String
		 * address=mapData.get("address").toString();
		 */
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		String joiningDateString = mapData.get("joiningDate").toString();
		String bloodGroup = mapData.get("bloodGroup").toString();
		String emailAddress = mapData.get("emailAddress").toString();
		String phoneNumber = mapData.get("phoneNumber").toString();
		String presentAddress = mapData.get("presentAddress").toString();
		String presentCity = mapData.get("presentCity").toString();
		String permanentAddress = mapData.get("permanentAddress").toString();
		String permanentCity = mapData.get("permanentCity").toString();

		String partyId = delegator.getNextSeqId("Party");

		Map<String, Object> partyIn = FastMap.newInstance();
		partyIn.put("partyId", partyId);
		partyIn.put("partyTypeId", "PERSON");
		/* partyIn.put("nationalid", nationalid); */
		partyIn.put("bloodgroup", bloodGroup);
		partyIn.put("statusId", "EMPL_POS_ACTIVE");
		/* partyIn.put("address", address); */
		partyIn.put("createdDate", currentDatetime);
		partyIn.put("createdByUserLogin", "admin");
		try {
			GenericValue partySetup = delegator.makeValue("Party", UtilMisc.toMap(partyIn));
			partySetup.create();
		} catch (Exception e) {
		}

		/* Person */
		String firstName = mapData.get("firstName").toString();
		String middleName = mapData.get("middleName").toString();
		String lastName = mapData.get("lastName").toString();
		String gender = mapData.get("gender").toString();
		String fatherName = mapData.get("fatherName").toString();
		String motherName = mapData.get("motherName").toString();

		Map<String, Object> personIn = FastMap.newInstance();
		personIn.put("partyId", partyId);
		personIn.put("firstName", firstName);
		personIn.put("middleName", middleName);
		personIn.put("lastName", lastName);
		personIn.put("gender", gender);
		personIn.put("fatherName", fatherName);
		personIn.put("mothersMaidenName", motherName);

		try {
			GenericValue personSetup = delegator.makeValue("Person", UtilMisc.toMap(personIn));
			personSetup.create();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* EmplPositionFulfillment */

		String emplPositionIdString = mapData.get("emplPositionId").toString();
		String str = new String(emplPositionIdString);
		String emplPositionId = null;
		if (str.endsWith(".0")) {
			emplPositionId = str.substring(0, str.length() - 2);
		} else {
			emplPositionId = str;
		}

		Timestamp joiningDate = currentDatetime; // declare timestamp
		try {
			Date d = new Date(joiningDateString); // Intialize date with the
													// string date
			if (d != null) { // simple null check
				joiningDate = new java.sql.Timestamp(d.getTime()); // convert
																	// gettime
																	// from date
																	// and
																	// assign it
																	// to your
																	// timestamp.
			}
		} catch (Exception e) {
		}
		Map<String, Object> emplpositionFulfillmentIn = FastMap.newInstance();
		emplpositionFulfillmentIn.put("emplPositionId", emplPositionId);
		emplpositionFulfillmentIn.put("partyId", partyId);
		emplpositionFulfillmentIn.put("fromDate", joiningDate);

		try {
			GenericValue emplpositionFulfillmentSetup = delegator.makeValue("EmplPositionFulfillment",
					UtilMisc.toMap(emplpositionFulfillmentIn));
			emplpositionFulfillmentSetup.create();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* PartyRole */
		Map<String, Object> partyRoleIn = FastMap.newInstance();
		partyRoleIn.put("partyId", partyId);
		partyRoleIn.put("roleTypeId", "EMPLOYEE");
		try {
			GenericValue partyRoleInSetup = delegator.makeValue("PartyRole", UtilMisc.toMap(partyRoleIn));
			partyRoleInSetup.create();
		} catch (Exception e) {
		}

		/* Employment */
		String employmentType = mapData.get("employmentType").toString();
		Map<String, Object> employmentIn = FastMap.newInstance();
		employmentIn.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		employmentIn.put("roleTypeIdTo", "EMPLOYEE");
		employmentIn.put("partyIdFrom", "CSL");
		employmentIn.put("partyIdTo", partyId);
		employmentIn.put("fromDate", joiningDate);
		employmentIn.put("employmentType", employmentType);
		try {
			GenericValue employmentSetup = delegator.makeValue("Employment", UtilMisc.toMap(employmentIn));
			employmentSetup.create();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* UserLogin */
		/* String userLoginId = delegator.getNextSeqId("UserLogin"); */
		String userLoginIdStr = mapData.get("userLoginId").toString();
		String strUl = new String(userLoginIdStr);
		String userLoginId = null;
		if (strUl.endsWith(".0")) {
			userLoginId = strUl.substring(0, strUl.length() - 2);
		} else {
			userLoginId = strUl;
		}
		Map<String, Object> userloginIn = FastMap.newInstance();
		userloginIn.put("userLoginId", userLoginId);
		userloginIn.put("currentPassword", "{SHA}85194893f9ca54d2fbe7de5c6356f6aaf3769d0b");
		userloginIn.put("partyId", partyId);

		try {
			GenericValue userloginSetup = delegator.makeValue("UserLogin", UtilMisc.toMap(userloginIn));
			userloginSetup.create();
		} catch (Exception e) {
		}

		// Enter data into UserLoginSecurityGroup
		GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");

		userLoginSecurityGroup.set("userLoginId", userLoginId);
		userLoginSecurityGroup.set("groupId", "HUMANRES_EMPLOYEE");
		userLoginSecurityGroup.set("fromDate", currentDatetime);
		try {
			GenericValue UserLoginSecurityGroupSetup = delegator.makeValue("UserLoginSecurityGroup",
					UtilMisc.toMap(userLoginSecurityGroup));
			UserLoginSecurityGroupSetup.create();
		} catch (Exception e) {
		}

		// Enter data into UserPreference
		GenericValue userPreference = delegator.makeValue("UserPreference");

		userPreference.set("userLoginId", userLoginId);
		userPreference.set("userPrefTypeId", "ORGANIZATION_PARTY");
		userPreference.set("userPrefGroupTypeId", "GLOBAL_PREFERENCES");
		userPreference.set("userPrefValue", "CSL");
		try {
			GenericValue UserPreferenceSetup = delegator.makeValue("UserPreference", UtilMisc.toMap(userPreference));
			UserPreferenceSetup.create();
		} catch (Exception e) {
		}

		// ----------------Add Phone Number-------------------------------
		try {
			String contactMechId = delegator.getNextSeqId("ContactMech");
			Timestamp formDate = UtilDateTime.nowTimestamp();
			Map<String, Object> PostalContactMechIn = FastMap.newInstance();

			PostalContactMechIn.put("contactMechId", contactMechId);
			PostalContactMechIn.put("contactMechTypeId", "TELECOM_NUMBER");
			try {
				GenericValue createPostalContactValue = delegator.makeValue("ContactMech",
						UtilMisc.toMap(PostalContactMechIn));
				createPostalContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();

			PostalPartyContactMechIn.put("partyId", partyId);
			PostalPartyContactMechIn.put("contactMechId", contactMechId);
			PostalPartyContactMechIn.put("fromDate", formDate);
			try {
				GenericValue createPostalPartyContactValue = delegator.makeValue("PartyContactMech",
						UtilMisc.toMap(PostalPartyContactMechIn));
				createPostalPartyContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalAddressIn = FastMap.newInstance();
			PostalAddressIn.put("contactMechId", contactMechId);
			PostalAddressIn.put("countryCode", "880");
			PostalAddressIn.put("contactNumber", phoneNumber);

			try {
				GenericValue createPostalAddresssValue = delegator.makeValue("TelecomNumber",
						UtilMisc.toMap(PostalAddressIn));
				createPostalAddresssValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
		} catch (Exception e) {
		}

		// ----------------------------------------------------------------------------//

		// ----------------Add Email Address-------------------------------
		try {
			String contactMechId = delegator.getNextSeqId("ContactMech");
			Timestamp formDate = UtilDateTime.nowTimestamp();
			Map<String, Object> PostalContactMechIn = FastMap.newInstance();

			PostalContactMechIn.put("contactMechId", contactMechId);
			PostalContactMechIn.put("contactMechTypeId", "EMAIL_ADDRESS");
			PostalContactMechIn.put("infoString", emailAddress);
			try {
				GenericValue createPostalContactValue = delegator.makeValue("ContactMech",
						UtilMisc.toMap(PostalContactMechIn));
				createPostalContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();

			PostalPartyContactMechIn.put("partyId", partyId);
			PostalPartyContactMechIn.put("contactMechId", contactMechId);
			PostalPartyContactMechIn.put("fromDate", formDate);
			try {
				GenericValue createPostalPartyContactValue = delegator.makeValue("PartyContactMech",
						UtilMisc.toMap(PostalPartyContactMechIn));
				createPostalPartyContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
		} catch (Exception e) {
		}
		// ----------------------------------------------------------------------------//

		/// ----------------Add
		/// POSTAL_ADDRESS---------------------------------------------
		try {

			String contactMechId = delegator.getNextSeqId("ContactMech");
			Timestamp formDate = UtilDateTime.nowTimestamp();
			Map<String, Object> PostalContactMechIn = FastMap.newInstance();

			PostalContactMechIn.put("contactMechId", contactMechId);
			PostalContactMechIn.put("contactMechTypeId", "POSTAL_ADDRESS");
			try {
				GenericValue createPostalContactValue = delegator.makeValue("ContactMech",
						UtilMisc.toMap(PostalContactMechIn));
				createPostalContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();

			PostalPartyContactMechIn.put("partyId", partyId);
			PostalPartyContactMechIn.put("contactMechId", contactMechId);
			PostalPartyContactMechIn.put("fromDate", formDate);
			try {
				GenericValue createPostalPartyContactValue = delegator.makeValue("PartyContactMech",
						UtilMisc.toMap(PostalPartyContactMechIn));
				createPostalPartyContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalAddressIn = FastMap.newInstance();
			PostalAddressIn.put("contactMechId", contactMechId);
			PostalAddressIn.put("address1", presentAddress);
			PostalAddressIn.put("city", presentCity);
			PostalAddressIn.put("postalCode", "N/A");
			PostalAddressIn.put("countryGeoId", "BGD");

			try {
				GenericValue createPostalAddresssValue = delegator.makeValue("PostalAddress",
						UtilMisc.toMap(PostalAddressIn));
				createPostalAddresssValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
		}
		// ----------------------------------------------------------------------------//

		/// ----------------Add
		/// POSTAL_ADDRESS---------------------------------------------
		try {

			String contactMechId = delegator.getNextSeqId("ContactMech");
			Timestamp formDate = UtilDateTime.nowTimestamp();
			Map<String, Object> PostalContactMechIn = FastMap.newInstance();

			PostalContactMechIn.put("contactMechId", contactMechId);
			PostalContactMechIn.put("contactMechTypeId", "MAILING_ADDRESS");
			try {
				GenericValue createPostalContactValue = delegator.makeValue("ContactMech",
						UtilMisc.toMap(PostalContactMechIn));
				createPostalContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();

			PostalPartyContactMechIn.put("partyId", partyId);
			PostalPartyContactMechIn.put("contactMechId", contactMechId);
			PostalPartyContactMechIn.put("fromDate", formDate);
			try {
				GenericValue createPostalPartyContactValue = delegator.makeValue("PartyContactMech",
						UtilMisc.toMap(PostalPartyContactMechIn));
				createPostalPartyContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalAddressIn = FastMap.newInstance();
			PostalAddressIn.put("contactMechId", contactMechId);
			PostalAddressIn.put("address1", presentAddress);
			PostalAddressIn.put("city", presentCity);
			PostalAddressIn.put("postalCode", "N/A");
			PostalAddressIn.put("countryGeoId", "BGD");

			try {
				GenericValue createPostalAddresssValue = delegator.makeValue("PostalAddress",
						UtilMisc.toMap(PostalAddressIn));
				createPostalAddresssValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
		}
		// ----------------------------------------------------------------------------//

		/// ----------------Add
		/// PERMANENT_ADDRESS---------------------------------------------
		try {

			String contactMechId = delegator.getNextSeqId("ContactMech");
			Timestamp formDate = UtilDateTime.nowTimestamp();
			Map<String, Object> PostalContactMechIn = FastMap.newInstance();

			PostalContactMechIn.put("contactMechId", contactMechId);
			PostalContactMechIn.put("contactMechTypeId", "PERMANENT_ADDRESS");
			try {
				GenericValue createPostalContactValue = delegator.makeValue("ContactMech",
						UtilMisc.toMap(PostalContactMechIn));
				createPostalContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();

			PostalPartyContactMechIn.put("partyId", partyId);
			PostalPartyContactMechIn.put("contactMechId", contactMechId);
			PostalPartyContactMechIn.put("fromDate", formDate);
			try {
				GenericValue createPostalPartyContactValue = delegator.makeValue("PartyContactMech",
						UtilMisc.toMap(PostalPartyContactMechIn));
				createPostalPartyContactValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}
			Map<String, Object> PostalAddressIn = FastMap.newInstance();
			PostalAddressIn.put("contactMechId", contactMechId);
			PostalAddressIn.put("address1", permanentAddress);
			PostalAddressIn.put("city", permanentCity);
			PostalAddressIn.put("postalCode", "N/A");
			PostalAddressIn.put("countryGeoId", "BGD");

			try {
				GenericValue createPostalAddresssValue = delegator.makeValue("PostalAddress",
						UtilMisc.toMap(PostalAddressIn));
				createPostalAddresssValue.create();
			} catch (Exception e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
		}
		// ----------------------------------------------------------------------------//

	}

	public static Map<String, Object> setAttendanceDataImportReq(List<String> objStr) {

		Map<String, Object> dataMap = FastMap.newInstance();
		dataMap.put("Date", objStr.get(0));
		dataMap.put("ID", objStr.get(1));
		dataMap.put("First-In Time", objStr.get(2));
		dataMap.put("Last-Out Time", objStr.get(3));
		/* dataMap.put("Result", objStr.get(6)); */
		return dataMap;
	}

	public static void addDataIntoDailyAttendance(Delegator delegator, Map<String, Object> mapData) {

		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		String attendanceDate = mapData.get("Date").toString();
		String employeeId = mapData.get("ID").toString();
		if (employeeId.endsWith(".0")) {
			employeeId = employeeId.substring(0, employeeId.length() - 2);
		}
		String firstInTime = mapData.get("First-In Time").toString();
		String lastOutTime = mapData.get("Last-Out Time").toString();
		/* String attendanceResult=mapData.get("Result").toString(); */
		String partyId = null;
		Timestamp dateOfAttendence = null;
		try {
			partyId = findPartyIdByEmployeeId(delegator, employeeId);
			try {
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				Date date = formatter.parse(attendanceDate);
				dateOfAttendence = new Timestamp(date.getTime());

			} catch (ParseException e) {
			}
		} catch (Exception e) {
		}
		
		HrDataImportServices hrDataImportServices = new HrDataImportServices();

		String attendanceStatus = hrDataImportServices.checkAttendanceLogic(firstInTime);
		
		/*String employeeName="";
		List<GenericValue> das=null;
		String employId="";
		String DailyAtt="";
		try {
			das=delegator.findByAnd("DailyAttendance", UtilMisc.toMap("partyId", partyId));
			if(das!=null){
				GenericValue da=EntityUtil.getFirst(das);
				if(da!=null){
					
						employId=da.getString("employeeId");
						DailyAtt=da.getString("DailyAttendance");
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
		if (null != partyId) {
			Map<String, Object> DailyAttendanceIn = FastMap.newInstance();
			DailyAttendanceIn.put("partyId", partyId);
			DailyAttendanceIn.put("dateOfAttendance", dateOfAttendence);
			DailyAttendanceIn.put("employeeId", employeeId);
			DailyAttendanceIn.put("firstInTime", firstInTime);
			DailyAttendanceIn.put("lastOutTime", lastOutTime);
			DailyAttendanceIn.put("attendanceResult", attendanceStatus);
			DailyAttendanceIn.put("attendanceStatus", attendanceStatus);
			DailyAttendanceIn.put("processStatus", "Data Loaded");
			DailyAttendanceIn.put("dateAdded", currentDatetime);
			try {
				GenericValue DailyAttendanceInSetup = delegator.makeValue("DailyAttendance",
						UtilMisc.toMap(DailyAttendanceIn));
				DailyAttendanceInSetup.create();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String findPartyIdByEmployeeId(Delegator delegator, String employeeId) {

		String partyId = "";
		if (null != employeeId) {
			GenericValue userLogin = null;
			try {
				System.out.println("hello");
				userLogin = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", employeeId));
				// userLogin = delegator.getRelatedOne("UserLogin",
				// UtilMisc.toMap("userLoginId", employeeId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			if (null != userLogin.get("partyId")) {
				partyId = userLogin.get("partyId").toString();
			}
		}
		return partyId;
	}

	public static Map<String, Object> setGoalsAndKpiDataImportReq(List<String> objStr) {

		
		Map<String, Object> dataMap = FastMap.newInstance();
		dataMap.put("Employee Id", objStr.get(0));
		dataMap.put("Manager Id", objStr.get(1));
		dataMap.put("Department", objStr.get(2));
		dataMap.put("Goal", objStr.get(3));
		dataMap.put("KPI", objStr.get(4));
		dataMap.put("Unit", objStr.get(5));
		dataMap.put("Unit Remarks", objStr.get(6));
	
//		Modified by Parves and Farhad
		BigDecimal target = new BigDecimal(String.valueOf(objStr.get(7)));
	      String formatedTarget = String.format ("%.0f", target);
		dataMap.put("Target", String.valueOf(formatedTarget));
		dataMap.put("Param", objStr.get(8));
		dataMap.put("Year", objStr.get(9));
//		End Modification
		
		return dataMap;
	}

	public static void addDataIntoGoalsAndKpi(Delegator delegator, Map<String, Object> mapData) {

		// Timestamp currentDatetime = new
		// Timestamp(System.currentTimeMillis());
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(0);
		String goalsKpiId = delegator.getNextSeqId("EmployeeGoalsAndKpi");
		String employeeId = mapData.get("Employee Id").toString();
		if (employeeId.endsWith(".0")) {
			employeeId = employeeId.substring(0, employeeId.length() - 2);
		}
		String managerId = mapData.get("Manager Id").toString();
		if (managerId.endsWith(".0")) {
			managerId = managerId.substring(0, managerId.length() - 2);
		}
		String goal = mapData.get("Goal").toString();
		String kpi = mapData.get("KPI").toString();
		String unit = mapData.get("Unit").toString();
		String department = mapData.get("Department").toString();
		String unitRemarks = mapData.get("Unit Remarks").toString();
		String target = mapData.get("Target").toString();
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		String param = mapData.get("Param").toString();
		String year =  mapData.get("Year").toString();
		double yearDouble = Double.parseDouble(year);
		year = df.format(yearDouble);
		String partyId = null;
		try {
			partyId = findPartyIdByEmployeeId(delegator, employeeId);

		} catch (Exception e) {
		}

		if (null != partyId) {
			
			Map<String, Object> GoalsAndKpi = FastMap.newInstance();
			GoalsAndKpi.put("goalsKpiId", goalsKpiId);
			GoalsAndKpi.put("partyId", partyId);
			GoalsAndKpi.put("employeeId", employeeId);
			GoalsAndKpi.put("managerId", managerId);
			GoalsAndKpi.put("department", department);
			GoalsAndKpi.put("goal", goal);
			GoalsAndKpi.put("kpi", kpi);
			GoalsAndKpi.put("unit", unit);
			GoalsAndKpi.put("unitRemarks", unitRemarks);
			GoalsAndKpi.put("target", target);
			GoalsAndKpi.put("kpiDate", currentDatetime);
			
			GoalsAndKpi.put("param", param);
			GoalsAndKpi.put("year", year);

			try {
				GenericValue GoalsAndKpiSetup = delegator.makeValue("EmployeeGoalsAndKpi", UtilMisc.toMap(GoalsAndKpi));
				GoalsAndKpiSetup.create();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
public String checkAttendanceLogic(String inTime){
		
		String attendanceStatus = "Absent";
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			java.sql.Time timeValue = new java.sql.Time(format.parse(inTime).getTime());
			java.sql.Time zeroValue = new java.sql.Time(format.parse("0:00").getTime());
			java.sql.Time InValue = new java.sql.Time(format.parse("9:31").getTime());
			java.sql.Time lateValue = new java.sql.Time(format.parse("13:01").getTime());
			
			if(timeValue.after(zeroValue) &&  timeValue.before(InValue) ){
				attendanceStatus = "Present";
			}else if ((timeValue.after(InValue) &&  timeValue.before(lateValue)) || timeValue.equals(InValue)) {
				attendanceStatus = "Late";
			}else if (timeValue.after(lateValue) || timeValue.equals(lateValue)) {
				attendanceStatus ="Late";
			}else if(timeValue.equals(zeroValue)){
				attendanceStatus = "Absent";
			}
			
			
			} catch (Exception e) {
				// TODO: handle exception
			}
		return attendanceStatus;
		}

public static Map saveAttendaceInputJava(DispatchContext dctx, Map context) {
	Delegator delegator = dctx.getDelegator();
	String partyId = (String) context.get("partyId");
	String employeeId = (String) context.get("employeeId");
	String attendanceDate = context.get("dateOfAttendance").toString();
	String firstInTime = (String) context.get("firstInTime");
	String lastOutTime = (String) context.get("lastOutTime");
	String employeeComments = (String) context.get("employeeComments");
	Timestamp dateOfAttendence = null;
	Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());	
	GenericValue manualAttnUpdate = null;
	Map resultMap = null;
	GenericValue personInfo = null; 
	String approverId="";
	HrDataImportServices hrDataImportServices = new HrDataImportServices();

	String attendanceStatus = hrDataImportServices.checkAttendanceLogic(firstInTime);
	try {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = formatter.parse(attendanceDate);
		dateOfAttendence = new Timestamp(date.getTime());

	} catch (ParseException e) {
	}

	try {
		
		
		/*List<EntityCondition> findAttn = FastList.newInstance();*/
		personInfo = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));

    	if (null != personInfo.get("attendanceSuperiorId")) {
    		approverId=personInfo.get("attendanceSuperiorId").toString();
        }
		/*findAttn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		findAttn.add(EntityCondition.makeCondition("dateOfAttendance", EntityOperator.LIKE, dateOfAttendence));
		List<GenericValue> manualAttn = delegator.findList("DailyAttendance",
				EntityCondition.makeCondition(findAttn, EntityOperator.AND), null, null, null, false);*/
		
		/*if(manualAttn.isEmpty()){
			attendanceStatus = "Manual Input";
		}*/

		/*if(manualAttn!=null){}*/
			//manualAttnUpdate = manualAttn.get(0);
			Map<String, Object> DailyAttendanceIn = FastMap.newInstance();
			DailyAttendanceIn.put("partyId", partyId);
			DailyAttendanceIn.put("dateOfAttendance", dateOfAttendence);
			DailyAttendanceIn.put("employeeId", employeeId);
			DailyAttendanceIn.put("firstInTime", firstInTime);
			DailyAttendanceIn.put("lastOutTime", lastOutTime);
			DailyAttendanceIn.put("attendanceResult", "Manual Input");
			DailyAttendanceIn.put("attendanceStatus", attendanceStatus);
			DailyAttendanceIn.put("employeeComments", employeeComments);
			DailyAttendanceIn.put("processStatus", "Forward To Reporting");
			DailyAttendanceIn.put("dateAdded", currentDatetime);
			DailyAttendanceIn.put("supervisorPartyId", approverId);
			try {
				GenericValue DailyAttendanceInSetup = delegator.makeValue("DailyAttendance",
						UtilMisc.toMap(DailyAttendanceIn));
				delegator.createOrStore(DailyAttendanceInSetup);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		resultMap = ServiceUtil.returnSuccess("Attendance Input is Successful");
		return resultMap;

	} catch (Exception e) {
		e.printStackTrace();
		resultMap = ServiceUtil.returnError("Attendance Input is Failed!");
		return resultMap;
	}
}

}
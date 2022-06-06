package org.ofbiz.humanres.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class DataImportServices {
	public static final String module = DataImportServices.class.getName();
	
	public static List<List<String>> getDataFromExcelFile2003(File uploadedFile) {
		List<List<String>> sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			/* Create a FileInputStream that will be use to read the excel file. */
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

	/**
	 * 
	 * @param uploadedFile
	 * @return
	 */
	public static List<List<String>> getDataFromExcelFile2007(File uploadedFile) {
		List<List<String>> sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			/* Create a FileInputStream that will be use to read the excel file. */
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
		new DataImportServices().deleteFile(filePath, "." + fileExt);
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
			String temp = new StringBuffer(folder).append(File.separator)
					.append(file).toString();
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
	
	public static Date convertDateOneFormatToAnother(String dateinString) {
		final String OLD_FORMAT = "dd-MMM-yyyy";
		final String NEW_FORMAT = "yyyy-MM-dd";

		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date dateOld = null;
		try {
			dateOld = sdf.parse(dateinString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(dateOld);
		Date shipmentDate = java.sql.Date.valueOf(newDateString.toString());
		return shipmentDate;
	}
	
	public static Date convertDateOneFormatToAnother(String dateinString,
			String dateOldStr, String dateNewStr) {
		final String OLD_FORMAT = dateOldStr;
		final String NEW_FORMAT = dateNewStr;

		String newDateString;
		Date newDateFormat = null;
		if (!OLD_FORMAT.isEmpty() && !NEW_FORMAT.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date dateOld = null;
			try {
				dateOld = sdf.parse(dateinString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sdf.applyPattern(NEW_FORMAT);
			newDateString = sdf.format(dateOld);
			newDateFormat = java.sql.Date.valueOf(newDateString.toString());
		}

		if (!OLD_FORMAT.isEmpty() || !NEW_FORMAT.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date dateOld = null;
			try {
				dateOld = sdf.parse(dateinString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sdf.applyPattern(NEW_FORMAT);
			newDateString = sdf.format(dateOld);
			newDateFormat = java.sql.Date.valueOf(newDateString.toString());
		}

		return newDateFormat;
	}
	
	
	
	public static Map<String, Object> setEmployeeDataImportReq(List<String> objStr) {
		Map<String, Object> dataMap = FastMap.newInstance();

		if (UtilValidate.isNotEmpty(objStr.get(0))) {
			int party = (int) Double.parseDouble(objStr.get(0).toString());
			dataMap.put("partyId", String.valueOf(party));
		}

		dataMap.put("partyTypeId", objStr.get(1));
		dataMap.put("statusId", objStr.get(2));
		
		return dataMap;
	}

	public static BigDecimal getStringToBigDecimal(String strData) {
		BigDecimal strToBigDecimal = new BigDecimal(0);
		if (UtilValidate.isNotEmpty(strData)) {
			strToBigDecimal = new BigDecimal(strData);
		}
		return strToBigDecimal;
	}
	public static void createOrUpdateParty(Delegator delegator,
			Map<String, Object> mapData) {
		if (UtilValidate.isEmpty(mapData.get("partyId"))) {
			mapData.put("partyId",
					delegator.getNextSeqId("Party"));
		}
		try {
			GenericValue newValue = delegator.makeValue("Party",
					mapData);
			delegator.createOrStore(newValue);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

	}
	
	/*public static boolean isValidDate(String shipmentDateStr) {
		if (shipmentDateStr == null)
			return false;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date shipmentDate = null;
		try {
			Date varDate = dateFormat.parse(shipmentDateStr);
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String PartyDateStr1 = dateFormat.format(varDate);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			shipmentDate = formatter.parse(PartyDateStr1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}*/
}
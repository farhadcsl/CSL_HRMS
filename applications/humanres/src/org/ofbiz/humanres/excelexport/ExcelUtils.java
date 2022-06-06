package org.ofbiz.humanres.excelexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.ofbiz.base.util.Debug;
import org.opentaps.common.util.UtilCommon;

/**
 * 
 * @zzz
 * 
 */

public class ExcelUtils {

	private static final String MODULE = ExcelUtils.class.getName();
	/**
	 * Download an existing Excel file from the ${opentaps_home}/runtime/output
	 * directory. The Excel file is deleted after the download.
	 * 
	 * @param filename
	 *            the file name String object
	 * @param request
	 *            a <code>HttpServletRequest</code> value
	 * @param response
	 *          a <code>HttpServletResponse</code> value
	 * @return a <code>String</code> value
	 */

	/**
	 * Input for {@link #getExceldata(List)}
	 * 
	 * @return
	 */

	private static List<String> columnNameList = new ArrayList<String>();

	public static void setColumnHeaders(List<String> headers) {
		columnNameList = headers;
	}
	
	public static  List<String> getColumnHeaders() {
		return columnNameList;
	}

	@SuppressWarnings("unused")
	public static String downloadExcel(String filename,
			HttpServletRequest request, HttpServletResponse response) {
		File file = null;
		ServletOutputStream out = null;
		FileInputStream fileToDownload = null;
		try {
			out = response.getOutputStream();
			file = new File(UtilCommon.getAbsoluteFilePath(request, filename));
			fileToDownload = new FileInputStream(file);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ filename);
			response.setContentLength(fileToDownload.available());
			int c;
			while ((c = fileToDownload.read()) != -1) {
				out.write(c);
			}
			out.flush();
		} catch (FileNotFoundException e) {
			Debug.logError("Failed to open the file: " + filename, MODULE);
			return "error";
		} catch (IOException ioe) {
			Debug.logError(
					"IOException is thrown while trying to download the Excel file: "
							+ ioe.getMessage(), MODULE);
			return "error";
		} finally {
			try {
				out.close();
				if (fileToDownload != null) {
					fileToDownload.close();
					// Delete the file under /runtime/output/ this is optional
					file.delete();
				}
			} catch (IOException ioe) {
				Debug.logError(
						"IOException is thrown while trying to download the Excel file: "
								+ ioe.getMessage(), MODULE);
				return "error";
			}
		}
		return "success";
	}

	/**
	 * Creates an Excel document with a given column name list, and column data
	 * list. The String objects in the column name list are used as Map keys to
	 * look up the corresponding column header and data. The column data to be
	 * exported is a List of Map objects where the first Map element contains
	 * column headers, and the rest has all the column data.
	 * 
	 * @param workBookName
	 *            a String object as Excel file name
	 * @param workSheetName
	 *            a String object as the name of the Excel sheet
	 * @param columnNameList
	 *            a List of String objects as column names, they usually
	 *            correspond to entity field names
	 * @param data
	 *            a List of Map objects to be exported where the first Map
	 *            element contains column headers, and the rest has all the
	 *            column data.
	 * @throws IOException
	 */
	public static void saveToExcel(final String workBookName,
			final String workSheetName, 
			final List<List<String>> data) throws IOException {
		if (StringUtils.isEmpty(workBookName)) {
			throw new IllegalArgumentException(
					"Argument workBookName can't be empty");
		}
		
		
		if (StringUtils.isEmpty(workSheetName)) {
			throw new IllegalArgumentException(
					"Argument workSheetName can't be empty");
		}

		if (columnNameList == null || columnNameList.isEmpty()) {
			throw new IllegalArgumentException(
					"Argument columnNameList can't be empty");
		}

		// the data list should have at least one element for the column headers
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("Argument data can't be empty");
		}

		FileOutputStream fileOut = new FileOutputStream(new File(workBookName));
		assert fileOut != null;

		HSSFWorkbook workBook = new HSSFWorkbook();
		assert workBook != null;

		HSSFSheet workSheet = workBook.createSheet(workSheetName);
		workSheet.setDefaultColumnWidth(14);
		workSheet.autoSizeColumn(6);
		workSheet.setDefaultRowHeightInPoints(16);
		assert workSheet != null;

		// create the header row

		HSSFRow headerRow = workSheet.createRow(0);
		
		assert workSheet != null;

		HSSFFont headerFont = workBook.createFont();
		assert headerFont != null;
		
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);

		HSSFCellStyle headerCellStyle = workBook.createCellStyle();
		assert headerCellStyle != null;

		headerCellStyle.setFont(headerFont);
		


		// the first data list element should always be the column header map

		if (columnNameList != null) {
			for (short i = 0; i < columnNameList.size(); i++) {
				HSSFCell cell = headerRow.createCell(i);
				assert cell != null;

				cell.setCellStyle(headerCellStyle);

				String headerColumnData = columnNameList.get(i);
				if (headerColumnData != null) {
					cell.setCellValue(new HSSFRichTextString(headerColumnData));
				}
			}
		}

		// create data rows

		// column data starts from the second element
		List dataLists = new ArrayList();
		if (data.size() > 0) {
			for (int dataRowIndex = 1; dataRowIndex <= data.size(); dataRowIndex++) {
				List<String> dataListsForExcel = new ArrayList<String>();
				dataListsForExcel.addAll(data.get(dataRowIndex - 1));

				HSSFRow dataRow = workSheet.createRow(dataRowIndex);
				assert dataRow != null;

				for (short i = 0; i < columnNameList.size(); i++) {
					HSSFCell cell = dataRow.createCell(i);
					assert cell != null;
					Object objCellData = dataListsForExcel.get(i);
					String cellDataStr = filterDataAndFormatToString(objCellData);
					cell.setCellValue(cellDataStr);
					
				}
			}
		}
		// create the Excel file
		workBook.write(fileOut);
		fileOut.close();
	}
	
	/**
	 * 
	 * @param objDataForFilter
	 * @return
	 */
	
	public static String filterDataAndFormatToString(Object objDataForFilter){
		String cellDataStr = "";
		Object objCellData = objDataForFilter;
		
		if (objCellData != null) {
			if (objCellData instanceof BigDecimal) {
				String dataDouble = ((BigDecimal) objCellData)
						.toString();
				cellDataStr = dataDouble;
			} else if (objCellData instanceof Double) {
				String dataDouble = ((Double) objCellData).toString();
				cellDataStr = dataDouble;
			} else if (objCellData instanceof Integer) {
				String dataInteger = ((Integer) objCellData).toString();
				cellDataStr = dataInteger;
			} else if (objCellData instanceof BigInteger) {
				String dataInteger = ((BigInteger) objCellData)
						.toString();
				cellDataStr = dataInteger;
			} else {
				cellDataStr = objCellData.toString();
			}
		} else
			cellDataStr = "  ";
		
		return cellDataStr;
	}
	
	/**
	 * 
	 * @param dbListMap
	 * @return
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<List<String>> perpareDataToExportIntoExcel(
			List<Map<String, String>> dbListMap) {
		List<List<String>> listOfLists = new ArrayList<List<String>>();
		
		int i = 0;
		if (dbListMap.size() >= 1) {
			for (Map<String, String> obj : dbListMap) {
				int j = 0;
				List exList = new ArrayList();
				for (Map.Entry<String, String> entry : obj.entrySet()) {
					String cellDataStr = "";
					Object objData = null;
					objData = entry.getValue();
					if (objData != null) {
						if (objData instanceof BigDecimal) {
							String dataDouble = ((BigDecimal) objData)
									.toString();
							cellDataStr = dataDouble;
						} else if (objData instanceof Double) {
							String dataDouble = ((Double) objData).toString();
							cellDataStr = dataDouble;
						} else if (objData instanceof Integer) {
							String dataInteger = ((Integer) objData).toString();
							cellDataStr = dataInteger;
						} else if (objData instanceof BigInteger) {
							String dataInteger = ((BigInteger) objData)
									.toString();
							cellDataStr = dataInteger;
						} else {
							cellDataStr = objData.toString();
						}
					} else
						cellDataStr = "";
					exList.add(j, cellDataStr);
					j++;
				}
				listOfLists.add(i, exList);
				i++;
			}
			return listOfLists;
		} else
			return null;
	}
	
	
	public static void saveToExcel2(final String workBookName,
			final String workSheetName, 
			final List<List<String>> data) throws IOException {
		if (StringUtils.isEmpty(workBookName)) {
			throw new IllegalArgumentException(
					"Argument workBookName can't be empty");
		}
		
		
		if (StringUtils.isEmpty(workSheetName)) {
			throw new IllegalArgumentException(
					"Argument workSheetName can't be empty");
		}

		if (columnNameList == null || columnNameList.isEmpty()) {
			throw new IllegalArgumentException(
					"Argument columnNameList can't be empty");
		}

		// the data list should have at least one element for the column headers
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("Argument data can't be empty");
		}

		FileOutputStream fileOut = new FileOutputStream(new File(workBookName));
		assert fileOut != null;

		HSSFWorkbook workBook = new HSSFWorkbook();
		assert workBook != null;

		HSSFSheet workSheet = workBook.createSheet(workSheetName);
		workSheet.setDefaultColumnWidth(18);
		workSheet.setDefaultRowHeightInPoints(16);
		assert workSheet != null;

		// create the header row

		HSSFRow headerRow = workSheet.createRow(0);
		
		assert workSheet != null;

		HSSFFont headerFont = workBook.createFont();
		assert headerFont != null;
		
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);

		HSSFCellStyle headerCellStyle = workBook.createCellStyle();
		assert headerCellStyle != null;

		headerCellStyle.setFont(headerFont);
		


		// the first data list element should always be the column header map

		if (columnNameList != null) {
			for (short i = 0; i < columnNameList.size(); i++) {
				HSSFCell cell = headerRow.createCell(i);
				assert cell != null;

				cell.setCellStyle(headerCellStyle);

				String headerColumnData = columnNameList.get(i);
				if (headerColumnData != null) {
					cell.setCellValue(new HSSFRichTextString(headerColumnData));
				}
			}
		}

		// create data rows

		// column data starts from the second element
		List dataLists = new ArrayList();
		if (data.size() > 0) {
			for (int dataRowIndex = 1; dataRowIndex <= data.size(); dataRowIndex++) {
				List<String> dataListsForExcel = new ArrayList<String>();
				dataListsForExcel.addAll(data.get(dataRowIndex - 1));

				HSSFRow dataRow = workSheet.createRow(dataRowIndex);
				assert dataRow != null;

				for (short i = 0; i < columnNameList.size(); i++) {
					HSSFCell cell = dataRow.createCell(i);
					assert cell != null;
					Object objCellData = dataListsForExcel.get(i);
					String cellDataStr = filterDataAndFormatToString(objCellData);
					cell.setCellValue(cellDataStr);
					
				}
			}
		}
		// create the Excel file
		workBook.write(fileOut);
		fileOut.close();
	}

	

}

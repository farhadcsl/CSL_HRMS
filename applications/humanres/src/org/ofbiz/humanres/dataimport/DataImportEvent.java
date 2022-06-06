package org.ofbiz.humanres.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
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
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;


public class DataImportEvent {
	public static final String module = DataImportEvent.class.getName();
	/**
	 * This event handler to response for shipment schedule data migration
	 * @param request
	 * @param response
	 * @return
	 */
	public static String impEmployeeDataFromExcel(HttpServletRequest request,
			HttpServletResponse response) {
		
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		DispatchContext context = dispatcher.getDispatchContext();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		List<List<String>> sheetData = new ArrayList();
		if (isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				// Parse the request
				List items = upload.parseRequest(request);
				Iterator iterator = items.iterator();
				while (iterator.hasNext()) {
					FileItem item = (FileItem) iterator.next();
					if (!item.isFormField()) {
						String fileName = item.getName();
						String root = request.getSession().getServletContext()
								.getRealPath("/");
						File path = new File(root + "/uploads");
						if (!path.exists()) {
							boolean status = path.mkdirs();
						}

						File uploadedFile = new File(path + "/" + fileName);
						item.write(uploadedFile);
						String extension = fileName.replaceAll(
								"^.*\\.([^.]+)$", "$1");
						if (extension.equals("xlsx")) {
							sheetData = DataImportServices.getDataFromExcelFile2007(uploadedFile);
							sheetData.remove(0);
						}
						if (extension.equals("xls")) {
							sheetData = DataImportServices.getDataFromExcelFile2003(uploadedFile);
							sheetData.remove(0);
							
						}

						if (extension.equals("xlsx") || extension.equals("xls")
								|| extension.equals("sxc")
								|| extension.equals("ods")) {

							for (List<String> datasList : sheetData) {
								if (datasList.size() > 0) {
									Map<String,Object> input = DataImportServices.setEmployeeDataImportReq(datasList);
									DataImportServices.createOrUpdateParty(delegator, input);
								}
							}

						}

					}
				}
				request.setAttribute("_EVENT_MESSAGE_",
						"Successfully Data Imported");
				return ("success");
			} catch (FileUploadException e) {
				e.printStackTrace();
				request.setAttribute("_EVENT_MESSAGE_", "File Upload Error");
				return ("error");
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("_EVENT_MESSAGE_", "Data Import Error");
				return ("error");
			}

		} else {
			request.setAttribute("_EVENT_MESSAGE_", "Unexpected Operation");
			return ("error");
		}

	}
}

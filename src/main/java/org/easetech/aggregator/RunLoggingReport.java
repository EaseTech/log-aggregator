package org.easetech.aggregator;

import java.io.IOException;

import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

public class RunLoggingReport {
	private static final String UNIQUE_EXCEPTIONS_REPORT = "UniqueExceptions.jrxml"; // TODO get proper location
	private static final String EXCEPTION_COUNT_REPORT = "ExceptionCount.jrxml"; // TODO get proper location

	private JasperReport uniqueExceptionsReport;
	private JasperReport exceptionCountReport;

    public RunLoggingReport() {
        try {
        	uniqueExceptionsReport = getJasperReport(UNIQUE_EXCEPTIONS_REPORT);
        	exceptionCountReport = getJasperReport(EXCEPTION_COUNT_REPORT);
        } catch (Exception e) {
            e.printStackTrace();
                
        }
    }
    
    public static void main(String [ ] args) throws Exception {
    	// main
    	RunLoggingReport runLoggingReport = new RunLoggingReport();
    	
    	String outputType = "pdf";
    	String destinationFolder = "/Users/anuj/Documents/workspace/log-analyzer/src/test/resources";
    	String reportName = "reportName" + new Date().getTime();
    	String reportType = "exceptionCountReport"; // exceptionCountReport
    	
    	List<ExceptionCountBean> beans = new ArrayList<ExceptionCountBean>();
    	ExceptionCountBean bean = new ExceptionCountBean();
    	bean.setComponentName("circ BL");
    	bean.setDescription("desc");
    	List<ExceptionAndCountMap> list = new ArrayList<ExceptionAndCountMap>();
    	ExceptionAndCountMap map = new ExceptionAndCountMap();
    	map.setCount(2);
    	map.setException("Exception");
    	
    	ExceptionAndCountMap map1 = new ExceptionAndCountMap();
        map1.setCount(1);
        map1.setException("Exception 2");
    	list.add(map);
    	list.add(map1);
    	bean.setUniqueExceptions(list);
    	beans.add(bean);
    	Set<String> uniqueExceptions = new HashSet<String>();
    	uniqueExceptions.add("Exception 1");
    	
//    	List<UniqueExceptionsBean> beans = new ArrayList<UniqueExceptionsBean>();
//    	UniqueExceptionsBean bean = new UniqueExceptionsBean();
//    	bean.setComponentName("Circ UI");
//    	bean.setDescription("Desc");
//    	List<UniqueExceptions> exList = new ArrayList<UniqueExceptions>();
//    	UniqueExceptions ex = new UniqueExceptions();
//    	ex.setString("EXCEPTIONNNNNN");
//    	exList.add(ex);
//    	bean.setUniqueExceptions(exList);
//    	beans.add(bean);

    	JRDataSource data = runLoggingReport.createExceptionCountBeansDataSource(beans);
    	
    	runLoggingReport.runReport(outputType, reportType, destinationFolder, reportName, data);
    }
    
    
    
    public void runReport(String outputType, String reportType, String destinationFolder, String reportName, JRDataSource data) throws JRException {
    	JasperReport jasperReport = null;
    	if (reportType.equalsIgnoreCase("exceptionCountReport")) {
    		jasperReport = exceptionCountReport;
    	} else if (reportType.equalsIgnoreCase("uniqueExceptionsReport")) {
    		jasperReport = uniqueExceptionsReport;
    	}
    	
    	JasperPrint jasperPrint = getJasperPrint(jasperReport, data, new HashMap<String, Object>());
    	
    	if (outputType.equalsIgnoreCase("pdf")) {
    		exportPDF(destinationFolder, reportName, jasperPrint);
    	} else if (outputType.equalsIgnoreCase("html")) {
    		exportHTML(destinationFolder, reportName, jasperPrint);
    	} if (outputType.equalsIgnoreCase("xls")) {
    		exportXLS(destinationFolder, reportName, jasperPrint);
    	}
    }
    

    public JRDataSource createExceptionCountBeansDataSource(Collection<ExceptionCountBean> data) {
    	JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(data);
    	return beanCollectionDataSource;
    }

    public JRDataSource createUniqueExceptionBeansDataSource(Collection<UniqueExceptionsBean> data) {
    	JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(data);
    	return beanCollectionDataSource;
    }
    
    public JRDataSource createDataSource(Collection<?> data) {
        return new JRBeanCollectionDataSource(data);
    }
    
    private JasperPrint getJasperPrint(JasperReport jasperReport, JRDataSource dataSource,
            Map<String, Object> jasperParameters) throws JRException {
    	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameters, dataSource);
    	return jasperPrint;
    }
    
    private JasperReport getJasperReport(String reportResource) throws JRException, IOException {
        ResourceLoader resourceLoader = new ResourceLoaderStrategy(this.getClass());
        Resource fileResource = resourceLoader.getResource(reportResource);
        JasperReport jasperReport= null;
        if(fileResource.exists()) {
            jasperReport = JasperCompileManager.compileReport(fileResource.getInputStream());
        }
        
        
        return jasperReport;
    }

	private void exportPDF(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
	        JasperExportManager.exportReportToPdfFile(jasperPrint, destinationFolder + File.separatorChar + reportName
	                        + "." + "pdf");
	}
	
	private void exportHTML(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
	        JRHtmlExporter exporter = new JRHtmlExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destinationFolder + File.separatorChar + reportName
	                        + "." + "html");
	        exporter.setParameter(JRHtmlExporterParameter.IGNORE_PAGE_MARGINS, true);
	        exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true);
	        exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
	        exporter.exportReport();
	}
	
	private void exportXLS(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
	        JRXlsExporter exporter = new JRXlsExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destinationFolder + File.separatorChar + reportName
	                        + "." + "xls");
	        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
	        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
	        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
	        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
	        exporter.exportReport();
	}    
}

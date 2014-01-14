/****************************************************************************************************************
 *
 *  Copyright (c) 2013 OCLC, Inc. All Rights Reserved.
 *
 *  OCLC proprietary information: the enclosed materials contain
 *  proprietary information of OCLC, Inc. and shall not be disclosed in whole or in 
 *  any part to any third party or used by any person for any purpose, without written
 *  consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
 *
 ******************************************************************************************************************/

package org.easetech.aggregator;

import org.oclc.log.internal.xml.SSHProperties;

import static org.grep4j.core.Grep4j.constantExpression;
import static org.grep4j.core.Grep4j.grep;
import static org.grep4j.core.Grep4j.regularExpression;
import static org.grep4j.core.fluent.Dictionary.on;
import static org.grep4j.core.fluent.Dictionary.option;
import static org.grep4j.core.fluent.Dictionary.options;
import static org.grep4j.core.fluent.Dictionary.with;
import static org.grep4j.core.options.Option.extraLinesAfter;
import static org.grep4j.core.options.Option.extraLinesBefore;
import static org.grep4j.core.options.Option.ignoreCase;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResult;
import org.grep4j.core.result.GrepResults;
import org.oclc.log.internal.xml.Application;
import org.oclc.log.internal.xml.Env;
import org.oclc.log.internal.xml.Logging;
import org.oclc.log.internal.xml.MailProperties;
import org.oclc.log.internal.xml.ReportProperties;

/**
 * Class responsible for retrieving the logs based on certain search criteria
 * 
 */
public class LogAggregator implements Aggregator {

    private final Map<String , Map<String , List<Profile>>> appNameToProfileMap;
    
    private final Map<String , Set<String>> envNameToAppNameMap;
    
    private final String[] filePaths;

    private final ReportProperties globalReportProperties;
    
    private final MailProperties globalMailProperties;
    
    private final Set<String> environmentNames;
    
    private final Set<String> applicationNames;
    
    private final Set<String> componentNames;
    
    private final List<Env> envList;
    
    private Map<String , ReportProperties> appNameToReportPropertiesMap = new HashMap<String, ReportProperties>();
    
    private Map<String , MailProperties> appNameToMailPropertiesMap = new HashMap<String, MailProperties>();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
//    private static final SimpleDateFormat dateAndHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
//    
//    private final Pattern DATE_TIME_PATTERN = Pattern.compile("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]");
    
    private static final Pattern linePattern = Pattern.compile(".*\\r?\\n");

    private static final String EXCEPTION = "Exception";
    private static final String COLON = ":";
    private static final String NEW_LINE = "\\r?\\n";


    public LogAggregator(String propertyFileLocation) {
        envList = new ArrayList<Env>();
        environmentNames = new HashSet<String>();
        applicationNames = new HashSet<String>();
        componentNames = new HashSet<String>();
        Logging logProperties = LogUtil.getProperties(LogUtil.getFileInputStream(propertyFileLocation));
        
        globalReportProperties = logProperties.getReport();
        globalMailProperties = logProperties.getMailProperties();
        appNameToProfileMap = new HashMap<String, Map<String,List<Profile>>>();
        envNameToAppNameMap = new HashMap<String, Set<String>>();
        filePaths = new String[2];
        filePaths[0] = "/logs/webapp-out.log";
        filePaths[1] = "/logs/webapp-err.log";
        init(logProperties);
        
    }

    public void init(Logging logProperties) {
        
        Map<String , List<String>> profileNameToFileMap = LogUtil.getProfileNameToFileMap(logProperties);
        List<Env> environments = logProperties.getEnv();
        for(Env env : environments) {
            List<Application> logApplications = env.getApplication();
            environmentNames.add(env.getName());
            Set<String> applicationNames = new HashSet<String>();
            if(envNameToAppNameMap.get(env.getName()) != null) {
                applicationNames = envNameToAppNameMap.get(env.getName());
            }
            envNameToAppNameMap.put(env.getName(), applicationNames);
            for(Application app : logApplications) {
                String appName = app.getName();
                applicationNames.add(appName);
                
                applicationNames.add(appName);
                appNameToReportPropertiesMap.put(appName, app.getReport());
                appNameToMailPropertiesMap.put(appName, app.getMailProperties());
                String key = env.getName() + "_" + appName;
                Map<String , List<Profile>> profileMap = new HashMap<String, List<Profile>>();
                appNameToProfileMap.put(key, profileMap);
                List<org.oclc.log.internal.xml.Profile> definedProfiles = app.getProfile();
                
                for(org.oclc.log.internal.xml.Profile definedProfile : definedProfiles) {
                    String profileMapKey = definedProfile.getName();
                    componentNames.add(profileMapKey);
                    List<Profile> profiles = new ArrayList<Profile>();
                    profileMap.put(profileMapKey, profiles);
                    List<String> filePaths = definedProfile.getLogFile() == null || definedProfile.getLogFile().isEmpty() ? profileNameToFileMap.get(profileMapKey) : definedProfile.getLogFile();
                    for(String filePath : filePaths) {
                        Profile profile = null;
                        SSHProperties sshProperties = logProperties.getSSHProperties();
                        if(definedProfile.getUsername() != null && sshProperties != null) {
                            profile = ProfileBuilder
                                .newBuilder()
                                .name(appName + "_" + profileMapKey)
                                .filePath(filePath)
                                .onRemotehost(definedProfile.getHost())
                                .userAuthPrivateKeyLocationAndPassphrase(sshProperties.getPrivateKeyLocation(), sshProperties.getPassphrase())
                                .withUser(definedProfile.getUsername())
                                .build();
                        } else {
                            profile = ProfileBuilder
                                .newBuilder()
                                .name(appName + "_" + profileMapKey)
                                .filePath(filePath)
                                .onLocalhost().build();
                        }
                        
                            
                        profiles.add(profile);
                    }
                    
                    
                }
            }
        }
    }
    
    public void loadProperties(InputStream inputStream) {
        init(LogUtil.getProperties(inputStream));
    }
    
    public void generateErrorAndCountReport(String env , String app , List<String> modules) {        
        for(Profile profile : getProfiles(env, app, modules)) {
            errorAndTheirCount(profile);
        }
    }
    
    public Result generateExceptionAndCountReport(String env , String app , List<String> modules) {  
        List<ExceptionCountBean> exceptionCountBean = new ArrayList<ExceptionCountBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            exceptionsAndTheirCount(profile , exceptionCountBean);
        }
        String path = generateReport(app, "exceptionCountReport", "ExceptionCountReport", exceptionCountBean);
        Result result = new Result();
        result.setData(exceptionCountBean);
        result.setPath(path);
        return result;
    }
    
    public List<ExceptionCountBean> fetchExceptionAndCount(String env , String app , List<String> modules) {  
        List<ExceptionCountBean> exceptionCountBean = new ArrayList<ExceptionCountBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            exceptionsAndTheirCount(profile , exceptionCountBean);
        }
        
        return exceptionCountBean;
    }
    
    public GrepResults fetchExceptionStackTrace(String env , String app , List<String> modules , String exceptionName , String startDate , String endDate, int linesBefore , int linesAfter) {  

        GrepResults results = new GrepResults();
        for(Profile profile : getProfiles(env, app, modules)) {
            results.addAll(getExceptionStackTrace(profile , exceptionName , startDate ,endDate, linesBefore , linesAfter));
        }
        
        return results;
    }
    
    public GrepResults getExceptionStackTrace(Profile profile , String exceptionName,  String startDate , String endDate, int linesBefore , int linesAfter) {
        linesBefore = linesBefore > 0 ? linesBefore : 0;
        linesAfter = linesAfter > 0 ? linesAfter : 100;
        GrepResults finalResults = new GrepResults();
        GrepResults grepResults = grep(regularExpression(exceptionName), on(profile) , with(options(extraLinesBefore(linesBefore) , extraLinesAfter(linesAfter) , ignoreCase())));
        for(GrepResult result : grepResults) {
            result = filterBy(startDate , endDate , result);
            finalResults.add(result);
            System.out.println(result);
        }
        return finalResults;
        
    }
    
    
    public List<String> fetchUsingConstantExpression(String env , String app , List<String> modules , String constantExpression) {  
        List<String> results = new ArrayList<String>();
        for(Profile profile : getProfiles(env, app, modules)) {
            getLogUsingConstantExpression(profile , results , constantExpression);
        }
        if(!results.isEmpty())
            Collections.sort(results);
        return results;
    }
    
    public List<String> fetchUsingRegularExpression(String env , String app , List<String> modules , String constantExpression) {  
        List<String> results = new ArrayList<String>();
        for(Profile profile : getProfiles(env, app, modules)) {
            getLogUsingRegularExpression(profile , results , constantExpression);
        }
        if(!results.isEmpty())
            Collections.sort(results);
        return results;
    }
    
    public void getLogUsingConstantExpression(Profile profile , List<String> results , String constantExpression) {
        GrepResults grepResults = grep(constantExpression(constantExpression), on(profile) , with(option(ignoreCase())));
        for (GrepResult result : grepResults) {
            String[] splitStr = result.getText().split(NEW_LINE);
            results.addAll(Arrays.asList(splitStr));
            
        }
        
    }

    
    private GrepResult filterBy(String startDate , String endDate, GrepResult result) {
        StringBuilder textResult = new StringBuilder();
        Matcher lm = linePattern.matcher(result.getText());
        boolean startFound = false;
        boolean endFound = false;
        boolean startOfParsing = true;
        while (lm.find()) {
            CharSequence cs = lm.group();
            if(!startFound ) {
                if(StringUtils.contains(cs, startDate)) {
                    startFound = true;
                } else {
                    String[] splitStr = cs.toString().split(",");
                    if(splitStr.length > 0){
                        String potentialDate = splitStr[0];
                        try {
                            dateFormat.parse(potentialDate);
                            if(potentialDate.compareTo(startDate)> 0) {
                                startFound = true;
                            }
                        } catch (ParseException e) {
                            if(startOfParsing) {
                                startFound = true;
                            }
                        }
                        
                    }
                }
                
            }
            if(startFound) {
                if(StringUtils.contains(cs, endDate)) {
                    endFound = true;
                    break;
                } else {
                    String[] splitStr = cs.toString().split(",");
                    if(splitStr.length > 0){
                        String potentialDate = splitStr[0];
                        try {
                            dateFormat.parse(potentialDate);
                            if(potentialDate.compareTo(endDate)>= 0) {
                                endFound = true;
                                break;
                            }
                        } catch (ParseException e) {
                            //intentionally left empty
                        }
                        
                    }
                }
                if(!endFound) {
                    textResult.append(cs);
                }
                
            }
            startOfParsing = false;
        }

        return new GrepResult(result.getGrepRequest().copyWithNoRegEx(), result.getFileName(), textResult.toString(), result.getExecutionTime());
    }
    
    public void getLogUsingRegularExpression(Profile profile , List<String> results , String constantExpression) {
        GrepResults grepResults = grep(regularExpression(constantExpression), on(profile) , with(option(ignoreCase())));
        for (GrepResult result : grepResults) {
            String[] splitStr = result.getText().split(NEW_LINE);
            results.addAll(Arrays.asList(splitStr));
            
        }
        
    }
    public Result generateUniqueExceptionNamesReport(String env , String app , List<String> modules) {   
        List<UniqueExceptionsBean> uniqueExceptionNamesList = new ArrayList<UniqueExceptionsBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            uniqueExceptionNames(profile , uniqueExceptionNamesList);
        }
        String path = generateReport(app, "uniqueExceptionsReport", "UniqueExceptionNamesReport", uniqueExceptionNamesList);
        Result result = new Result();
        result.setData(uniqueExceptionNamesList);
        result.setPath(path);
        return result;
    }
    
    public List<UniqueExceptionsBean> fetchUniqueExceptionNames(String env , String app , List<String> modules) {   
        List<UniqueExceptionsBean> uniqueExceptionNamesList = new ArrayList<UniqueExceptionsBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            uniqueExceptionNames(profile , uniqueExceptionNamesList);
        }
        
        
        return uniqueExceptionNamesList;
    }
    
    public Result generateUniqueExceptionsWithTruncatedDetailReport(String env , String app , List<String> modules) {    
        List<UniqueExceptionsBean> uniqueExceptionsList = new ArrayList<UniqueExceptionsBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            uniqueExceptionsWithTruncatedDetail(profile , uniqueExceptionsList);
        }
        String path = generateReport(app,"uniqueExceptionsReport", "UniqueExceptionsReport", uniqueExceptionsList);
        Result result = new Result();
        result.setData(uniqueExceptionsList);
        result.setPath(path);
        return result;
    }
    
    public List<UniqueExceptionsBean> fetchUniqueExceptionsWithTruncatedDetail(String env , String app , List<String> modules) {    
        List<UniqueExceptionsBean> uniqueExceptionsList = new ArrayList<UniqueExceptionsBean>();
        for(Profile profile : getProfiles(env, app, modules)) {
            uniqueExceptionsWithTruncatedDetail(profile , uniqueExceptionsList);
        }
        
        return uniqueExceptionsList;
    }
    
    private List<Profile> getProfiles(String env , String app , List<String> modules) {
        List<Profile> result = new ArrayList<Profile>();
        Map<String , List<Profile>> availableProfiles = appNameToProfileMap.get(env + "_" + app);
        if(availableProfiles == null) {
            throw new RuntimeException("No Profile has been defined for ENV " + env + " and Application : " + app);
        }
        for(String profile : modules) {
            result.addAll(availableProfiles.get(profile));
        }
       
        if(result.isEmpty()) {
            throw new RuntimeException("Profiles with name :" + modules.toString() + " is not defined for ENV : " + env + " and Application : " + app);
        } 
        return result;
    }
    
    public Set<String> getComponentNames(String env, String app) {
        Map<String , List<Profile>> availableProfiles = appNameToProfileMap.get(env + "_" + app);
        return availableProfiles.keySet();
    }
    
    public Set<String> getApplicationNames(String env) {
        return envNameToAppNameMap.get(env);
    }
    public String generateReport(String appName , String reportType, String reportNamePrefix, Collection<?> reportData) {
        String path = null;
        ReportProperties reportProperties = appNameToReportPropertiesMap.get(appName);
        if(globalReportProperties != null || reportProperties != null) {
            RunLoggingReport runLoggingReport = new RunLoggingReport();
            String outputType = reportProperties.getType().value() != null ? reportProperties.getType().value() : globalReportProperties.getType().value();
            String destinationFolder = reportProperties.getOutputLocation() != null ? reportProperties.getOutputLocation() : globalReportProperties.getOutputLocation();
            String dateTime = dateFormat.format(new Date());
            String reportName = reportNamePrefix + "-" + dateTime;
            JRDataSource data = runLoggingReport.createDataSource(reportData);
            try {
                runLoggingReport.runReport(outputType, reportType, destinationFolder, reportName, data);
            } catch (JRException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            path = destinationFolder + "/" + reportName + "."+outputType.toLowerCase();
        }
        
        return path;
    }
    
    public void sendReport(String appName, String ... reportPaths) {
        MailProperties mailProperties = appNameToMailPropertiesMap.get(appName);
        String from = getFrom(mailProperties); 
        if(from != null) {
            List<String> to = getTo(mailProperties); 
            if(to != null) {
                EmailReport mailReport = new EmailReport(getMailProperties());
                mailReport.send(reportPaths, from, to);
            }
        }
        
    }
    
    private String getFrom(MailProperties mailProperties) {
        String result = null;
        if(mailProperties != null) {
            result = mailProperties.getFrom();
        }
        if(result == null && globalMailProperties != null) {
            result = globalMailProperties.getFrom();
        }
        return result;
    }
    
    private List<String> getTo(MailProperties mailProperties) {
        List<String> result = null;
        if(mailProperties != null) {
            result = mailProperties.getTo();
        }
        if(result == null && globalMailProperties != null) {
            result = globalMailProperties.getTo();
        }
        return result;
    }
    private Properties getMailProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", globalMailProperties.getSmtpHost());
        prop.put("mail.smtp.port", globalMailProperties.getSmtpPort());
        prop.put("mail.smtp.auth", globalMailProperties.isSmtpAuth());
        prop.put("mail.user", globalMailProperties.getUsername());
        prop.put("mail.password", globalMailProperties.getPassword());
        return prop;
    }

    public void exceptionsAndTheirCount(Profile profile , List<ExceptionCountBean> exceptionCountBean) {
        GrepResults grepResults = grep(constantExpression(EXCEPTION), on(profile));

        Map<String, Integer> exceptionCount = new HashMap<String, Integer>();
        List<ExceptionAndCountMap> exceptionCountList = new ArrayList<ExceptionAndCountMap>();

        for (GrepResult result : grepResults) {
            String[] splitStr = result.getText().split(NEW_LINE);
            for (String str : splitStr) {
                String[] exceptionClass = str.split(COLON);
                if (exceptionClass[0].endsWith(EXCEPTION)) {

                    if (exceptionCount.containsKey(exceptionClass[0])) {
                        exceptionCount.put(exceptionClass[0], exceptionCount.get(exceptionClass[0]) + 1);
                    } else {
                        exceptionCount.put(exceptionClass[0], 1);
                    }
                }

            }
        }

        for (String key : exceptionCount.keySet()) {
            ExceptionAndCountMap dataBean = new ExceptionAndCountMap();
            dataBean.setException(key);
            dataBean.setCount(exceptionCount.get(key));
            exceptionCountList.add(dataBean);
        }
        if (exceptionCountList != null && !exceptionCountList.isEmpty()) {
            ExceptionCountBean bean = new ExceptionCountBean();
            bean.setDescription("Unique Exceptions that occured in " + profile.getFilePath() + " and their count");
            bean.setComponentName(profile.getName());
            bean.setUniqueExceptions(exceptionCountList);
            exceptionCountBean.add(bean);
        }

    }

    public void errorAndTheirCount(Profile profile) {
        GrepResults grepResults = grep(constantExpression("ERROR"), on(profile));

        Map<String, Integer> exceptionCount = new HashMap<String, Integer>();
        List<ExceptionAndCountMap> exceptionCountList = new ArrayList<ExceptionAndCountMap>();

        for (GrepResult result : grepResults) {
            String[] splitStr = result.getText().split(NEW_LINE);
            for (String str : splitStr) {
                String[] exceptionClass = str.split(",");
                if (exceptionClass[1].endsWith("ERROR")) {
                    System.out.println(str);

                }

            }
        }

    }

    public void uniqueExceptionsWithTruncatedDetail(Profile profile , List<UniqueExceptionsBean> uniqueExceptionsList) {
        GrepResults grepResults = grep(constantExpression(EXCEPTION), on(profile));
        List<UniqueExceptions> exceptions = new ArrayList<UniqueExceptions>();
        Set<String> uniqueExceptions = new HashSet<String>();
        for (GrepResult result : grepResults) {
            String[] splitStr = result.getText().split(NEW_LINE);
            for (String str : splitStr) {
                String[] exceptionClass = str.split(":");
                if (exceptionClass[0].trim().endsWith(EXCEPTION)) {

                    uniqueExceptions.add(str);

                }
            }
        }
        for (String strToAdd : uniqueExceptions) {
            UniqueExceptions uniqueException = new UniqueExceptions();
            uniqueException.setString(strToAdd);
            exceptions.add(uniqueException);
        }
        if (exceptions != null && !exceptions.isEmpty()) {
            UniqueExceptionsBean bean = new UniqueExceptionsBean();
            bean.setComponentName(profile.getName());
            bean.setDescription("Unique Exceptions with truncated details that occured in " + profile.getFilePath());
            bean.setUniqueExceptions(exceptions);
            uniqueExceptionsList.add(bean);
        }

    }

    public void uniqueExceptionNames(Profile profile , List<UniqueExceptionsBean> uniqueExceptionNamesList) {
        GrepResults grepResults = grep(constantExpression(EXCEPTION), on(profile) , with(option(extraLinesBefore(20))));
        // Set<String> uniqueExceptions = new HashSet<String>();
        List<UniqueExceptions> exceptions = new ArrayList<UniqueExceptions>();
        Set<String> uniqueExceptions = new HashSet<String>();
        for (GrepResult result : grepResults) {
            
            String[] splitStr = result.getText().split(NEW_LINE);
            for (String str : splitStr) {
                String[] exceptionClass = str.split(":");
                if (exceptionClass[0].trim().endsWith(EXCEPTION)) {
                    uniqueExceptions.add(exceptionClass[0]);

                }

            }
        }
        for (String strToAdd : uniqueExceptions) {
            UniqueExceptions uniqueException = new UniqueExceptions();
            uniqueException.setString(strToAdd);
            exceptions.add(uniqueException);
        }

        if (exceptions != null && !exceptions.isEmpty()) {
            UniqueExceptionsBean bean = new UniqueExceptionsBean();
            bean.setComponentName(profile.getName());
            bean.setDescription("Unique Exceptions Class Names that occured in " + profile.getFilePath());
            bean.setUniqueExceptions(exceptions);
            uniqueExceptionNamesList.add(bean);
        }

    }


    /**
     * @return the appNameToReportPropertiesMap
     */
    public Map<String, ReportProperties> getAppNameToReportPropertiesMap() {
        return appNameToReportPropertiesMap;
    }

    /**
     * @param appNameToReportPropertiesMap the appNameToReportPropertiesMap to set
     */
    public void setAppNameToReportPropertiesMap(Map<String, ReportProperties> appNameToReportPropertiesMap) {
        this.appNameToReportPropertiesMap = appNameToReportPropertiesMap;
    }

    /**
     * @return the appNameToMailPropertiesMap
     */
    public Map<String, MailProperties> getAppNameToMailPropertiesMap() {
        return appNameToMailPropertiesMap;
    }

    /**
     * @param appNameToMailPropertiesMap the appNameToMailPropertiesMap to set
     */
    public void setAppNameToMailPropertiesMap(Map<String, MailProperties> appNameToMailPropertiesMap) {
        this.appNameToMailPropertiesMap = appNameToMailPropertiesMap;
    }

    /**
     * @return the appNameToProfileMap
     */
    public Map<String, Map<String, List<Profile>>> getAppNameToProfileMap() {
        return appNameToProfileMap;
    }

    /**
     * @return the globalReportProperties
     */
    public ReportProperties getGlobalReportProperties() {
        return globalReportProperties;
    }

    /**
     * @return the globalMailProperties
     */
    public MailProperties getGlobalMailProperties() {
        return globalMailProperties;
    }

    /**
     * @return the environmentNames
     */
    public Set<String> getEnvironmentNames() {
        return environmentNames;
    }

    /**
     * @return the applicationNames
     */
    public Set<String> getApplicationNames() {
        return applicationNames;
    }

    /**
     * @return the envList
     */
    public List<Env> getEnvList() {
        return envList;
    }

    /**
     * @return the envNameToAppNameMap
     */
    public Map<String, Set<String>> getEnvNameToAppNameMap() {
        return envNameToAppNameMap;
    }
    /**
     * @return the componentNames
     */
    public Set<String> getComponentNames() {
        return componentNames;
    }

    
}

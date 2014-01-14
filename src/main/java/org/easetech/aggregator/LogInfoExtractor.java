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

import java.util.Collections;

import static org.grep4j.core.Grep4j.constantExpression;
import static org.grep4j.core.Grep4j.grep;
import static org.grep4j.core.fluent.Dictionary.on;
import static org.grep4j.core.fluent.Dictionary.option;
import static org.grep4j.core.fluent.Dictionary.with;
import static org.grep4j.core.options.Option.extraLinesAfter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.grep4j.core.model.Profile;
import org.grep4j.core.result.GrepResult;
import org.grep4j.core.result.GrepResults;

/**
 * TODO Describe me
 *
 */
public class LogInfoExtractor {
    
    private final Profile[] profiles;    

    private String baseExpression = ".";
    
    
    private Translator<GrepResult , List<LogRow>> listTranslator = new GrepResultToLogRowTranslator();
    
    public LogInfoExtractor(Profile... profiles) {
        super();
        this.profiles = profiles;
    }

    public LogInfoExtractor(String expression, Profile... profiles) {
        super();
        this.profiles = profiles;
        this.baseExpression = expression;
    }
    


    public LogInfo extract() {
        
        GrepResults results = grep(constantExpression(baseExpression), on(profiles) , with(option(extraLinesAfter(50))));
        
        LogInfo info = null;
        List<LogRow> loggingRows = null;
        if(results != null) {
            info = new LogInfo();
            info.setGrepResults(results);
            List<LogRow> logRows = new ArrayList<LogRow>();
            Set<String> requestIds = new HashSet<String>();
            Map<String , String> requestIdToOperationMap = new HashMap<String, String>();
            info.setLogRows(logRows);
            info.setProfiles(profiles);
            info.setRequestIds(requestIds);
            info.setRequestIdToOperationMap(requestIdToOperationMap);
            for(GrepResult result : results) {
                loggingRows = listTranslator.translate(result);
                if(loggingRows != null) {
                    logRows.addAll(loggingRows);
                    for (LogRow logRow : loggingRows) {
                        requestIds.add(logRow.getRequestId());
                        requestIdToOperationMap.put(logRow.getRequestId(), logRow.getOperation());
                    }                   
                }
                
            }
          //finally sort the list on log Date
            Comparator<LogRow> comparator = new Comparator<LogRow>() {

                @Override
                public int compare(LogRow lr1, LogRow lr2) {
                    return lr1.getDate().compareTo(lr2.getDate());
                }};
            
            Collections.sort(logRows, comparator);
            info.setLogRows(logRows);
        }
        
        
        return info;
        
    }

    /**
     * @return the baseExpression
     */
    public String getBaseExpression() {
        return baseExpression;
    }

    /**
     * @param baseExpression the baseExpression to set
     */
    public void setBaseExpression(String baseExpression) {
        this.baseExpression = baseExpression;
    }

    
    

    /**
     * @return the listTranslator
     */
    public Translator<GrepResult, List<LogRow>> getListTranslator() {
        return listTranslator;
    }

    /**
     * @param listTranslator the listTranslator to set
     */
    public void setListTranslator(Translator<GrepResult, List<LogRow>> listTranslator) {
        this.listTranslator = listTranslator;
    }

    /**
     * @return the profile
     */
    public Profile[] getProfiles() {
        return profiles;
    }

    
}

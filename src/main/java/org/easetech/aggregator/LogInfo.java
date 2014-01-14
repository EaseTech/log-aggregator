package org.easetech.aggregator;

import org.grep4j.core.result.GrepResults;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.grep4j.core.model.Profile;

public class LogInfo {
    
    private Profile[] profiles;
    
    private Set<String> requestIds;
    
    private Map<String , String> requestIdToOperationMap;
    
    private List<LogRow> logRows;
    
    private GrepResults grepResults;

    /**
     * @return the grepResults
     */
    public GrepResults getGrepResults() {
        return grepResults;
    }

    /**
     * @param grepResults the grepResults to set
     */
    public void setGrepResults(GrepResults grepResults) {
        this.grepResults = grepResults;
    }

    /**
     * @return the profile
     */
    public Profile[] getProfiles() {
        return profiles;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfiles(Profile... profiles) {
        this.profiles = profiles;
    }

    /**
     * @return the requestIds
     */
    public Set<String> getRequestIds() {
        return requestIds;
    }

    /**
     * @param requestIds the requestIds to set
     */
    public void setRequestIds(Set<String> requestIds) {
        this.requestIds = requestIds;
    }

    /**
     * @return the requestIdToOperationMap
     */
    public Map<String, String> getRequestIdToOperationMap() {
        return requestIdToOperationMap;
    }

    /**
     * @param requestIdToOperationMap the requestIdToOperationMap to set
     */
    public void setRequestIdToOperationMap(Map<String, String> requestIdToOperationMap) {
        this.requestIdToOperationMap = requestIdToOperationMap;
    }

    /**
     * @return the logRows
     */
    public List<LogRow> getLogRows() {
        return logRows;
    }

    /**
     * @param logRows the logRows to set
     */
    public void setLogRows(List<LogRow> logRows) {
        this.logRows = logRows;
    }
    
    

}

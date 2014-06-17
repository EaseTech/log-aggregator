

package org.easetech.aggregator;

import java.util.List;

/**
 * TODO Describe me
 *
 */
public class ExceptionCountBean implements LogInfoData{
    
    List<ExceptionAndCountMap> uniqueExceptions;
    
    private String description;
    
    private String componentName;


   

    /**
     * @return the uniqueExceptions
     */
    public List<ExceptionAndCountMap> getUniqueExceptions() {
        return uniqueExceptions;
    }

    /**
     * @param uniqueExceptions the uniqueExceptions to set
     */
    public void setUniqueExceptions(List<ExceptionAndCountMap> uniqueExceptions) {
        this.uniqueExceptions = uniqueExceptions;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the componentName
     */
    @Override
    public String getComponentName() {
        return componentName;
    }

    /**
     * @param componentName the componentName to set
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public List<?> getLogData() {
        return uniqueExceptions;
    }
    
    

}

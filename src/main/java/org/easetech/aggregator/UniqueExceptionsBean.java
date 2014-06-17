

package org.easetech.aggregator;

import java.util.List;

/**
 * TODO Describe me
 *
 */
public class UniqueExceptionsBean implements LogInfoData{
    
    String description;
    
    List<UniqueExceptions> uniqueExceptions;
    
    String componentName;

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
     * @return the uniqueExceptions
     */
    public List<UniqueExceptions> getUniqueExceptions() {
        return uniqueExceptions;
    }

    /**
     * @param uniqueExceptions the uniqueExceptions to set
     */
    public void setUniqueExceptions(List<UniqueExceptions> uniqueExceptions) {
        this.uniqueExceptions = uniqueExceptions;
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

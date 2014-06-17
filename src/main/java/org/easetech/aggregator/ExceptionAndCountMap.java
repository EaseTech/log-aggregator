

package org.easetech.aggregator;

/**
 * TODO Describe me
 *
 */
public class ExceptionAndCountMap {


    private String exception;
    
    private Integer count;

    /**
     * @return the exception
     */
    public String getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Integer count) {
        this.count = count;
    }
    
    
    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExceptionAndCountMap [exception=");
        builder.append(exception);
        builder.append(", count=");
        builder.append(count);
        builder.append("]");
        return builder.toString();
    }
    

}

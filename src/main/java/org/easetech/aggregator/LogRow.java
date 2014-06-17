
package org.easetech.aggregator;

import java.util.Date;

/**
 * TODO Describe me
 *
 */
public class LogRow {

    private Date date;
    
    private String logType;
    
    private String transport;
    
    private String loggingClass;
    
    private String requestId;
    
    private String service;
    
    private String operation;
    
    private String institution;
    
    private String message;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the logType
     */
    public String getLogType() {
        return logType;
    }

    /**
     * @param logType the logType to set
     */
    public void setLogType(String logType) {
        this.logType = logType;
    }

    /**
     * @return the transport
     */
    public String getTransport() {
        return transport;
    }

    /**
     * @param transport the transport to set
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * @return the loggingClass
     */
    public String getLoggingClass() {
        return loggingClass;
    }

    /**
     * @param loggingClass the loggingClass to set
     */
    public void setLoggingClass(String loggingClass) {
        this.loggingClass = loggingClass;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the institution
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * @param institution the institution to set
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LogRow [date=");
        builder.append(date);
        builder.append(", logType=");
        builder.append(logType);
        builder.append(", transport=");
        builder.append(transport);
        builder.append(", loggingClass=");
        builder.append(loggingClass);
        builder.append(", requestId=");
        builder.append(requestId);
        builder.append(", service=");
        builder.append(service);
        builder.append(", operation=");
        builder.append(operation);
        builder.append(", institution=");
        builder.append(institution);
        builder.append(", message=");
        builder.append(message);
        builder.append("]");
        return builder.toString();
    }
    
    
}

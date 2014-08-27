package com.intuit.developer.sampleapp.timetracking.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/22/14
 * Time: 4:51 PM
 */
public class SyncRequest {

    public enum EntityType {
        Employee,
        Customer,
        ServiceItem
    }

    private EntityType type;
    private String companyId;
    private boolean successful;
    private String message;

    public SyncRequest() {

    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

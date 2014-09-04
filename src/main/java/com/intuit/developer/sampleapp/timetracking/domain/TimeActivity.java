package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.time.LocalDate;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/2/14
 * Time: 10:49 AM
 */
@Entity
public class TimeActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String qboId;

    private int minutes;

    private LocalDate date;

    private String description;

    private boolean billed = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_fk", referencedColumnName = "id")
    private ServiceItem serviceItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_fk", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_fk", referencedColumnName = "id")
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_fk", referencedColumnName = "id")
    private Company company;

    public boolean isBilled() {
        return billed;
    }

    public void setBilled(boolean billed) {
        this.billed = billed;
    }

    public String getQboId() {
        return qboId;
    }

    public void setQboId(String qboId) {
        this.qboId = qboId;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}

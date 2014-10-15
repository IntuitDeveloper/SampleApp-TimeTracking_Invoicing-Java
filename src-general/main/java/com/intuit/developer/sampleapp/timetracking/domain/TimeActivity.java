package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.Money;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @ManyToOne
    @JoinColumn(name = "invoice_fk", referencedColumnName = "id")
    private Invoice invoice;

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

    public Invoice getInvoice() {
        return invoice;
    }


    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Money getAmount() {
        final Money serviceItemRatePerHour = getServiceItem().getRate();

        final Money amount = serviceItemRatePerHour.multipliedBy(getHours(), RoundingMode.HALF_UP);

        return amount;
    }

    public BigDecimal getHours() {
        BigDecimal hours = new BigDecimal((double) minutes / 60);
        hours.setScale(2);

        return hours;
    }
}

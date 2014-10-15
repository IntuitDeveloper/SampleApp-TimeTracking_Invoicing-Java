package com.intuit.developer.sampleapp.timetracking.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 10:05 AM
 */
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String qboId;

    private InvoiceStatus status = InvoiceStatus.Pending;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_fk", referencedColumnName = "id")
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_fk", referencedColumnName = "id")
    private Company company;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice")
    private List<TimeActivity> timeActivities;

    public void addTimeActivity(TimeActivity timeActivity) {
        if (this.timeActivities == null) {
            this.timeActivities = new ArrayList<>();
        }
        this.timeActivities.add(timeActivity);
        timeActivity.setInvoice(this);
    }

    public Long getId() {
        return id;
    }

    public String getQboId() {
        return qboId;
    }

    public void setQboId(String qboId) {
        this.qboId = qboId;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
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

    /**
     * A function that returns an invoice summary used in the InvoiceSummaryProjection
     *
     * @return
     */
    public InvoiceSummary getSummary() {
        return new InvoiceSummary(this);
    }


    public List<TimeActivity> getTimeActivities() {
        TreeSet<TimeActivity> sortedSet = new TreeSet<>(new Comparator<TimeActivity>() {
            @Override
            public int compare(TimeActivity o1, TimeActivity o2) {
                int result = o1.getDate().compareTo(o2.getDate());

                if (result != 0) {
                    return result;
                }

                result = o1.getEmployee().getLastName().compareTo(o2.getEmployee().getLastName());

                if (result != 0) {
                    return result;
                }

                result = o1.getEmployee().getFirstName().compareTo(o2.getEmployee().getFirstName());

                if (result != 0) {
                    return result;
                }

                result = o1.getServiceItem().getName().compareTo(o2.getServiceItem().getName());

                return result;
            }
        });

        sortedSet.addAll(this.timeActivities);

        return new ArrayList<>(sortedSet);
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}

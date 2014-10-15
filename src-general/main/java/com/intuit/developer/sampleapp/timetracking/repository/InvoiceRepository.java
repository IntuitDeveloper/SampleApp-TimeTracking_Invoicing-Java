package com.intuit.developer.sampleapp.timetracking.repository;

import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.InvoiceStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 12:38 PM
 */
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {


    List<Invoice> findByCustomer_IdAndStatus(@Param("customerId") Long customerId, @Param("status") InvoiceStatus status);

    @RestResource(path = "findByCompanyAndStatus", rel = "findByCompanyAndStatus        ")
    List<Invoice> findByCompany_IdAndStatus(@Param("companyId") Long comapnyId, @Param("status") InvoiceStatus status);


}

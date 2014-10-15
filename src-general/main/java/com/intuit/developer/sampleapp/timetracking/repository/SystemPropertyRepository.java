package com.intuit.developer.sampleapp.timetracking.repository;

import com.intuit.developer.sampleapp.timetracking.domain.SystemProperty;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 10/1/14
 * Time: 9:57 AM
 */
@RestResource
public interface SystemPropertyRepository extends PagingAndSortingRepository<SystemProperty, String> {

}

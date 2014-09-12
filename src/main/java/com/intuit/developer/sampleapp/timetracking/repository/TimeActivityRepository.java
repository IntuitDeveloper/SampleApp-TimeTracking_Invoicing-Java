package com.intuit.developer.sampleapp.timetracking.repository;

import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/2/14
 * Time: 12:52 PM
 */
@RepositoryRestResource
public interface TimeActivityRepository extends PagingAndSortingRepository<TimeActivity, Long> {

}

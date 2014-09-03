package com.intuit.developer.sampleapp.timetracking.repository;

import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/2/14
 * Time: 12:52 PM
 */
public interface TimeActivityRepository extends PagingAndSortingRepository<TimeActivity, Long> {
}

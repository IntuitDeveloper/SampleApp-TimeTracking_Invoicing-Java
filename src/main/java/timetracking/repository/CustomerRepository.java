package timetracking.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import timetracking.domain.Customer;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/20/14
 * Time: 3:41 PM
 */
@RepositoryRestResource
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

}

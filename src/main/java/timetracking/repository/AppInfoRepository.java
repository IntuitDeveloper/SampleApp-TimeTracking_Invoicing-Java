package timetracking.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import timetracking.domain.AppInfo;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/24/14
 * Time: 1:01 PM
 */
@RestResource(exported = false)
public interface AppInfoRepository extends PagingAndSortingRepository<AppInfo, Long> {

}

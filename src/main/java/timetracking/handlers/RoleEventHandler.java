package timetracking.handlers;

import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import timetracking.domain.Role;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/20/14
 * Time: 9:55 AM
 * <p>
 * See http://docs.spring.io/spring-data/rest/docs/2.1.0.RELEASE/reference/html/events-chapter.html#d5e443
 */
@RepositoryEventHandler(Role.class)
public class RoleEventHandler {


    @HandleBeforeCreate
    public void handleBeforeCreated(Role p) {
        System.out.println("Role created");
    }

    @HandleAfterDelete
    public void handleAfterDelete(Role p) {
    }
}
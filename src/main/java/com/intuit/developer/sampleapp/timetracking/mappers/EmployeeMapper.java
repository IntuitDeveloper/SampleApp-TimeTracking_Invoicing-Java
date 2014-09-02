package com.intuit.developer.sampleapp.timetracking.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 8:58 AM
 */
public class EmployeeMapper {

    private static BoundMapperFacade<Employee, com.intuit.ipp.data.Employee> domainToQBOMapper;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(Employee.class, com.intuit.ipp.data.Employee.class)
                .field("firstName", "givenName")
                .field("lastName", "familyName")
                .field("emailAddress", "primaryEmailAddr.address")
                .field("phoneNumber", "primaryPhone.freeFormNumber")
                .exclude("id")
                .byDefault()
                .register();

        domainToQBOMapper = mapperFactory.getMapperFacade(Employee.class, com.intuit.ipp.data.Employee.class);

    }

    public static com.intuit.ipp.data.Employee buildQBOObject(Employee pDomainEmployee) {

        if (pDomainEmployee == null) {
            return null;
        }

        final com.intuit.ipp.data.Employee qboEmployee = domainToQBOMapper.map(pDomainEmployee);

        return qboEmployee;

    }
}

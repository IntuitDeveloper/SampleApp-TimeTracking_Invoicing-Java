package com.intuit.developer.sampleapp.timetracking.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/2/14
 * Time: 1:21 PM
 */
public class TimeActivityMapper {
    private static BoundMapperFacade<TimeActivity, com.intuit.ipp.data.TimeActivity> domainToQBOMapper;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(LocalDate.class, Date.class)
                .customize(new CustomMapper<LocalDate, Date>() {
                    @Override
                    public void mapAtoB(LocalDate a, Date b, MappingContext context) {
                        super.mapAtoB(a, b, context);
                        b.setTime(a.toDate().getTime());
                    }
                })
                .register();

        mapperFactory.classMap(TimeActivity.class, com.intuit.ipp.data.TimeActivity.class)
                .field("date", "txnDate")
                .field("customer.qboId", "customerRef.value")
                .field("employee.qboId", "employeeRef.value")
                .field("serviceItem.qboId", "itemRef.value")
                .exclude("id")
                .exclude("hours") //getHours() is a conveinence function for the UI, we dont want it  mapped into the QBO object because we have logic below to handle it
                .customize(new CustomMapper<TimeActivity, com.intuit.ipp.data.TimeActivity>() {
                    @Override
                    public void mapAtoB(TimeActivity a, com.intuit.ipp.data.TimeActivity b, MappingContext context) {
                        super.mapAtoB(a, b, context);

                        Period period = new Period(0, a.getMinutes(), 0, 0).normalizedStandard();
                        b.setHours(period.getHours());
                        b.setMinutes(period.getMinutes());

                        /*
                        In this example we decided to go with the service item's rate. You could also use:
                         -- Employee's hourly rate (not present in this sample app's domain, but still a reasonable choice)
                         -- If employee's had roles (e.g. Chief Counsel, Paralegal) you could use the role's global rate,
                            or even a rate for a given combination of role and service item

                         It all depends on what kind of billing model the user would want.
                         */
                        b.setHourlyRate(a.getServiceItem().getRate().getAmount());
                    }
                })
                .byDefault()
                .register();


        domainToQBOMapper = mapperFactory.getMapperFacade(TimeActivity.class, com.intuit.ipp.data.TimeActivity.class);

    }

    public static com.intuit.ipp.data.TimeActivity buildQBOObject(TimeActivity pSalesReceipt) {

        if (pSalesReceipt == null) {
            return null;
        }

        final com.intuit.ipp.data.TimeActivity qboTimeActivity = domainToQBOMapper.map(pSalesReceipt);

        return qboTimeActivity;

    }
}

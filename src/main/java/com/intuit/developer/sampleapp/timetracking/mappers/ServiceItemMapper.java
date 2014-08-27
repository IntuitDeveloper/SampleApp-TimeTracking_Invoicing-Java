package com.intuit.developer.sampleapp.timetracking.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 8:58 AM
 */
public class ServiceItemMapper {

    private static BoundMapperFacade<ServiceItem, com.intuit.ipp.data.Item> domainToQBOMapper;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(ServiceItem.class, com.intuit.ipp.data.Item.class)
                .field("rate.amount", "unitPrice")
                .byDefault()
                .register();

        domainToQBOMapper = mapperFactory.getMapperFacade(ServiceItem.class, com.intuit.ipp.data.Item.class);
    }

    public static com.intuit.ipp.data.Item buildQBOObject(ServiceItem serviceItem) {

        if (serviceItem == null) {
            return null;
        }

        final com.intuit.ipp.data.Item qboServiceItem = domainToQBOMapper.map(serviceItem);

        return qboServiceItem;

    }
}

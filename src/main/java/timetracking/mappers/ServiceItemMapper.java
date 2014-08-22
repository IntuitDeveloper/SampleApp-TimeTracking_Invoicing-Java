package timetracking.mappers;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import timetracking.domain.ServiceItem;

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
                .field("qboIncomeAccountId", "incomeAccountRef.value")
                .byDefault()
                .register();

        domainToQBOMapper = mapperFactory.getMapperFacade(ServiceItem.class, com.intuit.ipp.data.Item.class);
    }

    public static com.intuit.ipp.data.Item buildQBOObject(ServiceItem pSalesReceipt) {

        if (pSalesReceipt == null) {
            return null;
        }

        final com.intuit.ipp.data.Item qboServiceItem = domainToQBOMapper.map(pSalesReceipt);

        return qboServiceItem;

    }
}

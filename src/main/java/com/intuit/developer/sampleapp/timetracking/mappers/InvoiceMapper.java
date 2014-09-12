package com.intuit.developer.sampleapp.timetracking.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LineDetailTypeEnum;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesItemLineDetail;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/11/14
 * Time: 10:06 AM
 */
public class InvoiceMapper {
    private static BoundMapperFacade<Invoice, com.intuit.ipp.data.Invoice> domainToQBOMapper;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(Invoice.class, com.intuit.ipp.data.Invoice.class)
                .field("customer.qboId", "customerRef.value")
                .exclude("id")
                .exclude("status")
                .customize(new CustomMapper<Invoice, com.intuit.ipp.data.Invoice>() {
                    @Override
                    public void mapAtoB(Invoice a, com.intuit.ipp.data.Invoice b, MappingContext context) {
                        super.mapAtoB(a, b, context);

                        final List<Line> invoiceLines = b.getLine();

                        int lineNum = 1;

                        /* taking a simple route and adding an invoice line per time entry
                            you could get creative here and aggregate invoice lines per service item or per employee
                         */
                        final List<TimeActivity> timeActivities = a.getTimeActivities();
                        timeActivities.size();

                        for (TimeActivity timeActivity : timeActivities) {
                            Line invoiceLine = new Line();
                            invoiceLines.add(invoiceLine);
                            invoiceLine.setLineNum(BigInteger.valueOf(lineNum));
                            lineNum++;

                            invoiceLine.setAmount(timeActivity.getAmount().getAmount());
                            StringBuilder description = new StringBuilder(timeActivity.getEmployee().getFullName())
                                    .append(" on ")
                                    .append(timeActivity.getDate().toString());

                            invoiceLine.setDescription(description.toString());
                            invoiceLine.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);

                            SalesItemLineDetail detail = new SalesItemLineDetail();
                            invoiceLine.setSalesItemLineDetail(detail);

                            final ServiceItem serviceItem = timeActivity.getServiceItem();
                            detail.setUnitPrice(serviceItem.getRate().getAmount());
                            detail.setQty(timeActivity.getHours());

                            final ReferenceType itemRef = new ReferenceType();
                            detail.setItemRef(itemRef);
                            itemRef.setValue(serviceItem.getQboId());
                        }
                    }
                })
                .byDefault()
                .register();

        domainToQBOMapper = mapperFactory.getMapperFacade(Invoice.class, com.intuit.ipp.data.Invoice.class);
    }

    public static com.intuit.ipp.data.Invoice buildQBOObject(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        final com.intuit.ipp.data.Invoice qboInvoice = domainToQBOMapper.map(invoice);

        return qboInvoice;
    }
}

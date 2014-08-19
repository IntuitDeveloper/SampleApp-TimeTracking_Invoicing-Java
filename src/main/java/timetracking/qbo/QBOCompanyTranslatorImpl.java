package timetracking.qbo;


import qbo.QBOCompanyTranslator;
import timetracking.domain.Company;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/25/14
 * Time: 7:52 AM
 */
public class QBOCompanyTranslatorImpl implements QBOCompanyTranslator {


    private final Company domainCompany;

    public QBOCompanyTranslatorImpl(Company domainCompany) {
        this.domainCompany = domainCompany;
    }

    @Override
    public void updateDomainCompany(com.intuit.ipp.data.Company qboCompany) {
        this.domainCompany.setName(qboCompany.getCompanyName());
    }
}

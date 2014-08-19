package qbo;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Company;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/24/14
 * Time: 5:23 PM
 */
public class QboCompanyService {

    private final String qboCompanyId;
    private final QBOCompanyTranslator companyTranslator;
    private final DataService service;

    public QboCompanyService(String appToken,
                             String consumerKey,
                             String consumerSecret,
                             String accessToken,
                             String accessTokenSecret,
                             String qboCompanyId,
                             QBOCompanyTranslator companyTranslator) {

        this.qboCompanyId = qboCompanyId;
        this.companyTranslator = companyTranslator;

        OAuthAuthorizer oauth = new OAuthAuthorizer(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        Context qboContext;
        try {
            qboContext = new Context(oauth, appToken, ServiceType.QBO, qboCompanyId);
        } catch (FMSException e) {
            throw new RuntimeException("Failed to initialize the QBO SDK context", e);
        }
        this.service = new DataService(qboContext);
    }

    public void syncFromQBO() {
        Company company = new Company();
        company.setId(qboCompanyId);

        try {
            company = this.service.findById(company);
        } catch (FMSException e) {
            throw new RuntimeException("Failed to find company by id", e);
        }

        companyTranslator.updateDomainCompany(company);
    }
}

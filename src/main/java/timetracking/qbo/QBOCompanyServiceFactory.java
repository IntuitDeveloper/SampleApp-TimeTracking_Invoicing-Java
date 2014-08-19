package timetracking.qbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qbo.QboCompanyService;
import timetracking.domain.AppInfo;
import timetracking.domain.Company;
import timetracking.repository.AppInfoRepository;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/25/14
 * Time: 8:10 AM
 */
@Service
public class QBOCompanyServiceFactory {

    @Autowired
    private AppInfoRepository appInfoRepository;

    public QboCompanyService create(Company domainCompany) {

        final AppInfo appInfo = appInfoRepository.findAll().iterator().next();

        QboCompanyService companyService = new QboCompanyService(appInfo.getAppToken(),
                appInfo.getConsumerKey(),
                appInfo.getConsumerSecret(),
                domainCompany.getAccessToken(),
                domainCompany.getAccessTokenSecret(),
                domainCompany.getQboId(),
                new QBOCompanyTranslatorImpl(domainCompany));

        return companyService;
    }
}

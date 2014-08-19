package qbo;

import com.intuit.ipp.data.Company;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/25/14
 * Time: 7:49 AM
 */
public interface QBOCompanyTranslator {

    //    public com.intuit.ipp.data.Company toQBOCompany();
    public void updateDomainCompany(Company qboComapny);

}

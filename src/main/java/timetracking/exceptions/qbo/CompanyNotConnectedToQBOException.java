package timetracking.exceptions.qbo;

import timetracking.domain.Company;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 1:00 PM
 */
public class CompanyNotConnectedToQBOException extends RuntimeException {

    public CompanyNotConnectedToQBOException(Company company) {
        super("Company is not connected to QBO: " + company.getName());
    }
}

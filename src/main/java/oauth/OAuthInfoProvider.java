package oauth;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 7/17/14
 * Time: 2:26 PM
 */
public interface OAuthInfoProvider {

    public String getAppToken();

    public String getConsumerKey();
    public String getConsumerSecret();

    /**
     * Persist the request token values for a given company in your app
     * @param appCompanyId
     * @param requestTokenValues
     */
    public void setRequestTokenValuesForCompany(String appCompanyId, RequestTokenValues requestTokenValues);

    /**
     *
     * @param requestToken
     * @return
     */
    public CompanyRequestTokenSecret getCompanyRequestTokenSecret(String requestToken);
    public void setAccessTokenForCompany(String appCompanyId, String realmId, AccessTokenValues accessTokenValues);
}

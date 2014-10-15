package com.intuit.developer.sampleapp.timetracking.oauth;

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
     *
     * @param appCompanyId
     * @param requestToken
     * @param requestTokenSecret
     */
    public void setRequestTokenValuesForCompany(String appCompanyId, String requestToken, String requestTokenSecret);

    /**
     * Lookup the request token secret and appCompanyId by a request token in your app
     *
     * @param requestToken
     * @return
     */
    public CompanyRequestTokenSecret getCompanyRequestTokenSecret(String requestToken);

    /**
     * Persist the accessToken, accessTokenSecret and realmId in your app for a given company
     *
     * @param appCompanyId
     * @param realmId
     * @param accessToken
     * @param accessTokenSecret
     */
    public void setAccessTokenForCompany(String appCompanyId, String realmId, String accessToken, String accessTokenSecret);
}

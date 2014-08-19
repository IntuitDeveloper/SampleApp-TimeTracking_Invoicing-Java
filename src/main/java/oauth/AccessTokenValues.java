package oauth;

/**
* Created with IntelliJ IDEA.
* User: russellb337
* Date: 7/17/14
* Time: 9:43 AM
*/
public class AccessTokenValues {

    private final String accessToken;
    private final String accessTokenSecret;

    public AccessTokenValues(String accessToken, String accessTokenSecret) {
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccessTokenValues{");
        sb.append("accessToken='").append(accessToken).append('\'');
        sb.append(", accessTokenSecret='").append(accessTokenSecret).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

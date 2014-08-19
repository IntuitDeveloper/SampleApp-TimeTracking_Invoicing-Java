package oauth;

/**
* Created with IntelliJ IDEA.
* User: russellb337
* Date: 7/17/14
* Time: 9:44 AM
*/
public class RequestTokenValues {
    private final String requestToken;
    private final String requestTokenSecret;

    public RequestTokenValues(String requestToken, String requestTokenSecret) {
        this.requestToken = requestToken;
        this.requestTokenSecret = requestTokenSecret;
    }

    public String getRequestToken() {
        return requestToken;
    }
    public String getRequestTokenSecret() {
        return requestTokenSecret;
    }

}

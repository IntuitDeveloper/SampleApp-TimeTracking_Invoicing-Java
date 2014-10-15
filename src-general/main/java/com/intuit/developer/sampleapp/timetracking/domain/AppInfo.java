package com.intuit.developer.sampleapp.timetracking.domain;

import javax.persistence.*;

/**
 * A sample app domain entity that holds the app token, consumer key, and consumer secret that identifies the
 * sample app to the Intuit APIs.
 * <p/>
 * In the sample app, there should only ever be one of these entities in the database.
 * <p/>
 * User: russellb337
 * Date: 6/24/14
 * Time: 12:58 PM
 */
@Entity
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String appToken;

    @Column(nullable = false)
    private String consumerKey;

    @Column(nullable = false)
    private String consumerSecret;

    public AppInfo(String appToken, String consumerKey, String consumerSecret) {
        this.appToken = appToken;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public AppInfo() {

    }

    public long getId() {
        return id;
    }

    public String getAppToken() {
        return appToken;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }
}

package jp.shts.streamobserver.configuration;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfiguration {

    private static final String oAuthConsumerKey = "";
    private static final String oAuthConsumerSecret = "";
    private static final String oAuthAccessToken = "";
    private static final String oAuthAccessTokenSecret = "";

    private static Configuration sSingleton;

    public static Configuration get() {

        if (sSingleton == null) {
            sSingleton = build();
        }
        return sSingleton;
    }

    private static Configuration build() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        // アプリ固有の情報
        builder.setOAuthConsumerKey(oAuthConsumerKey);
        builder.setOAuthConsumerSecret(oAuthConsumerSecret);
        // アプリ＋ユーザー固有の情報
        builder.setOAuthAccessToken(oAuthAccessToken);
        builder.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);
        return builder.build();
    }
}

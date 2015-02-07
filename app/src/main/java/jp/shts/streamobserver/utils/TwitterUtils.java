package jp.shts.streamobserver.utils;

import android.os.Handler;
import android.util.Log;

import jp.shts.streamobserver.configuration.TwitterConfiguration;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;

public class TwitterUtils {

    public interface GetIdHandler {
        public void onSuccess(long id);
        public void onFailure();
    }

    public static boolean getUserId(final GetIdHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: should save id at preferences!
                long id = getUserId();
                if (id != -1) {
                    handler.onSuccess(id);
                } else {
                    handler.onFailure();
                }
            }
        }).start();
        return true;
    }

    private static long getUserId() {
        long id = -1;
        Configuration conf = TwitterConfiguration.get();
        TwitterFactory twitterFactory = new TwitterFactory(conf);
        Twitter twitter = twitterFactory.getInstance();
        // TODO: should save id at preferences!
        String screenName = "ngf_observer";
        try {
            User user = twitter.showUser(screenName);
            id = user.getId();
            Log.i("USER", "get user id : " + id);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return id;
    }
}

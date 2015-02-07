package jp.shts.streamobserver.observers;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jp.shts.streamobserver.utils.ShortUrl;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.URLEntity;

public abstract class TweetObserver implements StatusListener {

    private static final String TAG = TweetObserver.class.getSimpleName();

    @Override
    public void onStatus(Status status) {
        Log.i("onStatus", "Tweet : " + status.getText());

        String[] urls = null;

        //. ツイート本文にリンクURLが含まれていれば取り出す
        URLEntity[] uentitys = status.getURLEntities();
        if( uentitys != null && uentitys.length > 0 ){
            List list = new ArrayList();
            for( int i = 0; i < uentitys.length; i ++ ){
                URLEntity uentity = uentitys[i];
                String expandedURL = uentity.getExpandedURL();
                list.add( expandedURL );
            }
            urls = ( String[] )list.toArray( new String[0] );
        }

        if (urls == null || urls.length < 0) {
            return;
        }

        for (String url : urls) {
            Log.d("onStatus", "Url short : " + url);
            ShortUrl.expand(url, new ShortUrl.OnExpandedListener() {
                @Override
                public void onSuccess(String url) {
                    Log.d(TAG, "success to expand : " + url);
                    // TODO: only first url. check validate.
                    if (TextUtils.isEmpty(url)) {

                    }
                    onUpdate(url);
                }
                @Override
                public void onFailure() {
                    Log.e(TAG, "failed to expand");
                }
            });
        }

    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        Log.i("onDeletionNotice", "statusDeletionNotice : " + statusDeletionNotice.toString());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        Log.i("onTrackLimitationNotice", "numberOfLimitedStatuses : " + numberOfLimitedStatuses);

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {
        Log.w("onException", "Exception : " + ex);
    }

    public abstract void onUpdate(String url);
}

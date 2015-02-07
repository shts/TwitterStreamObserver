package jp.shts.streamobserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

import jp.shts.streamobserver.configuration.TwitterConfiguration;
import jp.shts.streamobserver.observers.TweetObserver;
import jp.shts.streamobserver.observers.TweetUpdateListener;
import jp.shts.streamobserver.utils.TwitterUtils;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

public class ObserverService extends Service {

    private static final String TAG = ObserverService.class.getSimpleName();

    private static ObserverService sObserverService;

    private static boolean sIsStopSelf = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand.");

        if (sIsStopSelf) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sObserverService != null) {
                        sObserverService.stopSelf();
                    }
                }
            }, 500);
            return START_NOT_STICKY;
        }

        sObserverService = this;
        startObserve();

        return START_STICKY;
    }

    private void startObserve() {

        TwitterUtils.getUserId(new TwitterUtils.GetIdHandler() {
            @Override
            public void onSuccess(long id) {
                // TODO: そのほか切断条件や切断検知についてもAPIをよく理解すること
                Configuration conf = TwitterConfiguration.get();
                TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
                // 2. TwitterStream をインスタンス化する
                TwitterStream twitterStream = twitterStreamFactory.getInstance();

                // ユーザーストリーム操作
                twitterStream.addListener(new TweetUpdateListener());

                // 検索用のフィルターを作ります
                long[] list = {id};
                FilterQuery filterQuery = new FilterQuery(list);
                // フィルターします
                twitterStream.filter(filterQuery);
                Log.i("", "set id");
            }

            @Override
            public void onFailure() {
                Log.i("", "failed to get user id");
            }
        });
    }

    private static void stopObserver() {
        Configuration conf = TwitterConfiguration.get();
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
        TwitterStream twitterStream = twitterStreamFactory.getInstance();
        twitterStream.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startResident(Context context) {
        if (sObserverService == null) {
            sIsStopSelf = false;
            Intent intent = new Intent(context, ObserverService.class);
            context.startService(intent);
        }
    }

    public static void stopResidentIfActive() {
        if (sObserverService != null) {
            sObserverService.stopSelf();
            sObserverService = null;
            sIsStopSelf = true;
//            Configuration conf = TwitterConfiguration.get();
//            TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
//            TwitterStream twitterStream = twitterStreamFactory.getInstance();
//            twitterStream.shutdown();
            stopObserver();
        }
    }
}

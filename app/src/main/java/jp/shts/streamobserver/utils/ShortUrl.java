package jp.shts.streamobserver.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ShortUrl {

    private static final String TAG = ShortUrl.class.getSimpleName();

    private static final Set<URL> services;

    public interface OnExpandedListener {
        public void onSuccess(String url);
        public void onFailure();
    }

    static {
        try {
            Set<URL> tmp = new HashSet<URL>();
            tmp.add(new URL("http://t.co/"));
            tmp.add(new URL("http://bit.ly/"));
            tmp.add(new URL("http://goo.gl/"));
            services = Collections.unmodifiableSet(tmp);
        } catch (MalformedURLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Add
    public static boolean expand(final String urlStr, final OnExpandedListener handler) {

        if (TextUtils.isEmpty(urlStr)) {
            return false;
        }

        if (handler == null) {
            return false;
        }

        final URL url = convert(urlStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.onSuccess(expand(url).toString());
                } catch (IOException e) {
                    Log.e(TAG, "failed to expanded : " + e);
                    handler.onFailure();
                }
            }
        }).start();

        return true;
    }

    // Add
    private static URL convert(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, "cannot create URL" + e);
        }
        return url;
    }

    private static URL expand(URL shortUrl) throws IOException {
        boolean isTarget = false;
        for (URL service : services) {
            if (service.getProtocol().equals(shortUrl.getProtocol())
                    && service.getHost().equals(shortUrl.getHost())) {
                isTarget = true;
                break;
            }
        }
        if (!isTarget) {
            return shortUrl;
        }

        HttpURLConnection conn = (HttpURLConnection) shortUrl.openConnection();
        try {
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("HEAD");
            conn.setDoOutput(false);
            int statusCode = conn.getResponseCode();
            if (statusCode == 301) {
                String tmpUrlStr = conn.getHeaderField("Location");
                return new URL(tmpUrlStr);
            } else {
                return shortUrl;
            }
        } finally {
            conn.disconnect();
        }
    }



}

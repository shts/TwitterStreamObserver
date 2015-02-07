package jp.shts.streamobserver;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import jp.shts.streamobserver.utils.ShortUrl;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.ok);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObserverService.startResident(getApplicationContext());
            }
        });
        Button finish = (Button) findViewById(R.id.cancel);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObserverService.stopResidentIfActive();
            }
        });

    }

}

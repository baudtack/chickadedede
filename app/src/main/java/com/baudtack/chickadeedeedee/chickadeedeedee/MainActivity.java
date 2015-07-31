package com.baudtack.chickadeedeedee.chickadeedeedee;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.SharedPreferences;

import twitter4j.Twitter;
import twitter4j.*;
import twitter4j.Status;

import java.util.List;

import twitter4j.TwitterException;

import android.webkit.WebView;

import twitter4j.auth.RequestToken;
import twitter4j.auth.AccessToken;

import android.widget.Toast;
import android.os.StrictMode;
import android.content.Intent;
import android.net.Uri;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button btnLogin;
    EditText etConsumerKey;
    EditText etConsumerSecret;
    Twitter twitter;
    RequestToken requestToken;
    AccessToken accessToken;

    public static final String PREFS_NAME = "chickadeedeedee_prefs";
    public static final String CALLBACK_URL = "callback://com.baudtack.chickadeedeedee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String consumerKey = settings.getString("consumer_key", "");
        String consumerSecret = settings.getString("consumer_secret", "");

        etConsumerKey = (EditText) findViewById(R.id.etConsumerKey);
        etConsumerSecret = (EditText) findViewById(R.id.etConsumerSecret);

        etConsumerKey.setText(consumerKey);
        etConsumerSecret.setText(consumerSecret);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        etConsumerKey = (EditText) findViewById(R.id.etConsumerKey);
        etConsumerSecret = (EditText) findViewById((R.id.etConsumerSecret));
        String consumerKey = etConsumerKey.getText().toString();
        String consumerSecret = etConsumerSecret.getText().toString();
        editor.putString("consumer_key", consumerKey);
        editor.putString("consumer_secret", consumerSecret);
        editor.commit();

        AsyncTwitterFactory factory = new AsyncTwitterFactory();

        twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                editor.putString("req_token", requestToken.getToken());
                editor.putString("req_secret", requestToken.getTokenSecret());
                editor.commit();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
                System.out.println(requestToken.getAuthenticationURL());
                startActivity(intent);
            } catch (TwitterException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.getIntent() != null && this.getIntent().getData() != null) {
            Uri uri = this.getIntent().getData();
            System.out.println(uri.toString());
            if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
                try {
                    twitter = TwitterFactory.getSingleton();
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();


                    System.out.println(uri.getQueryParameter("oauth_verifier"));
                    requestToken = new RequestToken(settings.getString("req_token", ""), settings.getString("reg_secret", ""));
                    AccessToken at = twitter.getOAuthAccessToken(requestToken, uri.getQueryParameter("oauth_verifier"));

                    editor.putString("accessToken", at.getToken());
                    editor.putString("accessSecret", at.getTokenSecret());
                    editor.commit();

                    setContentView(R.layout.activity_main);
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show();

                    try {
                        List<Status> statuses = twitter.getHomeTimeline();
                        System.out.println("Showing home timeline.");
                        for (Status status : statuses) {
                            System.out.println(status.getUser().getName() + ":" +
                                    status.getText());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

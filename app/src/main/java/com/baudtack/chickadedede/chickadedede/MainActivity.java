package com.baudtack.chickadedede.chickadedede;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.SharedPreferences;

public class MainActivity extends ActionBarActivity {

    Button btnLogin;
    EditText etConsumerKey;
    EditText etConsumerSecret;
    public static final String PREFS_NAME = "chickadedede_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                etConsumerKey = (EditText)findViewById(R.id.etConsumerKey);
                etConsumerSecret = (EditText)findViewById((R.id.etConsumerSecret));
                editor.putString("consumer_key", etConsumerKey.getText().toString());
                editor.putString("consumer_secret", etConsumerSecret.getText().toString());
                editor.commit();
            }});

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String consumerKey = settings.getString("consumer_key", "");
        String consumerSecret = settings.getString("consumer_secret", "");

        etConsumerKey = (EditText)findViewById(R.id.etConsumerKey);
        etConsumerSecret = (EditText)findViewById(R.id.etConsumerSecret);

        etConsumerKey.setText(consumerKey);
        etConsumerSecret.setText(consumerSecret);

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

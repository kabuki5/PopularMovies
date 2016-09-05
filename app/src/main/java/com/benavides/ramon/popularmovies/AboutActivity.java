package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Function:
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(R.string.about);

        TextView versionTev = (TextView)findViewById(R.id.about_version);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionName = getString(R.string.version)+versionName;
            versionTev.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



    }

    public void onButtonsClick(View view) {
        int id = view.getId();
        String url = "";
        switch (id){
            case R.id.about_optimus_btn:
                url = "http://www.optimusmobile.es/";
                break;
            case R.id.about_github_btn:
                url = "https://www.github.com/kabuki5";
                break;
            case R.id.about_udacity_btn:
                url = "https://profiles.udacity.com/u/ramnbenavides";
                break;
        }

        Intent buttonIntent = new Intent(Intent.ACTION_VIEW);
        buttonIntent.setData(Uri.parse(url));
        startActivity(buttonIntent);


    }
}

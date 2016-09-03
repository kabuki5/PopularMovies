package com.benavides.ramon.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benavides.ramon.popularmovies.fragments.ActorInfoFragment;

/**
 * Function:
 */
public class ActorInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_info);

        if(savedInstanceState == null){
            if(getIntent().getExtras()!= null){
                int actorId = getIntent().getExtras().getInt(getString(R.string.actor_id_param));
                getSupportFragmentManager().beginTransaction().add(R.id.actor_info_container, ActorInfoFragment.newInstance(actorId),"info").commit();

            }
        }


    }
}

package com.thunder.thunderplane;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thunder.thunderplane.base.BaseActivity;
import com.thunder.thunderplane.tool.MusicTool;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        MusicTool.INSTANCE.playLaunchMusic();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicTool.INSTANCE.releaseAllMusic();
    }
}
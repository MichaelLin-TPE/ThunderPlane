package com.thunder.thunderplane;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.thunder.thunderplane.base.BaseActivity;
import com.thunder.thunderplane.level_page.LevelActivity;
import com.thunder.thunderplane.playground.PlayGroundActivity;
import com.thunder.thunderplane.tool.MusicTool;
import com.thunder.thunderplane.tool.Tool;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //透明度動畫
        ImageView ivPress = findViewById(R.id.iv_press);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.2f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        ivPress.startAnimation(alphaAnimation);

        ConstraintLayout menuView = findViewById(R.id.menu_view);


        ConstraintLayout root = findViewById(R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Michael","點擊背景");
                ivPress.clearAnimation();
                ivPress.setVisibility(View.GONE);
                Tool.INSTANCE.expend(menuView,1000,Tool.INSTANCE.getPixel(LauncherActivity.this,200));
            }
        });

        TextView tvPlayGame = findViewById(R.id.play_game);
        TextView tvSetting = findViewById(R.id.setting);
        TextView tvCloseGame = findViewById(R.id.close);

        tvPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, LevelActivity.class);
                startActivity(intent);
            }
        });

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvCloseGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        MusicTool.INSTANCE.playLaunchMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicTool.INSTANCE.releaseAllMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicTool.INSTANCE.stopLaunchMusic();
    }
}
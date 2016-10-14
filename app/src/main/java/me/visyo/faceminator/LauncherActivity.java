package me.visyo.faceminator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        TextView firstLine = (TextView) findViewById(R.id.first_line);
        TextView secondLine = (TextView) findViewById(R.id.second_line);

        Shader textShader = new LinearGradient(0, 0, 0, firstLine.getTextSize(),
                Color.BLUE,Color.WHITE, Shader.TileMode.CLAMP);

        // change shader to gradient
        firstLine.getPaint().setShader(textShader);

        textShader = new LinearGradient(0, 0, 0, firstLine.getTextSize(),
                Color.BLUE,Color.WHITE, Shader.TileMode.CLAMP);
        secondLine.getPaint().setShader(textShader);

        // change typeface
        firstLine.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/terminator.ttf"));
        secondLine.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/terminator.ttf"));

        // delays for 3 seconds to show the launcher screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }
}

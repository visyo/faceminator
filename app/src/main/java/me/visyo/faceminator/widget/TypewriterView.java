package me.visyo.faceminator.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import me.visyo.faceminator.R;

/**
 * View that make typewriter effect, displaying
 * each character sequentially.
 */

public class TypewriterView extends TextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; //Default 500ms delay
    private MediaPlayer mediaPlayer;


    public TypewriterView(Context context) {
        super(context);
        playBeep();
    }

    private void playBeep() {
        if (!isInEditMode()){
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);
        }
    }

    public TypewriterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        playBeep();
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mediaPlayer.start();
//        if (mediaPlayer.isPlaying()){
//            mediaPlayer.stop();
//        }

        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}

package me.visyo.faceminator.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

import me.visyo.faceminator.GraphicOverlay;
import me.visyo.faceminator.R;

/**
 * View to detect faces.
 */

public class FaceDetectorView extends FrameLayout {
    private static final String TAG = "FaceDetectorView";
    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;
    private GraphicOverlay mOverlay;
    private TypewriterView detectionStatus;

    public FaceDetectorView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        init(mContext, attrs);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private void init(Context context, AttributeSet attrs){
        this.mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        // inflate all views
        inflate(context, R.layout.face_detector_view, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());

        detectionStatus = (TypewriterView) findViewById(R.id.detection_status);
        detectionStatus.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/terminator.ttf"));
        detectionStatus.setCharacterDelay(100);
        detectionStatus.animateText(context.getString(R.string.searching_target));

        View detectionSquare = findViewById(R.id.detection_square);

        // blink animation of detection square during targets searching
        startBlinkAnimation(detectionSquare);
    }

    private void startBlinkAnimation(View detectionSquare) {

        // blink animation
        ObjectAnimator blinkAnimation = ObjectAnimator.ofFloat(detectionSquare, "alpha", 0.0f, 1.0f);
        blinkAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        blinkAnimation.setDuration(150);
        blinkAnimation.setRepeatCount(ValueAnimator.INFINITE);
        blinkAnimation.setRepeatMode(ValueAnimator.REVERSE);
        blinkAnimation.start();
    }

    /***
     *
     */
    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (SecurityException se) {
                Log.e(TAG,"Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

    public TypewriterView getDetectionStatus() {
        return detectionStatus;
    }
}
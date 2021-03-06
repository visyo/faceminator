/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.visyo.faceminator.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import com.google.android.gms.vision.face.Face;

import java.util.Locale;

import me.visyo.faceminator.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 80.0f;
    private static final float ID_X_OFFSET = -80.0f;
    private static final float BOX_STROKE_WIDTH = 10.0f;

//    private static final int COLOR_CHOICES[] = {
//            Color.BLUE,
//            Color.CYAN,
//            Color.GREEN,
//            Color.MAGENTA,
//            Color.RED,
//            Color.WHITE,
//            Color.YELLOW
//    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    public FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        //mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        //final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(Color.WHITE);

        mIdPaint = new Paint();
        mIdPaint.setColor(Color.WHITE);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(Color.WHITE);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setAlpha(200);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    public void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateFace(Face face) {
        mFace = face;
        mFaceHappiness = face.getIsSmilingProbability();
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x + ID_X_OFFSET, y - ID_Y_OFFSET - 100, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y - ID_Y_OFFSET - 50, mIdPaint);
        canvas.drawText("happiness: " + String.format(Locale.getDefault(), "%.2f", face.getIsSmilingProbability()), x + ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);

        if (wasSadnessDetected()){
            canvas.drawText("YOU'RE SAD.", x + ID_X_OFFSET, y - ID_Y_OFFSET + 150, mIdPaint);
            canvas.drawText("TERMINATE!", x + ID_X_OFFSET, y - ID_Y_OFFSET + 200, mIdPaint);
        } else {
            canvas.drawText("YOU'RE HAPPY.", x + ID_X_OFFSET, y - ID_Y_OFFSET + 150, mIdPaint);
            canvas.drawText("TARGET SKIPPED.", x + ID_X_OFFSET, y - ID_Y_OFFSET + 200, mIdPaint);
        }

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }

    private boolean wasSadnessDetected() {
        return mFaceHappiness != -1.0f && mFaceHappiness <= 0.30f;
    }
}

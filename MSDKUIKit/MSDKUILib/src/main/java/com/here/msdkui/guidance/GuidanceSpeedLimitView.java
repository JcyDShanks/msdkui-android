/*
 * Copyright (C) 2017-2018 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.here.msdkui.guidance;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.here.msdkui.R;

/**
 * A view that shows the current speed limit. When user does not exceed speed limit, then
 * this view is hidden. This view consumes data contained in {@link GuidanceSpeedData}.
 */
public class GuidanceSpeedLimitView extends RelativeLayout {

    private GuidanceSpeedData mGuidanceSpeedData;

    /**
     * Constructs a new instance.
     *
     * @param context
     *         the required {@link Context}.
     */
    public GuidanceSpeedLimitView(Context context) {
        this(context, null);
    }

    /**
     * Constructs a new instance.
     *
     * @param context
     *         the required {@link Context}.
     *
     * @param attrs
     *         a set of attributes.
     */
    public GuidanceSpeedLimitView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructs a new instance.
     *
     * @param context
     *         the required {@link Context}.
     *
     * @param attrs
     *         a set of attributes.
     *
     * @param defStyleAttr
     *         a default style attribute.
     */
    public GuidanceSpeedLimitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Constructs a new instance.
     *
     * @param context
     *         the required {@link Context}.
     *
     * @param attrs
     *         a set of attributes.
     *
     * @param defStyleAttr
     *         a default style attribute.
     *
     * @param defStyleRes
     *         a default style resource.
     *
     * Requires Lollipop (API level 21).
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GuidanceSpeedLimitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        mGuidanceSpeedData = new GuidanceSpeedData();
        LayoutInflater.from(context).inflate(R.layout.guidance_speed_limit_panel, this);
    }

    private void populateUi(@Nullable GuidanceSpeedData data) {
        final View container = (View) findViewById(R.id.guidance_speed_limit_container);
        if (data == null) {
            container.setVisibility(GONE);
            return;
        }

        if (!data.equals(mGuidanceSpeedData)) {
            if (data.getCurrentSpeedLimit() > 0) {
                final TextView speedLimit = (TextView) findViewById(R.id.speed_limit);
                speedLimit.setText(String.valueOf(data.getCurrentSpeedLimit()));
                container.setVisibility(VISIBLE);
            } else {
                container.setVisibility(GONE);
            }
        }
    }

    /**
     * Sets current speed data.
     * @param data
     *             the data that should be used to populate this view.
     */
    public void setCurrentSpeedData(@Nullable GuidanceSpeedData data) {
        populateUi(data);

        if (data == null) {
            mGuidanceSpeedData.invalidate();
        } else if (!data.equals(mGuidanceSpeedData)) {
            mGuidanceSpeedData = new GuidanceSpeedData(data.getCurrentSpeed(), data.getCurrentSpeedLimit());
        }
    }

    /**
     * Gets current speed data.
     * @return the data that was used to populate this view.
     */
    public @NonNull GuidanceSpeedData getCurrentSpeedData() {
        return new GuidanceSpeedData(mGuidanceSpeedData.getCurrentSpeed(), mGuidanceSpeedData.getCurrentSpeedLimit());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mGuidanceSpeedData == null) {
            return superState;
        }
        final GuidanceSpeedLimitView.SavedState savedState = new GuidanceSpeedLimitView.SavedState(superState);
        savedState.setStateToSave(this.mGuidanceSpeedData);
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof GuidanceSpeedLimitView.SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final GuidanceSpeedLimitView.SavedState savedState = (GuidanceSpeedLimitView.SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentSpeedData(savedState.getSavedState());
    }

    /**
     * State class to save internal data on activity re-creation.
     */
    static class SavedState extends BaseSavedState {
        /**
         * Creator for parcelable.
         */
        public static final Parcelable.Creator<GuidanceSpeedLimitView.SavedState> CREATOR =
                new Parcelable.Creator<GuidanceSpeedLimitView.SavedState>() {
                    public GuidanceSpeedLimitView.SavedState createFromParcel(Parcel in) {
                        return new GuidanceSpeedLimitView.SavedState(in);
                    }

                    public GuidanceSpeedLimitView.SavedState[] newArray(int size) {
                        return new GuidanceSpeedLimitView.SavedState[size];
                    }
                };
        private GuidanceSpeedData mStateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);
            mStateToSave = GuidanceSpeedData.CREATOR.createFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            mStateToSave.writeToParcel(out, flags);
        }

        GuidanceSpeedData getSavedState() {
            return mStateToSave;
        }

        void setStateToSave(GuidanceSpeedData mStateToSave) {
            this.mStateToSave = mStateToSave;
        }
    }
}
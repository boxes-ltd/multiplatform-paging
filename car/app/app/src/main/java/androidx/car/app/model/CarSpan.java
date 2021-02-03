/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.car.app.model;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
 * Base class for all span types allowed for a car app.
 *
 * @see CarText
 */
@Keep
public class CarSpan extends CharacterStyle {
    @Override
    public void updateDrawState(@NonNull TextPaint tp) {
        // Do nothing
    }
}

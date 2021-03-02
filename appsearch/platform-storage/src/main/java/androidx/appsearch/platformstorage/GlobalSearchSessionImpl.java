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
package androidx.appsearch.platformstorage;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.appsearch.app.GlobalSearchSession;
import androidx.appsearch.app.SearchResults;
import androidx.appsearch.app.SearchSpec;
import androidx.appsearch.platformstorage.converter.SearchSpecToPlatformConverter;
import androidx.core.util.Preconditions;

import java.util.concurrent.ExecutorService;

/**
 * An implementation of {@link androidx.appsearch.app.GlobalSearchSession} which proxies to a
 * platform {@link android.app.appsearch.GlobalSearchSession}.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@RequiresApi(Build.VERSION_CODES.S)
class GlobalSearchSessionImpl implements GlobalSearchSession {
    private final android.app.appsearch.GlobalSearchSession mPlatformSession;
    private final ExecutorService mExecutorService;

    GlobalSearchSessionImpl(
            @NonNull android.app.appsearch.GlobalSearchSession platformSession,
            @NonNull ExecutorService executorService) {
        mPlatformSession = Preconditions.checkNotNull(platformSession);
        mExecutorService = Preconditions.checkNotNull(executorService);
    }

    @Override
    @NonNull
    public SearchResults search(
            @NonNull String queryExpression,
            @NonNull SearchSpec searchSpec) {
        Preconditions.checkNotNull(queryExpression);
        Preconditions.checkNotNull(searchSpec);
        android.app.appsearch.SearchResults platformSearchResults =
                mPlatformSession.search(
                        queryExpression,
                        SearchSpecToPlatformConverter.toPlatformSearchSpec(searchSpec));
        return new SearchResultsImpl(platformSearchResults, mExecutorService);
    }

    @Override
    public void close() {
        mPlatformSession.close();
    }
}

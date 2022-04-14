/*
 * Copyright (C) 2022 The Android Open Source Project
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
package androidx.health.data.client.records

import androidx.annotation.RestrictTo
import androidx.health.data.client.aggregate.LongAggregateMetric
import androidx.health.data.client.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset

/** Captures the user's heart rate. Each record represents a series of measurements. */
@RestrictTo(RestrictTo.Scope.LIBRARY) // Will be made public after API reviews
public class HeartRateSeries(
    override val startTime: Instant,
    override val startZoneOffset: ZoneOffset?,
    override val endTime: Instant,
    override val endZoneOffset: ZoneOffset?,
    override val samples: List<HeartRate>,
    override val metadata: Metadata = Metadata.EMPTY,
) : SeriesRecord<HeartRate> {

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HeartRateSeries) return false

        if (startTime != other.startTime) return false
        if (startZoneOffset != other.startZoneOffset) return false
        if (endTime != other.endTime) return false
        if (endZoneOffset != other.endZoneOffset) return false
        if (samples != other.samples) return false
        if (metadata != other.metadata) return false

        return true
    }

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + (startZoneOffset?.hashCode() ?: 0)
        result = 31 * result + endTime.hashCode()
        result = 31 * result + (endZoneOffset?.hashCode() ?: 0)
        result = 31 * result + samples.hashCode()
        result = 31 * result + metadata.hashCode()
        return result
    }

    companion object {
        /** Metric identifier to retrieve average heart rate from [AggregateDataRow]. */
        @JvmStatic val BPM_AVG: LongAggregateMetric = LongAggregateMetric("HeartRate", "avg", "bpm")

        /** Metric identifier to retrieve minimum heart rate from [AggregateDataRow]. */
        @JvmStatic val BPM_MIN: LongAggregateMetric = LongAggregateMetric("HeartRate", "min", "bpm")

        /** Metric identifier to retrieve maximum heart rate from [AggregateDataRow]. */
        @JvmStatic val BPM_MAX: LongAggregateMetric = LongAggregateMetric("HeartRate", "max", "bpm")
    }
}

/**
 * Represents a single measurement of the heart rate.
 *
 * @param beatsPerMinute Heart beats per minute. Validation range: 1-300.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY) // Will be made public after API reviews
public class HeartRate(
    val time: Instant,
    val beatsPerMinute: Long,
) {

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HeartRate) return false

        if (time != other.time) return false
        if (beatsPerMinute != other.beatsPerMinute) return false

        return true
    }

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + beatsPerMinute.hashCode()
        return result
    }
}

/*
 * Copyright 2023 The Android Open Source Project
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

package androidx.camera.video.internal.audio

import androidx.camera.testing.mocks.MockConsumer
import androidx.camera.testing.mocks.helpers.CallTimes
import androidx.camera.video.internal.audio.AudioStream.PacketInfo
import androidx.core.util.Preconditions.checkArgument
import java.nio.ByteBuffer
import java.util.concurrent.Executor

class FakeAudioStream(
    private val audioDataProvider: (index: Int) -> AudioData,
    isSilenced: Boolean = false,
    private val exceptionOnStart: AudioStream.AudioStreamException? = null
) : AudioStream {
    var isSilenced: Boolean = isSilenced
        set(value) {
            if (field != value) {
                field = value
                notifySilence()
            }
        }
    private val _audioDataList = mutableListOf<AudioData>()
    private val startCalls = MockConsumer<Unit>()
    private val stopCalls = MockConsumer<Unit>()
    private val releaseCalls = MockConsumer<Unit>()
    private var bufferIndex = 0
    private var isReleased = false
    private var isStarted = false
    private var audioStreamCallback: AudioStream.AudioStreamCallback? = null
    private var callbackExecutor: Executor? = null

    override fun start() {
        if (isReleased) {
            throw IllegalStateException()
        }
        if (exceptionOnStart != null) {
            throw exceptionOnStart
        }
        if (isStarted) {
            return
        }
        isStarted = true
        startCalls.accept(Unit)
        notifySilence()
    }

    override fun stop() {
        if (isReleased) {
            throw IllegalStateException()
        }
        if (!isStarted) {
            return
        }
        isStarted = false
        stopCalls.accept(Unit)
    }

    override fun release() {
        if (isReleased) {
            return
        }
        isReleased = true
        releaseCalls.accept(Unit)
    }

    override fun read(byteBuffer: ByteBuffer): PacketInfo {
        if (isReleased || !isStarted) {
            throw IllegalStateException()
        }
        val audioData = audioDataProvider.invoke(bufferIndex++)
        _audioDataList.add(audioData)
        val packet = PacketInfo.of(audioData.byteBuffer.remaining(), audioData.timestampNs)
        if (packet.sizeInBytes > 0) {
            val originalPosition = byteBuffer.position()
            byteBuffer.put(audioData.byteBuffer)
            byteBuffer.limit(byteBuffer.position())
            byteBuffer.position(originalPosition)
        }
        return packet
    }

    override fun setCallback(callback: AudioStream.AudioStreamCallback?, executor: Executor?) {
        checkArgument(
            callback == null || executor != null,
            "executor can't be null with non-null callback."
        )
        audioStreamCallback = callback
        callbackExecutor = executor
        notifySilence()
    }

    fun getAudioDataList(): List<AudioData> {
        return _audioDataList
    }

    fun verifyStartCall(
        callTimes: CallTimes,
        timeoutMs: Long = MockConsumer.NO_TIMEOUT,
        inOder: Boolean = false
    ) = startCalls.verifyAcceptCall(
        Unit::class.java,
        inOder,
        timeoutMs,
        callTimes,
    )

    fun verifyStopCall(
        callTimes: CallTimes,
        timeoutMs: Long = MockConsumer.NO_TIMEOUT,
        inOder: Boolean = false
    ) = stopCalls.verifyAcceptCall(
        Unit::class.java,
        inOder,
        timeoutMs,
        callTimes,
    )

    fun verifyReleaseCall(
        callTimes: CallTimes,
        timeoutMs: Long = MockConsumer.NO_TIMEOUT,
        inOder: Boolean = false
    ) = releaseCalls.verifyAcceptCall(
        Unit::class.java,
        inOder,
        timeoutMs,
        callTimes,
    )

    private fun notifySilence() {
        if (!isStarted || isReleased) {
            return
        }
        callbackExecutor?.execute {
            audioStreamCallback?.onSilenceStateChanged(isSilenced)
        }
    }

    data class AudioData(val byteBuffer: ByteBuffer, val timestampNs: Long)
}

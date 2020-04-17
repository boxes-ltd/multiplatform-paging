/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.ui.tooling

import androidx.test.filters.SmallTest
import androidx.ui.core.DensityAmbient
import androidx.ui.core.DrawLayerModifier
import androidx.ui.core.DrawModifier
import androidx.ui.core.LayoutModifier2
import androidx.ui.core.Modifier
import androidx.ui.core.OwnedLayer
import androidx.ui.core.drawLayer
import androidx.ui.core.positionInRoot
import androidx.ui.foundation.Box
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.unit.px
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class ModifierInfoTest : ToolingTest() {
    fun Group.all(): Collection<Group> =
        listOf(this) + this.children.flatMap { it.all() }

    @Test
    fun testBounds() {
        show {
            Inspectable {
                with(DensityAmbient.current) {
                    val px10 = 10.px.toDp()
                    val px5 = 5.px.toDp()
                    Box {
                        Column(Modifier.padding(px10).drawLayer().drawBackground(Color.Blue)) {
                            Box(Modifier.padding(px5).size(px5))
                        }
                    }
                }
            }
        }

        activityTestRule.runOnUiThread {
            val modifierInfo = tables.findGroupForFile("ModifierInfoTest")!!.all()
                .map {
                    it.modifierInfo
                }
                .filter { it.isNotEmpty() }
                .sortedBy { it.size }

            assertEquals(2, modifierInfo.size)

            val boxModifierInfo = modifierInfo[0]
            assertEquals(2, boxModifierInfo.size)
            assertTrue("Box should only have LayoutModifiers, but the first was " +
                "${boxModifierInfo[0].modifier}", boxModifierInfo[0].modifier is LayoutModifier2)
            assertEquals(10.px, boxModifierInfo[0].coordinates.positionInRoot.x)

            @Suppress("DEPRECATION")
            assertTrue("Box should only have LayoutModifiers, but the second was " +
                    "${boxModifierInfo[1].modifier}",
                boxModifierInfo[1].modifier is androidx.ui.core.LayoutModifier
            )
            assertEquals(15.px, boxModifierInfo[1].coordinates.positionInRoot.x)

            val columnModifierInfo = modifierInfo[1]
            assertEquals(3, columnModifierInfo.size)
            assertTrue(
                "The first modifier in the column should be a LayoutModifier" +
                        "but was ${columnModifierInfo[0].modifier}",
                columnModifierInfo[0].modifier is LayoutModifier2
            )
            assertEquals(0.px, columnModifierInfo[0].coordinates.positionInRoot.x)
            assertTrue(
                "The second modifier in the column should be a DrawLayerModifier" +
                        "but was ${columnModifierInfo[1].modifier}",
                columnModifierInfo[1].modifier is DrawLayerModifier
            )
            assertTrue(columnModifierInfo[1].extra is OwnedLayer)
            assertEquals(10.px, columnModifierInfo[1].coordinates.positionInRoot.x)
            assertTrue(
                "The third modifier in the column should be a DrawModifier" +
                        "but was ${columnModifierInfo[2].modifier}",
                columnModifierInfo[2].modifier is DrawModifier
            )
            assertEquals(10.px, columnModifierInfo[2].coordinates.positionInRoot.x)
        }
    }
}
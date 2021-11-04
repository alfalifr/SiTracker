package sidev.app.android.sitracker.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
  @Test
  fun getIndexWithMaskTest() {
    var allMask = 0
    val m1 = 1 shl 0
    val m2 = 1 shl 1
    val m3 = 1 shl 2
    val m4 = 1 shl 3

    /*

    val mList = listOf(
      m1, m2, m3, m4,
    )

    val randomSubset = mList.randomSubset()

    for(m in randomSubset) {
      allMask = allMask or m
    }

    for(m in randomSubset) {
      assert(allMask hasMask m)
    }

    for(m in randomSubset) {
      assert(allMask hasMask m)
    }
     */

    allMask = allMask or m1
    allMask = allMask or m2
    //allMask = allMask or m3
    allMask = allMask or m4

    assert(allMask hasMask m1)
    assert(allMask hasMask m2)
    assert(allMask notHasMask m3)
    assert(allMask hasMask m4)

    assertEquals(0, getIndexWithMask(allMask, m1))
    assertEquals(1, getIndexWithMask(allMask, m2))
    assertEquals(null, getIndexWithMask(allMask, m3))
    assertEquals(2, getIndexWithMask(allMask, m4))
  }
}
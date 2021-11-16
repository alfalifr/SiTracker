package sidev.app.android.sitracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun WrappingRow_preview() {
  WrappingRow(
    Modifier
      .width(150.dp)
      .background(Color.Red),
    horizontalArrangement = Arrangement.SpaceEvenly, //Arrangement.spacedBy(58.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Bottom
  ) {
    for(i in 0 until 5) {
      Text(
        text = i.toString(),
        modifier = Modifier
          //.size(30.dp)
          .height((i*30+20).dp)
          .background(Color.Blue)
          .padding(5.dp),
      )
    }
  }
}

//*
/*
TODO: Make fun in future
*/
@Composable
fun WrappingRow(
  modifier: Modifier = Modifier,
  /*
  TODO: For now, the arrangement is start and the alignment is top.
  horizontalArrangement: Arrangement.Horizontal,
  verticalAlignment: Alignment.Vertical,
   */
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  verticalArrangement: Arrangement.Vertical = Arrangement.Top,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  content: @Composable () -> Unit,
) {
  Layout(
    modifier = modifier,
    content = content,
    measurePolicy = { measurables, parentConstraints ->
      //var rowCount = 1
      val maxWidth = parentConstraints.maxWidth
      //var maxHeight = constraints.maxHeight

      //Contains a bunch of list of placeable of each row.
      val allRowPlaceables = mutableListOf<List<Placeable>>()
      val eachRowHeight = mutableListOf<Int>()
      val eachRowWidth = mutableListOf<Int>()
      val eachRowChildXStart = mutableListOf<List<Int>>()
      val eachRowChildYStart = mutableListOf<List<Int>>()
      val eachRowYStart = mutableListOf<Int>()

      //constraints don't change because the children can overflow to next line.
      // why all children are measured in the beginning? to avoid re-measurable exception.
      // TODO: Is it really the best way to measure all children in the beginning?
      val constraints = parentConstraints.copy(
        minWidth = 0,
        minHeight = 0,
      )
      val placeables = measurables.map { it.measure(constraints) }

      placeables.forEachIndexed { i, it ->
        println("i = $i width = ${it.width} maxWidth = $maxWidth constraints = $constraints")
      }

      /**
       * Returns true if the measurement doesn't reach the end of [measurables].
       */
      fun measureEachRow(start: Int): Boolean {
        if(start >= measurables.size) {
          return false
        }
        val eachRowPlaceables = mutableListOf<Placeable>()

        var i = start -1
        var accWidth = 0

        var greatestHeight = -1 //There is no way height can be negative.

        while(++i < measurables.size) {
          //max width doesn't decrease here because the overflown child will be placed below, unlike the conventional Row.
          val placeable = placeables[i] //measurables[i].measure(rowConstraints)
          eachRowPlaceables += placeable

          val tempAcc = accWidth + placeable.width
          println("i = $i tempAcc = $tempAcc maxWidth = $maxWidth accWidth = $accWidth")
          if(tempAcc <= maxWidth) {
            accWidth = tempAcc
            if(greatestHeight < placeable.height) {
              greatestHeight = placeable.height
            }
          } else {
            break
          }
        }
        if(eachRowPlaceables.isNotEmpty()) {
          /**
           * Returns the result of all children's X start
           * that **FIT** the [eachRowMaxWidth]
           */
          fun measureChildHorizontalArrangement(
            eachRowPlaceables: List<Placeable>,
            eachRowMaxWidth: Int,
          ): List<Int> {
            val outXStarts = IntArray(eachRowPlaceables.size)

            val sizes = eachRowPlaceables.map { it.width }.let { list ->
              IntArray(list.size) { list[it] }
            }

            with(horizontalArrangement) {
              arrange(
                eachRowMaxWidth,
                sizes,
                layoutDirection,
                outXStarts,
              )
            }

            fun getOverlappingChildIndex(
              sizes: List<Int>,
              starts: List<Int>,
            ): Int {
              if(sizes.size != starts.size) {
                throw IllegalArgumentException(
                  "sizes.size (${sizes.size}) != starts.size (${starts.size})"
                )
              }
              var acc = 0
              starts.forEachIndexed { index, i ->
                if(i < acc) {
                  return index
                }
                acc = i + sizes[index]
              }
              return -1
            }
            var u = getOverlappingChildIndex(
              sizes.asList(),
              outXStarts.asList(),
            )
            println("outXStarts = ${outXStarts.joinToString()}")

            if(u < 0) {
              u = sizes.size
            }

            /*
            var u = outXStarts.lastIndex
            do {
              val rowWidth = outXStarts[u] + eachRowPlaceables[u].width
              println("rowWidth = $rowWidth eachRowMaxWidth= $eachRowMaxWidth horizontalArrangement.spacing = ${horizontalArrangement.spacing} u = $u")
              println("outXStarts[u] = ${outXStarts[u]} eachRowPlaceables[u].width = ${eachRowPlaceables[u].width}")
            } while(rowWidth > eachRowMaxWidth && --u >= 0)
            // here `u` is first index of `eachRowPlaceables` that DOESN'T FIT `eachRowMaxWidth`
            // except when `u` < 0 (when -1)

             */

            /*
            var u = -1
            var arrangedAccWidth = 0
            while(++u < eachRowPlaceables.size) {
              val tempAcc = arrangedAccWidth +
                eachRowPlaceables[u].width +
                outXStarts[u]
              if(tempAcc <= eachRowMaxWidth) {
                arrangedAccWidth = tempAcc
              } else {
                break
              }
            }
             */

            // TODO: For now, we hope there's no way `u <= 0` (the arrangement results empty child)
            println("outXStarts.asList().subList(0, u) u = $u")
            return outXStarts.asList().subList(0, u)
          }

          fun measureChildVerticalAlignment(
            eachRowPlaceables: List<Placeable>,
            eachRowMaxHeight: Int,
          ): List<Int> = with(verticalAlignment) {
            eachRowPlaceables.map {
              align(it.height, eachRowMaxHeight)
            }
          }

          val childXStart = measureChildHorizontalArrangement(
            eachRowPlaceables, maxWidth,
          ).also {
            eachRowChildXStart += it
          }

          val firstIndexOverflow = childXStart.size

          println("firstIndexOverflow = $firstIndexOverflow i = $i start = $start eachRowPlaceables.size = ${eachRowPlaceables.size}")
          //Then it means there some placeables overflow the `maxWidth`.
          if(firstIndexOverflow < eachRowPlaceables.size) {
            for(o in eachRowPlaceables.lastIndex downTo firstIndexOverflow) {
              accWidth -= eachRowPlaceables.removeLast().width
            }
            greatestHeight = eachRowPlaceables.maxOf { it.height }
            i = firstIndexOverflow
          }

          //println("measureChildHorizontalArrangement result = $it")
          accWidth = childXStart.lastOrNull()
            ?.plus(eachRowPlaceables.last().width)
            ?: 0

          eachRowChildYStart += measureChildVerticalAlignment(
            eachRowPlaceables, greatestHeight,
          )

          // TODO: For now, we hope there's no way `firstIndexOverflow <= 0` (the arrangement results empty child)
          allRowPlaceables += eachRowPlaceables
          eachRowHeight += greatestHeight
          eachRowWidth += accWidth
          //maxHeight -= greatestHeight
        }
        println("start = $start i = $i measurables.size = ${measurables.size}")
        return i < measurables.size
      }

      var eachRowStart = 0
      while(measureEachRow(eachRowStart).also { println("measureEachRow(eachRowStart) = $it eachRowStart= $eachRowStart") }) {
        eachRowStart += allRowPlaceables.last().size
      }

      with(verticalArrangement) {
        val eachRowYOutStart = IntArray(eachRowHeight.size)
        val spacingPx = spacing.roundToPx()
        arrange(
          totalSize = if(constraints.maxHeight < Int.MAX_VALUE) constraints.maxHeight
            else eachRowHeight.reduce { acc, i -> acc + i + spacingPx },
          sizes = IntArray(eachRowHeight.size) { eachRowHeight[it] },
          outPositions = eachRowYOutStart,
        )
        eachRowYStart += eachRowYOutStart.asList()
      }

      println("""
        allRowPlaceables = $allRowPlaceables
        eachRowHeight = $eachRowHeight
        eachRowWidth = $eachRowWidth        
        eachRowChildXStart = $eachRowChildXStart
        eachRowChildYStart = $eachRowChildYStart
        eachRowYStart = $eachRowYStart
      """.trimIndent())

      val contentWidth = (eachRowWidth.maxOrNull() ?: 0).also { println("layout width = $it") }
      val contentHeight = eachRowYStart.lastOrNull()
        ?.plus(eachRowHeight.last())
        ?: 0

      println("""
        contentWidth = $contentWidth
        contentHeight = $contentHeight
        parentConstraints = $parentConstraints
      """.trimIndent())
      layout(
        width = if(contentWidth >= parentConstraints.minWidth) contentWidth
          else parentConstraints.minWidth, //There must be no way `contentWidth` can be bigger than parent width cuz the children measurement is constraint'd to parent max width.
        height = if(contentHeight >= parentConstraints.minHeight) contentHeight
          else parentConstraints.minHeight,
        /*
        eachRowHeight.foldIndexed(0) { index, acc, i ->
          acc + i + eachRowYStart[index]
        }.also {
               println("layout height = $it")
        },
        */
      ) {
        //var currentBaseline = 0
        for(i in allRowPlaceables.indices) {
          for(u in allRowPlaceables[i].indices) {
            allRowPlaceables[i][u].place(
              x = eachRowChildXStart[i][u],
              y = eachRowChildYStart[i][u] +
                eachRowYStart[i],
            )
          }
        }
      }
    }
  )
}

// */

/*

private fun wrapMeasurePolicy(
  orientation: Direction,
  arrangement: Arrangement,
): MeasurePolicy {
  //orientation.declaringClass
  return object: MeasurePolicy {
    override fun MeasureScope.measure(
      measurables: List<Measurable>,
      constraints: Constraints
    ): MeasureResult {

    }
  }
}
 */
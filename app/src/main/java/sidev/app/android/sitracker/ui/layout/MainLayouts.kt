package sidev.app.android.sitracker.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sidev.app.android.sitracker.ui.component.DefaultText
import sidev.app.android.sitracker.ui.component.IconProgressionPic
import sidev.app.android.sitracker.ui.component.Placeholder
import sidev.app.android.sitracker.ui.component.measurePrefixPostfixComponents
import sidev.app.android.sitracker.ui.model.ActionData
import sidev.app.android.sitracker.util.*
import sidev.app.android.sitracker.util.Const.contentPaddingDp
import sidev.app.android.sitracker.util.Const.iconSizeDp
import sidev.app.android.sitracker.util.model.DpSize
import sidev.app.android.sitracker.util.model.sizeToDp


///*
/**
 * [ignoreContentPadding] means whether this method
 * draw with content padding or not. If [ignoreContentPadding] is true,
 * then this method draws each composable block without padding,
 * and passes the supposed content padding in to `contentPadding` parameter
 * of each composable block.
 */
@Composable
fun MainScaffold(
  header: @Composable ((contentPadding: Dp) -> Unit)? = null,
  icon: @Composable ((contentPadding: Dp) -> Unit)? = null,
  actions: @Composable ((contentPadding: Dp) -> Unit)? = null,
  ignoreContentPadding: Boolean = false,
  content: MainScaffoldScope.(contentPadding: Dp) -> Unit,
) {
  //var measurablePointer = 0

  var allMask = 0
  val iconMask = 1 shl 0
  val headerMask = 1 shl 1
  val actionMask = 1 shl 2
  val contentMask = 1 shl 3

  val passedContentPadding =
    if(ignoreContentPadding) contentPaddingDp
    else 0.dp

  val drawnPadding =
    if(ignoreContentPadding) 0.dp
    else contentPaddingDp

  val headerItemSpace = 15.dp

  //var headerPlaceables: PrefixPostfixPlaceables? = null

  /*
  //TODO: MainScaffold with robust vertical scroll

  Placeholder(
    key = headerContentMaxWidth,
    placeholder = {

    }) {

  }
   */


  BoxWithConstraints {
    //val screenWidth = maxWidth
    //var headerContentMaxWidth by remember { mutableStateOf(maxWidth) }
    var actionSize by remember { mutableStateOf<DpSize?>(null) }
    var headerHeight by remember { mutableStateOf<Dp?>(null) }

    LazyColumn(
      modifier = Modifier.padding(
        horizontal = drawnPadding,
      ),
    ) {
      val lazyListScope = toMainScaffoldScope()
      item {
        Row(
          modifier = Modifier
            .wrapContentSize()
            .padding(top = drawnPadding,)
            .requiredWidthIn(
              max = (maxWidth - (actionSize?.width ?: 0.dp)).also {
                println("Row requiredWidthIn max = $it actionWidth = $actionSize maxWidth = $maxWidth")
              },
            ).getSize {
               headerHeight = it.height.toDp()
            },
          horizontalArrangement = Arrangement.spacedBy(
            headerItemSpace,
          ),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          icon?.apply {
            invoke(passedContentPadding)
          }
          header?.apply {
            invoke(passedContentPadding)
          }
        }
      }
      lazyListScope.content(passedContentPadding)

      if(!ignoreContentPadding) {
        item {
          Spacer(Modifier.height(drawnPadding))
        }
      }
      /*
      item {
        Box(
          Modifier.padding(
            start = drawnPadding,
            end = drawnPadding,
            top = (headerHeight ?: 0.dp),
            bottom = drawnPadding,
          )
        ) {
        }
      }
       */
    }

    actions?.apply {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(
            top = (
              if(headerHeight == null || actionSize == null) 0
              else getStartCenterAligned(
                parentLen = headerHeight!!.value.toInt(),
                childLen = actionSize!!.height.value.toInt(),
              )
            ).dp + drawnPadding,
            end = drawnPadding,
          )
          //.wrapContentSize()
          .getSize {
            actionSize = sizeToDp(it)
          },
      ) {
        invoke(passedContentPadding)
      }
    }
  }
/*
  BoxWithConstraints {
    //val screenHeight = maxHeight
    val scrollState = rememberScrollState()

    //println("MainScaffold.BoxWithConstraints screenHeight = $screenHeight scrollState.value = ${scrollState.value} scrollState.maxValue = ${scrollState.maxValue}")

    Layout(
      modifier = Modifier
        .verticalScroll(scrollState),
      content = {
        icon?.apply {
          invoke(passedContentPadding)
          allMask = allMask or iconMask
          //println("allMask icon = $allMask")
        }
        header?.apply {
          invoke(passedContentPadding)
          allMask = allMask or headerMask
          //println("allMask header = $allMask")
        }
        actions?.apply {
          invoke(passedContentPadding)
          allMask = allMask or actionMask
          //println("allMask action = $allMask")
        }
        Box(
          Modifier.padding(
            bottom = drawnPadding,
          )
        ) {
          content(passedContentPadding)
        }
        allMask = allMask or contentMask
        //println("allMask content = $allMask")
      },
      measurePolicy = { measurables, constraints ->
        10.toDp()
        val layoutConstraints = if(ignoreContentPadding) constraints
          else constraints.copy(
            maxWidth = constraints.maxWidth -
              (contentPaddingDp.roundToPx() * 2)
          )
        /*
        val constraints = constraints.copy(
          minHeight = 0,
          maxHeight = Constraints.Infinity, //500.dp.roundToPx(), //screenHeight.roundToPx() * 2, //
        )
         */
        //println("allMask measurePolicy = $allMask")

        val iconMeasurable = if(allMask hasMask iconMask) {
          measurables[0]
        } else null

        val headerMeasurable = measurables.getWithMask(
          allMask, headerMask,
        )

        val actionMeasurable = measurables.getWithMask(
          allMask, actionMask,
        )

        val contentMeasurable = measurables.getWithMask(
          allMask, contentMask,
        )!!

        val headerPlaceables = if(
          listOf(iconMask, headerMask, actionMask)
            .any { allMask hasMask it }
        ) {
          measurePrefixPostfixComponents(
            prefixMeasurable = iconMeasurable,
            postfixMeasurable = actionMeasurable,
            contentMeasurable = headerMeasurable,
            constraints = layoutConstraints,
          )
        } else null

        val headerHeight = headerPlaceables?.overallHeight ?: 0

        //println("headerHeight = $headerHeight screenHeight.roundToPx()= ${screenHeight.roundToPx()} constraints= $constraints")

        val contentPlaceable = contentMeasurable.measure(layoutConstraints)

        val drawnPaddingInPx = drawnPadding.roundToPx()

        //println("drawnPaddingInPx = $drawnPaddingInPx ignoreContentPadding = $ignoreContentPadding")

        layout(
          height = headerHeight + contentPlaceable.height
            + drawnPaddingInPx,
          width = constraints.maxWidth,
        ) {

          headerPlaceables?.apply {
            val prefixWidth = (prefixPlaceable?.run {
              place(
                x = drawnPaddingInPx,
                y = getStartCenterAligned(
                  parentLen = headerHeight,
                  childLen = height,
                ) + drawnPaddingInPx,
              )
              val spaceWidth = 15.dp.roundToPx()
              width + spaceWidth
            } ?: 0) + drawnPaddingInPx

            this.contentPlaceable?.apply {
              place(
                x = prefixWidth,
                y = getStartCenterAligned(
                  parentLen = headerHeight,
                  childLen = height,
                ) + drawnPaddingInPx,
              )
            }
          }

          contentPlaceable.apply {
            place(
              x = drawnPaddingInPx,
              y = headerHeight + drawnPaddingInPx,
            )
          }

          headerPlaceables?.postfixPlaceable?.apply {
            val y = getStartCenterAligned(
              parentLen = headerHeight,
              childLen = height,
            )

            val yOffset = scrollState.value

            place(
              x = constraints.maxWidth - width - drawnPaddingInPx,
              y = y + yOffset + drawnPaddingInPx,
            )
          }
        }
      },
    )
  }
 */
}


@Composable
fun TitleIconLayout(
  title: String? = null,
  icon: Painter? = null,
  iconColor: Color = MaterialTheme.colors.primary,
  actionData: List<ActionData> = emptyList(),
  titleMaxLines: Int = 3,
  titleOverflow: TextOverflow = TextOverflow.Ellipsis,
  ignoreContentPadding: Boolean = false,
  content: MainScaffoldScope.(contentPadding: Dp) -> Unit,
) {
  MainScaffold(
    header = if(title != null) {
      {
        Text(
          text = title,
          style = MaterialTheme.typography.h6,
          fontWeight = FontWeight.Bold,
          maxLines = titleMaxLines,
          overflow = titleOverflow,
          modifier = Modifier
            .padding(
              top = it,
              start =
                if(icon != null) 0.dp
                else it,
            ),
        )
        //Spacer(Modifier.height(15.dp))
      }
    } else null,
    icon = if (icon != null) {
      {
        IconProgressionPic(
          icon = icon,
          mainColor = iconColor,
          name = null,
          modifier = Modifier
            .padding(
              top = it,
              start = it,
            )
            .size(iconSizeDp)
        )
      }
    } else null,
    actions = if(actionData.isNotEmpty()) {
      {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .wrapContentHeight()
            .padding(
              top = it,
              end = it,
            ),
        ) {
          for(actionItem in actionData) {
            IconProgressionPic(
              icon = actionItem.icon,
              mainColor = actionItem.color ?: iconColor,
              name = null,
              modifier = Modifier
                .size(iconSizeDp)
                .semantics {
                  contentDescription =
                    actionItem.name
                  customActions = listOf(
                    CustomAccessibilityAction("Click ${actionItem.name}") {
                      actionItem.onClick()
                      true
                    }
                  )
                },
            )
          }
        }
      }
    } else null,
    ignoreContentPadding = ignoreContentPadding,
    content = {
      item {
        Spacer(Modifier.height(contentPaddingDp))
      }
      content(it)
    },
  )
}


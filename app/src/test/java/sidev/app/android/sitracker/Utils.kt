package sidev.app.android.sitracker

import android.graphics.Color.luminance
import com.github.javafaker.Faker
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

val faker = Faker()

fun <T> List<T>.joinToStringLongList(
  transform: ((T) -> CharSequence)? = null,
) = joinToString(
  prefix = "[\n",
  postfix = "\n]",
  separator = "\n",
  transform = transform,
)

fun ProgressImportanceCalculator.isActive(): Boolean =
  tiDelta > 0 && tdDelta > 0



fun <T> List<T>.randomSubset(): Set<T> {
  val res = mutableSetOf<T>()
  for(i in 0 .. indices.random()) {
    res += this[indices.random()]
  }
  return res
}


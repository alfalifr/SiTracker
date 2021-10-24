package sidev.app.android.sitracker

import com.github.javafaker.Faker

val faker = Faker()

fun <T> List<T>.joinToStringLongList(
  transform: ((T) -> CharSequence)? = null,
) = joinToString(
  prefix = "[\n",
  postfix = "\n]",
  separator = "\n",
  transform = transform,
)

fun ProgressImportanceFactors.isActive(): Boolean =
  tiDelta > 0 && tdDelta > 0
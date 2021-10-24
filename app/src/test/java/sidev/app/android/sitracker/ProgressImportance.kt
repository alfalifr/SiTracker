package sidev.app.android.sitracker

data class ProgressImportance(
  val progressId: Int,
  //val importance: Double,
  val factors: ProgressImportanceFactors,
) {
  val importance: Double by lazy {
    factors.calculateImportance()
  }
}

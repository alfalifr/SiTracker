package sidev.app.android.sitracker.core.domain.usecase

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import sidev.app.android.sitracker.R


/**
 * Contains all form validation in this app.
 * All validation method in this interface accepts Map<String, String?> as argument
 * and returns Map<String, String?>.
 *
 * The argument's keys are field ids and the values are field inputs.
 * The returned map's keys are field ids and the values are error messages, null if the inputs are valid.
 */
interface FormValidationUseCase {
  /*
  object TaskInfo {
    const val taskName = "task_name"
    const val defaultPriority = "default_priority"
    const val desc = "description"
  }
  object ScheduleInfo {
    const val taskId = "task_id"
    const val label = "label"
    const val progressType = "progress_type"
    const val duration = "duration"
    const val intervalType = "interval_type"
    const val preferredTimes = "preferred_times"
    const val preferredDays = "preferred_days"
    const val activeDates = "activeDates"
  }

  ===========================
  This approach is not suitable because this app validate each field each time single input changes.
  ===========================

  fun validateTaskInfo(inputs: Map<String, String?>): Map<String, String?>
  fun validateScheduleInfo(inputs: Map<String, String?>): Map<String, String?>
   */

  /**
   * [min] null means that [input] accept null.
   *
   * Returns true if valid, and otherwise.
   */
  fun validateStrLen(
    input: String?,
    min: Int? = 0,
    max: Int = input?.length ?: Int.MAX_VALUE,
  ): Boolean

  fun validateNumeric(
    input: String?,
  ): Boolean

  fun validateClockFormat(
    input: String?,
  ): Boolean
}

object FormValidationUseCaseImpl: FormValidationUseCase {
  /**
   * [min] null means that [input] accept null.
   *
   * Returns true if valid, and otherwise.
   */
  override fun validateStrLen(
    input: String?,
    min: Int?,
    max: Int,
  ): Boolean {
    when {
      input == null -> return min == null
      min == null -> return false
    }

    if(min?.compareTo(max) == 1) {
      throw IllegalArgumentException("`min` can't be more than `max`")
    }

    return input!!.length in min!!..max
  }

  override fun validateNumeric(input: String?): Boolean = input?.run {
    isNotBlank() && isDigitsOnly()
  } ?: false

  override fun validateClockFormat(input: String?): Boolean {
    if(input.isNullOrBlank()) {
      return false
    }

    val nums = input.split(":")
    if(nums.size == 1) {
      return false
    }

    return nums.all {
      it.length == 2 || it.isDigitsOnly()
    }
  }
}

/*
class FormValidationUseCaseImpl(private val context: Context): FormValidationUseCase {
  private fun getNamedFieldErrorMsg(
    @StringRes errorMsgStrId: Int,
    @StringRes fieldNameStrId: Int,
  ): String {
    val fieldNameStr = context.getString(fieldNameStrId)
    return context.getString(errorMsgStrId, fieldNameStr)
  }

  private fun getBlankFieldErrorMsg(@StringRes fieldNameStrId: Int): String = getNamedFieldErrorMsg(
    R.string.field_invalid_blank_message, fieldNameStrId,
  )
  private fun getNonNumericFieldErrorMsg(@StringRes fieldNameStrId: Int): String = getNamedFieldErrorMsg(
    R.string.field_invalid_non_numeric_message, fieldNameStrId,
  )

  override fun validateTaskInfo(inputs: Map<String, String?>): Map<String, String?> =
    Validation.validateInputs(inputs) {
      validateNotBlank(FormValidationUseCase.TaskInfo.taskName,
        getBlankFieldErrorMsg(R.string.task_name)
      )
      validateNumeric(FormValidationUseCase.TaskInfo.defaultPriority,
        getNonNumericFieldErrorMsg(R.string.default_priority)
      )
  }

  override fun validateScheduleInfo(inputs: Map<String, String?>): Map<String, String?> =
    Validation.validateInputs(inputs) {
      validateNumeric(FormValidationUseCase.ScheduleInfo.taskId,
        getBlankFieldErrorMsg(R.string.task)
      )
      validateNotBlank(FormValidationUseCase.ScheduleInfo.label,
        getBlankFieldErrorMsg(R.string.label)
      )
      validateNumeric(FormValidationUseCase.ScheduleInfo.progressType,
        getBlankFieldErrorMsg(R.string.progress_type)
      )
      validateClockTimeFormat(FormValidationUseCase.ScheduleInfo.duration,
        context.getString(R.string.field_invalid_clock_format_message)
      )
      validateNumeric(FormValidationUseCase.ScheduleInfo.intervalType,
        getBlankFieldErrorMsg(R.string.interval_type)
      )
    }
}


private object Validation {
  fun String.checkLen(min: Int, max: Int = length): Boolean = length in min..max
  fun String.minLen(min: Int): Boolean = checkLen(min)
  fun String.maxLen(max: Int): Boolean = checkLen(0, max)

  class ValidationContainer(private val inputs: Map<String, String?>) {
    private val _result: MutableMap<String, String?> = mutableMapOf()
    val result: Map<String, String?>
      get() = _result
    //private val result: MutableMap<String, String?> = mutableMapOf()

    fun validate(
      //inputs: Map<String, String?>,
      //resultMap: MutableMap<String, String?>,
      fieldId: String, validation: (String?) -> String?
    ) {
      _result[fieldId] = validation(inputs[fieldId])
    }

    fun validateNotBlank(
      //inputs: Map<String, String?>,
      //resultMap: MutableMap<String, String?>,
      fieldId: String,
      errorMsg: String,
    ) = validate(fieldId) {
      if(it.isNullOrBlank()) errorMsg else null
    }

    fun validateNumeric(
      //inputs: Map<String, String?>,
      //resultMap: MutableMap<String, String?>,
      fieldId: String,
      errorMsg: String,
    ) = validate(fieldId) {
      if(it?.isDigitsOnly() != true) errorMsg else null
    }

    /**
     * Time format = "HH:mm:ss"
     */
    fun validateClockTimeFormat(
      //inputs: Map<String, String?>,
      //resultMap: MutableMap<String, String?>,
      fieldId: String,
      errorMsg: String,
    ) = validate(fieldId) { input ->
      if(input == null) {
        return@validate errorMsg
      }

      val nums = input.split(":")
      if(nums.any {
          it.length != 2 || !it.isDigitsOnly()
      }) {
        errorMsg
      } else null
    }
  }


  fun validateInputs(
    inputs: Map<String, String?>,
    validationScope: ValidationContainer.() -> Unit
  ): Map<String, String?> = with(ValidationContainer(inputs)) {
    validationScope()
    result
  }
  //fun String.maxLen(max: Int): Boolean = checkLen(0, max)
}
 */
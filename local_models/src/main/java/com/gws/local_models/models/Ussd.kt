package com.gws.local_models.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Ussd(

    var id: String? = null,
    var idaccount: String? = null,
    var num: String? = null,
    var ussd: String? = null,
    var sumstep: String? = null,
    var step1: String? = null,
    var step2: String? = null,
    var step3: String? = null,
    var step4: String? = null,
    var step5: String? = null,
    var step6: String? = null,
    var step7: String? = null,
    var step8: String? = null,
    var step9: String? = null,
    var step10: String? = null,
    var iduser: String? = null,
    var sim: String? = null,
    var date: String? = null,
    var heure: String? = null,
    var etat: String? = null,
    var reponceussd: String? = null,
    var idlogin: String? = null,
    var datesaisie: String? = null

) : Parcelable

fun Ussd.getStepsList(): List<String> {
    val stepList = mutableListOf<String>()
    val stepsClass = Ussd::class.java

    for (i in 1..10) {
        val stepField = stepsClass.getDeclaredField("step$i")
        stepField.isAccessible = true
        val stepValue = stepField.get(this) as? String
        if (!stepValue.isNullOrBlank()) {
            stepList.add(stepValue)
        }
    }

    return stepList
}
fun Ussd.duplicateSteps(): List<String> {

    val inputList = this.getStepsList()
    val resultList = mutableListOf<String>()

    for ((index, item) in inputList.withIndex()) {
        resultList.add(item)
        if (index < inputList.size - 1) {
            resultList.add(item)
        }
    }

    return resultList
}
fun Ussd.getConcatUssd(): String{
    if (this == null) {
        return "" // Handle null Ussd object
    }

    val sumstep = this.sumstep?.toInt() ?: 0 // Convert sumstep to an integer, defaulting to 0 if null
    val ussdBase = this.ussd?.removeSuffix("#") ?: "" // Remove '#' from ussd and default to an empty string

   // Replace with your actual step values

    return buildString {
        append(ussdBase)
        for (i in 1..sumstep) {
            val stepValues =  when(i){
                1 -> step1
                2 -> step2
                3 -> step3
                4 -> step4
                5 -> step5
                6 -> step6
                7 -> step7
                8 -> step8
                9 -> step9
                10 -> step10
                else -> {""}
            }
            append("*${stepValues ?: ""}") // Handle null step values
        }
        append("#")
    }
}

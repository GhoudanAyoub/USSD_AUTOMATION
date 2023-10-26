package chari.groupewib.com.networking.handler

import com.gws.common.utils.FileUtil
import com.gws.local_models.models.Ussd
import com.gws.local_models.models.Ussds
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class UssdHandler @Inject constructor(
    private val fileUtil: FileUtil,
    private val jsonSerializer: Json
) {
    companion object {
        const val FILE_NAME = "/ussd_File"
    }


    fun addOrUpdateUssd(ussd: Ussd, resultFunc: (Ussd) -> Unit) {
        getOrCreateUssd().apply {
            if (this.ussds?.isNotEmpty()==true) {
                this.ussds?.toMutableList()?.also { _Ussds ->
                    try {
                        _Ussds.add(ussd)
                        this.ussds = _Ussds.toList()
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }else{
                this.ussds = listOf(ussd)
            }
            resultFunc.invoke(ussd)
            updateCachedUssd(this)
        }
    }


    fun clearUssd() {
        updateCachedUssd(Ussds(ussds = emptyList()))
    }

    fun getOrCreateUssd(resultFunc: ((Ussds) -> Unit)? = null): Ussds {
        val UssdsObjectString = fileUtil.readFileFromInternalStorage(
            FILE_NAME
        )
        val UssdsObject: Ussds = if (UssdsObjectString.isNotEmpty()) {
            try {
                jsonSerializer.decodeFromString(Ussds.serializer(), UssdsObjectString)
            } catch (e: Exception) {
                Timber.e(e)
                Ussds(ussds = emptyList())
            }
        } else {
            Ussds(ussds = emptyList())
        }
        resultFunc?.invoke(UssdsObject)
        updateCachedUssd(UssdsObject)
        return UssdsObject
    }

    /**
     * every time we sent/receive an request/response we sync those changes in cache
     */
    private fun updateCachedUssd(Ussd: Ussds) {
        val UssdCacheString = try {
            jsonSerializer.encodeToString(Ussds.serializer(), Ussd)
        } catch (e: Exception) {
            ""
        }
        if (UssdCacheString.isNotEmpty()) {
            fileUtil.saveDataToInternalStorage(
                FILE_NAME,
                UssdCacheString
            )
        }
    }
    fun getResponseObject(responseString:String):Ussds? {
        return  try {
            jsonSerializer.decodeFromString(Ussds.serializer(), responseString)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }
}

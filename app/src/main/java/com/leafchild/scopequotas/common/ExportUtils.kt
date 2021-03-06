package com.leafchild.scopequotas.common

import android.os.Environment
import android.util.Log
import com.leafchild.scopequotas.data.Quota
import com.opencsv.CSVWriter
import java.io.FileWriter
import java.io.IOException

/**
 * @author victor
 * @date 12/25/17
 */

object ExportUtils {

    private fun getExportFileName(name: String): String {
        return Environment.DIRECTORY_DOWNLOADS +
        "scopes_quotas_" + name + "_" + System.currentTimeMillis() + ".csv"
    }

    //TODO: Export is not working due to permissions checks
    @Throws(IOException::class)
    fun exportData(headers: Array<String>, quotes: List<Quota>) {

        val exportFile = Environment.getExternalStoragePublicDirectory(getExportFileName(quotes[0].quotaType!!.name))

        return try {

            if (exportFile.createNewFile()) {
                val writer = CSVWriter(FileWriter(exportFile))
                writer.writeNext(headers)

                quotes.forEach({ writer.writeNext(it.toExportString()) })
            } else { }
        } catch (e: IOException) {
            Log.e("Export data", e.message)
            return
        }
    }

}

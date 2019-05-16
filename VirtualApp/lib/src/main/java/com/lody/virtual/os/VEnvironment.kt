package com.lody.virtual.os

import android.os.Build
import android.os.Build.VERSION_CODES.O
import com.lody.virtual.helper.utils.VLog
import mirror.dalvik.system.VMRuntime
import java.io.File
import java.lang.UnsupportedOperationException

object KVEnvironment {

    private object OdexFileFetcher {

        private val fixedOdexLocationInitializer = {
            { packageName: String ->
                File(VEnvironment.getDalvikCacheDirectory(), "data@app@$packageName-1@base.apk@classes.dex")
            }
        }
        private val relativeOdexLocationInitializer = {
            { instructionSet: String ->
                { packageName: String ->
                    val oatDir = ensureCreated(File(VEnvironment.getDataAppPackageDirectory(packageName), "oat" + File.separator + instructionSet))
                    File(oatDir, "base.odex")
                }
            }(VMRuntime.getCurrentInstructionSet.call())
        }

        var getOdexFile: (String) -> File = { throw UnsupportedOperationException("") }

        init {
            val versions: IntArray = intArrayOf(O, Integer.MAX_VALUE)
            val odxFileOpt: Array<() -> (String) -> File> = arrayOf(fixedOdexLocationInitializer, relativeOdexLocationInitializer)

            for (idx in versions.indices) {
                if (Build.VERSION.SDK_INT < versions[idx]) {
                    getOdexFile = odxFileOpt[idx]();
                    break;
                }
            }
        }
    }

    fun getOdexFile(packageName: String): File {
        return OdexFileFetcher.getOdexFile(packageName);
    }

    private fun ensureCreated(folder: File): File {
        if (!folder.exists() && !folder.mkdirs()) {
            VLog.w(OdexFileFetcher.javaClass.simpleName, "Unable to create the directory: %s.", folder.path)
        }
        return folder
    }
}




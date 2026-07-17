package com.yoke.gainful.datastore

import okio.Path.Companion.toPath
import java.io.File

fun getDataStorePath(): () -> okio.Path = {
    File(System.getProperty("java.io.tmpdir"), DATASTORE_FILE).absolutePath.toPath()
}

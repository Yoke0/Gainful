package com.yoke.gainful.datastore

import android.content.Context
import okio.Path.Companion.toPath

fun getDatastorePath(context: Context): () -> okio.Path = {
    context.filesDir.resolve(DATASTORE_FILE).absolutePath.toPath()
}

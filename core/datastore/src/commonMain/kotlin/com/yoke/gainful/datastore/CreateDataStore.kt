package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.yoke.gainful.proto.GainfulDataProto
import okio.FileSystem
import okio.Path
import okio.SYSTEM

const val DATASTORE_FILE = "gainful.datastore_pb"

fun createGainfulDataStore(produceFile: () -> Path): DataStore<GainfulDataProto> {
    val storage =
        OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = GainfulDataSerializer,
            producePath = produceFile,
        )
    return DataStoreFactory.create(storage)
}

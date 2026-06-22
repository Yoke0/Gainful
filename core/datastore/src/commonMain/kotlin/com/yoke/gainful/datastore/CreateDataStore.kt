package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path

const val DATASTORE_FILE = "gainful.preferences_pb"

fun createDataStore(produceFile: () -> Path): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = produceFile)

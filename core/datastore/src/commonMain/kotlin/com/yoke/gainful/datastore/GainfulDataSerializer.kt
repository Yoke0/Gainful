package com.yoke.gainful.datastore

import androidx.datastore.core.okio.OkioSerializer
import com.yoke.gainful.proto.GainfulDataProto
import okio.BufferedSink
import okio.BufferedSource

object GainfulDataSerializer : OkioSerializer<GainfulDataProto> {
    override val defaultValue: GainfulDataProto = GainfulDataProto()

    override suspend fun readFrom(source: BufferedSource): GainfulDataProto =
        GainfulDataProto.ADAPTER.decode(source)

    override suspend fun writeTo(t: GainfulDataProto, sink: BufferedSink) {
        GainfulDataProto.ADAPTER.encode(sink, t)
    }
}

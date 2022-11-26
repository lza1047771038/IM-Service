package org.im.service.moshi

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi: Moshi by lazy {
    val baseMoshi: Moshi = Moshi.Builder()
        .add(NullSafeAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()
    baseMoshi
}
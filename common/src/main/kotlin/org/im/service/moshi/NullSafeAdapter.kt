package org.im.service.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

object NullSafeAdapter: JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        return when (type) {
            String::class.java -> StringAdapter
            else -> null
        }
    }

    internal object StringAdapter: JsonAdapter<String>() {
        override fun fromJson(reader: JsonReader): String? {
            return with(reader) {
                when (peek()) {
                    JsonReader.Token.NULL -> {
                        nextNull<Unit>()
                        ""
                    }
                    JsonReader.Token.BEGIN_OBJECT -> fromObject()
                    JsonReader.Token.BEGIN_ARRAY -> fromArray()
                    JsonReader.Token.NAME -> nextName()
                    else -> nextString()
                }
            }
        }

        private fun JsonReader.fromObject(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("{")
            beginObject()
            while (hasNext()) {
                fromNestJson(stringBuilder)
            }
            endObject()
            stringBuilder.append("}")
            return stringBuilder.toString()
        }

        private fun JsonReader.fromNestJson(stringBuilder: StringBuilder) {
            when (peek()) {
                JsonReader.Token.NULL -> {
                    nextNull<Unit>()
                }
                JsonReader.Token.BEGIN_OBJECT -> {
                    stringBuilder.append(fromObject())
                        .appendSeparator(this)
                }

                JsonReader.Token.BEGIN_ARRAY -> {
                    stringBuilder.append(fromArray())
                        .appendSeparator(this)
                }

                JsonReader.Token.NAME -> {
                    val name = nextName()
                    if (peek() == JsonReader.Token.NULL) {
                        skipValue()
                        if (!hasNext() && stringBuilder.isNotEmpty() && stringBuilder[stringBuilder.lastIndex] == ',') {
                            stringBuilder.deleteCharAt(stringBuilder.lastIndex)
                        }
                    } else {
                        stringBuilder.append("\"").append(name).append("\" : ")
                    }
                }
                JsonReader.Token.BOOLEAN -> {
                    stringBuilder.append(nextBoolean()).appendSeparator(this)
                }
                JsonReader.Token.NUMBER -> {
                    stringBuilder.append(nextString()).appendSeparator(this)
                }
                else -> {
                    stringBuilder.append("\"").append(nextString()).append("\"")
                        .appendSeparator(this)
                }
            }
        }

        private fun StringBuilder.appendSeparator(reader: JsonReader) {
            if (reader.hasNext()) {
                append(",")
            }
        }

        private fun JsonReader.fromArray(): String {
            beginArray()
            val sb = StringBuilder("[")
            while (hasNext()) {
                fromNestJson(sb)
            }
            if (sb[sb.lastIndex] == ',') {
                sb.deleteCharAt(sb.lastIndex)
            }
            endArray()
            return sb.append("]").toString()
        }

        override fun toJson(writer: JsonWriter, value: String?) {
            if (value != null) {
                writer.value(value)
            } else {
                writer.value("")
            }
        }
    }
}

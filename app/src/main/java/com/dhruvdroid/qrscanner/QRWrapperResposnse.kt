package com.dhruvdroid.qrscanner

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DecimalFormat

//
// Created by Dhruv on 12/09/20.
//
@Serializable
data class QRWrapperResponse(
    @SerializedName("location_id") val locationId: String,
    @SerializedName("location_details") val locationDetails: String,
    @SerializedName("price_per_min") val pricePerMin: String
) {

    fun toAmountFormat(): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(pricePerMin)
    }
}

class ModelDeserializer : JsonDeserializer<QRWrapperResponse> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): QRWrapperResponse {

        json as JsonObject

        val locationId = json.get("location_id").asString
        val locationDetails = json.get("location_details").asString

        val pricePerMinJson = json.get("price_per_min")
        val pricePerMin =
            if (pricePerMinJson.isJsonObject) pricePerMinJson.asJsonObject.toString() else pricePerMinJson.asString

        return QRWrapperResponse(locationId, locationDetails, pricePerMin)
    }
}
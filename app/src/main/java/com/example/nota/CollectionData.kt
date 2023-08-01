package com.example.nota

import java.io.Serializable

data class CollectionData(
    val email: String,
    val imageUrls: List<String>,
    val title: String,
    val rating: Long,
    val Y: Long,
    val M: Long,
    val D: Long,
    val category: String,
    val content: String,
    val optioncontent:String,
    val optioncontent2:String,
    val optioncontent3:String,
    val optiontitle:String,
    val optiontitle2:String,
    val optiontitle3:String,
): Serializable
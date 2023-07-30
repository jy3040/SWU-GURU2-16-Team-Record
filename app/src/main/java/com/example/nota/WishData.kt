package com.example.nota

import java.io.Serializable

data class WishData(val email:String, val category: String, val content: String, val title: String):
    Serializable
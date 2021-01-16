package ru.shirobokov.reanimation.utils

import android.content.Context

interface ResourcesHandler {

    fun getString(stringId: Int, vararg args: Any?): String
}

class ResourcesHandlerImpl(private val context: Context) : ResourcesHandler {

    override fun getString(stringId: Int, vararg args: Any?): String = context.getString(stringId, *args)
}
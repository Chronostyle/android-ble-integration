package com.sample.bleintegration

import android.text.TextUtils

object ParameterUtils {
    fun versionNumberOver1_7_2(str: String): Boolean{
        if(TextUtils.isEmpty(str))
            return false
        val split = str.split("\\.")
        if(split.size > 3){
            return Integer.parseInt(split[0]) >= 1 && Integer.parseInt(split[1]) >= 7 && Integer.parseInt(split[2]) > 2
        }
        return false
    }
}
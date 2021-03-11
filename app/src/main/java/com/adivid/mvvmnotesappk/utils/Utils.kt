package com.adivid.mvvmnotesappk.utils

import java.util.*

object Utils {

    public fun getRandomNumber(): Int{
        val r = Random()
        return r.nextInt(7 - 1) + 1
    }

}
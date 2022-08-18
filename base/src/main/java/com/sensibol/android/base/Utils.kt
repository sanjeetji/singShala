package com.sensibol.android.base

import java.io.File


public fun File.sizeBytes(): Long {
    return if (isDirectory) {
        walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    } else {
        length()
    }
}
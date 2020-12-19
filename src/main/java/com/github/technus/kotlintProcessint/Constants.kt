package com.github.technus.kotlintProcessint

object Constants {
    val size=Size(22f,12f)
    val actualGlyphSize=Size(4f,5f)
    val glyphSize=Size(5f,6f)
    val cellSize=Size(27f,27f)
    val smolDiff=cellSize/4f
    const val scale=144f
    val perEm=(cellSize.h* actualGlyphSize.h- smolDiff.h*2)/ scale
}
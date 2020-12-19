package com.github.technus.kotlintProcessint

import processing.event.KeyEvent
import processing.event.MouseEvent

open class Area(val position: Position,val size: Size) {
    private val endPosition=position+size

    open fun mouseDragged(event: MouseEvent){}

    open fun mousePosition(position: Position?){}

    open fun mouseClicked(event: MouseEvent){}

    open fun keyTyped(event: KeyEvent) {}

    open fun draw(app: App) {
        app.pushStyle()
        app.fill(0f,0f,255f,16f)
        app.stroke(0f,255f,255f,32f)
        app.rect(position.x,position.y,size.w,size.h)
        app.popStyle()
    }

    fun envelops(point: Position?): Boolean =
        if (point==null) false else point.x>position.x && point.x<endPosition.x && point.y>position.y && point.y<endPosition.y

    fun envelops(point: MouseEvent): Boolean =
        point.x>position.x && point.x<endPosition.x && point.y>position.y && point.y<endPosition.y

    fun innerPoint(point: Position):Position = (point-position)/size

    fun innerPoint(point: MouseEvent):Position = Position((point.x-position.x)/size.w,(point.y-position.y)/size.h)
}
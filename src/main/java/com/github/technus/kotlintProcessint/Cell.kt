package com.github.technus.kotlintProcessint

import processing.core.PConstants.*
import processing.event.MouseEvent

class Cell(_position: Position,_size: Size= Size(32f,32f)):Area(_position, _size) {
    var exists=false
        set(value) {
            if(!value){
                leftUp=false
                downRight=false
            }
            field = value
        }
    var leftUp= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }
    var downRight= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }

    fun toByte():Byte{
        if(exists){
            var v=0b10000
            if(leftUp){
                v=v or 0b1010
            }
            if(downRight){
                v=v or 0b0101
            }
            return v.toByte()
        }
        return 0
    }

    fun toChar():Char = if (exists) ('a'.toByte()+toByte()-0b10000).toChar() else ' '

    var ontop=false

    override fun mouseDragged(event: MouseEvent) {
        if(envelops(event)){
            val (x, y) = innerPoint(event)
            when {
                x>1/4f && x<3/4f && y>1/4f && y<3/4f -> {//center
                    exists=event.button== LEFT
                }
                x+y<1 -> {
                    leftUp=event.button== LEFT
                }
                x+y>1 -> {
                    downRight=event.button== LEFT
                }
            }
        }
    }

    override fun mousePosition(position: Position?) {
        ontop=envelops(position)
    }

    override fun mouseClicked(event: MouseEvent) {
        if(envelops(event)){
            exists=!exists
        }
    }

    override fun draw(app: App) {
        super.draw(app)
        app.pushMatrix()
        app.translate(position.x,position.y)
        if(ontop){
            app.pushStyle()
            app.fill(0f,128f,255f,32f)
            app.stroke(0f,255f,255f,64f)
            app.rect(0f,0f,size.w,size.h)
            app.popStyle()
        }
        if(exists){
            if(leftUp){
                app.triangle(0f,0f,size.w,0f,0f,size.h)
            }else{
                app.arc(size.w,size.h,size.w*2,size.h*2,PI,PI+ HALF_PI,CHORD)
            }
            if(downRight){
                app.triangle(size.w,0f,size.w,size.h,0f,size.h)
            }else{
                app.arc(0f,0f,size.w*2,size.h*2,0f, HALF_PI,CHORD)
            }
        }else if(leftUp || downRight) throw RuntimeException(this.toString())
        app.popMatrix()
    }

    override fun toString(): String {
        return "$exists $leftUp $downRight"
    }
}

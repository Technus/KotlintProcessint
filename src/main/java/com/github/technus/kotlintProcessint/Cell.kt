package com.github.technus.kotlintProcessint

import com.github.technus.kotlintProcessint.Constants.smolDiff
import processing.core.PConstants.*
import processing.event.MouseEvent


class Cell(_position: Position,_size: Size= Size(32f,32f)):Area(_position, _size) {
    var exists=false
        set(value) {
            if(!value){
                left=false
                right=false
                up=false
                down=false
            }
            field = value
        }
    var left= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }
    var right= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }
    var up= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }
    var down= false
        set(value) {
            if(value){
                exists=true
            }
            field = value
        }
    private var onTop=false

    override fun mouseDragged(event: MouseEvent) {
        if(envelops(event)){
            val (x, y) = innerPoint(event)
            when {
                x>1f/4f && x<3f/4f && y>1f/4f && y<3f/4f -> {//center
                    exists=event.button== LEFT
                }
                x+y<1f -> {
                    when {
                        x>y -> {
                            up=event.button== LEFT
                        }
                        x<y -> {
                            left=event.button== LEFT
                        }
                    }
                }
                x+y>1f -> {
                    when {
                        x>y -> {
                            right=event.button== LEFT
                        }
                        x<y -> {
                            down=event.button== LEFT
                        }
                    }
                }
            }
        }
    }

    override fun mousePosition(position: Position?) {
        onTop=envelops(position)
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
        if(onTop){
            app.pushStyle()
            app.fill(0f,128f,255f,32f)
            app.stroke(0f,255f,255f,64f)
            app.rect(0f,0f,size.w,size.h)
            app.popStyle()
        }
        if(exists){
            if(!up && !left){
                app.arc(size.w-smolDiff.w,size.h-smolDiff.h,
                    (size.w-smolDiff.w-smolDiff.w)*2,(size.h-smolDiff.h-smolDiff.h)*2,PI,PI+ HALF_PI,CHORD)
            }else{
                app.triangle(smolDiff.w,smolDiff.h,
                    size.w-smolDiff.w, smolDiff.h,
                    smolDiff.w,size.h-smolDiff.h)
                if(left){
                    app.rect(0f, smolDiff.h, smolDiff.w,size.h-2* smolDiff.h)
                }
                if(up){
                    app.rect(smolDiff.w,0f,size.w-2* smolDiff.w, smolDiff.h)
                }
            }
            if(!down && !right){
                app.arc(smolDiff.w,smolDiff.h,
                    (size.w-smolDiff.w- smolDiff.w)*2, (size.h-smolDiff.h- smolDiff.h)*2,0f, HALF_PI,CHORD)
            }else{
                app.triangle(size.w- smolDiff.w,
                    smolDiff.h, size.w- smolDiff.w,size.h-smolDiff.h,
                    smolDiff.w,size.h- smolDiff.h)
                if(right){
                    app.rect(size.w- smolDiff.w, smolDiff.h, smolDiff.w,size.h-2* smolDiff.h)
                }
                if(down){
                    app.rect(smolDiff.w,size.h- smolDiff.h,size.w-2* smolDiff.w, smolDiff.h)
                }
            }
        }else if(left || down || right || up) throw RuntimeException(this.toString())
        app.popMatrix()
    }
}

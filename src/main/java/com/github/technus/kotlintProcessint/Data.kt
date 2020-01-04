package com.github.technus.kotlintProcessint

data class Position(val x: Float=0f, val y: Float=0f): Comparable<Position>{
    fun shift(x:Float,y:Float)=Position(this.x+x,this.y+y)
    operator fun plus(position: Position) = Position(x+position.x,y+position.y)
    operator fun minus(position: Position) = Position(position.x-x,position.y-y)
    operator fun times(position: Position) = Position(position.x*x,position.y*y)
    operator fun div(position: Position) = Position(x/position.x,y/position.y)
    operator fun plus(size: Size) = Position(size.w+x,size.h+y)
    operator fun minus(size: Size) = Position(x-size.w,y-size.h)
    operator fun times(size: Size) = Position(size.w*x,size.h*y)
    operator fun div(size: Size) = Position(x/size.w,y/size.h)
    override operator fun compareTo(other: Position): Int {
        val compare = y.compareTo(other.y)
        return if(compare==0) x.compareTo(other.x) else compare
    }
    operator fun unaryMinus(): Position =Position(-x,-y)
}
data class Size(val w: Float=0f, val h: Float=0f){
    operator fun plus(size: Size) = Size(size.w+w,size.h+h)
    operator fun minus(size: Size) = Size(w-size.w,h-size.h)
    operator fun times(size: Size) = Size(size.w*w,size.h*h)
    operator fun div(divider: Number)= Size(w/divider.toFloat(),h/divider.toFloat())
}

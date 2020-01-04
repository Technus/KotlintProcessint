package com.github.technus.kotlintProcessint

import processing.event.KeyEvent
import processing.event.MouseEvent
import java.util.*

open class Grid<V:Area>(_position: Position,val gridSize:Size,val cellSize:Size):Area(_position,gridSize*cellSize) {
    protected val cells=TreeMap<Position,V>()

    operator fun get(position: Position)=cells[position]
    operator fun contains(position: Position)=cells.contains(position)

    override fun mouseDragged(event: MouseEvent) {
        if(envelops(event)) {
            cells.forEach { (_, obj) -> obj.mouseDragged(event) }
        }
    }

    override fun mousePosition(position: Position?) {
        if(envelops(position)){
            cells.forEach{ (_, obj) -> obj.mousePosition(position) }
        }else{
            cells.forEach { (_, obj) -> obj.mousePosition(null) }
        }
    }

    override fun mouseClicked(event: MouseEvent) {
        if(envelops(event)) {
            cells.forEach { (_, obj) -> obj.mouseClicked(event) }
        }
    }

    override fun keyTyped(event: KeyEvent) {
        cells.forEach { (_, obj) -> obj.keyTyped(event) }
    }
}
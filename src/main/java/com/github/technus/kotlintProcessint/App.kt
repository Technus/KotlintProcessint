package com.github.technus.kotlintProcessint

import processing.core.PApplet
import processing.event.KeyEvent
import processing.event.MouseEvent

class App : PApplet() {
    private val text=Text(_gridSize = Size(16f,8f))

    override fun settings() {
        size(800,600, P2D)
    }

    override fun setup() {
        surface.setResizable(true)
        surface.setTitle("Kotlin't Processin't")
        smooth()
    }

    override fun draw() {
        background(0)
        text.mousePosition(Position(mouseX.toFloat(),mouseY.toFloat()))
        text.draw(this)
    }

    override fun mouseClicked(event: MouseEvent) {
        text.mouseClicked(event)
    }

    override fun mouseDragged(event: MouseEvent) {
        text.mouseDragged(event)
    }

    override fun keyTyped(event: KeyEvent) {
        if(event.key==' '){
            text.fixShit()
        }
    }
}
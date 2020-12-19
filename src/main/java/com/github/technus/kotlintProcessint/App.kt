package com.github.technus.kotlintProcessint

import processing.core.PApplet
import processing.event.KeyEvent
import processing.event.MouseEvent
import java.io.File
import java.nio.file.Files

class App : PApplet() {
    private val text=Text(_gridSize = Constants.size)
    private val glyphs=File("glyphs.txt")

    override fun settings() {
        size(800,600, P2D)
    }

    override fun setup() {
        surface.setResizable(true)
        surface.setTitle("Kotlin't Processin't")
        textFont(createFont("Consolas",Constants.cellSize.h*0.85f))
        strokeWeight(1.1f)
        smooth()
        if(glyphs.exists()) {
            text.fromStringRepresentation(String(Files.readAllBytes(glyphs.toPath())))
        }
    }

    override fun draw() {
        background(0)
        text.mousePosition(Position(mouseX.toFloat(),mouseY.toFloat()))
        text.draw(this)
    }

    override fun mouseExited() {
        text.mousePosition(Position(-1f,-1f))
    }

    override fun mouseClicked(event: MouseEvent) {
        text.mouseClicked(event)
    }

    override fun mouseDragged(event: MouseEvent) {
        text.mouseDragged(event)
    }

    override fun keyTyped(event: KeyEvent) {
        text.keyTyped(event)
    }

    override fun keyPressed(event: KeyEvent) {
        if(event.keyCode==27){
            Files.write(glyphs.toPath(),text.toStringRepresentation().lines())
            val currentTimeMillis = System.currentTimeMillis()
            Files.write(File("font $currentTimeMillis.svg").toPath(),text.toFontSVG(currentTimeMillis.toString()).lines())
        }else{
            super.keyPressed(event)
        }
    }
}
package com.github.technus.kotlintProcessint

import com.github.technus.kotlintProcessint.Constants.actualGlyphSize
import com.github.technus.kotlintProcessint.Constants.smolDiff
import processing.core.PConstants.CENTER
import processing.event.KeyEvent
import processing.event.MouseEvent

class Glyph(
    _position:Position,
    _gridSize:Size=Constants.glyphSize,
    _cellSize:Size=Constants.cellSize,
    var char:Char='\u0000'
):Grid<Cell>(_position, _gridSize,_cellSize) {
    init {
        for (x in 0 until gridSize.w.toInt()) for(y in 0 until gridSize.h.toInt()){
            val gridPos = Position(x.toFloat(), y.toFloat())
            cells[gridPos] =
                Cell(position+gridPos*cellSize,cellSize)
        }
    }

    private var onTop=false
    private var valid=true
    private val activeArea=Area(position,cellSize* actualGlyphSize)

    override fun draw(app: App) {
        super.draw(app)
        app.pushMatrix()
        app.translate(position.x,position.y)
        app.pushStyle()
        app.fill(0f,128f,255f,32f)
        app.stroke(0f,255f,255f,64f)
        if(onTop){
            app.rect(0f,0f,size.w,size.h)
        }
        app.textAlign(CENTER,CENTER)
        app.fill(255f)
        app.text("$char ${char.toInt()}",size.w-cellSize.w,size.h-cellSize.h/2F)
        app.popStyle()
        app.popMatrix()

        app.pushStyle()
        app.fill(0f,if(valid) 0f else 128f,255f)
        app.stroke(0f,if(valid) 0f else 128f,255f)
        cells.forEach { (_,cell)->cell.draw(app) }
        app.popStyle()
    }

    fun toStringRepresentation():String=
        "${cells.entries.stream()
            .filter { (position, _) -> position.x < actualGlyphSize.w && position.y < actualGlyphSize.h }
            .map { (_, cell) -> cell.toChar() }.toArray()
            .joinToString(separator = "")
        }=${char.toInt()}"
    fun fromStringRepresentation(string: String){
        val split = string.split("=")
        char=Integer.parseInt(split[1]).toChar()
        var pos=0
        cells.entries.stream()
            .filter { (position, _) -> position.x < actualGlyphSize.w && position.y < actualGlyphSize.h }
            .map { (_,cell) -> cell }
            .forEach{ cell ->
            cell.fromChar(split[0][pos++])
        }
        fixShit(true)
    }

    fun toSVG(glyph:Boolean=false,perEm: Float=1f):String {
        val stringBuilder=StringBuilder()
        if (glyph){
            cells.forEach { (position, cell) ->
                val svg = ArrayList(cell.toSVG(position * cellSize).split(" "))
                var isY=false
                for ((index, s) in svg.withIndex()) {
                    val num= s.toFloatOrNull() ?: continue
                    if(isY) svg[index]= (((actualGlyphSize.h)*cellSize.h -num- smolDiff.h)/perEm).toString()
                    else svg[index]= ((num- smolDiff.w)/perEm).toString()
                    isY=!isY
                }
                stringBuilder.append(svg.joinToString(separator = " "))
            }
        }else{
            cells.forEach { (position, cell) -> stringBuilder.append(cell.toSVG(position*cellSize)) }
        }
        return stringBuilder.toString()
    }

    fun toGlyphSVG(perEm: Float):String = if (char.isLetterOrDigit())
        "<glyph unicode=\"$char\"        horiz-adv-x=\"${size.w/perEm}\" d=\"${toSVG(true,perEm)}\" />"
        else
        "<glyph unicode=\"&#x${ char.toInt().toString(16).padStart(4,'0')};\" horiz-adv-x=\"${size.w/perEm}\" d=\"${toSVG(true,perEm)}\" />"

    override fun mouseClicked(event: MouseEvent) {
        if(activeArea.envelops(event)) {
            if(event.button==3){
                clear()
                valid=true
            }else{
                super.mouseClicked(event)
                fixShit(true)
            }
        }else if(envelops(event)){
            fixShit()
            println(toStringRepresentation())
        }
    }

    private fun clear() {
        cells.forEach { (_, cell) -> cell.exists=false }
    }

    override fun mousePosition(position: Position?) {
        val envelops = envelops(position)
        if(onTop && !envelops) {
            fixShit()
        }
        onTop=envelops
        super.mousePosition(position)
    }

    override fun mouseDragged(event: MouseEvent) {
        if(activeArea.envelops(event)) {
            super.mouseDragged(event)
            fixShit(true)
        }
    }

    override fun keyTyped(event: KeyEvent) {
        if(onTop){
            char=event.key
            fixShit()
            println(toStringRepresentation())
        }else{
            super.keyTyped(event)
        }
    }

    private fun fixShit(validCheck:Boolean=false) {
        var valid=true;
        cells.forEach{ (_, cell) -> cell.valid=true }
        cells.forEach { (position, cell) ->
            val left=cells[position.shift(-1f,0f)]
            val up=cells[position.shift(0f,-1f)]
            if(cell.up && (up==null || !up.down)){
                if(validCheck) {
                    cell.valid=false
                    valid = false
                }else{
                    cell.up=false
                }
            }
            if(cell.left && (left==null || !left.right)){
                if(validCheck) {
                    cell.valid=false
                    valid=false
                }else{
                    cell.left=false
                }
            }
            val down=cells[position.shift(0f,1f)]
            val right=cells[position.shift(1f,0f)]
            if(cell.down){
                if(down==null || !down.up){
                    if(validCheck) {
                        cell.valid=false
                        valid=false
                    }else{
                        cell.down=false
                    }
                }else if(left!=null && left.right && right!=null && right.left){
                    if(validCheck) {
                        cell.valid=false
                        valid=false
                    }else{
                        cell.down=false
                        down.up= false
                    }
                }
            }
            if(cell.right){
                if(right==null || !right.left){
                    if(validCheck) {
                        cell.valid=false
                        valid=false
                    }else{
                        cell.right=false
                    }
                }else if(down!=null && down.up && up!=null && up.down){
                    if(validCheck) {
                        cell.valid=false
                        valid=false
                    }else{
                        cell.right=false
                        right.left=false
                    }
                }
            }
        }
        this.valid=valid
    }
}

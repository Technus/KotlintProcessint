package com.github.technus.kotlintProcessint

import com.github.technus.kotlintProcessint.Constants.glyphSize
import com.github.technus.kotlintProcessint.Constants.perEm
import com.github.technus.kotlintProcessint.Constants.scale
import kotlin.streams.toList

class Text(
    _position:Position= Position(0f,0f),
    _gridSize:Size=Size(32f,32f),
    _cellSize:Size= glyphSize *Constants.cellSize
):Grid<Glyph>(_position, _gridSize,_cellSize) {
    init {
        for (x in 0 until gridSize.w.toInt()) for(y in 0 until gridSize.h.toInt()){
            val gridPos = Position(x.toFloat(), y.toFloat())
            cells[gridPos] = Glyph(position+gridPos*cellSize)
        }
    }
    fun toFontSVG(string:String):String="""
<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg xmlns="http://www.w3.org/2000/svg">
  <defs>
    <font id="Quaghn${string}" horiz-adv-x="${(Constants.cellSize.w* glyphSize.w)/perEm}" >
      <font-face font-family="Quaghn"
          units-per-em="$scale"
          panose-1="0 0 0 0 0 0 0 0 0 0"
          ascent="$scale"
          descent="0"
          alphabetic="0" />
${cells.values.stream().filter{cell->cell.char!='\u0000'}.map { cell -> cell.toGlyphSVG(perEm) }.filter{ str-> str.isNotEmpty() }.toList().joinToString(separator = "\n")}
    </font>
  </defs>
</svg>""".trimIndent()


    fun toStringRepresentation():String= cells.entries.stream().map { entry->
        "${entry.value.toStringRepresentation()}@${entry.key.x};${entry.key.y}"
    }.toList().joinToString(separator = "\n")
    fun fromStringRepresentation(string: String){
        val lines = string.lines()
        lines.forEach { line->
            if(!line.isBlank()) {
                val entryPieces = line.split("@")
                val positionPieces=entryPieces[1].split(";")
                cells[Position(positionPieces[0].toFloat(),positionPieces[1].toFloat())]!!.fromStringRepresentation(entryPieces[0])
            }
        }
    }

    override fun draw(app: App) {
        super.draw(app)
        app.pushStyle()
        app.fill(0f,0f,255f,64f)
        app.stroke(0f,255f,255f,255f)
        cells.forEach { (_,glyph)->glyph.draw(app) }
        app.popStyle()
    }
}
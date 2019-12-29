package com.github.technus.kotlintProcessint

class Text(
    _position:Position= Position(0f,0f),
    _gridSize:Size=Size(32f,32f),
    _cellSize:Size=Constants.glyphSize*Constants.cellSize
):Grid<Glyph>(_position, _gridSize,_cellSize) {
    init {
        for (x in 0 until gridSize.w.toInt()) for(y in 0 until gridSize.h.toInt()){
            val gridPos = Position(x.toFloat(), y.toFloat())
            cells[gridPos] = Glyph(position+gridPos*cellSize)
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
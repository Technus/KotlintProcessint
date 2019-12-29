package com.github.technus.kotlintProcessint

import processing.event.MouseEvent

class Glyph(
    _position:Position,
    _gridSize:Size=Constants.glyphSize,
    _cellSize:Size=Constants.cellSize
):Grid<Cell>(_position, _gridSize,_cellSize) {
    init {
        for (x in 0 until gridSize.w.toInt()) for(y in 0 until gridSize.h.toInt()){
            val gridPos = Position(x.toFloat(), y.toFloat())
            cells[gridPos] =
                Cell(position+gridPos*cellSize,cellSize)
        }
    }

    private var onTop=false
    private val activeArea=Area(position,cellSize*Constants.actualGlyphSize)

    override fun draw(app: App) {
        super.draw(app)
        if(onTop){
            app.pushMatrix()
            app.translate(position.x,position.y)
            app.pushStyle()
            app.fill(0f,128f,255f,32f)
            app.stroke(0f,255f,255f,64f)
            app.rect(0f,0f,size.w,size.h)
            app.popStyle()
            app.popMatrix()
        }
        app.pushStyle()
        app.fill(255f)
        app.stroke(255f)
        cells.forEach { (_,cell)->cell.draw(app) }
        app.popStyle()
    }

    override fun mouseClicked(event: MouseEvent) {
        if(activeArea.envelops(event)) {
            if(event.button==3){
                clear()
            }else{
                super.mouseClicked(event)
            }
        }
    }

    private fun clear() {
        cells.forEach { (_, cell) -> cell.exists=false }
    }

    override fun mousePosition(position: Position?) {
        val envelops = envelops(position)
        if(onTop && !envelops){
            fixShit()
        }
        onTop=envelops
        super.mousePosition(position)
    }

    override fun mouseDragged(event: MouseEvent) {
        if(activeArea.envelops(event)) {
            super.mouseDragged(event)
        }
    }

    override fun fixShit() {
        super.fixShit()
        cells.forEach { (position, cell) ->
            val left=cells[position.shift(-1f,0f)]
            val up=cells[position.shift(0f,-1f)]
            val down=cells[position.shift(0f,1f)]
            val right=cells[position.shift(1f,0f)]
            if(cell.up && (up==null || !up.down)){
                cell.up=false
            }
            if(cell.left && (left==null || !left.right)){
                cell.left=false
            }
            if(cell.down){
                if(down==null || !down.up){
                    cell.down=false
                }else if(left!=null && left.right && right!=null && right.left){
                    cell.down=false
                    down.up= false
                }
            }
            if(cell.right){
                if(right==null || !right.left){
                    cell.right=false
                }else if(down!=null && down.up && up!=null && up.down){
                    cell.right=false
                    right.left=false
                }
            }

        }
    }
}
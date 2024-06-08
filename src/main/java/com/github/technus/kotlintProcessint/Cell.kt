package com.github.technus.kotlintProcessint

import com.github.technus.kotlintProcessint.Constants.smolDiff
import processing.core.PConstants.*
import processing.event.MouseEvent

class Cell(position: Position, size: Size = Size(32f, 32f)) : Area(position, size) {
    var exists = false
        set(value) {
            if (!value) {
                left = false
                right = false
                up = false
                down = false
            }
            field = value
        }
    var left = false
        set(value) {
            if (value) {
                exists = true
            }
            field = value
        }
    var right = false
        set(value) {
            if (value) {
                exists = true
            }
            field = value
        }
    var up = false
        set(value) {
            if (value) {
                exists = true
            }
            field = value
        }
    var down = false
        set(value) {
            if (value) {
                exists = true
            }
            field = value
        }
    var valid = true

    fun toByte(): Byte {
        if (exists) {
            var v = 0b10000
            if (left) {
                v = v or 0b0010
            }
            if (right) {
                v = v or 0b0001
            }
            if (up) {
                v = v or 0b1000
            }
            if (down) {
                v = v or 0b0100
            }
            return v.toByte()
        }
        return 0
    }

    fun toChar(): Char = if (exists) ('a'.toByte() + toByte() - 0b10000).toChar() else '-'

    fun fromChar(char: Char) {
        if (char == '-') {
            exists = false
        } else {
            exists = true
            val toInt = char.toInt() - 'a'.toInt()
            if (toInt and 0b1000 != 0) {
                up = true
            }
            if (toInt and 0b100 != 0) {
                down = true
            }
            if (toInt and 0b10 != 0) {
                left = true
            }
            if (toInt and 0b1 != 0) {
                right = true
            }
        }
    }

    fun toSVG(position: Position): String {
        val stringBuilder = StringBuilder()
        if (exists) {
            if (!up && !left) {
                stringBuilder.arc(
                    position, smolDiff.w, size.h - smolDiff.h,
                    smolDiff.w, smolDiff.h, size.w - smolDiff.w, smolDiff.h
                )
            } else {
                stringBuilder.triangle(
                    position, smolDiff.w, smolDiff.h,
                    size.w - smolDiff.w, smolDiff.h, smolDiff.w, size.h - smolDiff.h
                )
                if (left) {
                    stringBuilder.rect(position, 0f, smolDiff.h, smolDiff.w, size.h - 2 * smolDiff.h)
                }
                if (up) {
                    stringBuilder.rect(position, smolDiff.w, 0f, size.w - 2 * smolDiff.w, smolDiff.h)
                }
            }
            if (!down && !right) {
                stringBuilder.arc(
                    position, size.w - smolDiff.w, smolDiff.h,
                    size.w - smolDiff.w, size.h - smolDiff.h, smolDiff.w, size.h - smolDiff.h
                )
            } else {
                stringBuilder.triangle(
                    position, size.w - smolDiff.w, smolDiff.h,
                    size.w - smolDiff.w, size.h - smolDiff.h, smolDiff.w, size.h - smolDiff.h
                )
                if (right) {
                    stringBuilder.rect(position, size.w - smolDiff.w, smolDiff.h, smolDiff.w, size.h - 2 * smolDiff.h)
                }
                if (down) {
                    stringBuilder.rect(position, smolDiff.w, size.h - smolDiff.h, size.w - 2 * smolDiff.w, smolDiff.h)
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun StringBuilder.rect(position: Position, x: Float, y: Float, w: Float, h: Float) =
        append("M ${x + position.x} ${y + position.y} L ${x + w + position.x} ${y + position.y} L ${x + w + position.x} ${y + h + position.y} L ${x + position.x} ${y + h + position.y} Z ")

    private fun StringBuilder.triangle(
        position: Position,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float
    ) =
        append("M ${x1 + position.x} ${y1 + position.y} L ${x2 + position.x} ${y2 + position.y} L ${x3 + position.x} ${y3 + position.y} Z ")

    private fun StringBuilder.arc(
        position: Position,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float
    ) =
        append("M ${x1 + position.x} ${y1 + position.y} Q ${x2 + position.x} ${y2 + position.y} ${x3 + position.x} ${y3 + position.y} Z ")

    private var onTop = false

    override fun mouseDragged(event: MouseEvent) {
        if (envelops(event)) {
            val (x, y) = innerPoint(event)
            when {
                x > 1f / 4f && x < 3f / 4f && y > 1f / 4f && y < 3f / 4f -> {//center
                    exists = event.button == LEFT
                }

                x + y < 1f -> {
                    when {
                        x > y -> {
                            up = event.button == LEFT
                        }

                        x < y -> {
                            left = event.button == LEFT
                        }
                    }
                }

                x + y > 1f -> {
                    when {
                        x > y -> {
                            right = event.button == LEFT
                        }

                        x < y -> {
                            down = event.button == LEFT
                        }
                    }
                }
            }
        }
    }

    override fun mousePosition(position: Position?) {
        val envelops = envelops(position)
        //if (onTop && !envelops) {
        //todo something ...? println(toSVG())
        //}
        onTop = envelops
    }

    override fun mouseClicked(event: MouseEvent) {
        if (envelops(event)) {
            exists = !exists
        }
    }

    override fun draw(app: App) {
        super.draw(app)
        app.pushMatrix()
        app.translate(position.x, position.y)
        if (onTop) {
            app.pushStyle()
            app.fill(0f, 128f, 255f, 32f)
            app.stroke(0f, 255f, 255f, 64f)
            app.rect(0f, 0f, size.w, size.h)
            app.popStyle()
        }
        if (exists) {
            if (!valid) {
                app.pushStyle()
                app.fill(255f, 255f, 255f)
                app.stroke(255f, 255f, 255f)
            }
            if (!up && !left) {
                app.arc(
                    size.w - smolDiff.w,
                    size.h - smolDiff.h,
                    (size.w - smolDiff.w - smolDiff.w) * 2,
                    (size.h - smolDiff.h - smolDiff.h) * 2,
                    PI,
                    PI + HALF_PI,
                    CHORD
                )
            } else {
                app.triangle(
                    smolDiff.w, smolDiff.h,
                    size.w - smolDiff.w, smolDiff.h,
                    smolDiff.w, size.h - smolDiff.h
                )
                if (left) {
                    app.rect(0f, smolDiff.h, smolDiff.w, size.h - 2 * smolDiff.h)
                }
                if (up) {
                    app.rect(smolDiff.w, 0f, size.w - 2 * smolDiff.w, smolDiff.h)
                }
            }
            if (!down && !right) {
                app.arc(
                    smolDiff.w, smolDiff.h,
                    (size.w - smolDiff.w - smolDiff.w) * 2, (size.h - smolDiff.h - smolDiff.h) * 2, 0f, HALF_PI, CHORD
                )
            } else {
                app.triangle(
                    size.w - smolDiff.w,
                    smolDiff.h, size.w - smolDiff.w, size.h - smolDiff.h,
                    smolDiff.w, size.h - smolDiff.h
                )
                if (right) {
                    app.rect(size.w - smolDiff.w, smolDiff.h, smolDiff.w, size.h - 2 * smolDiff.h)
                }
                if (down) {
                    app.rect(smolDiff.w, size.h - smolDiff.h, size.w - 2 * smolDiff.w, smolDiff.h)
                }
            }
            if (!valid) {
                app.popStyle()
            }
        } else if (left || down || right || up) throw RuntimeException(toString())
        app.popMatrix()
    }
}

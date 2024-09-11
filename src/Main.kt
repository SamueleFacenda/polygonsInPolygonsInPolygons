import processing.core.PApplet

const val POW_SIZE = 10
const val MAX_SIZE = (2 shl POW_SIZE).toFloat()
const val WINDOW_SIZE = MAX_SIZE / 4
const val EDGES = 5
const val DEPTH = 100
const val FPS = 120f
const val INITIAL_RATIO = 0.8f
const val DEFAULT_STEP_TIME = 0.5f
const val DEFAULT_MIDPOINT_RATIO = 0.5f

class Main: PApplet() {
    private lateinit var parent: Polygon
    private var singleStepSize = 0f
    private var currentSize = 0f
    private var stepTime = DEFAULT_STEP_TIME
    private var midpointRatio = DEFAULT_MIDPOINT_RATIO

    init {
        generateNewParent()
    }

    private fun generateNewParent() {
        parent = Polygon(EDGES).scaledTo(MAX_SIZE * INITIAL_RATIO)
        singleStepSize = (MAX_SIZE * (1 - INITIAL_RATIO)) / (FPS * stepTime)
        currentSize = parent.getSize()
    }

    override fun settings() {
        size(WINDOW_SIZE.toInt(), WINDOW_SIZE.toInt())
    }

    override fun setup() {
        frameRate(FPS)
    }

    private fun nextPolygon() {
        parent = Polygon(parent, midpointRatio) // step to children
        currentSize = parent.getSize()
        singleStepSize = (MAX_SIZE - currentSize) / (FPS * stepTime)
        println(parent.computeRegularity())
    }

    override fun draw() {
        if (currentSize > MAX_SIZE)
            nextPolygon() // remove the biggest

        background(255) // clear
        strokeWeight(2f) // set line width
        translate(WINDOW_SIZE / 2, WINDOW_SIZE / 2) // center
        generateSequence(parent) { Polygon(it, midpointRatio) } // generate sequence of inscribed polygons
            .take(DEPTH)
            .forEach { it.draw(this) }
        currentSize += singleStepSize
        parent = parent.scaledTo(currentSize).centered()
    }

    override fun keyPressed() {
        when (key) {
            '-' -> stepTime *= 1.1f // slower
            '+' -> stepTime *= 0.9f // faster
            'l' -> midpointRatio = min(1f, midpointRatio + 0.05f)
            'k' -> midpointRatio = max(0f, midpointRatio - 0.05f)
            ' ' -> generateNewParent() // reset
            else -> println(key)
        }
    }

}
fun main() {
    PApplet.runSketch(arrayOf("main"), Main())
}
import processing.core.PApplet
import processing.core.PApplet.pow
import kotlin.math.*
import kotlin.random.Random

class Polygon(private val vertexes: List<Pair<Float, Float>>) {

    constructor(nEdges: Int): this(List(nEdges) { Pair(Random.nextFloat() - 0.5f, Random.nextFloat() - 0.5f)})
    constructor(parent: Polygon, midpointRatio: Float = DEFAULT_MIDPOINT_RATIO) : this(computeMidpoints(parent.vertexes, midpointRatio))

    companion object {

        /**
         * Compute a point between two other points, nearer to one of the ratio specified
         */
        private fun computePointAtRatio(a: Pair<Float, Float>, b: Pair<Float, Float>, ratio: Float): Pair<Float, Float> {
            val doubleRatio = ratio * 2
            return Pair((a.first * doubleRatio + b.first * (2f-doubleRatio))/2, (a.second * doubleRatio + b.second * (2f-doubleRatio))/2)
        }

        fun computeMidpoints(vertexes: List<Pair<Float, Float>>, midpointRatio: Float): List<Pair<Float, Float>> {
            return vertexes
                .plus(vertexes.first())
                .windowed(2)
                .map {(a,b) -> computePointAtRatio(a,b, midpointRatio) }
        }

        private fun computeDistance(a: Pair<Float, Float>, b: Pair<Float, Float>): Float {
            return sqrt(pow((a.first - b.first),2f) + pow((a.second - b.second),2f))
        }
    }

    fun getSize(): Float {
        val minX = vertexes.minOf { it.first }
        val minY = vertexes.minOf { it.second }
        val maxX = vertexes.maxOf { it.first }
        val maxY = vertexes.maxOf { it.second }
        val dimension = max(maxX - minX, maxY - minY)
        return dimension
    }

    fun scaledTo(pixels: Float): Polygon {
        return scaled( pixels / getSize())
    }

    private fun scaled(scale: Float): Polygon {
        return Polygon(vertexes.map { Pair(it.first * scale, it.second * scale) })
    }

    fun draw(sketch: PApplet) {
        vertexes.plus(vertexes.first()).windowed(2).forEach { (a, b) ->
            sketch.line(a.first, a.second, b.first, b.second)
        }
    }

    private fun getCenter(): Pair<Float, Float> {
        val meanX = vertexes.map { it.first }.average().toFloat()
        val meanY = vertexes.map { it.second }.average().toFloat()
        return Pair(meanX, meanY)
    }

    private fun offsetted(offsetX: Float, offsetY: Float): Polygon {
        return Polygon(vertexes.map { (x, y) ->
            Pair(x + offsetX, y + offsetY)
        })
    }

    fun centered(): Polygon {
        val center = getCenter()
        return offsetted(-center.first, -center.second)
    }

    private fun getAvgRadius(): Float {
        val center = getCenter()
        return vertexes
            .map { computeDistance(it, center) }
            .average().toFloat()
    }

    fun computeRegularity(): Double {
        val avgRadius = getAvgRadius()
        val center = getCenter()

        val variance = vertexes
            .map { pow(computeDistance(it, center) - avgRadius , 2f) }
            .average()
        return sqrt(variance) / avgRadius
    }
}
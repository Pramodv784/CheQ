package com.cheq.retail.ui.dashboard.view.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cheq.retail.R
import com.cheq.retail.ui.dashboard.model.ChartEntity
import com.cheq.retail.ui.dashboard.utility.GraphCanvasWrapper
import com.cheq.retail.ui.dashboard.utility.GraphPath
import java.util.*


/**
 * Created by Farshid Roohi.
 * Graph | Copyrights 2019-08-21.
 */
class LineChart : View {

    private var mPaddingTop: Float = 40f
    var mPaddingRight: Float = 40f
    var mPaddingLeft: Float = 40f
    var mPaddingBottom: Float = 10f
    var maxValue: Long = 0

    var lineColor: Int = 0


    private var xLength: Int = 0
    private var yLength: Int = 0

    private var chartXLength: Int = 0
    private var chartYLength: Int = 0

    private var p = Paint()
    private var pCircle = Paint()
    private var pCircleBG = Paint()
    private var pCircleBG2 = Paint()
    private var pLine = Paint()
    private var pBaseLine = Paint()
    private var pBaseLineX = Paint()

    private var pMarkTextValueBlack = Paint()
    private var pMarkTextMonth = Paint()
    private var pMarkTextValue = Paint()

    private var legendArray: MutableList<String> = mutableListOf()

    private var chartEntities: MutableList<ChartEntity> = mutableListOf()

    constructor(context: Context?) : super(context) {
        initialize(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
//        val colors = intArrayOf(
//            ContextCompat.getColor(
//                context,
//                R.color.colorPrimary
//            ),
//            ContextCompat.getColor(
//                context,
//                R.color.black
//            ),
//            ContextCompat.getColor(
//                context,
//                R.color.bg_grey
//            )
//        )
//        val positions = null //floatArrayOf(0f, 0.3f, 0.6f)
//        pBaseLine.setShader(
//            LinearGradient(
//                0f, 0f, measuredWidth.toFloat(), 0f,
//                colors,
//                positions,
//                Shader.TileMode.CLAMP
//            )
//        )
        attrs?.let {
            val typeArray = context.obtainStyledAttributes(it, R.styleable.LineChart)

            try {
//                this.bgColor = typeArray.getColor(
//                    R.styleable.LineChart_chart_bg_color,
//                    ContextCompat.getColor(
//                        context,
//                        com.airbnb.lottie.R.color.material_blue_grey_900
//                    )
//                )
                this.lineColor = typeArray.getColor(
                    R.styleable.LineChart_chart_line_color,
                    ContextCompat.getColor(context, R.color.black)
                )
                this.mPaddingTop =
                    typeArray.getDimension(R.styleable.LineChart_chart_padding_top, 20f)
                this.mPaddingRight =
                    typeArray.getDimension(R.styleable.LineChart_chart_padding_right, 20f)
                this.mPaddingBottom =
                    typeArray.getDimension(R.styleable.LineChart_chart_padding_bottom, 20f)
                this.mPaddingLeft =
                    typeArray.getDimension(R.styleable.LineChart_chart_padding_left, 20f)

            } finally {
                typeArray.recycle()

            }
        }

      //  val graph1 = floatArrayOf(700f, 852f, 600f, 822f, 654f, 900f)
        val legend = arrayListOf("APR", "MAY", "JUN", "JUL", "AUG", "SEP")
        // val graph2 = floatArrayOf(0f, 245000f, 1011000f, 1000f, 0f, 0f, 47000f)

//        val arrGraph = ArrayList<ChartEntity>().apply {
//            add(ChartEntity(Color.TRANSPARENT, Color.YELLOW, graph1))
//            //  add(ChartEntity(Color.WHITE, graph2))
//        }
        setLegend(legend)
       // setList(arrGraph)
        invalidate()
    }

    fun setLegend(list: ArrayList<String>) {
        setLegend(list.toList())
    }

    fun setLegend(list: List<String>) {
        legendArray.clear()

        if (list.isNotEmpty())
            legendArray.addAll(list)
    }

    fun setList(list: ArrayList<ChartEntity>) {
        setList(list.toList())
    }

    fun setList(list: List<ChartEntity>) {
        this.chartEntities.clear()
        invalidate()

        if (list.isEmpty()) //no need to go farther
            return

        this.chartEntities.addAll(list)

        val maxes = ArrayList<Float>()
        for (lineGraph in chartEntities) {
            val copies = lineGraph.values.copyOf(lineGraph.values.size)
            copies.sort()
            maxes.add(copies[copies.size - 1])
        }
        this.maxValue = (Collections.max(maxes) as Float).toLong()
        this.initializePaint()
    }


    private var isMoved = false
    private var locationX = 0f
    private var locationY = 0f

    private var distanceX = 0f
    private var distanceY = 0f
    private var lastX = 0f
    private var lastY = 0f

//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean = event?.let {
//        when (event.action) {
//            MotionEvent.ACTION_MOVE -> {
//                isMoved = true
//                locationX = event.x
//                locationY = event.y
//                distanceX += abs(lastX - event.x)
//                distanceY += abs(lastY - event.y)
//                lastY = event.y
//                lastX = event.x
//                invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                isMoved = false
//                invalidate()
//            }
//        }
//
//        true
//    } ?: false

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        this.initializePaint()

        this.xLength = (width - (mPaddingLeft + mPaddingRight)).toInt()
        this.yLength = (height - (mPaddingBottom + mPaddingTop)).toInt()

        this.chartXLength = (width - (mPaddingLeft + mPaddingRight)).toInt()
        this.chartYLength = (height - (mPaddingTop + mPaddingBottom)).toInt()

        canvas.drawColor(Color.TRANSPARENT)


        val graphCanvasWrapper = GraphCanvasWrapper(
            canvas,
            this.width,
            this.height,
            this.mPaddingLeft.toInt(),
            this.mPaddingBottom.toInt()
        )


        graphCanvasWrapper.drawLine(0.0f, 0.0f, chartXLength.toFloat(), 0.0f, pBaseLine)

        var newX: Float
        val gap = if (chartEntities[0].values.size>1) {
            chartXLength / (chartEntities[0].values.size - 1)
        } else {
            10
        }
        for (i in chartEntities[0].values.indices) {
            newX = (gap * i).toFloat()
            graphCanvasWrapper.drawLine(newX, 0.0f, newX, chartYLength.toFloat(), pBaseLine)

            drawGraph(graphCanvasWrapper)
            drawXText(graphCanvasWrapper)

        }

        canvas.save()
        canvas.restore()

        if (isMoved) {
            val index = (locationX / width).toInt()
            graphCanvasWrapper.drawLine(locationX, yLength.toFloat(), locationX, 0f, pBaseLine)
            graphCanvasWrapper.drawCircle(locationX, locationY, 8f, pCircle)
        }

    }


    private fun drawXText(graphCanvas: GraphCanvasWrapper) {

        if (legendArray.isEmpty()) {
            return
        }

        var x: Float
        var y: Float
        val xGap = if (chartEntities[0].values.size>1) {
            chartXLength / (chartEntities[0].values.size - 1)
        } else {
            10.0f
        }


        for (i in chartEntities[0].values.indices) {
            val rect = Rect()

            var semibold: Typeface = Typeface.createFromAsset( context.assets, "font/basiercircle_semibold.ttf" )
            val text = legendArray[i]
            pMarkTextMonth.measureText(text)
            pMarkTextMonth.textSize = 30f
            pMarkTextMonth.typeface = semibold

            x = if (i == 0){
                30f
            } else {
                xGap.toFloat() * i
            }
            y = (-(20 + rect.height())).toFloat()

            pMarkTextMonth.getTextBounds(text, 0, text.length, rect)
            val degree: Int = 0
            val px = rect.exactCenterX() + x + 10
            val py = y + rect.exactCenterY() - 10

            graphCanvas.drawText(
                text,
                (x - rect.width() / 2)+10,
                y+100,
                pMarkTextMonth,
                degree.toFloat(),
                px,
                py
            )
        }
    }

    private fun drawGraph(graphCanvasWrapper: GraphCanvasWrapper) {


// Gradient Shade colors distribution setting uniform for now
        val positions = null //floatArrayOf(0f, 0.3f, 0.6f)

// Gradient Shade colors
        val colors = intArrayOf(
            ContextCompat.getColor(
                context,
                R.color.demo
            ),
            ContextCompat.getColor(
                context,
                R.color.demo2
            ), ContextCompat.getColor(
                context,
                R.color.demo3
            ), ContextCompat.getColor(
                context,
                R.color.demo4
            ),
            ContextCompat.getColor(
                context,
                R.color.demo3
            )
        )

        //  this.pCircleBG.color = bgColor

        for (m in chartEntities.indices) {
            val linePath = GraphPath(width, height, mPaddingLeft.toInt(), mPaddingBottom.toInt())
            var first = false

            var x: Float
            var y: Float

            this.p.setShader(
                LinearGradient(
                    0f, 0f, measuredWidth.toFloat(), 0f,
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
            )

            this.pCircle.setShader(
                LinearGradient(
                    0f, 0f, measuredWidth.toFloat(), 0f,
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
            )
            //   this.pCircle.color = chartEntities[m].color
            val xGap = if (chartEntities[m].values.size>1) {
                chartXLength / (chartEntities[m].values.size - 1)
            } else {
                10
            }


            for (j in chartEntities[m].values.indices) {

                if (j < chartEntities[m].values.size) {
                    x = (xGap * j).toFloat()
                    y = yLength * chartEntities[m].values[j] / maxValue

                    if (first) {
                        linePath.lineTo(x, y)
                    } else {
                        linePath.moveTo(x, y)
                        first = true
                    }


//                    if (isMoved) {
//                        graphCanvasWrapper.drawLine(locationX, yLength.toFloat(), locationX, 0f, pBaseLine)
//                        graphCanvasWrapper.drawCircle(locationX, locationY, 8f, pCircle)
//                    }

                }

            }

            graphCanvasWrapper.canvas?.drawPath(linePath, p)


            for (t in chartEntities[m].values.indices) {
                if (t < chartEntities[m].values.size) {
                    x = (xGap * t).toFloat()
                    y = yLength * chartEntities[m].values[t] / maxValue
//
                    var bold: Typeface = Typeface.createFromAsset( context.assets, "font/basiercircle_medium.ttf" )
//
                    val rect = Rect()
                    val text = chartEntities[m].values[t].toInt().toString()

                    pMarkTextValueBlack.textSize = 30f
                    pMarkTextValue.textSize = 30f
                    pMarkTextMonth.textSize = 30f
                    pMarkTextValueBlack.typeface=bold



                    pMarkTextValue.getTextBounds(text, 0, text.length, rect)
                    val degree: Int = 0
                    val px = rect.exactCenterX() + x + 5
                    val py = y + rect.exactCenterY() - 5

                    if (chartEntities[m].values.last().equals(chartEntities[m].values[t])) {
                        graphCanvasWrapper.drawText(
                            text,
                            (x - rect.width() / 2) + 10,
                            y - 60,
                            pMarkTextValueBlack,
                            degree.toFloat(),
                            px,
                            py
                        )
                    } else {
                        graphCanvasWrapper.drawText(
                            text,
                            (x - rect.width() / 2) + 25,
                            y - 50,
                            pMarkTextValue,
                            degree.toFloat(),
                            px,
                            py
                        )
                    }
                    with(graphCanvasWrapper) {
                        drawCircle(x, y, 8.0f, pCircle)
                        if (chartEntities[m].values.last().equals(chartEntities[m].values[t])) {
                            drawCircle(x, y, 4.0f, pCircleBG)
                            drawCircle(x, y, 1.0f, pCircleBG2)

                        }else{
                            drawCircle(x, y, 4.0f, pCircleBG)
                        }
                    }

//                    valueMap[t] = x
                }

            }
        }


    }

    private fun initializePaint() {
        p = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            isFilterBitmap = true
            color = Color.BLUE
            strokeWidth = 10f
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        pCircle = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            isFilterBitmap = true
            // color = Color.WHITE
            strokeWidth = 20f
            style = Paint.Style.STROKE
        }

        pCircleBG = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            color = Color.WHITE
            strokeWidth = 10f
            style = Paint.Style.FILL_AND_STROKE
        }

        pCircleBG2 = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            color = Color.BLACK
            strokeWidth = 10f
            style = Paint.Style.FILL_AND_STROKE
        }

        pLine = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true //text anti alias
            isFilterBitmap = true // bitmap anti alias
            shader = LinearGradient(
                0f,
                300f,
                0f,
                0f,
                Color.BLUE,
                Color.YELLOW,
                Shader.TileMode.MIRROR
            )
        }


        pBaseLine = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            isFilterBitmap = true
            color = lineColor
            strokeWidth = 2f
        }

        pBaseLineX = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            isFilterBitmap = true
            color = lineColor
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }



        pMarkTextMonth = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = Color.parseColor("#CACFCF")

        }
        pMarkTextValue = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = Color.parseColor("#CFCFCF")
        }
        pMarkTextValueBlack = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = Color.BLACK
        }
    }
}
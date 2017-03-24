package com.example.pdolbik.myapplication

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.ImageView


class CircleImageView: ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet):
            super(context, attrs) { init(context, attrs) }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) { init(context, attrs) }


    companion object {
        val SCALE_TYPE = ScaleType.CENTER_CROP
        const val DEFAULT_BORDER_WIDTH  = 4F
    }


    private var paint:       Paint?    = null
    private var paintBorder: Paint?    = null
    private var borderWidth: Float?    = null
    private var canvasSize:  Int?      = null
    private var image:       Bitmap?   = null
    private var myDrawable:  Drawable? = null
    private var showBorder = false


    private fun init(context: Context, attrs: AttributeSet) {
        // Инициализируем Paint. Init paint.
        paint = Paint()
        paint?.isAntiAlias = true

        paintBorder = Paint()
        paintBorder?.isAntiAlias = true
        paintBorder?.style = Paint.Style.STROKE

        // Загружаем атрибуты и устанавливаем значения свойств.
        // Load the styled attributes and set their properties.
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
        if (attributes.getBoolean(R.styleable.CircleImageView_civ_border_active, true)) {
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
            setBorderWidth(attributes.getDimension(R.styleable.CircleImageView_civ_border_width, defaultBorderSize))
            setBorderColor(attributes.getColor(R.styleable.CircleImageView_civ_border_color, Color.TRANSPARENT))
        }
        attributes.recycle()
    }


    private fun setBorderWidth(borderWidth: Float) {
        this.borderWidth = borderWidth
        requestLayout()
        invalidate()
    }


    private fun setBorderColor(color: Int) {
        paintBorder?.color = color
        invalidate()
    }


    override fun getScaleType(): ScaleType = SCALE_TYPE


    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(
                    String.format("ScaleType %s not supported. ScaleType.CENTER_CROP is used by default. So you don't need to use ScaleType.", scaleType))
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width  = measureSize(widthMeasureSpec)
        val height = measureSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }


    private fun measureSize(measureSpec: Int) : Int {
        val result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when(specMode) {
        // Родитель точно задал размеры для ребенка.
        // The parent has determined an exact size for the child.
            MeasureSpec.EXACTLY -> result = specSize
        // Ребенок может быть настолько большим, насколько он хочет, до указанного размера.
        // The child can be as large as it wants up to the specified size.
            MeasureSpec.AT_MOST -> result = specSize
        // The parent has not imposed any constraint on the child.
        // Родитель не наложил на ребенка никаких ограничений.
            else -> result = canvasSize ?: 0
        }
        return result
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasSize = if (h < w) h else w
        if (image != null) { updateShader() }
    }


    override fun onDraw(canvas: Canvas) {
        loadBitmap()
        if (image == null) { return }
        val circleCenter = ( canvasSize?.minus( (borderWidth ?: 0F)*2 ) )?.div(2)
        val drawBorder = circleCenter?.plus((borderWidth ?: 0F)) ?: 0F

        // Рисуем рамку. Draw Border.
        if (showBorder) {
            val halfBorder = (borderWidth ?: 0F).div(2)
            paintBorder?.strokeWidth = borderWidth ?: 0F
            canvas.drawCircle(drawBorder, drawBorder, drawBorder - halfBorder, paintBorder)
        }
        // Рисуем изображение. Draw images.
        canvas.drawCircle(drawBorder, drawBorder, circleCenter ?: 0F, paint)
    }


    private fun loadBitmap() {
        if (myDrawable == drawable) { return }
        myDrawable = drawable
        if (myDrawable != null) {
            this.image = drawableToBitmap(myDrawable as Drawable)
            updateShader()
        }
    }


    /** Преобразуем drawable в bitmap. <br> Convert drawable to bitmap. */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) { return drawable.bitmap}

        val intrinsicWidth  = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight

        if ( !(intrinsicWidth > 0 && intrinsicHeight > 0) ) { return null!! }

        try {
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            Log.e(javaClass.toString(), "Encountered OutOfMemoryError while generating bitmap!")
            return null!!
        }
    }


    private fun updateShader() {
        if (image == null) { return }

        // Обрезаем изображение. Crop image.
        image = cropBitmap(image as Bitmap)

        // Создаем шейдер. Create Shader
        val shader = BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Центрируем изображение в шейдере. Center Image in Shader
        val matrix = Matrix()
        val imgWidth  = canvasSize?.div(image?.width?.toFloat() ?: 0F) ?: 0F
        val imgHeight = canvasSize?.div(image?.height?.toFloat() ?: 0F) ?: 0F
        matrix.setScale(imgWidth, imgHeight)
        shader.setLocalMatrix(matrix)

        // Устанавливаем шейдер в paint. Set Shader in Paint.
        paint?.shader = shader
    }


    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val btm: Bitmap
        if (bitmap.width > bitmap.height) {
            btm = Bitmap.createBitmap(
                    bitmap,
                    bitmap.width /2 - bitmap.height / 2,
                    0,
                    bitmap.height,
                    bitmap.height)
        } else {
            btm = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.height / 2 - bitmap.width / 2,
                    bitmap.width,
                    bitmap.width)
        }
        return btm
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                val scale = AnimationUtils.loadAnimation(context, R.anim.scale)
                startAnimation(scale)
            }
            MotionEvent.ACTION_UP -> {
                showBorder = !showBorder
                val scale = AnimationUtils.loadAnimation(context, R.anim.scale_2)
                startAnimation(scale)
            }
        }
        return true
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        image       = null
        myDrawable  = null
    }
}
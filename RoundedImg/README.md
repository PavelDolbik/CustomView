## RoundedImg

#### Init resources
```kotlin
constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) { init(context, attrs) }


private fun init(context: Context, attrs: AttributeSet) {
        paint = Paint()
        paint?.isAntiAlias = true

        paintBorder = Paint()
        paintBorder?.isAntiAlias = true

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
        if (attributes.getBoolean(R.styleable.CircleImageView_civ_border_active, true)) {
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
            setBorderWidth(attributes.getDimension(R.styleable.CircleImageView_civ_border_width, defaultBorderSize))
            setBorderColor(attributes.getColor(R.styleable.CircleImageView_civ_border_color, Color.TRANSPARENT))
        }
        attributes.recycle()
    }
```

#### Get drawable
```kotlin
private fun loadBitmap() {
        if (myDrawable == drawable) { return }
        myDrawable = drawable
        if (myDrawable != null) {
            this.image = drawableToBitmap(myDrawable as Drawable)
            updateShader()
        }
    }
```

#### Convert drawable to bitmap
```kotlin
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
```

#### Crop bitmap
```kotlin
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


#### Create shader
```kotlin
private fun updateShader() {
        if (image == null) { return }

        image = cropBitmap(image as Bitmap)
        val shader = BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val matrix = Matrix()
        val imgWidth  = canvasSize?.div(image?.width?.toFloat() ?: 0F) ?: 0F
        val imgHeight = canvasSize?.div(image?.height?.toFloat() ?: 0F) ?: 0F
        matrix.setScale(imgWidth, imgHeight)
        shader.setLocalMatrix(matrix)

        paint?.shader = shader
    }
```	
	

#### Draw view
```kotlin
override fun onDraw(canvas: Canvas) {
        loadBitmap()
        if (image == null) { return }
        val circleCenter = ( canvasSize?.minus( (borderWidth ?: 0F)*2 ) )?.div(2)

        val drawBorder = circleCenter?.plus((borderWidth ?: 0F)) ?: 0F
        if (showBorder) { canvas.drawCircle(drawBorder, drawBorder, drawBorder, paintBorder) }

        canvas.drawCircle(drawBorder, drawBorder, circleCenter ?: 0F, paint)
    }
```


#### Add animation
```kotlin
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
```

#### Anim
```xml
<?xml version="1.0" encoding="utf-8"?>
<scale
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXScale="1.0"
    android:toXScale="0.9"
    android:fromYScale="1.0"
    android:toYScale="0.9"
    android:duration="200"
    android:pivotX="50%"
    android:pivotY="50%"
    android:fillAfter="true"
    />

<?xml version="1.0" encoding="utf-8"?>
<scale
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXScale="0.9"
    android:toXScale="1.0"
    android:fromYScale="0.9"
    android:toYScale="1.0"
    android:duration="200"
    android:pivotX="50%"
    android:pivotY="50%"
    android:fillAfter="true"
    />
```
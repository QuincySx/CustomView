package com.a21vianet.sample.customview.passwordEdit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Choreographer
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.a21vianet.sample.customview.R
import kotlin.math.max

class PassWordEditTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }
    private var mOnEditCompleteListener: OnEditCompleteListener? = null

    var textSize: Float = 50f
        set(value) {
            field = value
            mPaint.textSize = field
            invalidate()
        }
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            mPaint.color = field
            invalidate()
        }
    var isPassword = false
        set(value) {
            field = value
            invalidate()
        }
    var isBoldText: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var passwordText: String = "●"
        set(value) {
            field = value
            invalidate()
        }
    var inputCount: Int = 4
        set(value) {
            field = value
            invalidate()
        }
    private var inputTexts = ArrayList<Char>(6)
        set(value) {
            field = value
            invalidate()
        }
    var space = dp2px(10F)
        set(value) {
            field = value
            invalidate()
        }

    var textBackgroundDrawable: Drawable? = ColorDrawable(Color.parseColor("#ffffff"))
        set(value) {
            field = value
            invalidate()
        }
    var textBackgroundDrawableWidth = dp2px(60F)
        set(value) {
            field = value
            invalidate()
        }
    var textBackgroundDrawableHeight = dp2px(60F)
        set(value) {
            field = value
            invalidate()
        }

    var textCursorDrawable: Drawable? = ColorDrawable(Color.parseColor("#000000"))
        set(value) {
            field = value
            invalidate()
        }
    var textCursorDrawableWidth = dp2px(2F)
        set(value) {
            field = value
            invalidate()
        }
    var textCursorDrawableHeight = dp2px(30F)
        set(value) {
            field = value
            invalidate()
        }

    // 一直打开光标
    private var cursorAlwaysOn = false

    // 记录光标的闪动状态
    private var needDrawCursor = false
    private var cursorInterval = 600L

    init {
        // 可以点击
        isClickable = true
        // 触摸模式和键盘模式下，是否可以获得焦点
        isFocusable = true
        // 触摸模式下，是否可以获得焦点
        isFocusableInTouchMode = true

        context.obtainStyledAttributes(attrs, R.styleable.PassWordEditTextView).apply {
            textSize = getDimension(R.styleable.PassWordEditTextView_android_textSize, 50f)
            textColor = getColor(R.styleable.PassWordEditTextView_android_textColor, Color.BLACK)
            inputCount = getInt(R.styleable.PassWordEditTextView_pwdInputCount, 4)
            isBoldText = getBoolean(R.styleable.PassWordEditTextView_pwdIsBoldText, false)
            isPassword = getBoolean(R.styleable.PassWordEditTextView_pwdIsPassword, true)
            passwordText = getString(R.styleable.PassWordEditTextView_pwdPasswordText) ?: "●"
            space = getDimensionPixelSize(R.styleable.PassWordEditTextView_pwdTextSpace, dp2px(10f))
            textBackgroundDrawable = getDrawable(R.styleable.PassWordEditTextView_pwdTextBackground)
            textBackgroundDrawableWidth =
                    getDimensionPixelSize(R.styleable.PassWordEditTextView_pwdTextBackgroundWidth, dp2px(60f))
            textBackgroundDrawableHeight =
                    getDimensionPixelSize(R.styleable.PassWordEditTextView_pwdTextBackgroundHeight, dp2px(60f))
            textCursorDrawable = getDrawable(R.styleable.PassWordEditTextView_pwdCursorDrawable)
            textCursorDrawableWidth =
                    getDimensionPixelSize(R.styleable.PassWordEditTextView_pwdCursorDrawableWidth, dp2px(2f))
            textCursorDrawableHeight =
                    getDimensionPixelSize(R.styleable.PassWordEditTextView_pwdCursorDrawableHeight, textSize.toInt())
            needDrawCursor = textCursorDrawable != null
            cursorAlwaysOn = getBoolean(R.styleable.PassWordEditTextView_pwdCursorAlwaysOn, false)
            cursorInterval = getInteger(R.styleable.PassWordEditTextView_pwdCursorInterval, 600).toLong()
            recycle()
        }

        mPaint.textSize = textSize
        mPaint.color = textColor
        mPaint.isFakeBoldText = isBoldText
    }

    /**
     * 只能输入数字
     */
    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        outAttrs!!.inputType = EditorInfo.TYPE_CLASS_NUMBER
        return null
    }

    /**
     * 该控件是 TextEdit
     */
    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    /**
     * 当前是否是编辑状态
     */
    override fun isInEditMode(): Boolean {
        return isFocused
    }

    // 如果实时开启、关闭控件，对键盘做出一些相应。
    override fun setEnabled(enabled: Boolean) {
        if (enabled == isEnabled) {
            return
        }
        if (!enabled) {
            hideSoft()
        }
        super.setEnabled(enabled)
        if (enabled) {
            getInputMethodManager().restartInput(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        when {
            widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST -> {
                val preHeight = textBackgroundDrawableHeight + paddingTop + paddingBottom
                val preWidth = (textBackgroundDrawableWidth * inputCount) + (space * (inputCount - 1)) + paddingStart + paddingEnd

                super.onMeasure(
                        MeasureSpec.makeMeasureSpec(preWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(preHeight, MeasureSpec.EXACTLY)
                )
            }
            widthMode == MeasureSpec.AT_MOST -> {
                val preWidth = (textBackgroundDrawableWidth * inputCount) + (space * (inputCount - 1)) + paddingStart + paddingEnd
                super.onMeasure(
                        MeasureSpec.makeMeasureSpec(preWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec,
                )
            }
            heightMode == MeasureSpec.AT_MOST -> {
                val maxHeight = max(textBackgroundDrawableHeight, textCursorDrawableHeight)
                val preHeight = maxHeight + paddingTop + paddingBottom
                super.onMeasure(
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(preHeight, MeasureSpec.EXACTLY)
                )
            }
            else -> {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        val fontMetrics = mPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom

        // 找到每一个字的位置
        val centerY = textBackgroundDrawableHeight / 2f + paddingTop
        val paddingLeft = ((width - paddingStart - paddingEnd - (textBackgroundDrawableWidth * inputCount) - (space * (inputCount - 1))) / 2) + paddingLeft
        val perWidth = textBackgroundDrawableWidth

        for (index in 0 until inputCount) {
            drawTextBackground(canvas, index, perWidth, paddingLeft)
            if (index < inputTexts.size) {
                val currentText = if (isPassword) {
                    passwordText
                } else {
                    inputTexts[index].toString()
                }
                val textWidth = mPaint.measureText(currentText)
                canvas.drawText(
                        currentText,
                        paddingLeft + (perWidth * index + perWidth / 2 - textWidth / 2) + space * index,
                        centerY + distance,
                        mPaint
                )
            }
            if (index == inputTexts.size) {
                drawCursor(canvas, index, perWidth, paddingLeft)
            }
        }
    }

    private val drawCursorCallback = Choreographer.FrameCallback {
        needDrawCursor = if (cursorAlwaysOn) {
            true
        } else {
            !needDrawCursor
        }
        invalidate()
    }

    private fun drawCursor(canvas: Canvas, index: Int, perWidth: Int, paddingLeft: Int) {
        if (!isInEditMode) {
            needDrawCursor = true
            return
        }
        textCursorDrawable?.let {
            if (needDrawCursor) {
                val left = paddingLeft + perWidth * index + space * index + perWidth / 2 - textCursorDrawableWidth / 2
                it.setBounds(
                        left,
                        ((height - paddingTop - paddingBottom - textCursorDrawableHeight) / 2) + paddingTop,
                        left + textCursorDrawableWidth,
                        ((height - paddingTop - paddingBottom - textCursorDrawableHeight) / 2) + paddingTop + textCursorDrawableHeight
                )
                it.draw(canvas)
            }
            // 开始cursor动画
            Choreographer.getInstance().removeFrameCallback(drawCursorCallback)
            Choreographer.getInstance().postFrameCallbackDelayed(drawCursorCallback, cursorInterval)
        }
    }

    private fun drawTextBackground(canvas: Canvas, index: Int, perWidth: Int, paddingLeft: Int) {
        if (textBackgroundDrawable == null) return
        val drawTextLeft = paddingLeft + perWidth * index + space * index + perWidth / 2 - textBackgroundDrawableWidth / 2
        textBackgroundDrawable?.setBounds(
                drawTextLeft,
                ((height - paddingTop - paddingBottom - textBackgroundDrawableHeight) / 2) + paddingTop,
                drawTextLeft + textBackgroundDrawableWidth,
                ((height - paddingTop - paddingBottom - textBackgroundDrawableHeight) / 2) + paddingTop + textBackgroundDrawableHeight
        )
        if (textBackgroundDrawable is StateListDrawable) {
            if (index == inputTexts.size && isFocused) {
                textBackgroundDrawable?.state = intArrayOf(android.R.attr.state_focused)
            } else {
                textBackgroundDrawable?.state = intArrayOf(android.R.attr.state_empty)
            }
            textBackgroundDrawable?.draw(canvas)
        } else {
            textBackgroundDrawable?.draw(canvas)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                if (inputTexts.size < inputCount) {
                    event?.displayLabel?.let { char ->
                        inputTexts.add(char)
                    }
                    invalidate()

                    if (inputTexts.size == inputCount) {
                        mOnEditCompleteListener?.onEditComplete(inputTexts.toUtf8String())
                    }
                }
                return true
            }
            KeyEvent.KEYCODE_DEL -> {
                if (inputTexts.isNotEmpty()) {
                    inputTexts.removeLast()
                    invalidate()
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                hideSoft()
                mOnEditCompleteListener?.onEditComplete(inputTexts.toUtf8String())
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            performClick()
            getInputMethodManager().let {
                if (isFocusable && !isFocused) {
                    requestFocus()
                }
                it.viewClicked(this)
                it.showSoftInput(this, 0)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getInputMethodManager(): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun hideSoft() {
        getInputMethodManager().let {
            if (it.isActive(this)) {
                it.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    // 在 Window 上解绑
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Choreographer.getInstance().removeFrameCallback(drawCursorCallback)
    }

    fun clear() {
        this.inputTexts.clear()
    }

    fun setOnEditCompleteListener(onEditCompleteListener: OnEditCompleteListener) {
        mOnEditCompleteListener = onEditCompleteListener
    }

    fun setContent(text: String) {
        this.inputTexts.clear()
        this.inputTexts.addAll(text.toCharArray().toList())
    }

    private fun dp2px(dipValue: Float): Int {
        val displayMetricsDensity = context.resources.displayMetrics.density
        return (dipValue * displayMetricsDensity + 0.5f).toInt()
    }

    fun interface OnEditCompleteListener {
        fun onEditComplete(text: String)
    }
}

private fun ArrayList<Char>.toUtf8String(): String {
    val sb = StringBuilder()
    for (ch in this) {
        sb.append(ch)
    }
    return sb.toString()
}
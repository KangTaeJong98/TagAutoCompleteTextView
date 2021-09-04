package com.taetae98.module.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.core.text.getSpans
import com.taetae98.module.R

class TagAutoCompleteTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.autoCompleteTextViewStyle,
) : AppCompatMultiAutoCompleteTextView(
    context, attrs, defStyleAttr
) {
    var tagTextSize: Float
    var tagTextColor: Int
    var tagBackground: Drawable?

    init {
        setTokenizer(SpaceTokenizer())
        setOnItemClickListener { adapter, _, position, _ ->
            changeTextToTag(adapter.getItemAtPosition(position))
        }

        context.theme.obtainStyledAttributes(attrs, R.styleable.TagAutoCompleteTextView, defStyleAttr, 0).apply {
            tagTextSize = getDimension(R.styleable.TagAutoCompleteTextView_tagTextSize, textSize)/resources.displayMetrics.density
            tagTextColor = getColor(R.styleable.TagAutoCompleteTextView_tagTextColor, Color.LTGRAY)
            tagBackground = getDrawable(R.styleable.TagAutoCompleteTextView_tagBackground)
        }
    }

    private fun changeTextToTag(tag: Any) {
        val originSelection = selectionEnd

        setText(update(text, tag))
        setSelection(originSelection)
    }

    private fun update(text: Editable, tag: Any): Spannable {
        return SpannableStringBuilder(text).apply {
            setSpan(
                createTag(tag),
                selectionEnd - tag.toString().length,
                selectionEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun createTag(tag: Any): Tag {
        val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val view = TextView(context).apply {
            text = tag.toString()
            textSize = tagTextSize
            background = tagBackground
            setTextColor(tagTextColor)
            measure(spec, spec)
            layout(0, 0, measuredWidth, measuredHeight)
        }

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        view.draw(canvas)
        val drawable = BitmapDrawable(resources, bitmap).apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }

        return Tag(tag, drawable)
    }

    fun getTags(): Array<out Tag> {
        return text.getSpans()
    }

    class Tag(
        private val item: Any,
        drawable: Drawable
    ) : ImageSpan(drawable)

    class SpaceTokenizer : Tokenizer {
        private val separators by lazy {
            arrayOf(' ', '\n')
        }

        override fun findTokenStart(text: CharSequence, cursor: Int): Int {
            var i = cursor

            while (i > 0 && text[i - 1] !in separators) {
                i--
            }
            while (i < cursor && text[i] == ' ') {
                i++
            }

            return i
        }

        override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
            var i = cursor
            val len = text.length

            while (i < len) {
                if (text[i] in separators) {
                    return i
                } else {
                    i++
                }
            }

            return len
        }

        override fun terminateToken(text: CharSequence): CharSequence {
            var i = text.length

            while (i > 0 && text[i - 1] == ' ') {
                i--
            }

            return if (i > 0 && text[i - 1] in separators) {
                text
            } else {
                if (text is Spanned) {
                    val sp = SpannableString("$text")
                    TextUtils.copySpansFrom(
                        text, 0, text.length,
                        Any::class.java, sp, 0
                    )
                    sp
                } else {
                    "$text"
                }
            }
        }
    }
}
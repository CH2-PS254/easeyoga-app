package com.dicoding.capstone_ch2ps254.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.capstone_ch2ps254.R
import com.dicoding.capstone_ch2ps254.utils.extension.isEmailValid

class EtEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                when {
                    email.isBlank() -> error = context.getString(R.string.email_empty)
                    !email.isEmailValid() -> error = context.getString(R.string.email_error)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}
package com.sensibol.lucidmusic.singstr.gui.login

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.EditText
import androidx.annotation.NonNull
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.sensibol.lucidmusic.singstr.gui.R
import org.jetbrains.annotations.NotNull


interface OtpViewListener{
    fun onEnterOtp(otp: String)
}

class OtpView: FrameLayout{


    private var etOtpDigit1: EditText? = null
    private  var etOtpDigit2:EditText? = null
    private  var etOtpDigit3:EditText? = null
    private  var etOtpDigit4:EditText? = null
    private  var etOtpDigit5:EditText? = null
    private  var etOtpDigit6:EditText? = null
    private lateinit var otpEt: Array<EditText?>


    private lateinit var listener: OtpViewListener

    fun setListener(listener: OtpViewListener){
        this.listener = listener
    }


    constructor(@NotNull context: Context): super(context){
        init()
    }

    constructor(@NonNull context: Context, attrs: AttributeSet) : super(context, attrs){
        init()

    }

    constructor(@NonNull context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        init()

    }

    private fun init() {
        inflate(context, R.layout.otp_view_layout, this)
        initViews()
    }

    private fun initViews() {
        otpEt = arrayOfNulls(6)
        etOtpDigit1 = findViewById(R.id.etOtpDigit1)
        etOtpDigit2 = findViewById(R.id.etOtpDigit2)
        etOtpDigit3 = findViewById(R.id.etOtpDigit3)
        etOtpDigit4 = findViewById(R.id.etOtpDigit4)
        etOtpDigit5 = findViewById(R.id.etOtpDigit5)
        etOtpDigit6 = findViewById(R.id.etOtpDigit6)
        otpEt[0] = etOtpDigit1
        otpEt[1] = etOtpDigit2
        otpEt[2] = etOtpDigit3
        otpEt[3] = etOtpDigit4
        otpEt[4] = etOtpDigit5
        otpEt[5] = etOtpDigit6
        setOtpEditTextHandler()
    }

    private fun setOtpEditTextHandler() { //This is the function to be called
        for (i in 0..5) { //Its designed for 6 digit OTP
            otpEt[i]?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    otpEt[i]!!.background = ContextCompat.getDrawable(otpEt[i]!!.context, R.drawable.bg_rounded_otp_color)
                }
                override fun afterTextChanged(s: Editable) {
                    if (i == 5 && !otpEt[i]?.text.toString().isEmpty()) {
                        otpEt[5]?.let { putFocus(it) } //Keep focus when you have entered the last digit of the OTP.
                    } else if (!otpEt[i]?.text.toString().isEmpty()) {
                        otpEt[i + 1]?.let { putFocus(it) } //focuses on the next edittext after a digit is entered.
                    }
                    if (otpEt[5]!!.text.isNotEmpty()){
                        allFill(true)
                    }else{
                        allFill(false)
                    }

                    listener.onEnterOtp(getEnterOTP())
                }
            })
            otpEt[i]?.setOnKeyListener(OnKeyListener { v, keyCode, event ->
                if (event.action !== KeyEvent.ACTION_DOWN) {
                    return@OnKeyListener false //onKeyListener is called twice and this condition is to avoid it.
                }
                if (keyCode == KeyEvent.KEYCODE_DEL && otpEt[i]?.text.toString().isEmpty() && i != 0) {
                    //this condition is to handel the delete input by users.
                    otpEt[i - 1]?.setText("") //Deletes the digit of OTP
                    otpEt[i - 1]?.let { putFocus(it) } //and sets the focus on previous digit
                }
                false
            })
            otpEt[i]?.setOnClickListener { otpEt[i]?.let { it1 -> putFocus(it1) } }
        }
    }

    fun allFill(check: Boolean) {

    }

    private fun putFocus(editText: EditText) {
        if (!editText.text.toString().isEmpty()) {
            editText.setSelection(editText.text.toString().length)
        }
        editText.requestFocus()
    }

    fun getEnterOTP(): String {
/*        etOtpDigit1!!.background = ContextCompat.getDrawable(etOtpDigit1!!.context, R.drawable.bg_rounded_otp_blue)
        etOtpDigit2!!.background = ContextCompat.getDrawable(etOtpDigit2!!.context, R.drawable.bg_rounded_otp_blue)
        etOtpDigit3!!.background = ContextCompat.getDrawable(etOtpDigit3!!.context, R.drawable.bg_rounded_otp_blue)
        etOtpDigit4!!.background = ContextCompat.getDrawable(etOtpDigit4!!.context, R.drawable.bg_rounded_otp_blue)
        etOtpDigit5!!.background = ContextCompat.getDrawable(etOtpDigit5!!.context, R.drawable.bg_rounded_otp_blue)
        etOtpDigit6!!.background = ContextCompat.getDrawable(etOtpDigit6!!.context, R.drawable.bg_rounded_otp_blue)*/
        return etOtpDigit1!!.text.toString() + etOtpDigit2?.getText().toString() +
                etOtpDigit3?.getText().toString() + etOtpDigit4?.getText().toString() +
                etOtpDigit5?.getText().toString() + etOtpDigit6?.getText().toString()
    }



    fun clearOtpView() {
        etOtpDigit1?.setText(null)
        etOtpDigit2?.setText(null)
        etOtpDigit3?.setText(null)
        etOtpDigit4?.setText(null)
        etOtpDigit5?.setText(null)
        etOtpDigit6?.setText(null)
        putFocus(etOtpDigit1!!)
    }

    fun setOTP(s: CharSequence) {
        for (i in this.otpEt.indices) {
            if (i < s.length) {
                (this.otpEt.get(i) as EditText).setText(s[i].toString())
            } else {
                (this.otpEt.get(i) as EditText).setText("")
            }
        }
    }
}
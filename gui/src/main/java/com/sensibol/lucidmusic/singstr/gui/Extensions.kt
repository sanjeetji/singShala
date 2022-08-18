package com.sensibol.lucidmusic.singstr.gui

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.sensibol.android.base.gui.AppToast
import com.sensibol.lucidmusic.singstr.domain.WebServiceFailure
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.log10
import kotlin.system.exitProcess

val isFakeSinging: Boolean = BuildConfig.DEBUG && false

internal fun ImageView.loadUrl(
    url: String,
    @DrawableRes placeholderResId: Int? = R.drawable.place_holder,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    val builder = Glide.with(this).load(url)
    if (null != placeholderResId) builder.placeholder(placeholderResId)
    builder.listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            this@loadUrl.scaleType = scaleType
            return false
        }

    })
        .into(this)
}

// TODO - reduce lot of boilerplate
// lot of boilerplate - start
internal fun Fragment.showErrorToast(msg: String) {
    AppToast.show(requireContext(), msg, Toast.LENGTH_SHORT)
}

internal fun Fragment.handleFailure(e: Exception?) {
    Timber.v("handleFailure: IN")
    Timber.e(e)
    when (e) {
        is WebServiceFailure.NoNetworkFailure -> showErrorToast("No internet connection!")
        is WebServiceFailure.NetworkTimeOutFailure, is WebServiceFailure.NetworkDataFailure -> showErrorToast("Uh Oh! Please Try Again")
        else -> showErrorToast("Oops! Something Went Wrong")
    }
    Timber.v("handleFailure: OUT")
}


internal fun Activity.showErrorToast(msg: String) {
    AppToast.show(applicationContext, msg, Toast.LENGTH_SHORT)
}

internal fun Activity.handleFailure(e: Exception?) {
    Timber.v("handleFailure: IN")
    Timber.e(e)
    when (e) {
        is WebServiceFailure.NoNetworkFailure -> showErrorToast("No internet connection!")
        is WebServiceFailure.NetworkTimeOutFailure, is WebServiceFailure.NetworkDataFailure -> showErrorToast("Uh Oh! Please Try Again")
        else -> showErrorToast("Oops! Something Went Wrong")
    }
    Timber.v("handleFailure: OUT")
}

// lot of boilerplate - end

internal fun getTimeStringFromMilliSecond(millis: Long): String {
    var finalTimerString = ""
    var secondsString = ""

    var hours = millis / (1000 * 60 * 60)
    var minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
    var seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60) / 1000)

    if (hours > 0) {
        finalTimerString = "" + hours + ":"
    }

    // Prepending 0 to seconds if it is one digit
    if (seconds < 10) {
        secondsString = "0" + seconds
    } else {
        secondsString = "" + seconds
    }

    finalTimerString = finalTimerString + minutes + ":" + secondsString

    // return timer string
    return finalTimerString;
}

internal fun convertDatePattern(date: String): String {
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
    val date = sdf.parse(date)
    sdf = SimpleDateFormat("dd-MM-yyyy")
    return sdf.format(date)
}

internal fun convertDatePatternComments(date: String): String {
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    val date = sdf.parse(date)
    sdf = SimpleDateFormat("dd MMM")
    return sdf.format(date)
}

internal fun convertDatePatternDetailAnalysis(date: String): String {
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    val date = sdf.parse(date)
    sdf = SimpleDateFormat("dd MMM yyyy")
    return sdf.format(date)
}

internal fun convertDatePatternWebEngage(date: String): Date {
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    val date = sdf.parse(date)
    sdf = SimpleDateFormat("yyyy mm dd")
    return date
}

internal fun validateTextUsingPattern(text: String, pattern: String): Boolean {
    val patternObj = Pattern.compile(pattern)
    return patternObj.matcher(text).matches()
}

fun ShapeableImageView.loadCenterCropImageFromUrl(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.ic_profile_placeholder)
        .centerCrop()
        .into(this)
}

internal fun getStateList(): Array<String> {
    return arrayOf(
        "Andaman and Nicobar Islands", " Andhra Pradesh", " Arunachal Pradesh", " Assam ", " Bihar ",
        "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli", "Delhi", "Goa", "Gujarat", "Haryana",
        "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep",
        "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
        "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
        "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"
    )
}

internal fun timeMS2mmss(timeMS: Int): String {
    val minutes = timeMS / 1000 / 60
    val seconds = (timeMS / 1000 % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}

internal fun String.checkUserHandle(): String {
    if (isNotEmpty() && first() == '@') {
        return this
    } else {
        return "@$this"
    }
}

internal fun convertDaysToMonths(days: Int): Int {
    return days / 30
}

internal fun convertPerMonthPrice(month: Int, price: Int): Int {
    return price / month
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun triggerAppRestart(context: Context) {
    Timber.v("triggerAppRestart IN")
    context.packageManager.getLaunchIntentForPackage(context.packageName)?.component?.let {
        context.startActivity(Intent.makeRestartActivityTask(it))
        exitProcess(0)
    }
    Timber.v("triggerAppRestart OUT")
}

fun prettyViewsCount(count: Long): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val value = floor(log10(count.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(count / Math.pow(10.0, (base * 3).toDouble())) + suffix[base].toString()
    } else {
        DecimalFormat("#,##0").format(count).toString()
    }
}

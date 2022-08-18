package com.sensibol.lucidmusic.singstr.gui.app.customs.navigation_view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sensibol.lucidmusic.singstr.gui.R

class ImageBottomNavigationView : BottomNavigationView {

    private var navController: NavController? = null
    private var isImageLoaded = false


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    fun loadImage(
        imageUrl: String,
        @IdRes itemId: Int,
        @DrawableRes placeHolderResourceId: Int,
        @IdRes fragmentNavigationId: Int = 0
    ) {

        val navigationItemView = findViewById<BottomNavigationItemView>(itemId)

        val imageView = navigationItemView.children.find {
            it is ImageView
        } as? ImageView ?: return


        loadProfileImage(imageView, imageUrl, placeHolderResourceId, itemId)

        handleProfileBottomNavigationItemSelection(imageView, itemId, fragmentNavigationId)

    }


    private fun loadProfileImage(
        imageView: ImageView,
        imageUrl: String,
        @DrawableRes placeHolderResourceId: Int,
        @IdRes itemId: Int
    ) {

        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .optionalCircleCrop()
            .placeholder(R.drawable.ic_profile_placeholder)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    isImageLoaded = true

                    if (selectedItemId == itemId) {
                        imageView.clearColorFilter()
                        handleImageItemSelected(imageView)
                    }

                    return false
                }

            })
            .into(imageView)

    }


    fun setupWithNavController(navController: NavController) {

        this.navController = navController

        NavigationUI.setupWithNavController(this, navController)

    }


    private fun handleProfileBottomNavigationItemSelection(
        imageView: ImageView, itemId: Int, @IdRes fragmentNavigationId: Int
    ) {

        navController?.let {

            it.addOnDestinationChangedListener { _, destination, _ ->

                if (destination.id == fragmentNavigationId) {
                    handleImageItemSelected(imageView)
                    return@addOnDestinationChangedListener
                }

                handleImageItemNotSelected(imageView)

            }

        } ?: run {

            setOnItemSelectedListener { menuItem ->

                if (menuItem.itemId == itemId) {
                    handleImageItemSelected(imageView)
                } else {
                    handleImageItemNotSelected(imageView)
                }

                true
            }

        }

    }


    private fun handleImageItemNotSelected(imageView: ImageView) {

        if (isImageLoaded) {
            imageView.setBackgroundColor(Color.TRANSPARENT)
            return
        }

        imageView.clearColorFilter()

    }


    private fun handleImageItemSelected(imageView: ImageView) {

        if (isImageLoaded) {
            imageView.setBackgroundResource(R.drawable.bg_bottom_nav_profile_img)
            imageView.translationZ = 10f
            return
        }

        imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
    }


}
package com.example.vendingmachine

import android.content.Context
import android.support.annotation.AttrRes
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import ir.apend.slider.ui.Slider

class CustomSlider : Slider{


    var onPageChangeListener: ViewPager.OnPageChangeListener? = null

    constructor(context: Context):super(context)

    constructor(context: Context, attrs: AttributeSet?):super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int):super(context,attrs,defStyleAttr)


    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)

        onPageChangeListener?.onPageSelected(position)
    }

    fun setPageChange( onPageChangeListener: ViewPager.OnPageChangeListener){
        this.onPageChangeListener = onPageChangeListener
    }

}
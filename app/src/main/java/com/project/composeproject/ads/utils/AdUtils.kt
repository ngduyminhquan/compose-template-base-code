package com.project.composeproject.ads.utils

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

object AdUtils {

    fun tryToCloseCollapsibleBanner() {
        fun viewsFromWM(wmClass: Class<*>, wmInstance: Any): Any? {
            val viewsField = wmClass.getDeclaredField("mViews")
            viewsField.isAccessible = true
            return viewsField.get(wmInstance)
        }
        try {
            @SuppressLint("PrivateApi")
            val wmgClass = Class.forName("android.view.WindowManagerGlobal")
            val wmgInstance = wmgClass.getMethod("getInstance").invoke(null)
            val views = wmgInstance?.let { viewsFromWM(wmgClass, it) }

            val xx = (views as? List<Any?>)
                ?.filter {
                    it is View && it::class.java.name.startsWith("android.widget.PopupWindow")
                }
                ?.find { view ->
                    val firstViewGroup = (view as? ViewGroup)
                        ?.children
                        ?.firstOrNull() as? ViewGroup

                    firstViewGroup
                        ?.children
                        ?.find {
                            it::class.java.name.startsWith("com.google.android.gms.ads.internal") ||
                                    it::class.java.name.startsWith("com.google.android.gms.internal.ads") ||
                                    it::class.java.name.startsWith("ads_mobile_sdk")
                        } != null
                }

            (((xx as? ViewGroup)
                ?.children
                ?.firstOrNull() as? ViewGroup)
                ?.children
                ?.find {
                    it::class.java.name.startsWith("com.google.android.gms.ads.internal") ||
                            it::class.java.name.startsWith("com.google.android.gms.internal.ads") ||
                            it::class.java.name.startsWith("ads_mobile_sdk")
                }
                ?.parent as? ViewGroup)
                ?.children
                ?.lastOrNull()
                ?.callOnClick()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
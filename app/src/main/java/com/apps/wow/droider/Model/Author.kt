package com.apps.wow.droider.Model

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri

/**
 * Created by Jackson on 04/06/2017.
 */

class Author(val titleString: String, val descriptionString: String,
             val imageDrawable: Drawable) {

    enum class ON_CLICK_INTENT {

        STUB_FOR_INTENT, GUZENKO, GOLOVIN, GAZIMZYANOV, ISTISHEV, VEDENSKIY;

        fun getIntentByName(enumName: ON_CLICK_INTENT): Intent? {
            when (enumName) {
                GUZENKO -> return Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://vk.com/jackson23")
                )
                GOLOVIN -> return Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://vk.com/awawave")
                )
                GAZIMZYANOV -> return Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://vk.com/virgil7")
                )
                ISTISHEV -> return Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://vk.com/istishev")
                )
                VEDENSKIY -> return Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://vk.com/borisvedensky")
                )
                else -> return null
            }
        }
    }
}


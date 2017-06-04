package com.apps.wow.droider.Model

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.AppContext

/**
 * Created by Jackson on 04/06/2017.
 */

class Author(val titleString: String, val descriptionString: String,
             val imageDrawable: Drawable) {


    fun getIntentByName(titleString: String): Intent? {
        when (titleString) {
            AppContext.context.getString(R.string.about_fragment_alex_title) -> return Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/jackson23")
            )
            AppContext.context.getString(R.string.about_fragment_art_title) -> return Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/awawave")
            )
            AppContext.context.getString(R.string.about_fragment_fatih_title) -> return Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/virgil7")
            )
            AppContext.context.getString(R.string.about_fragment_istishev_title) -> return Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/istishev")
            )
            AppContext.context.getString(R.string.about_fragment_vedenskiy_title) -> return Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/borisvedensky")
            )
            else -> return null
        }
    }
}


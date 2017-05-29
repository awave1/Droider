package com.apps.wow.droider.Article

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.apps.wow.droider.R
import com.apps.wow.droider.databinding.FragmentImagePrevBinding


/**
 * Created by awave on 2016-12-30.
 */

class ImagePreviewFragment : Fragment() {
    lateinit var mBinding: FragmentImagePrevBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentImagePrevBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.navigationBarColor = ContextCompat.getColor(
                    context,
                    R.color.image_preview_bckg
            )
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }

        mBinding.img!!.setPhotoUri(Uri.parse(arguments.getString(IMAGE_URL)))

        mBinding.closeBtn.setOnClickListener {
            activity
                    .supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(this@ImagePreviewFragment)
                    .commit()

            onDestroy()
        }
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    companion object {
        private val TAG = "ImagePreviewFragment"
        val IMAGE_URL = "IMAGE_URL"

        fun newInstance(imageUrl: String): ImagePreviewFragment {
            val args = Bundle()
            args.putString(IMAGE_URL, imageUrl)
            val fragment = ImagePreviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

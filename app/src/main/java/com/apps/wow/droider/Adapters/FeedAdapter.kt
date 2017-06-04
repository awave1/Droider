package com.apps.wow.droider.Adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apps.wow.droider.Article.ArticleActivity
import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.databinding.CardBinding

class FeedAdapter(val feedModel: FeedModel) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private val TAG = FeedAdapter::class.java.simpleName

    private var touchYCoordinate: Float = 0.toFloat()

    private var touchXCoordinate: Float = 0.toFloat()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(CardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(feedViewHolder: FeedViewHolder, i: Int) {
        val post = feedModel.posts[i]
        val binding = feedViewHolder.mBinding
        binding.feedCardTitle.text = post.titleValue

        if (!TextUtils.isEmpty(post.descriptionValue)) {
            binding.feedCardDescription.text = post.descriptionValue
        } else {
            binding.feedCardDescription.visibility = View.GONE
        }

        binding.feedCardSiteUrl.text = post.url

        binding.feedCardImage.setImageURI(post.pictureWide)

        val url = post.url

        binding.cardView.setOnTouchListener { v, event ->
            touchXCoordinate = event.rawX
            touchYCoordinate = event.rawY
            false
        }

        binding.cardView.setOnClickListener { v ->
            try {
                val articleIntent = Intent(binding.cardView.context,
                        ArticleActivity::class.java)
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE,
                        binding.feedCardTitle.text.toString())

                articleIntent.putExtra(Utils.EXTRA_SHORT_DESCRIPTION,
                        binding.feedCardDescription.text.toString())

                articleIntent.putExtra(Utils.EXTRA_ARTICLE_URL, url)

                articleIntent.putExtra(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate)

                articleIntent.putExtra(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate)

                articleIntent.putExtra(Utils.EXTRA_ARTICLE_IMG_URL, post.pictureWide)

                binding.cardView.context.startActivity(articleIntent)

            } catch (npe: NullPointerException) {
                // Ошибка происходит если пытаться отправить пикчу
                // в статью. Сначала он выкидывал NullPointerException
                // на article в ArticleActivity. Я закомментил
                // после этого ничего не открывалось
                Toast.makeText(binding.cardView.context,
                        "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show()
                Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.cause)
                npe.printStackTrace()
            }
        }
        binding.cardView.setOnLongClickListener { view ->
            Log.d(TAG, "onLingClick cardview")
            val clipboardManager = binding.cardView
                    .context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copyLink = ClipData.newPlainText("", post.url)
            clipboardManager.primaryClip = copyLink
            Toast.makeText(view.context, R.string.main, Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return feedModel.posts.size
    }

    companion object {

        var headerImageDrawable: Drawable? = null
    }

    inner class FeedViewHolder(internal var mBinding: CardBinding) : RecyclerView.ViewHolder(mBinding.root)

}
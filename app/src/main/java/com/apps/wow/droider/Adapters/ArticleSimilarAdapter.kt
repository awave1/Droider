package com.apps.wow.droider.Adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.apps.wow.droider.Article.ArticleActivity
import com.apps.wow.droider.Model.Post
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.databinding.CardPopularBinding
import timber.log.Timber
import java.util.*

/**
 * Created by awave on 2016-12-25.
 */

class ArticleSimilarAdapter(private val mData: ArrayList<Post>) : RecyclerView.Adapter<ArticleSimilarAdapter.PopularViewHolder>() {

    private var touchYCoordinate: Float = 0.toFloat()
    private var touchXCoordinate: Float = 0.toFloat()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(CardPopularBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val post = mData[position]
        holder.binding.popularCardImage.setImageURI(post.pictureWide)
        holder.binding.popularTitle.text = post.titleValue
        holder.binding.count.text = holder.binding.root.resources.getString(
                R.string.popular_count,
                (position + 1).toString(),
                itemCount.toString()
        )

        holder.binding.popularCard.setOnTouchListener { _, event ->
            touchXCoordinate = event.rawX
            touchYCoordinate = event.rawY
            false
        }

        holder.binding.popularCard.setOnClickListener {
            try {
                val articleIntent = Intent(holder.binding.popularCard.context, ArticleActivity::class.java)

                articleIntent.putExtra(Const.EXTRA_ARTICLE_TITLE, post.titleValue)
                articleIntent.putExtra(Const.EXTRA_SHORT_DESCRIPTION, post.descriptionValue)
                articleIntent.putExtra(Const.EXTRA_ARTICLE_URL, post.url)
                articleIntent.putExtra(Const.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate)
                articleIntent.putExtra(Const.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate)
                articleIntent.putExtra(Const.EXTRA_ARTICLE_IMG_URL, post.pictureWide)

                holder.binding.root.context.startActivity(articleIntent)

            } catch (npe: NullPointerException) {
                Toast.makeText(holder.binding.root.context,
                        "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show()
                Timber.e(npe, "onClick: Failed to open ArticleActivity!")
                npe.printStackTrace()
            }
        }
    }

    inner class PopularViewHolder(var binding: CardPopularBinding) : RecyclerView.ViewHolder(binding.root)
}

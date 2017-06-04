package com.apps.wow.droider.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.apps.wow.droider.Model.Author
import com.apps.wow.droider.databinding.AuthorsRecyclerViewItemBinding
import java.util.*

/**
 * Created by Jackson on 04/06/2017.
 */

class AuthorsRecyclerViewAdapter(val authorList: ArrayList<Author>, private val mRouter: AdapterToFragmentRouter) : RecyclerView.Adapter<AuthorsRecyclerViewAdapter.AuthorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorsViewHolder {
        return AuthorsViewHolder(AuthorsRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AuthorsViewHolder, position: Int) {
        val author = authorList[position]
        holder.mBinding.authorsTitle.setText(author.titleString)
        holder.mBinding.authorsDescription.setText(author.descriptionString)
        holder.mBinding.authorsImage.setImageDrawable(author.imageDrawable)
        holder.mBinding.authorsContainer.setOnClickListener({ mRouter.startActivityFromAdapter(Author.ON_CLICK_INTENT.STUB_FOR_INTENT.getIntentByName(Author.ON_CLICK_INTENT.values()[position])) })
    }

    override fun getItemCount(): Int {
        return authorList.size
    }

    inner class AuthorsViewHolder(var mBinding: AuthorsRecyclerViewItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}
package com.apps.wow.droider.NavDrawScreens

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apps.wow.droider.Adapters.AdapterToFragmentRouter
import com.apps.wow.droider.Adapters.AuthorsRecyclerViewAdapter
import com.apps.wow.droider.Model.Author
import com.apps.wow.droider.R
import com.apps.wow.droider.databinding.AboutFragmentBinding
import java.util.*

class AboutFragment : android.app.Fragment(), AdapterToFragmentRouter {

    lateinit internal var mBinding: AboutFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true
        mBinding = AboutFragmentBinding.inflate(inflater, container, false)
        recyclerViewSetting()
        return mBinding.root
    }

    private val authorsList: ArrayList<Author>
        get() {
            val authorsList = ArrayList<Author>()
            authorsList.add(
                    Author(
                            getString(R.string.about_fragment_vedenskiy_title),
                            getString(R.string.about_fragment_vedenskiy_description),
                            ContextCompat.getDrawable(activity, R.drawable.ic_vedensky)))
            authorsList.add(
                    Author(
                            getString(R.string.about_fragment_istishev_title),
                            getString(R.string.about_fragment_istishev_description),
                            ContextCompat.getDrawable(activity, R.drawable.ic_istishev)))

            return authorsList
        }

    private val developerAuthorsList: ArrayList<Author>
        get() {
            val developersList = ArrayList<Author>()
            developersList.add(
                    Author(
                            getString(R.string.about_fragment_art_title),
                            getString(R.string.about_fragment_art_description),
                            ContextCompat.getDrawable(activity, R.drawable.ic_golovin)))
            developersList.add(0,
                    Author(
                            getString(R.string.about_fragment_alex_title),
                            getString(R.string.about_fragment_alex_description),
                            ContextCompat.getDrawable(activity, R.drawable.ic_guzenko)))
            developersList.add(
                    Author(
                            getString(R.string.about_fragment_fatih_title),
                            getString(R.string.about_fragment_fatih_description),
                            ContextCompat.getDrawable(activity, R.drawable.ic_gazimzyanov)))

            return developersList
        }

    private fun recyclerViewSetting() {
        val authors = AuthorsRecyclerViewAdapter(authorsList, this)

        mBinding.authorsList.adapter = authors
        mBinding.authorsList.layoutManager = LinearLayoutManager(activity)

        val devs = AuthorsRecyclerViewAdapter(developerAuthorsList, this)

        mBinding.devsList.adapter = devs
        mBinding.devsList.layoutManager = LinearLayoutManager(activity)
    }

    override fun startActivityFromAdapter(intent: Intent?) {
        if (intent != null) {
            startActivity(intent)
        }
    }
}
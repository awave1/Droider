package com.apps.wow.droider.DB

import io.realm.RealmObject

/**
 * Created by Jackson on 09/07/2017.
 */
open class Article : RealmObject()  {

    var articleHtml: String? = null

    var articleUrl : String? = null

}

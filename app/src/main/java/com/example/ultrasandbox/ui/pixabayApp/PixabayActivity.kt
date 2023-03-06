package com.example.ultrasandbox.ui.pixabayApp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ultrasandbox.R
import com.example.ultrasandbox.ui.pixabayApp.Constantes.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException

class PixabayActivity : AppCompatActivity(), AdapterRecycler.OnItemClickListener{

    /** GLOBALS **/

    private var recyclerView: RecyclerView?= null
    private var adapterRecycler: AdapterRecycler ?= null
    private var itemArrayList : ArrayList<ModelItem> ?= null
    private var requestQueue: RequestQueue?= null
    private var search: String ?= null
    private var image_typeSearch: String? = null
    private var image_orientationSearch: String? = null

    private var lv_history: ListView?= null
    private var et_search: EditText?= null
    private var btn_search: FloatingActionButton?= null
    private var btn_history: FloatingActionButton?= null

    private var spin_types: Spinner?= null
    private var pbar_progress: ProgressBar?= null

    var listSearched : ArrayList<String> = ArrayList()

    /** Configurates the spinner **/
    //This spinner precises wht kind of image you want in the UI
    fun spinnerSettings(){
        spin_types = findViewById(R.id.spinner_types) //We base the spinner on the array present in R.values.strings.xml
        var adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this, R.array.spinner_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spin_types!!.adapter = adapter
    }

    fun listviewSettings(){
        lv_history = findViewById(R.id.lV_History)
        var adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSearched)
        lv_history!!.adapter = adapter
    }

    @SuppressLint("ResourceAsColor")
    fun progressBarSettings(){
        pbar_progress = findViewById(R.id.pB_progress)

    }
    fun init() {
        //Design Java link
        recyclerView = findViewById(R.id.rV_itemsList)
        et_search = findViewById(R.id.eT_search)
        btn_search = findViewById(R.id.fAB_search)
        btn_history = findViewById(R.id.fAB_history)

        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        itemArrayList = ArrayList()
        requestQueue = Volley.newRequestQueue(this)

        //add other elements
        spinnerSettings()
        progressBarSettings()
        listviewSettings()
    }

    private fun parseJSON() {
        search = et_search!!.text.toString()
        image_typeSearch = spin_types!!.selectedItem.toString().lowercase()
        image_orientationSearch = "horizontal"
        val pixaKey = "33662216-dc2a74f07d05ac9626e395f4b"
        //https://pixabay.com/api/?key=33662216-dc2a74f07d05ac9626e395f4b&q=yellow+flowers&image_type=photo
        val urlJSONFile = ("https://pixabay.com/api/"
                + "?key=" + pixaKey
                + "&q=" + search
                + "&image_type=" + image_typeSearch
                + "&orientation=" + image_orientationSearch
                + "&pretty=true")
        val request = JsonObjectRequest(
            Request.Method.POST, urlJSONFile, null,
            { response ->
                try {
                    // On récupère le tableau de données JSON à partir de notre objet JsonObjectRequest
                    // dans un try and catch ajouter en auto en corrigeant l'erreur
                    val jsonArray = response.getJSONArray("hits")

                    // On récupère dans un premier temps toutes les données présentent dans le Array avec une boucle for
                    for (i in 0 until jsonArray.length()) {
                        val hit = jsonArray.getJSONObject(i)
                        // Puis on sélectionne celles dn on à besoin soit user - likes - webformatURL
                        val creator = hit.getString("user")
                        val likes = hit.getInt("likes")
                        val imageUrl = hit.getString("webformatURL")
                        val views = hit.getInt("views")
                        val downloads = hit.getInt("downloads")
                        val tags = hit.getString("tags")

                        // On ajoute les données à notre tableau en utilisant son model
                        itemArrayList!!.add(ModelItem(imageUrl, creator, likes, views, downloads, tags))
                    }
                    // En dehors du try on adapte le tableau de données
                    adapterRecycler = AdapterRecycler(
                        this@PixabayActivity, //MainActivity.this form in Kotlin
                        itemArrayList
                    ) // Noter MainActivity.this car nous sommes dans une classe interne

                    // Puis on lie l'adpter au Recycler
                    recyclerView!!.adapter = adapterRecycler

                    /** #10.3 On peut alors ajouter le listener  */

                    /** #10.3 On peut alors ajouter le listener  */
                    adapterRecycler!!.setMOnItemClickListener(this@PixabayActivity)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        /** #9.1 On rempli la request avec les données récupérées  */
        requestQueue!!.add(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixabay)

        init()

        btn_search!!.setOnClickListener{
            itemArrayList!!.clear()
            parseJSON()
            listSearched.add(et_search!!.text.toString().uppercase())
        }

        btn_history!!.setOnClickListener{
            lv_history!!.isVisible = lv_history!!.isVisible != true

        }

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                pbar_progress!!.progress = adapterRecycler!!.itemPos
            }
        })
    }

    override fun onResume() {
        super.onResume()
        var i: Intent = intent
        et_search!!.setText(i.getStringExtra(EXTRA_SEARCH))

    }

    override fun onItemClick(position: Int) {
        Log.i("TAG", "onItemClick: ")
        val inte: Intent = object :
            Intent(
                this@PixabayActivity,
                DetailActivity::class.java){}

        var clickedItem: ModelItem = itemArrayList!!.get(position)
        inte.putExtra(EXTRA_URL, clickedItem.imageUrl)
        inte.putExtra(EXTRA_CREATOR, clickedItem.author)
        inte.putExtra(EXTRA_LIKES, clickedItem.likes.toString())
        inte.putExtra(EXTRA_VIEWS, clickedItem.views.toString())
        inte.putExtra(EXTRA_DOWNLOADS, clickedItem.downloads.toString())
        inte.putExtra(EXTRA_TAGS, clickedItem.tags)

        startActivity(inte)

    }
}
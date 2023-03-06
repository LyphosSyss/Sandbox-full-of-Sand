package com.example.ultrasandbox.ui.pixabayApp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.ultrasandbox.R
import com.example.ultrasandbox.ui.pixabayApp.Constantes.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    /** variables declaration **/
    //layout items
    var fab_notifs: FloatingActionButton?= null
    var imageView: ImageView?= null
    var tvCreator: TextView?= null
    var tvLikes: TextView?= null
    var tvViews: TextView?= null
    var tvTags: TextView?= null
    var tvDown: TextView?= null
    var llayout: LinearLayout?= null
    var llayoutH: LinearLayout?= null

    /** Linking code to layouts UI **/
    fun initUI(){
        //linking our variables to the items present in the layout
        imageView = findViewById(R.id.iV_picture)
        tvCreator = findViewById(R.id.tV_authorName)
        tvLikes = findViewById(R.id.tV_likes)
        llayout = findViewById(R.id.lL_CardInfos)
        fab_notifs = findViewById(R.id.fAB_notif)

        //create some items to be put in the layout
        tvViews = object : androidx.appcompat.widget.AppCompatTextView(this@DetailActivity){}
        tvDown = object : androidx.appcompat.widget.AppCompatTextView(this@DetailActivity){}


        //add items to the view
        llayout!!.addView(tvViews)
        llayout!!.addView(tvDown)

        //adjusting some display options
        tvViews!!.gravity = Gravity.CENTER
        tvDown!!.gravity = Gravity.CENTER

    }

    @SuppressLint("ResourceAsColor")
    fun initUITags(strings: List<String>){
        llayoutH = object : LinearLayout(this@DetailActivity){}
        llayout!!.addView(llayoutH)
        llayoutH!!.orientation = LinearLayout.HORIZONTAL
        llayoutH!!.gravity = Gravity.CENTER

        for (string in strings){ //for each item found in the array we :
            val btnTag = object : androidx.appcompat.widget.AppCompatButton(this@DetailActivity){}//1 : create a Button object : it will be our TAGs containers
            llayoutH!!.addView(btnTag) //2 : add it in our layout
            btnTag.gravity = Gravity.CENTER //3: set it into the center
            btnTag.text = string //4: set our item text value
            //btnTags!!.isClickable = false

            btnTag.setOnClickListener(){
                val intentS: Intent = object : Intent(this@DetailActivity, PixabayActivity::class.java){}
                intentS.putExtra(EXTRA_SEARCH, btnTag.text.toString())
                startActivity(intentS)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //data recorvery
        var i: Intent = intent
        var imgUrl: String? = i.getStringExtra(EXTRA_URL)
        var author: String?= i.getStringExtra(EXTRA_CREATOR)
        var likes: String?= i.getStringExtra(EXTRA_LIKES)
        var downloads: String?= i.getStringExtra(EXTRA_DOWNLOADS)
        var views: String? = i.getStringExtra(EXTRA_VIEWS)
        var tagsFromJson: String? = i.getStringExtra(EXTRA_TAGS)

        //tagsSplitting
        var tags = tagsFromJson!!.split(", ")

        //graph compos
        initUI()
        initUITags(tags)

        tvCreator!!.text = "By $author"
        tvLikes!!.text = "$likes personnes ont liké le post 🥰"
        tvViews!!.text = "Il y a eu $views personnes qui ont vu cette image 👀"
        tvDown!!.text = "Cette image a été téléchargée $downloads fois 💚"

        //notifications
        //--⬇-- Notification on an event --⬇--
        fab_notifs!!.setOnClickListener{
            showNotification("OnEvent Notification", "You got notified by clicking on $author's picture")
        }
        //--⬇-- Notification as a reminder --⬇--


        //manages images display / load errors
        val options = RequestOptions()
            .centerCrop()
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher_round)
        Glide.with(imageView!!)
            .load(imgUrl)
            .apply(options)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView!!)


    }

    private companion object{
        //notification identity in case there's more than one notification
        private const val CHANNEL_ID = "channel01"
    }

    private fun createNotifChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "My notification"
            val desc = "My notification channel desc"

            val importance =  NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            notificationChannel.description = desc
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, content: String){
        createNotifChannel()

        //let's set the identification of the notification
        val date = Date()
        val  notifID = SimpleDateFormat("ddHHmmss", Locale.FRANCE).format(date).toInt()
        //creation of a notif builder
        val notifBuilder = NotificationCompat.Builder(this, "$CHANNEL_ID")
        notifBuilder.setSmallIcon(R.drawable.ic_notification_add) //-> Sets the notif's icon
        notifBuilder.setContentTitle("$title") //-> Sets the notif's title
        notifBuilder.setContentText("$content") //-> Sets the notif's content
        notifBuilder.priority = NotificationCompat.PRIORITY_DEFAULT //-> Sets the notif's priority

        val notifManagerCompat = NotificationManagerCompat.from(this)
        notifManagerCompat.notify(notifID, notifBuilder.build())
    }
}
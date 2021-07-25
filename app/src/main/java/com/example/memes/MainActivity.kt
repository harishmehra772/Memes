package com.example.memes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    var currentImageUrl:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
    }

    private fun loadMeme()
    {


        val url = "https://meme-api.herokuapp.com/gimme"


        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
               currentImageUrl =response.getString("url")
                val memeImage=findViewById<ImageView>(R.id.memeImage)
                Glide.with(this).load(currentImageUrl).listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                }).into(memeImage)
            },
            {
//                progress.visibility=View.GONE
             Toast.makeText(this,"Error Occured",Toast.LENGTH_LONG).show()
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun shareMeme(view: View) {
        val memeImage=findViewById<ImageView>(R.id.memeImage)
        val image:Bitmap?=getImageFromView(memeImage)
     val intent= Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_STREAM,getImageUri(this,image!!))
        startActivity(Intent.createChooser(intent, "Share this meme via..."))
    }
    fun nextMeme(view: View) {
      loadMeme()
    }

    private fun getImageFromView(view:ImageView):Bitmap?
    {
        val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getImageUri(inContext:Context,inImage:Bitmap): Uri?
    {
        val bytes=ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes)
        val path= MediaStore.Images.Media.insertImage(inContext.contentResolver,inImage,"title",null)
        return Uri.parse(path)
    }
}
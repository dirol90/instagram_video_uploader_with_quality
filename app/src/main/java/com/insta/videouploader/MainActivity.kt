package com.insta.videouploader

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Base64
import android.util.Log
import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.arthenica.mobileffmpeg.FFmpeg
import com.insta.videouploader.util.RealPathUtil
import com.arthenica.mobileffmpeg.Config.enableLogCallback
import com.arthenica.mobileffmpeg.Config.enableStatisticsCallback
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.insta.videouploader.web_view.WebAppInterface
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    var mySwipeRefreshLayout: SwipeRefreshLayout? = null

    companion object {
        @kotlin.jvm.JvmField
        var capturedImageFilePath: Uri? = null
        @kotlin.jvm.JvmField
        var capturedPhotoFilePath: Uri? = null
        var isLoadJson = false

        //MFkQJ ABLKx VhasA _1-msl

        var fun1 = "javascript:(" +
                "function() {" +
                "var parent = document.getElementsByClassName('MFkQJ ABLKx VhasA _1-msl')[0];" +
                "parent.innerHTML = '';" +
                "})()"

//        var fun2 = "javascript:(" +
//                "function() {" +
//                "var parent = document.getElementsByClassName('APQi1')[0];" +
//                "parent.innerHTML = '';" +
//                "})()"

        var fun3 = fun(s: String): String {
            return "javascript:(" +
                    "function() {" +
                    "var parent = document.getElementsByClassName('mTGkH')[0];" +
                    "if (!parent.innerHTML.includes('Android.runGalery();')){" +
                    "var btn = parent.cloneNode(true);" +
                    "btn.addEventListener('click', function(){" +
                    "runGalery();" +
                    "});" +
                    "parent.innerHTML = '';" +
                    "var parentparent = parent.parentElement;" +
                    "parentparent.appendChild(btn);" +
                    "parentparent.removeChild(parent);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + s + "');" +
                    "btn.appendChild(script);" +
                    "}})()"
        }

        var fun4 = fun(s: String): String {
            return "javascript:(" +
                    "function() {" +
                    "var parent = document.getElementsByClassName('q02Nz _0TPg')[0];" +
                    "if (!parent.innerHTML.includes('Android.openCamera();')){" +
                    "var btn = parent.cloneNode(true);" +
                    "btn.addEventListener('click', function(){" +
                    "openCamera();" +
                    "});" +
                    "parent.innerHTML = '';" +
                    "var parentparent = parent.parentElement;" +
                    "parentparent.appendChild(btn);" +
                    "parentparent.removeChild(parent);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + s + "');" +
                    "btn.appendChild(script);" +
                    "}})()"
        }

        var fun5 = fun(s: String): String {
            return "javascript:(" +
                    "function() {" +
                    "var btnSample = document.getElementsByClassName('MEAGs')[0].getElementsByClassName('dCJp8 afkep')[0];" +
                    "var arr1 = document.getElementsByClassName('Ppjfr UE9AK  wdOqh');" +
                    "Array.prototype.forEach.call(arr1, function(item1) {" +
                    "var btn = btnSample.cloneNode(true); " +
                    "if (!item1.innerHTML.includes('dCJp8 afkep')) {var parentparent = item1; " +
                    "btn.innerHTML = '';" +
                    "btn.addEventListener('click', function(){" +
                    "var p = btn.parentElement.parentElement;" +
                    "var pImageVideo = p.getElementsByClassName('_97aPb');" +
                    "var index = 0;" +
                    "do {" +
                    "if (!pImageVideo[index].hasChildNodes()){index = index +1; pImageVideo = pImageVideo.parentElement;} else {" +
                    "pImageVideo = pImageVideo[index].childNodes;" +
                    "if (pImageVideo[index].hasChildNodes()){index = 0;}}" +
                    "} while (pImageVideo[index].hasChildNodes() || pImageVideo[0].src === undefined);" +
                    "downloadResource(pImageVideo[0].src);" +
                    "});" +
                    "var div = document.createElement('div');" +
                    "var styles = {" +
                    "display: 'inline-table'," +
                    "width: '100px'" +
                    "};" +
                    "for (let style in styles) {" +
                    " div.style[style] = styles[style];" +
                    "}" +
                    "var btnStyles= {" +
                    "float: 'left'," +
                    "'background-image': 'url(https://cdn2.iconfinder.com/data/icons/user-interface-180/128/User-Interface-16-128.png)'," +
                    "'background-size': 'cover'" +
                    "};" +
                    " for (let style in btnStyles) {" +
                    " btn.style[style] = btnStyles[style];" +
                    "}" +
                    "parentparent.appendChild(btn);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + s + "');" +
                    "btn.appendChild(script);" +
                    "}})" +
                    "}" +
                    ")();"
        }
    }

    var webView: WebView? = null
    var stopBtn: Button? = null
    val REQUEST_EXTERNAL_STORAGE = 1
    var PERMISSIONS_ALL = arrayOf(
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE, CAMERA
    )


    var logTv: TextView? = null
    var isFree: Boolean = false
    var lastDay: Long = 0
    var uploadsCounter: Int = 0


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        isFree = intent.getBooleanExtra("free", false)
        lastDay = InfoPage.sharedPref!!.getLong("day", -1)
        uploadsCounter = InfoPage.sharedPref!!.getInt("day_val", -1)

        if (isFree
            && lastDay > System.currentTimeMillis() - 24 * 60 * 60 * 1000
            && uploadsCounter > 0) {
            block_iv.visibility = View.VISIBLE
            block_iv.setOnClickListener {

            }
            block_text.visibility = View.VISIBLE
            block_subscribe_btn.visibility = View.VISIBLE
            block_subscribe_btn.setOnClickListener {
                val intentSubscribe = Intent(this, InfoPage::class.java)
                this.startActivity(intentSubscribe)
            }
        } else if (
            isFree
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Please note!")
                .setMessage("You have only one free video upload every 24 hours, please buy subscription to use application without limits !")
                .setCancelable(false)
                .setNegativeButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        val startCounter = InfoPage.sharedPref!!.getInt("startCounter", -1)
        InfoPage.sharedPref!!.edit().putInt("startCounter", startCounter+1).apply()

        val isOpendDialogAlready = InfoPage.sharedPref!!.getBoolean("isOpendDialogAlready", false)

        if(startCounter % 3 == 0 && !isOpendDialogAlready){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Please rate this app!")
                .setMessage("If you like this app please rate it in Play Market!")
                .setCancelable(true)
                .setNegativeButton("OK") { dialog, _ ->
                    val url =
                        "https://play.google.com/store/apps/details?id=com.insta.videouploader"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                    InfoPage.sharedPref!!.edit().putBoolean("isOpendDialogAlready", true).apply()
                    dialog.dismiss()}
            val alert = builder.create()
            alert.show()
        }

        logTv = log_tv
        webView = main_wv
        stopBtn = stop_btn
        loading_ll.visibility = View.GONE

        var permission =
            ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        permission =
            ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        permission = ActivityCompat.checkSelfPermission(this, CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }


        try {
            val settings = webView!!.settings
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(false)
            settings.builtInZoomControls = true
            settings.javaScriptEnabled = true
            settings.setAppCacheEnabled(true)
            settings.allowFileAccess = true
            settings.setAppCachePath("${applicationContext.filesDir.path}/$packageName/cache")
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.setGeolocationEnabled(true)
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.saveFormData = false
            settings.savePassword = false
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            settings.pluginState = WebSettings.PluginState.ON
            settings.setGeolocationEnabled(true)
        } catch (e: Exception) {
//            println(e.message)
        }

        webView!!.setInitialScale(0)
        webView!!.isVerticalScrollBarEnabled = false

        webView!!.webViewClient = object : WebViewClient() {

        }

        webView!!.addJavascriptInterface(WebAppInterface(this), "Android")

        webView!!.webViewClient = MyWebViewClient()
        webView!!.webChromeClient = MyWebChromeClient(this)

        WebView.setWebContentsDebuggingEnabled(true)

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true)

        webView!!.loadUrl("https://www.instagram.com/")

        webView!!.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            webView!!.evaluateJavascript(
                "(function(){return window.document.body.outerHTML})();"
            ) {
                isLoadJson = false
                setJsFunctionsToWebView(this, webView!!)
            }
        }


        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mySwipeRefreshLayout!!.setOnRefreshListener {
            isLoadJson = false
            webView!!.reload()
            mySwipeRefreshLayout
            mySwipeRefreshLayout!!.isRefreshing = false
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            try {
                val s = clipboard.primaryClip.getItemAt(clipboard.primaryClip.itemCount - 1)
                    .text.toString()
                if (URLUtil.isValidUrl(s) && (s.contains(".mp4") || s.contains(".mov") || s.contains(
                        ".jpg"
                    ) || s.contains(".png") || s.contains(".jpeg") || s.contains(".gif"))
                ) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Download file dialog...")
                    builder.setMessage("Do you like download this image or video?")

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        Thread(Runnable {
                            val url = URL(s)
                            val c = url.openConnection() as HttpURLConnection
                            c.requestMethod = "GET"
                            c.connect()

                            val apkStorage = File(
                                Environment.getExternalStorageDirectory().absolutePath + "/InstaVideoUploader/"
                            )

                            if (!apkStorage.exists()) {
                                apkStorage.mkdir()
                            }

                            val fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(s)
                            val downloadFileName = URLUtil.guessFileName(s, null, fileExtenstion)


                            val outputFile = File(apkStorage, downloadFileName)
                            if (!outputFile.exists()) {
                                outputFile.createNewFile()
                            }

                            val fos = FileOutputStream(outputFile)

                            val inpStr = c.inputStream

                            val buffer = ByteArray(1024)
                            var len1: Int

                            var isNeedExit = false
                            do {
                                len1 = inpStr.read(buffer)
                                if (len1 != -1) {
                                    fos.write(buffer, 0, len1)
                                } else {
                                    isNeedExit = true
                                }
                            } while (!isNeedExit)

                            fos.close()
                            inpStr.close()
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Image downloaded successfully, please find it in file manager in InstaVideoUploader directory",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }).start()

                        dialog.dismiss()
                    }

                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }


    override fun onResume() {
        super.onResume()
        if (isFree
            && lastDay > System.currentTimeMillis() - 24 * 60 * 60 * 1000
            && uploadsCounter > 0) {
            block_iv.visibility = View.VISIBLE
            block_iv.setOnClickListener {

            }
            block_text.visibility = View.VISIBLE
            block_subscribe_btn.visibility = View.VISIBLE
            block_subscribe_btn.setOnClickListener {
                val intentSubscribe = Intent(this, InfoPage::class.java)
                this.startActivity(intentSubscribe)
            }
        }
    }

    fun setJsFunctionsToWebView(context: Context, view: WebView) {
        var input = context.assets.open("js/script.js")
        var buffer = input.readBytes()
        input.read(buffer)
        input.close()
        var encoded = Base64.encodeToString(buffer, 2)

        var input1 = context.assets.open("js/script2.js")
        var buffer1 = input1.readBytes()
        input1.read(buffer1)
        input1.close()
        var encoded2 = Base64.encodeToString(buffer1, 2)

        var input2 = context.assets.open("js/script3.js")
        var buffer2 = input2.readBytes()
        input2.read(buffer2)
        input2.close()
        var encoded3 = Base64.encodeToString(buffer2, 2)

        view.loadUrl(fun1)

//        view.loadUrl(fun2)

        view.loadUrl(
            fun3(encoded)
        )

        view.loadUrl(
            fun5(encoded2)
        )

        view.loadUrl(
            fun4(encoded3)
        )


    }

    private class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            setJsFunctionsToWebView(view!!.context, view!!)
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            view.loadUrl(request.url!!.toString())
            setJsFunctionsToWebView(view.context, view)
            return true
        }

        fun setJsFunctionsToWebView(context: Context, view: WebView) {
            var input = context.assets.open("js/script.js")
            var buffer = input.readBytes()
            input.read(buffer)
            input.close()
            var encoded = Base64.encodeToString(buffer, 2)

            var input1 = context.assets.open("js/script2.js")
            var buffer1 = input1.readBytes()
            input1.read(buffer1)
            input1.close()
            var encoded2 = Base64.encodeToString(buffer1, 2)

            var input2 = context.assets.open("js/script3.js")
            var buffer2 = input2.readBytes()
            input2.read(buffer2)
            input2.close()
            var encoded3 = Base64.encodeToString(buffer2, 2)

            view.loadUrl(fun1)

//            view.loadUrl(fun2)

            view.loadUrl(
                fun3(encoded)
            )

            view.loadUrl(
                fun5(encoded2)
            )

            view.loadUrl(
                fun4(encoded3)
            )
        }
    }


    private class MyWebChromeClient(var context: Context) : WebChromeClient() {

        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            Log.d("LogTag", message)
            result.confirm()
            return true
        }

        override fun onProgressChanged(view: WebView, progress: Int) {
            if (!isLoadJson && progress == 100) {
                isLoadJson = true
                setJsFunctionsToWebView(context, view)
            }
        }

        fun setJsFunctionsToWebView(context: Context, view: WebView) {
            var input = context.assets.open("js/script.js")
            var buffer = input.readBytes()
            input.read(buffer)
            input.close()
            var encoded = Base64.encodeToString(buffer, 2)

            var input1 = context.assets.open("js/script2.js")
            var buffer1 = input1.readBytes()
            input1.read(buffer1)
            input1.close()
            var encoded2 = Base64.encodeToString(buffer1, 2)

            var input2 = context.assets.open("js/script3.js")
            var buffer2 = input2.readBytes()
            input2.read(buffer2)
            input2.close()
            var encoded3 = Base64.encodeToString(buffer2, 2)

            view.loadUrl(fun1)

//            view.loadUrl(fun2)

            view.loadUrl(
                fun3(encoded)
            )

            view.loadUrl(
                fun5(encoded2)
            )

            view.loadUrl(
                fun4(encoded3)
            )
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK && webView!!.canGoBack()) {
            webView!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    var isCanceled = false
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == 1234 || requestCode == 100) {
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImage =
                        if (requestCode != 100) data!!.data else if (capturedImageFilePath != null) capturedImageFilePath!! else capturedPhotoFilePath!!

                    if (selectedImage != null && selectedImage.toString() != "") {
                        capturedImageFilePath = null
                        capturedPhotoFilePath = null
                        var path = RealPathUtil.getPath(this, selectedImage)
                        if (path == null) {
                            path = "file://" + selectedImage.toString().replace(
                                "content://",
                                ""
                            ).replaceBeforeLast("%3A%2F", "").replace(
                                "%3A%2F",
                                ""
                            ).replaceAfterLast(" ", "").replaceAfterLast(" ", "").replace(
                                "%2F",
                                "/"
                            )
                        }

                        if (path.contains(".mov", true) || path.contains(
                                ".mp4",
                                true
                            ) || path.contains(".avi", true) || path.contains(".mkv", true)
                        ) {

                            val fileName =
                                path.replaceBeforeLast("/", "").replaceAfterLast(".", "")
                                    .replace(".", "")
                                    .replace("/", "")
                            val appPath =
                                Environment.getExternalStorageDirectory().absolutePath + "/InstaVideoUploader/"
                            val editedFilePath = appPath + "${fileName}_converted_for_insta.mp4"
                            val editedFileNamePathForIntent =
                                "${fileName}_converted_for_insta.mp4"

                            try {

                                var root = File(appPath)
                                if (!root.exists()) {
                                    root.mkdirs()
                                }
                            } catch (e: Exception) {
//                        e.printStackTrace()
                            }

                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(this, selectedImage)
                            val time =
                                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

                            val metaRotation =
                                retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION)
                            val rotation =
                                if (metaRotation == null) 0 else Integer.parseInt(metaRotation)

                            val width = if (rotation == 90 || rotation == 270) Integer.valueOf(
                                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                            ) else Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))

                            val height = if (rotation == 90 || rotation == 270) Integer.valueOf(
                                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                            ) else Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))

                            val timeInMillisec = time.toLongOrNull()

                            val resolUtionComman =
                                if (width < height) "scale=(iw*sar)*max(720/(iw*sar)\\,1560/ih):ih*max(720/(iw*sar)\\,1560/ih),crop=720:1560,fps=fps=30" else "scale=(iw*sar)*max(1560/(iw*sar)\\,720/ih):ih*max(1560/(iw*sar)\\,720/ih),crop=1560:720,fps=fps=30"

                            loading_ll.visibility = View.VISIBLE
                            Thread(Runnable {
                                enableLogCallback { message ->
                                    System.err.println(message.text)
                                }

                                enableStatisticsCallback { newStatistics ->
                                    runOnUiThread {
                                        var perc =
                                            (newStatistics.videoFrameNumber * 100 / ((timeInMillisec!! / 1000) * 30 + 12))
                                        var timeToEnd =
                                            ((((timeInMillisec / 1000) * 30 + 12) - newStatistics.videoFrameNumber) / 4).toInt()
                                        logTv!!.text =
                                            "${if (perc < 100.0) perc else 99.0}% - DONE, ${if (timeToEnd > 0) timeToEnd else 1} seconds to the end of encoding..."

                                        pb_horizontal.progress =
                                            if (perc < 100.0) perc.toInt() else 100

                                    }
                                }

                                stopBtn!!.setOnClickListener {
                                    FFmpeg.cancel()
                                    isCanceled = true
                                }

                                FFmpeg.execute(
                                    "-y -i $path -c:v libx264 -minrate 1000 -b:v 2000k -maxrate 2500k -bufsize 1500k -vf \"$resolUtionComman\" -x264-params \"--zones 0,15,b=0.85/15,30,b=0.80/30,60,b=0.80/60,90,b=0.90/90,150,b=0.95 \" $editedFilePath"
                                )

                                if (!isCanceled) {
                                    runOnUiThread {
                                        logTv!!.text = "VIDEO CONVERTED"
                                        shareInstaIntent(null, editedFileNamePathForIntent)
                                        loading_ll.visibility = View.GONE
                                    }
                                } else {
                                    runOnUiThread {
                                        loading_ll.visibility = View.GONE
                                        logTv!!.text = ""
                                    }
                                }

                                isCanceled = false


                            }).start()
                        } else if (selectedImage.toString().contains(
                                ".png",
                                true
                            ) || selectedImage.toString().contains(
                                ".jpeg",
                                true
                            ) || selectedImage.toString().contains(
                                ".jpg",
                                true
                            ) || selectedImage.toString().contains(".gif", true)
                        ) {
                            shareInstaIntent(selectedImage, "")
                        } else if (RealPathUtil.getPath(this, selectedImage)!!.contains(
                                ".png",
                                true
                            ) || RealPathUtil.getPath(this, selectedImage)!!.contains(
                                ".jpg",
                                true
                            ) || RealPathUtil.getPath(this, selectedImage)!!.contains(".jpeg", true)
                        ) {
                            shareInstaIntent(null, RealPathUtil.getPath(this, selectedImage)!!)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Wrong file selected!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(
                this@MainActivity,
                "There are some problems, please try again or restart this app and official Instagram app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun shareInstaIntent(
        uri: Uri?,
        path: String
    ) {
        try {

            var backgroundAssetUri: Uri

            if (uri == null && !path.contains("content://")) {
                backgroundAssetUri = FileProvider.getUriForFile(
                    this,
                    "com.insta.videouploader.fileprovider",
                    File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        "InstaVideoUploader/" + path
                    )
                )
            } else if (path.contains("content://")) {
                Toast.makeText(
                    this@MainActivity,
                    "Wrong file format, please select another file...",
                    Toast.LENGTH_LONG
                ).show()
                backgroundAssetUri = Uri.parse("")
            } else if (uri.toString().contains("content://")) {
                if (uri != null) {
                    backgroundAssetUri = uri
                } else {
                    throw java.lang.Exception()
                }
            } else {
                backgroundAssetUri = FileProvider.getUriForFile(
                    this,
                    "com.insta.videouploader.fileprovider",
                    File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        "InstaVideoUploader" + uri.toString().replaceBeforeLast("/", "")
                    )
                )
            }

            val attributionLinkUrl = "https://www.com.insta.videouploader.com/p/BhzbIOUBval/"

            val intent = Intent("com.instagram.share.ADD_TO_STORY")

            println("INTENT URI: " + backgroundAssetUri)

            val mStrExtension = MimeTypeMap.getFileExtensionFromUrl(backgroundAssetUri.toString())
            val mStrMimeType =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(mStrExtension)
            if (mStrExtension.equals("", true) || mStrMimeType == null) {
                intent.setDataAndType(backgroundAssetUri, "image/jpeg")
            } else {
                intent.setDataAndType(backgroundAssetUri, mStrMimeType)
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            grantUriPermission(
                "com.insta.android", backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            intent.putExtra("content_url", attributionLinkUrl)

            if (isFree) {
                InfoPage.sharedPref!!.edit().putInt("day_val", 100).apply()
                println(InfoPage.sharedPref!!.getInt("day_val", -1))
                block_iv.visibility = View.VISIBLE
                block_iv.setOnClickListener {

                }
                block_text.visibility = View.VISIBLE
                block_subscribe_btn.visibility = View.VISIBLE
                block_subscribe_btn.setOnClickListener {
                    val intentSubscribe = Intent(this, InfoPage::class.java)
                    this.startActivity(intentSubscribe)
                }
            }

            startActivityForResult(intent, 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@MainActivity,
                "Please install OFFICIAL Instagram app from play market, login to it and restart it fully if you start it at first time",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

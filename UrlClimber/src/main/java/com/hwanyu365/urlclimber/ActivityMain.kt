package com.hwanyu365.urlclimber

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hwanyu365.urlclimber.util.Log

class ActivityMain : AppCompatActivity() {
    companion object {
        private const val TAG = "ActivityMain"
    }

    private val prefKeyStage = "pref_key_stage"
    private val prefKeyUrlPrefix = "pref_key_url_prefix"
    private val prefKeyUrlMiddle = "pref_key_url_middle"
    private val prefKeyUrlPostfix = "pref_key_url_postfix"
    private val prefKeyGestureArea = "pref_gesture_area"

    private enum class Stage {
        READY,
        VIEW
    }

    private var mGestureDetector: GestureDetector? = null
    private val mHitRect = Rect()
    private var mToast: Toast? = null

    private var mContainerReady: View? = null
    private var mContainerView: View? = null
    private var mContainerBottomNavigator: View? = null
    private var mInputUrlPrefix: EditText? = null
    private var mInputUrlMiddle: EditText? = null
    private var mInputUrlPostfix: EditText? = null
    private var mInputGestureArea: EditText? = null
    private var mWebView: WebView? = null
    private var mChkBottomNavigator: CheckBox? = null

    private var mStage: Stage? = null
    private var mUrlPrefix: String? = null
    private var mUrlMiddle = 0
    private var mUrlPostfix: String? = null
    private var mGestureArea: Int? = null;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mContainerReady = findViewById(R.id.container_ready)
        mContainerView = findViewById(R.id.container_view)
        mContainerBottomNavigator = findViewById(R.id.container_bottom_navigator)
        mInputUrlPrefix = findViewById(R.id.input_url_prefix)
        mInputUrlMiddle = findViewById(R.id.input_url_middle)
        mInputUrlPostfix = findViewById(R.id.input_url_postfix)
        mInputGestureArea = findViewById(R.id.input_gesture_area)
        mChkBottomNavigator = findViewById(R.id.chk_bottom_navigator)
        mWebView = findViewById(R.id.wv_url)

        findViewById<View>(R.id.btn_start).setOnClickListener {
            if ((mInputGestureArea!!.text == null) || (Integer.valueOf(mInputGestureArea!!.text.toString()) == 0) || (Integer.valueOf(mInputGestureArea!!.text.toString()) > 50)) {
                Toast.makeText(this, "Please enter valid value (0 < value <=50)", Toast.LENGTH_SHORT).show()
            } else {
                mGestureArea = Integer.valueOf(mInputGestureArea!!.text.toString());
                mUrlPrefix = mInputUrlPrefix?.text.toString()
                mUrlMiddle = Integer.parseInt(mInputUrlMiddle?.text.toString())
                mUrlPostfix = mInputUrlPostfix?.text.toString()

                val pref = PreferenceManager.getDefaultSharedPreferences(this).edit()
                pref.putInt(prefKeyGestureArea, mGestureArea!!)
                pref.apply()

                setStage(Stage.VIEW)
            }
        }

        findViewById<View>(R.id.btn_prev).setOnClickListener {
            onClickPrev()
        }

        findViewById<View>(R.id.btn_next).setOnClickListener {
            onClickNext()
        }

        if (savedInstanceState == null) {
            val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            mStage = Stage.values()[pref.getInt(prefKeyStage, Stage.READY.ordinal)]
            mUrlPrefix = pref.getString(prefKeyUrlPrefix, "")
            mUrlMiddle = pref.getInt(prefKeyUrlMiddle, -1)
            mUrlPostfix = pref.getString(prefKeyUrlPostfix, "")
            mInputGestureArea?.setText(pref.getInt(prefKeyGestureArea, 40).toString())
            setStage(Stage.READY)
        }

        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                mWebView?.getHitRect(mHitRect)
                val areaPercent = mGestureArea!!.div(100f)
                val prevHitY = mHitRect.bottom * areaPercent
                val nextHitY = mHitRect.bottom - prevHitY
                val touchY = e!!.y.toInt()
                Log.d(TAG, "$mHitRect, prevHitY? $prevHitY, nextHitY? $nextHitY, touchY? $touchY")
                if (touchY < prevHitY) {
                    onClickPrev()
                    return true
                } else if (touchY > nextHitY) {
                    onClickNext()
                    return true
                }
                return super.onDoubleTap(e)
            }
        })

        val webSettings: WebSettings? = mWebView?.settings
        webSettings?.javaScriptEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_settings) {
            return true
        } else if(item?.itemId == R.id.action_history) {
            startActivity(Intent(this, ActivityHistory::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        if (mStage == Stage.VIEW) {
            val pref = PreferenceManager.getDefaultSharedPreferences(this).edit()
            pref.putInt(prefKeyStage, mStage!!.ordinal)
            pref.putString(prefKeyUrlPrefix, mUrlPrefix)
            pref.putInt(prefKeyUrlMiddle, mUrlMiddle)
            pref.putString(prefKeyUrlPostfix, mUrlPostfix)
            pref.apply()
        }
    }

    override fun onBackPressed() {
        if (mStage == Stage.VIEW) {
            setStage(Stage.READY)
        } else {
            super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (mStage == Stage.VIEW) {
                hideSystemUI()
            } else {
                showSystemUI()
            }
        }
    }

    private fun onClickPrev() {
        setUrl(mUrlPrefix + --mUrlMiddle + mUrlPostfix)
    }

    private fun onClickNext() {
        setUrl(mUrlPrefix + ++mUrlMiddle + mUrlPostfix)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setStage(stage: Stage) {
        mStage = stage

        if (stage == Stage.READY) {
            showSystemUI()
            mContainerReady?.visibility = View.VISIBLE
            mContainerView?.visibility = View.GONE
            mInputUrlPrefix?.setText(mUrlPrefix)
            if (mUrlMiddle >= 0) {
                mInputUrlMiddle?.setText(mUrlMiddle.toString())
            }
            mInputUrlPostfix?.setText(mUrlPostfix)
            mWebView?.setOnTouchListener(null)
        } else {
            hideSystemUI()
            mContainerReady?.visibility = View.GONE
            mContainerView?.visibility = View.VISIBLE
            mContainerBottomNavigator?.visibility = (if (mChkBottomNavigator!!.isChecked) View.VISIBLE else View.GONE)
            setUrl(mUrlPrefix + mUrlMiddle + mUrlPostfix)
            mWebView?.setOnTouchListener { _, motionEvent -> mGestureDetector!!.onTouchEvent(motionEvent) }
        }
    }

    private fun setUrl(url: String) {
        mWebView?.loadUrl(url)
    }

    private fun hideSystemUI() {
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        supportActionBar?.show()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    @Suppress("unused")
    private fun showToast(msg: String?) {
        mToast?.cancel()
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }
}

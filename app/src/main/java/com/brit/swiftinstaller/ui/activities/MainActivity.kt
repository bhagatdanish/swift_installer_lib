package com.brit.swiftinstaller.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.brit.swiftinstaller.R
import com.brit.swiftinstaller.utils.UpdateChecker
import com.brit.swiftinstaller.utils.getAccentColor
import com.brit.swiftinstaller.utils.getAppsToUpdate
import kotlinx.android.synthetic.main.dialog_about.view.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : ThemeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "overlays_to_update") {
                val apps = getAppsToUpdate(this)
                if (apps.isEmpty()) {
                    install_updates_tile.visibility = View.GONE
                } else {
                    install_updates_tile.visibility = View.VISIBLE
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        101)

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        101)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        install_tile.setOnClickListener {
            startActivity(Intent(this, OverlaysActivity::class.java))
        }

        update_tile_layout.setOnClickListener {
            val intent = Intent(this, OverlaysActivity::class.java)
            intent.putExtra("tab", OverlaysActivity.UPDATE_TAB)
            startActivity(intent)
        }

        accent_tile.setOnClickListener {
            val intent = Intent(this, CustomizeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0]
                                != PackageManager.PERMISSION_GRANTED)) {
                    // Permission denied show error dialog and exit
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar_subtitle_current_accent.setTextColor(getAccentColor(this))
        toolbar_subtitle_current_accent.text = getString(R.string.hex_string,
                String.format("%06x", getAccentColor(this)).substring(2))

        UpdateChecker(this, object : UpdateChecker.Callback() {
            override fun finished(installedCount: Int, updates: ArrayList<String>) {
                active_count.text = String.format("%d", installedCount)
                if (updates.isEmpty()) {
                    update_tile_layout.visibility = View.GONE
                } else {
                    updates_count.text = String.format("%d", updates.size)
                    update_tile_layout.visibility = View.VISIBLE
                }
            }

        }).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_about -> {
                val dialogView = View.inflate(this, R.layout.dialog_about, null)
                val builder = if (AppCompatDelegate.getDefaultNightMode()
                        == AppCompatDelegate.MODE_NIGHT_YES) {
                    AlertDialog.Builder(this, R.style.AppTheme_AlertDialog_Black)
                } else {
                    AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                }
                builder.setView(dialogView)
                val dialog = builder.create()

                dialogView.about_ok_btn.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    fun faq(@Suppress("UNUSED_PARAMETER") item: MenuItem) {
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet_faq, Toast.LENGTH_LONG).show()
        } else {
            val alert = AlertDialog.Builder(this, R.style.dialogNoTitle)
            val wv = WebView(this)
            wv.setBackgroundColor(Color.TRANSPARENT)
            wv.loadUrl("https://goo.gl/KoB1xi")
            wv.webViewClient = WebViewClient()
            alert.setView(wv)
            alert.show()
        }
    }
}

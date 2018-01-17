package com.brit.swiftinstaller.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.brit.swiftinstaller.R
import com.brit.swiftinstaller.utils.getAccentColor
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.overlay_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        installTile.setOnClickListener {
            startActivity(Intent(this, OverlayActivity::class.java))
        }

        accentTile.setOnClickListener {
            startActivity(Intent(this, AccentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        currentAccent.setTextColor(getAccentColor(this))
        currentAccent.text = "#" + String.format("%06x", getAccentColor(this))
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

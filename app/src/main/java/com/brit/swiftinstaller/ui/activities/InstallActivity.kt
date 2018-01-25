package com.brit.swiftinstaller.ui.activities

import android.content.*
import android.os.*
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ProgressBar
import com.brit.swiftinstaller.IInstallerService
import com.brit.swiftinstaller.InstallerService
import com.brit.swiftinstaller.R
import com.brit.swiftinstaller.utils.InstallerHandler
import kotlinx.android.synthetic.main.install_progress_sheet.*


class InstallActivity: AppCompatActivity() {

    private lateinit var mConnection: ServiceConnection
    private lateinit var mService: IInstallerService
    private lateinit var mProgressBar: ProgressBar

    fun installStarted() {

    }

    fun progressUpdate(label: String?, progress: Int, max: Int, uninstall: Boolean) {

       Handler(Looper.getMainLooper()).post( {
           if (mProgressBar.progress < progress) {
               mProgressBar.progress = progress
               mProgressBar.max = max
               main_content.invalidate()
               mProgressBar.postInvalidate()
               installProgressCount.text = progress.toString() + "/" + max
               installProgressPercent.text = ((progress * 100) / max).toString() + "%"
           }
        })
    }

    fun installComplete(uninstall: Boolean) {
        finish()
    }

    fun installFailed(reason: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.install_progress_sheet)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action.equals(InstallerHandler.INSTALL_PROGRESS)) {
                    progressUpdate(intent.getStringExtra("label"), intent.getIntExtra("progress", 0), intent.getIntExtra("max", 0), false)
                } else if (intent!!.action.equals(InstallerHandler.INSTALL_COMPLETE)) {
                    installComplete(intent.getBooleanExtra("uninstall", false))
                }
            }

        }

        val filter = IntentFilter(InstallerHandler.INSTALL_COMPLETE)
        filter.addAction(InstallerHandler.INSTALL_FAILED)
        filter.addAction(InstallerHandler.INSTALL_PROGRESS)
        filter.addAction(InstallerHandler.INSTALL_STARTED)

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

        mProgressBar = installProgressBar

        val serviceIntent = Intent(this, InstallerService::class.java)
        serviceIntent.putExtra(InstallerService.ARG_THEME_PACKAGE, packageName)

        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mService = IInstallerService.Stub.asInterface(service)
                try {
                    mService.setCallback(InstallerHandler(this@InstallActivity))
                    mService.startInstall(intent.getStringArrayListExtra("apps"))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        Handler().postDelayed( {
            startService(serviceIntent)
            bindService(serviceIntent, mConnection, Context.BIND_NOT_FOREGROUND)
        }, 700)
    }
}
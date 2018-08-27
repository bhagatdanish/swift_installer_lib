package com.brit.swiftinstaller.installer.rom

import android.content.Context
import android.content.Intent
import com.brit.swiftinstaller.utils.*
import com.brit.swiftinstaller.utils.OverlayUtils.getOverlayPackageName
import com.topjohnwu.superuser.io.SuFile

class PRomInfo(context: Context) : RomInfo(context) {

    private val systemApp = "/system/app"

    override fun installOverlay(context: Context, targetPackage: String, overlayPath: String) {
        val overlayPackage = getOverlayPackageName(targetPackage)
        if (ShellUtils.isRootAvailable) {
            remountRW("/system")
            ShellUtils.mkdir("$systemApp/$overlayPackage")
            ShellUtils.copyFile(overlayPath, "$systemApp/$overlayPackage/$overlayPackage.apk")
            ShellUtils.setPermissions(644, "$systemApp/$overlayPackage/$overlayPackage.apk")
            remountRO("/system")
        }
    }

    override fun postInstall(uninstall: Boolean, apps: ArrayList<String>, oppositeApps: ArrayList<String>?, intent: Intent?) {

        if (!uninstall && oppositeApps != null && oppositeApps.isNotEmpty()) {
            for (app in oppositeApps) {
                uninstallOverlay(context, app)
            }
        }

        if (intent != null) {
            context.applicationContext.startActivity(intent)
        }
    }

    override fun uninstallOverlay(context: Context, packageName: String) {
        val overlayPackage = getOverlayPackageName(packageName)
        if (ShellUtils.isRootAvailable) {
            remountRW("/system")
            deleteFileRoot("$systemApp/$overlayPackage/")
            remountRO("/system")
        }
    }

    override fun isOverlayInstalled(targetPackage: String): Boolean {
        val overlayPackage = getOverlayPackageName(targetPackage)
        return SuFile("$systemApp/$overlayPackage/$overlayPackage.apk").exists()
    }

    override fun getCustomizeFeatures() : Int {
        return 0
    }

    override fun useHotSwap(): Boolean { return true }
}
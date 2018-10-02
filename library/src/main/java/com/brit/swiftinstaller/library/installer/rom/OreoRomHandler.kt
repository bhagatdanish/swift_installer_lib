/*
 *
 *  * Copyright (C) 2018 Griffin Millender
 *  * Copyright (C) 2018 Per Lycke
 *  * Copyright (C) 2018 Davide Lilli & Nishith Khanna
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.brit.swiftinstaller.library.installer.rom

import android.content.Context
import android.content.Intent
import com.brit.swiftinstaller.library.ui.customize.CustomizeHandler
import com.brit.swiftinstaller.library.ui.customize.CustomizeSelection
import com.brit.swiftinstaller.library.ui.customize.PreviewHandler
import com.brit.swiftinstaller.library.utils.*
import com.brit.swiftinstaller.library.utils.OverlayUtils.getOverlayPackageName

open class OreoRomHandler(context: Context) : RomHandler(context) {

    override fun installOverlay(context: Context, targetPackage: String, overlayPath: String) {
        if (ShellUtils.isRootAvailable) {
            runCommand("cmd overlay disable ${getOverlayPackageName(targetPackage)}", true)
            runCommand("pm install -r $overlayPath", true)
            if (runCommand("cmd overlay list", true).output?.contains(getOverlayPackageName(targetPackage)) == true) {
                context.swift.prefs.edit().putBoolean("hotswap", true).apply()
            }
        }
    }

    override fun getChangelogTag(): String {
        return "oreo"
    }

    override fun getRequiredApps(): Array<String> {
        return arrayOf(
        )
    }

    override fun postInstall(uninstall: Boolean, apps: SynchronizedArrayList<String>,
                             oppositeApps: SynchronizedArrayList<String>, intent: Intent?) {
        if (ShellUtils.isRootAvailable && !uninstall) {
            apps.forEach { app ->
                runCommand("cmd overlay enable " + getOverlayPackageName(app), true)
            }
        }

        if (!uninstall && oppositeApps.isNotEmpty()) {
            oppositeApps.forEach { app ->
                uninstallOverlay(context, app)
            }
        }

        if (intent != null) {
            context.applicationContext.startActivity(intent)
        }
    }

    override fun uninstallOverlay(context: Context, packageName: String) {
        if (ShellUtils.isRootAvailable) {
            runCommand("cmd overlay disable ${getOverlayPackageName(packageName)}", true)
            runCommand("pm uninstall " + getOverlayPackageName(packageName), true)
            if (packageName != "com.android.systemui" && packageName != "android") {
                runCommand("pkill $packageName")
            } else {
                context.swift.prefs.edit().putBoolean("hotswap", true).apply()
            }
        }
    }

    override fun disableOverlay(targetPackage: String) {
        runCommand("cmd overlay disable ${getOverlayPackageName(targetPackage)}", true)
    }

    override fun useHotSwap(): Boolean {
        return true
    }

    override fun neverReboot(): Boolean {
        return true
    }

    override fun createCustomizeHandler(): CustomizeHandler {
        return object : CustomizeHandler(context) {
            override fun createPreviewHandler(context: Context): PreviewHandler {
                return OreoPreviewHandler(context)
            }
        }
    }

    class OreoPreviewHandler(context: Context) : PreviewHandler(context) {
        override fun updateIcons(selection: CustomizeSelection) {
            settingsIcons.forEach { icon ->
                icon.setColorFilter(selection.accentColor)
                val idName = "ic_${context.resources.getResourceEntryName(icon.id)}_aosp"
                val id = context.resources.getIdentifier("com.brit.swiftinstaller:drawable/$idName",
                        null, null)
                if (id > 0) {
                    icon.setImageDrawable(context.getDrawable(id))
                }
            }
            systemUiIcons.forEach { icon ->
                val idName = "ic_${context.resources.getResourceEntryName(icon.id)}_aosp"
                val id = context.resources.getIdentifier("com.brit.swiftinstaller:drawable/$idName",
                        null, null)
                if (id > 0) {
                    icon.setImageDrawable(context.getDrawable(id))
                }
                icon.setColorFilter(selection.accentColor)
            }
        }
    }
}
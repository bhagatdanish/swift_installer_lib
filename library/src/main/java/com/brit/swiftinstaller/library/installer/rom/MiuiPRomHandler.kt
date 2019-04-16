package com.brit.swiftinstaller.library.installer.rom

import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import com.brit.swiftinstaller.library.R
import com.brit.swiftinstaller.library.ui.customize.*
import com.brit.swiftinstaller.library.utils.*
import kotlinx.android.synthetic.main.customize_preview_sysui.view.*

class MiuiPRomHandler(context: Context) : PRomHandler(context) {

    override fun getChangelogTag(): String {
        return "miui-p"
    }

    override fun getDisabledOverlays(): SynchronizedArrayList<String> {
        return synchronizedArrayListOf(
                "com.google.android.marvin.talkback",
                "com.android.settings",
                "com.android.settings.intelligence",
                "com.android.providers.media",
                "com.android.bluetooth",
                "com.android.contacts",
                "com.android.emergency",
                "com.android.deskclock",
                "com.android.phone",
                "com.android.server.telecom"
        )
    }

    override fun getRequiredApps(): Array<String> {
        return arrayOf(
                "android",
                "com.android.systemui",
                "com.google.android.packageinstaller",
                "com.google.android.inputmethod.latin",
                "com.amazon.clouddrive.photos",
                "com.anydo",
                "com.apple.android.music",
                "com.ebay.mobile",
                "com.embermitre.pixolor.app",
                "com.google.android.apps.genie.geniewidget",
                "com.google.android.apps.inbox",
                "com.google.android.gm",
                "com.google.android.talk",
                "com.mxtech.videoplayer.ad",
                "com.mxtech.videoplayer.pro",
                "com.pandora.android",
                "com.simplecity.amp.pro",
                "com.Slack",
                "com.twitter.android",
                "com.google.android.gms",
                "com.google.android.apps.nexuslauncher",
                "com.lastpass.lpandroid",
                "com.weather.Weather"
        )
    }

    override fun getDefaultAccent(): Int {
        return ColorUtils.convertToColorInt("0099ff")
    }


    override fun createCustomizeHandler(): CustomizeHandler {
        return object : CustomizeHandler(context) {

            override fun getDefaultSelection(): CustomizeSelection {
                val selection = super.getDefaultSelection()
                selection["notif_background"] = "dark"
                selection["qs_alpha"] = "0"
                selection["sbar_icons_color"] = "default"
                return selection
            }

            override fun populateCustomizeOptions(categories: CategoryMap) {
                populatePieCustomizeOptions(categories)
                super.populateCustomizeOptions(categories)
            }
            override fun createPreviewHandler(context: Context): PreviewHandler {
                return PiePreviewHandler(context)
            }
        }
    }
    class PiePreviewHandler(context: Context) : PreviewHandler(context) {
        override fun updateView(palette: MaterialPalette, selection: CustomizeSelection) {
            super.updateView(palette, selection)
            val darkNotif = (selection["notif_background"]) == "dark"
            systemUiPreview?.let {
                it.notif_bg_layout.setImageResource(R.drawable.notif_bg_rounded)
                if (darkNotif) {
                    it.notif_bg_layout.drawable.setTint(
                            ColorUtils.handleColor(palette.backgroundColor, 8))
                } else {
                    it.notif_bg_layout.drawable.setTint(
                            context.getColor(R.color.notification_bg_light))

                }
            }
        }
        override fun updateIcons(selection: CustomizeSelection) {
            super.updateIcons(selection)
            settingsIcons.forEach { icon ->
                icon.clearColorFilter()
                val idName = "ic_${context.resources.getResourceEntryName(icon.id)}_p"
                val id = context.resources.getIdentifier(
                        "${context.packageName}:drawable/$idName", null, null)
                if (id > 0) {
                    icon.setImageDrawable(context.getDrawable(id))
                }
            }
            systemUiIcons.forEach { icon ->
                val idName =
                        "ic_${context.resources.getResourceEntryName(icon.id)}_aosp"
                val id = context.resources.getIdentifier(
                        "${context.packageName}:drawable/$idName", null, null)
                if (id > 0) {
                    val layerDrawable = context.getDrawable(id) as LayerDrawable
                    icon.setImageDrawable(layerDrawable)
                    layerDrawable.findDrawableByLayerId(R.id.icon_bg)
                            .setTint(selection.accentColor)
                    layerDrawable.findDrawableByLayerId(
                            R.id.icon_tint).setTint(selection.backgroundColor)
                }
            }
        }


    }
    override fun populatePieCustomizeOptions(categories: CategoryMap) {
        val notifBackgroundOptions = OptionsMap()
        notifBackgroundOptions.add(Option(context.getString(R.string.white), "white"))
        notifBackgroundOptions.add(Option(context.getString(R.string.dark), "dark"))
        categories.add(CustomizeCategory(context.getString(R.string.notification_tweaks),
                "notif_background", "white", notifBackgroundOptions, synchronizedArrayListOf("android")))

        val qsOptions = OptionsMap()
        val trans =
                SliderOption(context.getString(R.string.qs_transparency), "qs_alpha")
        trans.current = 0
        qsOptions.add(trans)
        categories.add(CustomizeCategory(context.getString(R.string.quick_settings_style),
                "qs_alpha", "0", qsOptions,
                synchronizedArrayListOf("android")))

        val sbarIconColorOptions = OptionsMap()
        sbarIconColorOptions.add(Option(context.getString(R.string.sbar_icons_color_default), "default"))
        sbarIconColorOptions["default"]!!.infoDialogTitle = context.getString(R.string.sbar_icons_color_default_dialog_title)
        sbarIconColorOptions["default"]!!.infoDialogText = context.getString(R.string.sbar_icons_color_default_dialog_text)
        sbarIconColorOptions.add(Option(context.getString(R.string.sbar_icons_color_white), "white"))
        sbarIconColorOptions["white"]!!.infoDialogTitle = context.getString(R.string.sbar_icons_color_white_dialog_title)
        sbarIconColorOptions["white"]!!.infoDialogText = context.getString(R.string.sbar_icons_color_white_dialog_text)

        categories.add(CustomizeCategory(context.getString(R.string.sbar_icons_color_category), "sbar_icons_color", "white", sbarIconColorOptions, synchronizedArrayListOf("com.android.systemui")))
    }
}
package com.brit.swiftinstaller.library.ui.changelog

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.brit.swiftinstaller.library.BuildConfig
import com.brit.swiftinstaller.library.installer.rom.RomInfo
import com.michaelflisar.changelog.ChangelogBuilder
import com.michaelflisar.changelog.ChangelogSetup
import com.michaelflisar.changelog.classes.IChangelogFilter
import com.michaelflisar.changelog.classes.IRecyclerViewItem
import com.michaelflisar.changelog.classes.Row
import com.michaelflisar.changelog.internal.ChangelogRecyclerViewAdapter

object ChangelogHandler {

    fun showChangelog(activity: AppCompatActivity, managedShow: Boolean) {
        val setup = ChangelogSetup.get()
        setup.clearTags()
        setup.registerTag(SwiftChangelogTag("oreo", "Oreo"))
        setup.registerTag(SwiftChangelogTag("oos-oreo", "OOS Oreo"))
        setup.registerTag(SwiftChangelogTag("oos-p", "OOS Pie"))
        setup.registerTag(SwiftChangelogTag("p", "Pie"))
        setup.registerTag(SwiftChangelogTag("samsung", "Samsung"))
        setup.registerTag(SwiftChangelogTag("installer", "Installer"))

        val builder = ChangelogBuilder()
                .withUseBulletList(true)
                .withFilter(SwiftChangelogFilter(RomInfo.getRomInfo(activity).getChangelogTag()))

        if (managedShow) {
            builder.withMinVersionToShow(BuildConfig.VERSION_CODE)
        }

        if ((managedShow && shouldShowDialog(activity)) || !managedShow) {
            ChangelogDialog.show(activity, builder)
        }
    }

    private fun shouldShowDialog(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val currentVer = BuildConfig.VERSION_CODE
        val lastShownVer = pref.getInt("changelog_version_code", -1)
        if (lastShownVer == -1) {
            pref.edit().putInt("changelog_version_code", currentVer).apply()
            return false
        }
        if (currentVer > lastShownVer) {
            pref.edit().putInt("changelog_version_code", currentVer).apply()
            return true
        }
        return false
    }

    class SwiftChangelogFilter(val tag: String): IChangelogFilter {

        constructor(p: Parcel): this(p.readString()!!)

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(tag)
        }

        override fun checkFilter(item: IRecyclerViewItem): Boolean {
            if (item.recyclerViewType == ChangelogRecyclerViewAdapter.Type.Row) {
                val tag = (item as Row).tag.xmlTagName
                if (tag != this.tag && tag != "installer") {
                    return false
                }
            }
            return true
        }

        override fun describeContents(): Int {
            return 0
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<SwiftChangelogFilter> {
            override fun createFromParcel(p: Parcel): SwiftChangelogFilter {
                return SwiftChangelogFilter(p)
            }

            override fun newArray(p0: Int): Array<SwiftChangelogFilter> {
                return newArray(p0)
            }
        }
    }
}
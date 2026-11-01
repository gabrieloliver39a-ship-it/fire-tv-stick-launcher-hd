package com.firetvlauncher.hd

import android.content.Context
import android.content.Intent

/** Lê do sistema a lista de aplicativos que o usuário pode abrir. */
object AppRepository {

    /** Pacotes que não fazem sentido como atalho (o próprio launcher da Amazon). */
    private val hiddenPackages = setOf("com.amazon.tv.launcher")

    fun loadLaunchableApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val found = LinkedHashMap<String, AppInfo>()

        // Primeiro apps de TV, depois os que só têm ícone de launcher comum.
        val categories = listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)

        for (category in categories) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(query, 0)) {
                val activity = info.activityInfo ?: continue
                val pkg = activity.packageName
                if (pkg == context.packageName || pkg in hiddenPackages) continue
                if (found.containsKey(pkg)) continue

                found[pkg] = AppInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = pkg,
                    activityName = activity.name,
                    icon = info.loadIcon(pm),
                    isTvApp = category == Intent.CATEGORY_LEANBACK_LAUNCHER
                )
            }
        }

        return found.values.sortedWith(
            compareBy<AppInfo> { !it.isTvApp }.thenBy { it.label.lowercase() }
        )
    }
}

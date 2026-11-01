package com.firetvlauncher.hd

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.format.DateFormat
import android.view.View
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private lateinit var recycler: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: AppAdapter

    private val worker = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var firstLoad = true

    private val reloadRunnable = Runnable { loadApps() }

    /** Atualiza a grade quando um app é instalado, removido ou atualizado. */
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Atualizar um app dispara vários avisos seguidos; junta tudo em uma recarga.
            handler.removeCallbacks(reloadRunnable)
            handler.postDelayed(reloadRunnable, 600)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupClock()

        emptyView = findViewById(R.id.empty_view)
        recycler = findViewById(R.id.app_grid)
        adapter = AppAdapter(onOpen = ::openApp, onInfo = ::openAppInfo)
        recycler.layoutManager = GridLayoutManager(this, computeSpanCount())
        recycler.adapter = adapter

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)

        loadApps()
    }

    override fun onDestroy() {
        handler.removeCallbacks(reloadRunnable)
        unregisterReceiver(packageReceiver)
        worker.shutdownNow()
        super.onDestroy()
    }

    /** Hora vem de HH:mm no layout; a data segue o idioma do aparelho. */
    private fun setupClock() {
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEEdMMMMyyyy")
        findViewById<TextClock>(R.id.date_text).apply {
            format12Hour = pattern
            format24Hour = pattern
        }
    }

    /** Quantas colunas cabem: funciona em 1280x720 e em telas maiores. */
    private fun computeSpanCount(): Int {
        val widthPx = resources.displayMetrics.widthPixels
        val marginPx = resources.getDimension(R.dimen.screen_margin_h) * 2
        val cardPx = resources.getDimension(R.dimen.card_min_width)
        return ((widthPx - marginPx) / cardPx).toInt().coerceIn(3, 8)
    }

    private fun loadApps() {
        worker.execute {
            val apps = AppRepository.loadLaunchableApps(applicationContext)
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                adapter.submit(apps)
                emptyView.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
                if (firstLoad) {
                    firstLoad = false
                    // Foco inicial no primeiro atalho, para o controle já funcionar.
                    recycler.post {
                        recycler.layoutManager?.findViewByPosition(0)?.requestFocus()
                    }
                }
            }
        }
    }

    private fun openApp(app: AppInfo) {
        val category = if (app.isTvApp) Intent.CATEGORY_LEANBACK_LAUNCHER else Intent.CATEGORY_LAUNCHER
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(category)
            component = ComponentName(app.packageName, app.activityName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_open_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppInfo(app: AppInfo) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", app.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_app_info, Toast.LENGTH_SHORT).show()
        }
    }
}

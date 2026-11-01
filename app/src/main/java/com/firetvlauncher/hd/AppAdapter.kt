package com.firetvlauncher.hd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/** Grade de atalhos. Cada cartão é focável: setas movem o foco, OK abre o app. */
class AppAdapter(
    private val onOpen: (AppInfo) -> Unit,
    private val onInfo: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.Holder>() {

    private var items: List<AppInfo> = emptyList()

    fun submit(newItems: List<AppInfo>) {
        items = newItems
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val label: TextView = view.findViewById(R.id.app_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)

        // Destaque ao receber foco: leve aumento + sobe na ordem de desenho.
        view.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.08f else 1f
            v.animate().scaleX(scale).scaleY(scale).setDuration(120).start()
            v.translationZ = if (hasFocus) 12f else 0f
        }
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val app = items[position]
        holder.icon.setImageDrawable(app.icon)
        holder.label.text = app.label
        holder.itemView.setOnClickListener { onOpen(app) }       // botão OK
        holder.itemView.setOnLongClickListener { onInfo(app); true } // segurar OK
    }

    override fun getItemCount(): Int = items.size
}

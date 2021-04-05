package at.uastw.medpr.reboot.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.uastw.medpr.reboot.R

/**
 * RecyclerView adapter for the select device list
 *
 * @property[dataSet] Data model of the list
 * @property[onDeviceListener] Callback listener of the list
 */
class SelectDeviceListAdapter(
    var dataSet: List<Array<String>>,
    private val onDeviceListener: OnDeviceListener
) :
    RecyclerView.Adapter<SelectDeviceListAdapter.ViewHolder>() {

    /**
     * Inner class that holds the item view
     * @param[view] View of the item
     * @property[onDeviceListener] onDeviceListener of the item
     */
    class ViewHolder(view: View, private val onDeviceListener: OnDeviceListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        val nameText: TextView = view.findViewById(R.id.select_devices_device_name)
        val addressText: TextView = view.findViewById(R.id.select_devices_device_address)

        /**
         * Triggers the onItemClicked event for the selected adapter position
         * @param[v] View of the item
         */
        override fun onClick(v: View?) {
            onDeviceListener.onItemClicked(adapterPosition)
        }
    }

    /**
     * Callback function if the RecyclerView needs a new ViewHolder instance
     * @param[parent] ViewGroup that will hold the new view
     * @param[viewType] View type of the new view
     * @return A ViewHolder that holds a View of viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_device_list_items, parent, false)
        return ViewHolder(view, onDeviceListener)
    }

    /**
     * Callback function that displays data at the specified position
     * @param[holder] ViewHolder to be updated
     * @param[position] Position of the item in the data set
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceData = dataSet[position]

        holder.nameText.text = deviceData[0]
        holder.addressText.text = deviceData[1]
    }

    /**
     * Returns the current amount of items in the data set
     * @return Size of current data set
     */
    override fun getItemCount(): Int {
        return dataSet.size
    }

    /**
     * Callback interface for onItemClicked events
     */
    interface OnDeviceListener {
        /**
         * Callback function for onItemClicked events
         * @param[position] Position of the clicked item within the data set
         */
        fun onItemClicked(position: Int)
    }
}
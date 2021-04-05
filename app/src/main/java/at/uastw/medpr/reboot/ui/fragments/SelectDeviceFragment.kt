package at.uastw.medpr.reboot.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.uastw.medpr.reboot.BluetoothWrapper
import at.uastw.medpr.reboot.R
import at.uastw.medpr.reboot.databinding.FragmentSelectDeviceBinding

/**
 * Select Device Fragment
 * Allows the user to select his device from a list of supported ones
 */

class SelectDeviceFragment : Fragment(), SelectDeviceListAdapter.OnDeviceListener, OnConnectedListener {
    private lateinit var adapter: SelectDeviceListAdapter
    private lateinit var binding: FragmentSelectDeviceBinding

    /**
     *  Initializes the view
     *
     *  @param[inflater] Used to inflate views in the fragment
     *  @param[container] If non-null holds the parent view that this fragment attaches to
     *  @param[savedInstanceState] If non-null can be used to reconstruct fragment from previous saved state
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_device, container, false
        )

        binding.selectDeviceList.layoutManager = LinearLayoutManager(this.context)

        adapter = SelectDeviceListAdapter(dataSet = emptyList(), this)

        // Initialize the device list data model
        refreshList()

        binding.selectDeviceList.adapter = adapter

        binding.selectDeviceRefreshBtn.setOnClickListener { refreshList() }

        return binding.root
    }

    /**
     * Updates the data model of the device list and changes the views accordingly
     */
    private fun refreshList() {
        adapter.dataSet = BluetoothWrapper.getCompatibleDevices(requireContext())
        adapter.notifyDataSetChanged()

        if (adapter.dataSet.isEmpty()) {
            binding.selectDeviceText.text = getText(R.string.select_device_no_device)

            binding.selectDeviceList.visibility = View.GONE
        } else {
            binding.selectDeviceText.text = getText(R.string.select_device_available_devices)

            binding.selectDeviceList.visibility = View.VISIBLE
        }
    }

    /**
     * Callback for click event on the list
     * Tries to establish a connection with the selected device
     *
     * @param[position] Index of the list item the user has selected
     */
    override fun onItemClicked(position: Int) {
        BluetoothWrapper.selectDevice(position)
        BluetoothWrapper.addOnConnectedListener(this)
        BluetoothWrapper.connect()
    }

    /**
     *  Implementation of the OnConnectedListener interface
     *
     *  Callback function for successful connection attempts
     *  Adds further listeners and sends a ping to the device, to make sure we are talking to a supported one
     *  (App sends ping "H", device should respond with pong "W")
     */
    override fun onConnected() {
        BluetoothWrapper.addListeners(this::onMessageReceived, this::onMessageSent, this::onReceiveError)
        BluetoothWrapper.sendMessage("<H>")
    }

    /**
     *  Callback function for sent message events
     *  Logs sent message to console
     */
    private fun onMessageSent(message: String) {
        Log.i("DEBUG", "Sent: $message")
    }

    /**
     * Callback function for received message events
     * Checks if we are indeed talking to a supported device and if so, saves the device and navigates to
     * Live View Fragment
     *
     * @param[message] Received message
     */
    private fun onMessageReceived(message: String) {
        val data = BluetoothWrapper.parseMessage(message)
        Log.i("DEBUG", data[0])
        if (data[0] == "W") {
            BluetoothWrapper.saveDevice()

            Toast.makeText(
                this.context,
                getString(R.string.device_address_saved),
                Toast.LENGTH_SHORT
            )
                .show()

            findNavController().navigate(SelectDeviceFragmentDirections.actionSelectDeviceFragmentToLiveViewFragment())
        }
    }

    /**
     * Logs any RxErrors to console and displays a toast to inform the user
     *
     * @param[error] Error that resulted in the function being called
     */
    private fun onReceiveError(error: Throwable) {
        Log.e("[BT/RxError]", "" + error.message)
        Toast.makeText(this.context, "ERROR: " + error.message, Toast.LENGTH_LONG).show()
    }

}
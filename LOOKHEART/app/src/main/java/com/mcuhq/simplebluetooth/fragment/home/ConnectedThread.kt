package com.mcuhq.simplebluetooth.fragment.home

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(private val mmSocket: BluetoothSocket, private val mHandler: Handler) :
    Thread() {
    private val mmInStream: InputStream?
    private val mmOutStream: OutputStream?

    init {
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null
        try {
            tmpIn = mmSocket.inputStream
            tmpOut = mmSocket.outputStream
        } catch (e: IOException) {
        }
        mmInStream = tmpIn
        mmOutStream = tmpOut
    }

    override fun run() {
        var buffer = ByteArray(1024) // buffer store for the stream
        var bytes: Int // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream!!.available()
                if (bytes != 0) {
                    if (bytes < 4096) {
                        buffer = ByteArray(4096)
                        SystemClock.sleep(100) //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available() // how many bytes are ready to be read?
                        bytes = mmInStream.read(
                            buffer,
                            0,
                            bytes
                        ) // record how many bytes we actually read
                        //                        mHandler.obtainMessage(HomeFragment.MESSAGE_READ, bytes, -1, buffer)
//                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    } else {
                        Log.d("RECV", "Recv Bytes over 4096($bytes")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
        //        mHandler.obtainMessage(OverviewActivity.DISCONNECTED)
//                .sendToTarget(); // Send the obtained bytes to the UI activity
    }

    /* Call this from the main activity to send data to the remote device */
    fun write(input: String) {
        val bytes = input.toByteArray() //converts entered String into bytes
        try {
            mmOutStream!!.write(bytes)
            mmOutStream.flush()
        } catch (e: IOException) {
            Log.d("Error", "write: " + e.message)
        }
    }

    /* Call this from the main activity to shutdown the connection */
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
        }
    }
}
package com.example.wirelesscashier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
//    var editTextcard: EditText? = null
//    var editTextestablishment: EditText? = null
    var editTextamount: EditText? = null
//    var buttonsend: Button? = null
    var card_id: String? = null
    var establishment = "46"

    companion object {
        val TAG = "Main Activity"
    }

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        editTextamount = findViewById(R.id.editTextamount)
        textEstablishment.append("Establishment ID" + establishment)
        var buttonsend: Button = findViewById(R.id.buttonsend)
        buttonsend.setOnClickListener { consumeService() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }

        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    Log.e(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                })

        Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
    }

    fun consumeService() {
        // ahora ejecutaremos el hilo creado
//        val card_id = editTextcard?.text.toString()
//        val establishment = editTextestablishment?.text.toString()
        val amount = editTextamount?.text.toString()

        val serviceTask = ServiceTask(this, "http://192.168.1.7:8000/transaction", card_id, establishment, amount)
        serviceTask.execute()
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null)
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007A0000002471001"))
        card_id = Utils.toHex(response)
        runOnUiThread { textCard.append("\nUser ID: "
                + card_id)}
        isoDep.close()
    }

}

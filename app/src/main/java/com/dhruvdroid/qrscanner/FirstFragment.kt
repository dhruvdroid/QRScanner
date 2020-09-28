package com.dhruvdroid.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var codeScanner: CodeScanner? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scannerView)
        codeScanner = activity?.let { CodeScanner(it, scannerView) }

        // Parameters (default values)
        codeScanner?.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner?.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner?.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner?.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner?.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner?.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner?.decodeCallback = DecodeCallback {

            activity?.runOnUiThread {
                Toast.makeText(
                    context, it.text,
                    Toast.LENGTH_LONG
                ).show()

//                val sanitizedJson =
//                    JsonParser.parseString(it.text).asString // will help in unescaping
//                val gson = GsonBuilder()
//                    .setLenient()
//                    .create()
//                val qrData = gson.fromJson(it.text, QRWrapperResponse::class.java)

//                val gson = GsonBuilder().registerTypeAdapter(
//                    QRWrapperResponse::class.java,
//                    ModelDeserializer()
//                ).create()
//
//                println(gson.fromJson(it.text, QRWrapperResponse::class.java))
            }
        }

        codeScanner?.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(
                    context, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner?.startPreview()
        }

        view.findViewById<Button>(R.id.start).setOnClickListener {
            if (isPermissionGranted()) {
                // startQrReader()
                scannerView.visibility = View.VISIBLE
                codeScanner?.startPreview()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    1005
                )
            }

        }

        view.findViewById<Button>(R.id.reset).setOnClickListener {
            // stopQrReader()
            scannerView.visibility = View.INVISIBLE
            codeScanner?.releaseResources()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults != null && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    1005 -> {
                        // setUpControls()
                        codeScanner?.startPreview()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Grant permission to proceed ahead", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        // Init and Start with SurfaceView
        // -------------------------------
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }
}
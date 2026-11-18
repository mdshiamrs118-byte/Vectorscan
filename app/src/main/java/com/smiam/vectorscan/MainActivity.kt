package com.smiam.vectorscan

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            setPadding(48, 48, 48, 48)
            textSize = 15f
            text = """
                VectorScan v0.1

                1. Enable this module in Vector (LSPosed).
                2. Add the apps you want to scan to its scope.
                3. Force-stop and reopen the target app.
                4. The dump is written to:
                   /sdcard/Android/data/<target.package>/files/vectorscan_dump.txt
                   (falls back to the app's private files dir)

                Check the Vector log for "VectorScan" lines.
            """.trimIndent()
        }
        setContentView(tv)
    }
}

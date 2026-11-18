package com.smiam.vectorscan

import android.app.Application
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import kotlin.concurrent.thread

class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfigLike.SELF) return
        if (!lpparam.isFirstApplication) return

        val loader = lpparam.classLoader
        val pkg = lpparam.packageName

        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java, "onCreate",
                object : XC_MethodHook() {
                    private var done = false
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (done) return
                        done = true
                        val ctx = param.thisObject as Context
                        // Run off the main thread so the app doesn't ANR.
                        thread(name = "VectorScan") {
                            try {
                                val out = outputFile(ctx, pkg)
                                val count = Scanner.dump(loader, pkg, out)
                                XposedBridge.log("VectorScan: $pkg -> $count classes -> ${out.absolutePath}")
                            } catch (t: Throwable) {
                                XposedBridge.log("VectorScan: scan failed for $pkg: $t")
                            }
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("VectorScan: hook failed for $pkg: $t")
        }
    }

    private fun outputFile(ctx: Context, pkg: String): File {
        val dir = ctx.getExternalFilesDir(null) ?: ctx.filesDir
        dir.mkdirs()
        return File(dir, "vectorscan_dump.txt")
    }
}

object BuildConfigLike {
    const val SELF = "com.smiam.vectorscan"
}

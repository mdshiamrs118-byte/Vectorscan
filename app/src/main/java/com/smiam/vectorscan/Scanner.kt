package com.smiam.vectorscan

import dalvik.system.DexFile
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.Modifier

object Scanner {

    /** Only dump classes under the app's own package prefix. Set false to dump everything. */
    private const val ONLY_APP_PACKAGE = true

    /** Returns the number of classes written. */
    fun dump(loader: ClassLoader, pkg: String, out: File): Int {
        val names = enumerateClassNames(loader)
            .filter { !ONLY_APP_PACKAGE || it.startsWith(pkg) }
            .sorted()

        var written = 0
        out.bufferedWriter().use { w ->
            w.appendLine("# VectorScan dump for $pkg (${names.size} classes)")
            for (name in names) {
                val cls = try {
                    Class.forName(name, false, loader)
                } catch (t: Throwable) {
                    w.appendLine("CLASS $name  <unloadable: ${t.javaClass.simpleName}>")
                    continue
                }
                try {
                    w.appendLine("CLASS ${Modifier.toString(cls.modifiers)} $name" +
                            (cls.superclass?.let { " extends ${it.name}" } ?: ""))
                    for (f in cls.declaredFields) {
                        w.appendLine("  FIELD ${Modifier.toString(f.modifiers)} ${f.type.name} ${f.name}")
                    }
                    for (m in cls.declaredMethods) {
                        val params = m.parameterTypes.joinToString(", ") { it.name }
                        w.appendLine("  METHOD ${Modifier.toString(m.modifiers)} ${m.returnType.name} ${m.name}($params)")
                    }
                    written++
                } catch (t: Throwable) {
                    w.appendLine("  <reflection failed: ${t.javaClass.simpleName}>")
                }
            }
        }
        return written
    }

    /** Walks BaseDexClassLoader -> pathList -> dexElements -> dexFile.entries(). */
    @Suppress("DEPRECATION")
    private fun enumerateClassNames(loader: ClassLoader): List<String> {
        val result = ArrayList<String>()
        try {
            val pathList = XposedHelpers.getObjectField(loader, "pathList")
            val elements = XposedHelpers.getObjectField(pathList, "dexElements") as Array<*>
            for (element in elements) {
                val dexFile = XposedHelpers.getObjectField(element, "dexFile") as? DexFile ?: continue
                val e = dexFile.entries()
                while (e.hasMoreElements()) result.add(e.nextElement())
            }
        } catch (t: Throwable) {
            de.robv.android.xposed.XposedBridge.log("VectorScan: enumerate failed: $t")
        }
        return result
    }
}

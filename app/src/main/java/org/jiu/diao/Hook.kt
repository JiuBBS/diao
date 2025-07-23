package com.example.hodor

import android.app.Application
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field
import kotlin.concurrent.fixedRateTimer

class HodorHook : IXposedHookLoadPackage {
    var HoderText = "\uD83D\uDE02你用个迪奥\uD83D\uDE02"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
//        if (lpparam.packageName == "android" || lpparam.packageName.startsWith("com.android"))
//            return

        XposedBridge.log("Hodor hooked: ${lpparam.packageName}")

        // Hook Application.attach，确保在 Context 可用后执行替换逻辑
        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            android.content.Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val context = param.args[0] as android.content.Context
                    startTextReplaceTimer(context)
                }
            }
        )

        // Hook WebView.loadUrl 插入 JS 替换网页内文字
        XposedHelpers.findAndHookMethod(
            WebView::class.java,
            "loadUrl",
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val url = param.args[0] as String
                    if (!url.startsWith("javascript:")) {
                        val js = """
                            javascript:(function() {
                                function replaceText(node) {
                                    if (node.nodeType === 3) {
                                        node.nodeValue = '$HoderText';
                                    } else if (node.nodeType === 1) {
                                        for (var i = 0; i < node.childNodes.length; i++) {
                                            replaceText(node.childNodes[i]);
                                        }
                                    }
                                }
                                replaceText(document.body);
                                setInterval(() => replaceText(document.body), 5000);
                            })();
                        """.trimIndent()
                        param.args[0] = "$url\n$js"
                    }
                }
            }
        )
    }

    private fun startTextReplaceTimer(context: android.content.Context) {
        val handler = Handler(Looper.getMainLooper())
        fixedRateTimer("HodorTimer", initialDelay = 5000, period = 10000) {
            handler.post {
                try {
                    val topActivity = getTopActivity(context)
                    topActivity?.window?.decorView?.let {
                        replaceTextRecursive(it)
                    }
                } catch (e: Throwable) {
                    XposedBridge.log("Hodor error: ${e.message}")
                }
            }
        }
    }

    private fun getTopActivity(context: android.content.Context): android.app.Activity? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = XposedHelpers.callStaticMethod(activityThreadClass, "currentActivityThread")
            val activities = XposedHelpers.getObjectField(currentActivityThread, "mActivities") as Map<*, *>
            for (activityRecord in activities.values) {
                val paused = XposedHelpers.getBooleanField(activityRecord, "paused")
                if (!paused) {
                    return XposedHelpers.getObjectField(activityRecord, "activity") as android.app.Activity
                }
            }
            null
        } catch (e: Throwable) {
            null
        }
    }

    private fun replaceTextRecursive(view: View) {
        when (view) {
            is TextView -> {
                if (view.text.toString() != HoderText) {
                    view.text = HoderText
                }
            }
            else -> {
                // 尝试 Hook Compose 的 text delegate
                try {
                    val clazz = view.javaClass
                    val fields: Array<Field> = clazz.declaredFields
                    for (field in fields) {
                        if (field.type == String::class.java || field.type.name.contains("AnnotatedString")) {
                            field.isAccessible = true
                            val value = field.get(view)
                            if (value is CharSequence && value != HoderText) {
                                field.set(view, HoderText)
                            }
                        }
                    }
                } catch (_: Throwable) {}
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                replaceTextRecursive(view.getChildAt(i))
            }
        }
    }
}

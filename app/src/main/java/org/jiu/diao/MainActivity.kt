package org.jiu.diao

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jiu.diao.ui.theme.你用个迪奥Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import org.jiu.diao.ui.theme.MainBackground
import org.jiu.diao.ui.theme.MainColor
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var senrenBankaPackageName by rememberSaveable { mutableStateOf<String?>(null) }
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current // 监听生命周期事件
            val versionName = try { // 版本号
                packageManager.getPackageInfo(packageName, 0).versionName
            } catch (e: Exception) {
                "\uD83D\uDE0B！勇敢迪奥！\uD83D\uDE0B"
            }
            var showDialog by remember { mutableStateOf(true) } // 控制是否显示弹窗，每次打开app都显示

            senrenBankaPackageName = FindSenrenBanka(context)

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        senrenBankaPackageName = FindSenrenBanka(context)// 每次从后台切回来时更新包名状态
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                // 退出时移除观察者
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // 初次进入时也刷新一次
            LaunchedEffect(Unit) {
                senrenBankaPackageName = FindSenrenBanka(context)
            }
            if (showDialog) {
                Dialog(
                    title = "应用信息",
                    text =  "版本：$versionName\n" +
                            "\n" +
                            "软件介绍：本软件是一款全应用适用の娱乐性模块，不过在此建议第一次使用模块或内嵌用户谨慎使用，因为或许可能会在未来给您带来不必要的麻烦。\n" +
                            "\n" +
                            "交流群：『653540436』\n",
                    onDismiss = {
                        showDialog = false
                        Toast.makeText(
                            context,
                            "\uD83D\uDE2D无视了迪奥！\uD83D\uDE2D",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onConfirm = {
                        showDialog = false
                        Toast.makeText(
                            context,
                            "\uD83D\uDE0B就要用迪奥！\uD83D\uDE0B",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onCancel = {
                        showDialog = false
                        Toast.makeText(
                            context,
                            "\uD83D\uDE02用了个迪奥！\uD83D\uDE02",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            你用个迪奥Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        containerColor = Color.Transparent, // 避免 Scaffold 覆盖背景色
                        content = { padding ->
                            Column(modifier = Modifier.padding(padding)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                        .shadow(2.dp, shape = RoundedCornerShape(12.dp))
                                        .background(
                                            MainBackground,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (senrenBankaPackageName != null) Color(
                                                    0xFF33BBA7
                                                ) else Color(0xFFFD0001)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (senrenBankaPackageName != null) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            Modifier.fillMaxSize(0.9f),
                                            tint = Color(0xFFE6EAE6)
                                        )
                                    }
                                    Text(
                                        if (senrenBankaPackageName != null) "您已安装“千恋万花”\n不过没有关系" else "您还没有安装“千恋万花”\n可在下方安装APK",
                                        modifier = Modifier.padding(16.dp),
                                        color = MainColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Mainbotton(name = "软件信息", onClick = {Open_More(context)})
                                    Mainbotton(
                                        name = "模块交流群",
                                        onClick = { Open_ChatGroup(context) })
                                    Mainbotton(
                                        name = if (senrenBankaPackageName != null) "打开千恋万花" else "立马下载千恋万花",
                                        onClick = {
                                            if (senrenBankaPackageName != null) Open_SenrenBanka(context, senrenBankaPackageName) else Down_SenrenBanka(context)
                                        })
                                }
                            }
                        }
                    )
                }
            }
        }
    }


    @Composable
    fun Mainbotton(
        name: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = name,
                modifier.background(Color.Transparent),
                color = MainColor,
                textAlign = TextAlign.Center
            )
        }
    }


    /**
     * 获取所有已安装啊应用程序,寻找有没有包含"千恋万花"名字的app
     */
    fun FindSenrenBanka(context: Context): String? {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val keywords = listOf("千", "恋", "万", "花")

        for (app in apps) {
            val appName = packageManager.getApplicationLabel(app).toString()

            val containsAll = keywords.all { keyword -> appName.contains(keyword) }

            if (containsAll) {
                return app.packageName
            }
        }

        return null
    }

    /**
     * 模块更多资源按钮
     */
    fun Open_More(context: Context) {
        val url = "https://link3.cc/suxiaojiu"
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }

    /**
     * 模块交流群按钮
     */
    fun Open_ChatGroup(context: Context) {
        val url =
            "https://qun.qq.com/universal-share/share?ac=1&authKey=OadqaydPb3hCn%2FBij%2FeRt%2BCw5ejVE%2BK3ook%2BOqf4%2BZqM%2B6CMEROJEQ8e5lF6hrdM&busi_data=eyJncm91cENvZGUiOiI2NTM1NDA0MzYiLCJ0b2tlbiI6IjQ2R1pad2cxT2c2S3NEaEQ3c3h4aVdoUi91WitSNlF2eG9YYUhDdjZaYUliZHNVTk01K1grWVp3UXhYU00vS24iLCJ1aW4iOiIzOTg2NjEyNjg0In0%3D&data=fl0iW6YLBCnNa6sfZYcoTVUV0SMhSCfgrNxAn0WK29a4RWgwgD04oKDl7UHNvDQYIBjo_oPczjULxG4waK0mWQ&svctype=4&tempid=h5_group_info"
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }

    /**
     * 打开千恋万花
     */
    fun Open_SenrenBanka(context: Context, senrenBankaPackageName: String? = FindSenrenBanka(context)) {
        Toast.makeText(context, "正在启动 $senrenBankaPackageName", Toast.LENGTH_SHORT).show()

        if (senrenBankaPackageName.isNullOrEmpty()) {
            Down_SenrenBanka(context)
            return
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(senrenBankaPackageName)

        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Down_SenrenBanka(context)
        }
    }


    /**
     * 下载千恋万花
     */
    fun Down_SenrenBanka(context: Context) {
        val url = "https://m.win860.com/app/19979.html"
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }

    /**
     * 开屏弹窗
     */
    @Composable
    fun Dialog(
        title: String,
        text: String,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("我要用迪奥！")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text("Ciallo ～(∠・ω< )⌒★!")
                }
            }
        )
    }


    // 测试函数
    fun FunTest() {
        println("测试函数,按钮被点击了")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    你用个迪奥Theme {
    }
}
在 Android 项目中接入微信和QQ分享功能，可以使用微信和QQ官方提供的 SDK，分别是 **微信 SDK** 和 **QQ SDK**。这两个 SDK 都提供了 Android 平台的集成方案，允许你实现分享、登录等功能。

下面是如何使用 **Kotlin** 和 **Gradle** 在 Android 项目中集成微信和QQ分享功能的步骤。

### 1. **集成微信分享 SDK（WeChat SDK）**

#### 1.1 在 Gradle 中配置微信 SDK

1. **添加微信 SDK Maven 仓库**  
   微信的 SDK 需要通过 Maven 仓库下载，你可以在 `build.gradle` 中添加微信的 Maven 仓库地址。通常微信的 SDK 会放在腾讯的 Maven 仓库中。

   在 **`build.gradle.kts`** 中添加如下配置：

   ```kotlin
   repositories {
       maven { url = uri("https://maven.weixin.qq.com") }  // 微信 SDK Maven 仓库
   }

   dependencies {
       implementation("com.tencent.mm.opensdk:wechat-sdk-android:6.8.0") // 使用适当的版本
   }
   ```

   > 注意：请确认版本号是最新的，版本号可能会随时间更新。

#### 1.2 配置微信 SDK

2. **修改 `AndroidManifest.xml` 配置**  
   添加微信分享所需的权限以及相应的 `<activity>` 声明：

   ```xml
   <application
       ...>
       <!-- 微信SDK需要在Manifest文件中添加的配置 -->
       <activity
           android:name="com.tencent.mm.opensdk.openapi.WXApiImplV10"
           android:label="@string/app_name"
           android:theme="@android:style/Theme.Translucent.NoTitleBar"
           android:launchMode="singleTask">
           <intent-filter>
               <action android:name="android.intent.action.VIEW" />
               <category android:name="android.intent.category.DEFAULT" />
               <category android:name="android.intent.category.BROWSABLE" />
               <data android:scheme="wx" />
           </intent-filter>
       </activity>
   </application>
   ```

3. **初始化微信 SDK**  
   在你的主 `Activity` 中初始化微信 SDK：

   ```kotlin
   import com.tencent.mm.opensdk.openapi.IWXAPI
   import com.tencent.mm.opensdk.openapi.WXAPIFactory

   class MainActivity : AppCompatActivity() {

       private lateinit var api: IWXAPI

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_main)

           // 初始化微信API
           api = WXAPIFactory.createWXAPI(this, "<YOUR_APP_ID>", true)
           api.registerApp("<YOUR_APP_ID>")
       }

       private fun shareToWeChat() {
           val request = SendMessageToWX.Req()
           request.transaction = System.currentTimeMillis().toString()
           request.message = WXTextObject().apply { 
               text = "分享内容"
           }

           api.sendReq(request)
       }
   }
   ```

   替换 `<YOUR_APP_ID>` 为你在微信开放平台注册的 `App ID`。

#### 1.3 分享到微信

你可以通过 `SendMessageToWX` 来实现分享内容：

```kotlin
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage

private fun shareToWeChat() {
    val textObj = WXTextObject()
    textObj.text = "我正在使用微信分享功能！"

    val msg = WXMediaMessage()
    msg.mediaObject = textObj
    msg.description = "分享文本"

    val req = SendMessageToWX.Req()
    req.transaction = System.currentTimeMillis().toString()
    req.message = msg
    req.scene = SendMessageToWX.Req.WXSceneSession // WXSceneSession: 发送到微信好友, WXSceneTimeline: 发送到朋友圈

    api.sendReq(req)
}
```

### 2. **集成 QQ 分享 SDK（QQ SDK）**

#### 2.1 在 Gradle 中配置 QQ SDK

1. **添加 QQ SDK Maven 仓库**  
   QQ SDK 通过腾讯的 Maven 仓库提供，可以在 **`build.gradle.kts`** 中配置：

   ```kotlin
   repositories {
       maven { url = uri("https://maven.tencent.com") }  // 腾讯 QQ SDK Maven 仓库
   }

   dependencies {
       implementation("com.tencent.tauth:qq-sdk-android:3.2.0")  // QQ SDK 版本
   }
   ```

   > 请注意，确保使用的是最新的 SDK 版本。

#### 2.2 配置 QQ SDK

2. **修改 `AndroidManifest.xml` 配置**  
   配置 QQ 分享所需要的权限和活动：

   ```xml
   <application
       ... >
       <!-- QQ SDK需要在Manifest文件中添加的配置 -->
       <activity
           android:name="com.tencent.tauth.AuthActivity"
           android:theme="@android:style/Theme.Translucent.NoTitleBar" />

       <activity
           android:name="com.tencent.connect.common.AssistActivity"
           android:theme="@android:style/Theme.Translucent.NoTitleBar" />
   </application>
   ```

#### 2.3 初始化 QQ SDK

3. **初始化 QQ SDK**

   在你的 `MainActivity` 或其他活动中初始化 QQ SDK：

   ```kotlin
   import com.tencent.tauth.Tencent
   import com.tencent.connect.common.Constants

   class MainActivity : AppCompatActivity() {

       private lateinit var tencent: Tencent

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_main)

           tencent = Tencent.createInstance("<YOUR_APP_ID>", this)
       }
   }
   ```

   替换 `<YOUR_APP_ID>` 为你在 QQ 开放平台注册的 `App ID`。

#### 2.4 分享到 QQ

你可以通过 `QQShare` 来实现分享到 QQ 或 QQ 空间：

```kotlin
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QQShare.QQShareErrorType

private fun shareToQQ() {
    val params = Bundle()
    params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享标题")
    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享内容")
    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://www.qq.com")
    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "https://www.example.com/sample.jpg")
    params.putInt(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称")

    tencent.shareToQQ(this, params, object : IUiListener {
        override fun onComplete(response: Any) {
            // 分享成功
        }

        override fun onError(e: UiError) {
            // 分享失败
        }

        override fun onCancel() {
            // 分享取消
        }
    })
}
```

### 3. **AndroidManifest.xml 配置**

确保你在 `AndroidManifest.xml` 中配置了微信和 QQ 的相关权限和活动，特别是如果你需要进行登录授权等操作。

```xml
<application
    android:name=".MyApplication"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    android:theme="@style/Theme.AppCompat.DayNight">

    <!-- 微信相关配置 -->
    <activity
        android:name="com.tencent.mm.opensdk.openapi.WXApiImplV10"
        android:label="@string/app_name"
        android:theme="@android:style/Theme.Translucent.NoTitleBar" />

    <!-- QQ相关配置 -->
    <activity
        android:name="com.tencent.tauth.AuthActivity"
        android:theme="@android:style/Theme.Translucent.NoTitleBar" />

</application>
```

### 4. **总结**

- **微信分享**：使用 `com.tencent.mm.opensdk` 库来实现微信分享功能。
- **QQ分享**：使用 `com.tencent.connect.share` 库来实现 QQ 分享功能。
- 在 `build.gradle.kts` 中分别配置微信和QQ的 Maven 仓库地址和依赖。
- 在 `AndroidManifest.xml` 中添加必要的权限和声明活动。
- 使用相应的 API 调用方法来实现分享功能。

确保你已经在微信和 QQ 开放平台注册并获取了正确的 `App ID`。通过这些步骤，你就可以将微信和 QQ 的分享功能集成到你的 Kotlin 项目中了。如果有任何问题或需要进一步的帮助，请随时告诉我！
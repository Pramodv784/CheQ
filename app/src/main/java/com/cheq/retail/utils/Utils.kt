package com.cheq.retail.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.text.style.StyleSpan
import android.util.*
import android.util.Base64
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.MasterKeys
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.fragment.ProductFragment
import com.cheq.retail.ui.main.model.SelectOfferResponseItem
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.moengage.core.analytics.MoEAnalyticsHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.lang.Float
import java.lang.reflect.Type
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URISyntaxException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharArray
import kotlin.Double
import kotlin.Exception
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.RuntimeException
import kotlin.String
import kotlin.Throws
import kotlin.Unit
import kotlin.also
import kotlin.apply
import kotlin.arrayOf
import kotlin.intArrayOf
import kotlin.let
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.plus
import kotlin.toString


class Utils(private var context: Context) {


    companion object {
        private const val TAG = "Utils"
        var dialog: Dialog? = null
        var rewardRate: ((Double) -> Unit)? = null
       // var itemList: ((Array<SelectOfferResponseItem>?) -> Unit)? = null
        var itemList = ArrayList<SelectOfferResponseItem>()
        fun showToast(context: Context?, message: String?) {
            try {
                val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun convertDecimalFormate(number: Double): String {
            val formatter: NumberFormat = DecimalFormat("#0.00")
            return formatter.format(number)
        }

        fun getUserClickBehaviour(isClickable: Boolean, context: AppCompatActivity) {
            if (isClickable) {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                context.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }


        fun getDateLocalFormatInMMYY(ServerDate: String): String {
            // 2018-12-24T15:48:15.707+05:30
            return if (ServerDate !== "") {
                val originalFormat: DateFormat = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",

                    Locale.getDefault()
                ) //These format come to server
                originalFormat.timeZone = TimeZone.getDefault()
                val targetFormat: DateFormat = SimpleDateFormat(
                    "MM/yy", Locale.getDefault()
                ) //change to new format here  //dd-MM-yyyy HH:mm:ss
                var date: Date? = null
                var formattedDate = ""
                try {
                    date = originalFormat.parse(ServerDate)
                    formattedDate = targetFormat.format(date) //result
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                formattedDate
            } else {
                "null"
            }
        }


        fun getDateToMilliseconds(time: String): Long {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                val mDate = sdf.parse(time.substring(0, 19))
                return mDate.time
            } catch (exception: Exception) {
                0
            }

//            return try {
//                val formatter: DateTimeFormatter =
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
//                val localDate: LocalDateTime = LocalDateTime.parse(time.substring(0, 19), formatter)
//
//                localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
//            } catch (e: Exception) {
//                0
//            }
        }


        fun isPanNoCodeValid(panCode: String): Boolean {
            val regExp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"
            var isvalid = false
            if (panCode.length > 0) {
                isvalid = panCode.matches(regExp.toRegex())
            }
            return isvalid
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun setBackGroundGradient(
            startColor: String, middleColor: String, endColor: String, gradient: View
        ) {
            val gradientDrawableBack = GradientDrawable(
                GradientDrawable.Orientation.TL_BR, intArrayOf(
                    Color.parseColor(startColor),
                    Color.parseColor(middleColor),
                    Color.parseColor(endColor),
                )
            )
            gradientDrawableBack.setGradientCenter(gradient.width.toFloat(), 0f)
            gradientDrawableBack.cornerRadius = 18f
            gradientDrawableBack.gradientType = GradientDrawable.LINEAR_GRADIENT
            gradient.setBackground(gradientDrawableBack);
        }

        fun getFormattedDecimal(value: Double): String {
            val formattedString = DecimalFormat("##,##,##,###.##").format(value).toString()
            val splittedString = formattedString.split(".")

            if (splittedString.size > 1 && splittedString[1].take(1) == "0") {
                return splittedString[0]
            }

            return formattedString
        }

        fun makeFirstCapital(string: String): String {
            return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
        }

        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.getInetAddresses())
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress()) {
                            val sAddr: String = addr.getHostAddress()
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                        0, delim
                                    ).uppercase(
                                        Locale.getDefault()
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (ignored: java.lang.Exception) {
            } // for now eat exceptions
            return ""
        }

        fun setStatusBarColor(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(R.color.white)
            }
        }


        fun setTransparentStatusBar(activity: Activity) {
            activity.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }

        fun hashCal(str: String): String {
            val hashseq = str.toByteArray()
            val hexString = StringBuilder()
            try {
                val algorithm = MessageDigest.getInstance("SHA-512")
                algorithm.reset()
                algorithm.update(hashseq)
                val messageDigest = algorithm.digest()
                for (aMessageDigest in messageDigest) {
                    val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                    if (hex.length == 1) {
                        hexString.append("0")
                    }
                    hexString.append(hex)
                }
            } catch (ignored: NoSuchAlgorithmException) {
            }
            return hexString.toString()
        }
        fun sha256(base: String): String? {
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(base.toByteArray(charset("UTF-8")))
                val hexString = java.lang.StringBuilder()
                for (i in hash.indices) {
                    val hex = Integer.toHexString(0xff and hash[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                hexString.toString()
            } catch (ex: java.lang.Exception) {
                throw RuntimeException(ex)
            }
        }
       fun randomHash( length :Int): String{
           var ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
           val random: Random = SecureRandom()
           val result = CharArray(length)
           for (i in result.indices) {
               // picks a random index out of character set > random character
               val randomCharIndex = random.nextInt(ALPHABET.length)
               result[i] = ALPHABET.get(randomCharIndex)
           }


           return String(result)

       }
        fun randomString(maxLength: Int): String {
            val generator = Random()
            val randomStringBuilder = StringBuilder()
            val randomLength = generator.nextInt(maxLength)
            var tempChar: Char
            for (i in 0 until randomLength) {
                tempChar = (generator.nextInt(96) + 32).toChar()
                randomStringBuilder.append(tempChar)
            }
            return randomStringBuilder.toString()
        }

        fun showCustomDialog(context: Context?, resId: Int): Dialog {
            val dialog = Dialog(context!!)
            Objects.requireNonNull(dialog.window)!!
                .setBackgroundDrawableResource(android.R.color.transparent)
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(resId)
            val windo = dialog.window
            windo!!.setDimAmount(0.7f)
            val wlp = windo.attributes
            wlp.gravity = Gravity.CENTER
            windo.setBackgroundDrawableResource(android.R.color.transparent)
            windo.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            windo.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            windo.attributes = wlp
            return dialog
        }

        fun showCustomDialogBottum(context: Context?, resId: Int): Dialog {
            val dialog = Dialog(context!!)
            try {
                Objects.requireNonNull(dialog.window)
                    ?.setBackgroundDrawableResource(android.R.color.transparent)
            } catch (_: Exception) {

            }

            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(resId)

            dialog.setCancelable(false)


            val windo = dialog.window
            windo!!.setDimAmount(0.5f)
            val wlp = windo.attributes
            wlp.gravity = Gravity.BOTTOM
            try {
                windo.setBackgroundDrawableResource(android.R.color.transparent)
            } catch (_: Exception) {
            }
            windo.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            windo.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            windo.attributes = wlp
            return dialog
        }
         fun setupFullHeightBottomSheet(bottomSheet: View, context: Context) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = (context.resources.displayMetrics.heightPixels * 1.0).roundToInt()
            bottomSheet.layoutParams = layoutParams
        }
        fun showCustomDialogBottum2(context: Context?, resId: Int): BottomSheetDialog {
            val dialog = BottomSheetDialog(context!!)
            Objects.requireNonNull(dialog.window)
                ?.setBackgroundDrawableResource(android.R.color.transparent)

            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(resId)
            dialog.getWindow()?.setFlags(
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            );


            val windo = dialog.window
            windo!!.setDimAmount(0.5f)
            val wlp = windo.attributes
            wlp.gravity = Gravity.BOTTOM
            windo.setBackgroundDrawableResource(android.R.color.transparent)
            windo.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            windo.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            windo.attributes = wlp
            dialog.behavior.isDraggable = false
            return dialog
        }

        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(
                        context.contentResolver, Settings.Secure.LOCATION_MODE
                    )
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                }
                locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                )
                !TextUtils.isEmpty(locationProviders)
            }
        }

        fun isValidEmail(email: String?): Boolean {

            if (email!!.length > 5 && email.substring(email.length - 4, email.length) == ".web") {
                return false
            }

            val pattern: Pattern
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            val matcher: Matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isValidPasswordLength(number: String): Boolean {
            val isLengthValid: Boolean
            isLengthValid = if (number.length <= 5) {
                false
            } else {
                true
            }
            return isLengthValid
        }

        fun getImageUri(context: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }


        fun getRealPathFromURI(context: Context, uri: Uri?): String {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            return picturePath
        }

        //    public static void shareString(String subject, String message, Context mContext) {
        //        try {
        //            Intent sendIntent = new Intent();
        //            sendIntent.setAction(Intent.ACTION_SEND);
        //            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        //            sendIntent.setType("text/plain");
        //            mContext.startActivity(sendIntent);
        //        } catch (Exception e) {
        //            showToast(mContext, mContext.getString(R.string.activity_not_found));
        //        }
        //
        //    }
        fun getRealPathFromURIAll(context: Context, contentUri: Uri): String? {
            val cursor = context.contentResolver.query(contentUri, null, null, null, null)
            val idx: Int
            idx =
                if (contentUri.path!!.startsWith("/external/image") || contentUri.path!!.startsWith(
                        "/internal/image"
                    )
                ) {
                    cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
                } else if (contentUri.path!!.startsWith("/external/video") || contentUri.path!!.startsWith(
                        "/internal/video"
                    )
                ) {
                    cursor!!.getColumnIndex(MediaStore.Video.Media.DATA)
                } else if (contentUri.path!!.startsWith("/external/audio") || contentUri.path!!.startsWith(
                        "/internal/audio"
                    )
                ) {
                    cursor!!.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
                } else {
                    return contentUri.path
                }
            return if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(idx)
            } else null
        }

        @SuppressLint("NewApi")
        @Throws(URISyntaxException::class)
        fun getFilePath(context: Context, uri: Uri): String? {
            var uri = uri
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                    context, uri
                )
            ) { //DocumentsContract.isDocumentUri(context.getApplicationContext(), uri))
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(
                        split[1]
                    )
                }
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(
                        uri, projection, selection, selectionArgs, null
                    )
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: Exception) {
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun openKeyPad(context: Context, view: View?) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        fun checkPermissions(activity: Activity?, vararg permission: String?): Boolean {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    for (i in 0 until permission.size) {
                        if (ActivityCompat.checkSelfPermission(
                                activity!!, permission[i]!!
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return false
                        }
                    }
                } else {
                    return true
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                return false
            }
            return true
        }

        fun showProgressDialog(activity: Activity?) {
            try {
                if (dialog != null) {
                    dialog = null
                }
                dialog = showCustomDialog(activity, R.layout.dialog_progress)
                dialog!!.setCanceledOnTouchOutside(false)

                if (!dialog!!.isShowing) {
                    dialog!!.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        fun onRequestPermissionsResult(
            permissions: Array<String?>?, grantResults: IntArray
        ): Boolean {
            try {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return true
        }

        @Throws(IOException::class)
        fun streamToString(`is`: InputStream?): String {
            var str = ""
            if (`is` != null) {
                val sb = StringBuilder()
                var line: String?
                try {
                    val reader = BufferedReader(
                        InputStreamReader(`is`)
                    )
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    reader.close()
                } finally {
                    `is`.close()
                }
                str = sb.toString()
            }
            return str
        }

        fun hideKeyboard(activity: Activity?) {
            val imm = activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun hideSoftKeyboard(activity: Activity?) {
            try {
                if (activity!!.currentFocus != null) {
                    val inputMethodManager = activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE
                    ) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus!!.windowToken, 0
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun hideSoftKeyboard(activity: AppCompatActivity, editText: EditText) {
            try {
                if (activity.currentFocus != null) {
                    val inputMethodManager = activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE
                    ) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        editText.windowToken, 0
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun convertTimeToAmPm(startTime: String): String {

            var newTime = ""

            val sdf = SimpleDateFormat("HH:mm:ss")
            val sdfs = SimpleDateFormat("h:mm a")
            val dt: Date
            try {
                dt = sdf.parse(startTime)
                println("Time Display: " + sdfs.format(dt)) // <-- I got result here
                newTime = sdfs.format(dt).replace(" ","")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return newTime.uppercase()
        }

        fun showKeyboard(activity: AppCompatActivity) {
            try {
                if (activity.currentFocus != null) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(activity.currentFocus, InputMethodManager.SHOW_IMPLICIT)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun showKeyboard(activity: AppCompatActivity, editText: EditText?) {
            try {
                if (activity.currentFocus != null) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun writeData(context: Context, data: String, fileName: String?) {
            var fout: FileOutputStream? = null
            try {
                //getting output stream
                fout = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                //writng data
                fout.write(data.toByteArray())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fout != null) {
                    //closing the output stream
                    try {
                        fout.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun readData(context: Context, fileName: String?): String {
            val sb = StringBuilder()
            try {
                val fis = context.openFileInput(fileName)
                val isr = InputStreamReader(fis)
                val bufferedReader = BufferedReader(isr)
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return sb.toString()
        }

        //    public static void showSelectImageDialog(final AppCompatActivity activity_payment_method) {
        //        try {
        //            android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(activity_payment_method);
        //
        //            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity_payment_method, R.layout.dialog_list_item);
        //            arrayAdapter.add("Camera");
        //            arrayAdapter.add("Gallery");
        //
        //            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        //                @Override
        //                public void onClick(DialogInterface dialog, int which) {
        //                    dialog.dismiss();
        //                }
        //            });
        //
        //            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
        //                @Override
        //                public void onClick(DialogInterface dialog, int which) {
        //                    String strName = arrayAdapter.getItem(which);
        //
        //                    if (strName.equalsIgnoreCase("Camera"))
        //                        Utils.openImageCamera(activity_payment_method);
        //                    else
        //                        Utils.openImageGallery(activity_payment_method);
        //                }
        //            });
        //            builderSingle.show();
        //        } catch (Exception ex) {
        //            ex.printStackTrace();
        //        }
        //    }
        //    public static void openImageCamera(AppCompatActivity activity_payment_method) {
        //        try {
        //            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //            activity_payment_method.startActivityForResult(cameraIntent, ConstantsLib.SELECT_IMAGE_CAMERA);
        //        } catch (ActivityNotFoundException ex) {
        //            ex.printStackTrace();
        //        } catch (Exception ex) {
        //            ex.printStackTrace();
        //        }
        //    }
        //    public static void openImageGallery(AppCompatActivity activity_payment_method) {
        //        try {
        //            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //            activity_payment_method.startActivityForResult(galleryIntent, ConstantsLib.SELECT_IMAGE_GALLERY);
        //        } catch (ActivityNotFoundException ex) {
        //            ex.printStackTrace();
        //        } catch (Exception ex) {
        //            ex.printStackTrace();
        //        }
        //    }
        fun shareFile(context: Context, filePath: String?) {
            try {
                val file = File(filePath)
                if (!file.exists() || !file.canRead()) {
                    showToast(context, "file_not_found_message")
                    return
                }
                val uri = Uri.fromFile(file)
                val intentShareFile = Intent(Intent.ACTION_SEND)
                if (file.exists()) {
                    val subject = ""
                    intentShareFile.type = "application/pdf"
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sent from VestDavit:Android")
                    context.startActivity(Intent.createChooser(intentShareFile, "Share File"))
                }
            } catch (ex: ActivityNotFoundException) {
                showToast(context, "application_not_found")
                ex.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun openDatePicker(context: Context?, view: View?) {
            try {
                val newCalendar = Calendar.getInstance()
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                val datePickerDialog = DatePickerDialog(
                    context!!,
                    { datePicker, year, monthOfYear, dayOfMonth ->
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        if (view is TextView) view.text = dateFormatter.format(newDate.time)
                    },
                    newCalendar[Calendar.YEAR],
                    newCalendar[Calendar.MONTH],
                    newCalendar[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun showHidePassword(context: Context?, editText: EditText) {
            val transformationMethod = editText.transformationMethod
            if (transformationMethod is PasswordTransformationMethod) {
//            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_remove_red_eye1), null);
                editText.transformationMethod = null
            } else {
//            editText.setCompoundDrawablesWithIntrinsicBounds(null, null,  context.getResources().getDrawable(R.drawable.ic_remove_red_eye), null);
                editText.transformationMethod = PasswordTransformationMethod()
            }
        }

        fun getBase64Image(bitmap: Bitmap): String {
            var bas64Image = ""
            bas64Image = try {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            } catch (ex: Exception) {
                ex.printStackTrace()
                return ""
            }
            return bas64Image
        }

        fun getFormattedDate(dateString: String?): String? {
            var formattedDate: String? = null
            try {
                val inputPattern = "yyyyMMdd"
                val outputPattern = "dd MMMM yyyy"
                val inputFormat = SimpleDateFormat(inputPattern)
                val outputFormat = SimpleDateFormat(outputPattern)
                val date = inputFormat.parse(dateString)
                formattedDate = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun getConnectivityStatusString(context: Context): String? {
            var status: String? = null
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    status = "Wifi enabled"
                    return status
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    status = "Mobile data enabled"
                    return status
                }
            } else {
                status = "No internet is available"
                return status
            }
            return status
        }

        fun pickVideoFromGallery(context: AppCompatActivity, requestCode: Int) {
            try {
                val intent = Intent()
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                context.startActivityForResult(
                    Intent.createChooser(intent, "Select Vine"), requestCode
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /*   //    public static void printHashKey(Context context) {
           //        // Add code to print out the key hash
           //        try {
           //            PackageInfo info = context.getPackageManager().getPackageInfo(
           //                    BuildConfig.APPLICATION_ID,
           //                    PackageManager.GET_SIGNATURES);
           //            for (Signature signature : info.signatures) {
           //                MessageDigest md = MessageDigest.getInstance("SHA");
           //                md.update(signature.toByteArray());
           //                LogUtils.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
           //            }
           //        } catch (PackageManager.NameNotFoundException e) {
           //            e.printStackTrace();
           //
           //        } catch (NoSuchAlgorithmException e) {
           //
           //            e.printStackTrace();
           //        }
           //    }*/
        fun getFormattedDate(date: Date): String {
            var returnDate = ""
            try {
                val sdf1 = SimpleDateFormat("yyyy-MM-dd")
                returnDate = sdf1.format(date.time)
                val myTime = currentTime
                //            returnDate = Utils.formattedDateFromString("yyyy-MM-dd HH:mm a", "yyyy-MM-dd HH:mm:ss", s_date, myTime, true);
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return returnDate
        }

        fun isValidUrl(url: String?): Boolean {
            try {
                return Patterns.WEB_URL.matcher(url).matches()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return false
        }

        fun hideProgressDialog() {
            try {
                if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
//        fun saveBitmapOnSdCard(context: Context, bitmap: Bitmap): String {
//            var imagePath = ""
//            try {
//                val root = Environment.getExternalStorageDirectory()
//                    .toString() + "/" + context.resources.getString(R.string.app_name)
//                val myDir = File(root)
//                if (!myDir.exists()) myDir.mkdirs()
//                val fileName = "image_" + System.currentTimeMillis() + ".jpg"
//                val file = File(myDir, fileName)
//                try {
//                    val out = FileOutputStream(file)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//                    out.flush()
//                    out.close()
//                    imagePath = "$root/$fileName"
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//            return imagePath
//        }

        fun saveBitmapOnSdCache(context: Context?, bitmapImage: Bitmap): String {
            var filePath = ""
            try {
                val cw = ContextWrapper(context)
                // path to /data/data/yourapp/app_data/imageDir
                val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
                // Create imageDir
                val mypath = File(directory, "tempImg.jpg")
                filePath = mypath.absolutePath
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(mypath)
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        fos!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return filePath
        }

        /**
         * dialog width 75% of screen width
         *
         * @param context
         * @return width
         */
        fun getDialogWidth(context: Activity): Int {
            val metrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metrics)
            val width = metrics.widthPixels
            return width * 90 / 100
        }

        /**
         * dialog width 60% of screen height
         *
         * @param context
         * @return height
         */
        fun getDialogHeight(context: Activity): Int {
            val metrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metrics)
            val height = metrics.heightPixels
            return height * 60 / 100
        }

        fun toTitleCase(str: String?): String? {
            if (str == null) {
                return null
            }
            var space = true
            val builder = StringBuilder(str)
            val len = builder.length
            for (i in 0 until len) {
                val c = builder[i]
                if (space) {
                    if (!Character.isWhitespace(c)) {
                        // Convert to title case and switch out of whitespace mode.
                        builder.setCharAt(i, Character.toTitleCase(c))
                        space = false
                    }
                } else if (Character.isWhitespace(c)) {
                    space = true
                } else {
                    builder.setCharAt(i, Character.toLowerCase(c))
                }
            }
            return builder.toString()
        }

        fun toGetLast4Digit(str: String?): String? {
            var lastFourDigits = ""

            if (str!!.length > 4) {
                lastFourDigits = str.substring(str.length - 4);
            } else {
                lastFourDigits = str;
            }
            return lastFourDigits
        }


        fun getDateFormat(ServerDate: String): String {
            // 2018-12-24T15:48:15.707+05:30
            return if (ServerDate !== "") {
                val originalFormat: DateFormat = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",

                    Locale.getDefault()
                ) //These format come to server
                originalFormat.timeZone = TimeZone.getDefault()
                val targetFormat: DateFormat = SimpleDateFormat(
                    "EEE, MMM dd", Locale.getDefault()
                ) //change to new format here  //dd-MM-yyyy HH:mm:ss
                var date: Date? = null
                var formattedDate = ""
                try {
                    date = originalFormat.parse(ServerDate)
                    formattedDate = targetFormat.format(date) //result
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                formattedDate
            } else {
                "null"
            }
        }


        fun getDateLocalFormat(ServerDate: String): String {
            // 2018-12-24T15:48:15.707+05:30
            return if (ServerDate !== "") {
                val originalFormat: DateFormat = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",

                    Locale.getDefault()
                ) //These format come to server
                originalFormat.timeZone = TimeZone.getDefault()
                val targetFormat: DateFormat = SimpleDateFormat(
                    "dd/MM/yyyy", Locale.getDefault()
                ) //change to new format here  //dd-MM-yyyy HH:mm:ss
                var date: Date? = null
                var formattedDate = ""
                try {
                    date = originalFormat.parse(ServerDate)
                    formattedDate = targetFormat.format(date) //result
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                formattedDate
            } else {
                "null"
            }
        }


        fun leftTransaction(activity: Activity) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
        }

        fun rightTransaction(activity: Activity) {
            activity.overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formattedTimeFromDate(ServerDate: String): String {
            val odt = OffsetDateTime.parse(ServerDate);
            val dtf = DateTimeFormatter.ofPattern("dd MMM uu", Locale.ENGLISH);
            return dtf.format(odt)
        }


        suspend fun setMoengageuser(context: Context, checkUserId: String) =
            coroutineScope {  // this: CoroutineScope
                launch {
                    //  val idInfo: AdvertisingIdClient.Info

                    try {

                        try {
                            // Method provided by Segment for identification of users
                            MoEAnalyticsHelper.setUniqueId(context, checkUserId)
                            SharePrefs.getInstance(context)?.getString(SharePrefsKeys.FIRST_NAME)
                                ?.let { MoEAnalyticsHelper.setFirstName(context, it) }
                            SharePrefs.getInstance(context)?.getString(SharePrefsKeys.LAST_NAME)
                                ?.let { MoEAnalyticsHelper.setLastName(context, it) }
                            MoEAnalyticsHelper.setUserName(
                                context,
                                SharePrefs.getInstance(context)
                                    ?.getString(SharePrefsKeys.FIRST_NAME) + SharePrefs.getInstance(
                                    context
                                )?.getString(SharePrefsKeys.LAST_NAME)
                            )
                            SharePrefs.getInstance(context)?.getString(SharePrefsKeys.MOBILE_NUMBER)
                                ?.let { MoEAnalyticsHelper.setMobileNumber(context, it) }
                            SharePrefs.getInstance(context)?.getString(SharePrefsKeys.USER_EMAIL)
                                ?.let { MoEAnalyticsHelper.setEmailId(context, it) }

                        } catch (e: Exception) {
                        }
                        //do sth with the id
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: GooglePlayServicesNotAvailableException) {
                        e.printStackTrace()
                    } catch (e: GooglePlayServicesRepairableException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                    }
                }

            }


        fun validateTime(startTime: String?, endTime: String?): String {
            var startT = "N/A"
            var endT = "N/A"
            startT = if (startTime == null || startTime == "null" || startTime == "") {
                "N/A"
            } else {
                val timeArr = startTime.split(":").toTypedArray()
                val h = timeArr[0]
                val m = timeArr[1]
                if (Integer.valueOf(h) > 12) {
                    val h1 = Integer.valueOf(h) - 12
                    "$h1:$m pm"
                } else {
                    "$h:$m am"
                }
            }
            endT = if (endTime == null || endTime == "null" || endTime == "") {
                "N/A"
            } else {
                val timeArr = endTime.split(":").toTypedArray()
                val h = timeArr[0]
                val m = timeArr[1]
                if (Integer.valueOf(h) > 12) {
                    val h1 = Integer.valueOf(h) - 12
                    "$h1:$m pm"
                } else {
                    "$h:$m am"
                }
            }
            return "$startT to $endT"
        }

        fun getDateJson(dateList: List<String?>): String {
            val jsonArray = JSONArray()
            try {
                for (i in dateList.indices) {
                    jsonArray.put(dateList[i])
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return jsonArray.toString()
        }

        fun getDateJson(date: String): String {
            var date = date
            val jsonArray = JSONArray()
            try {
                jsonArray.put(date)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            date = jsonArray.toString()
            date = date.replace("/", "")
            return date
        }

        val currentDate: String
            get() {
                val calendar = Calendar.getInstance()
                val date = calendar.time
                val inFormat = SimpleDateFormat("dd-MM-yyyy")
                return inFormat.format(date)
            }

        fun getCurrentDateByFormat(format: String?): String {
            val calendar = Calendar.getInstance()
            val date = calendar.time
            val inFormat = SimpleDateFormat(format)
            return inFormat.format(date)
        }

        /* public static String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm:ss");
        String timeValue = inFormat.format(date);
        return timeValue;
    }*/
        fun validateIntValue(value: String?): String {
            var returntValue: String? = ""
            if (value == null || value == "" || value == "null") return "0"
            returntValue = value
            return returntValue
        }

        fun validateStringValue(value: String?): String {
            var returntValue: String? = ""
            if (value == null || value == "" || value == "null") return ""
            returntValue = value
            return returntValue
        }

        fun validateValue(value: String?): String {
            return if (value == null || value == "" || value == "null") "N/A" else value
        }

        fun validateValue(value: Int?): String {
            return if (value == null) "N/A" else value.toString() + ""
        }

        fun validateInt(value: Int): Int {
            return value ?: 0
        }

        fun getWeekName(id: String?): String {
            when (id) {
                "1" -> return "Sunday"
                "2" -> return "Monday"
                "3" -> return "Tuesday"
                "4" -> return "Wednesday"
                "5" -> return "Thursday"
                "6" -> return "Friday"
                "7" -> return "Saturday"
            }
            return ""
        }

        fun validateString(value: String?): String {
            val respose = ""
            return if (value == null || value == "null" || value == "") respose else value
        }

        fun isStringsEmpty(str1: String?, str2: String?): Boolean {
            return if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
                true
            } else false
        }

        fun isStringsEmpty(str1: String?): Boolean {
            return if (TextUtils.isEmpty(str1)) {
                true
            } else false
        }

        //UTC conversion
        fun getTime(calendar_time: Calendar, myFormat: String?, convertUtc: Boolean): String {
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            if (convertUtc) {
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
            return sdf.format(calendar_time.time)
        }

        fun getTypedFormattedNumber(mobileNo: String?): String {
            val stringBuilder = StringBuilder(mobileNo)
            try {
                if (stringBuilder.length == 5) {
                    stringBuilder.trimToSize()
                    stringBuilder.insert(4, " ")
                }
                if (stringBuilder.length == 9) {
                    stringBuilder.trimToSize()
                    stringBuilder.insert(8, " ")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return stringBuilder.toString()
        }

        fun fromJson(bookingDate: String?): String? {
            var date = ""
            date = try {
                val jsonArray = JSONArray(bookingDate)
                jsonArray[0] as String
            } catch (e: JSONException) {
                e.printStackTrace()
                ""
            }
            return date
        }

        /* public static void showProgressDialog(Activity activity) {


        try {
            if (progressDialog != null) {
                progressDialog = null;
            }

            progressDialog = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Loading..Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            if (!progressDialog.isShowing()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }, 6000);
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static Dialog customDialog;

    public static void showProgressDialogNew(Activity activity) {
        if (customDialog != null) {
            customDialog = null;
        }
        customDialog = new Dialog(activity, R.style.CustomDialogNew);
        customDialog.setCancelable(false);
        customDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.progress_dialog_new, null);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing()) {
            customDialog.show();
            System.out.println("progress dialog shown " + activity);
        } else {
            customDialog.dismiss();
        }
    }*/
        fun isValidPhoneNumber(str: String?): Boolean {

            val regex = ("(0/91)?[6-9][0-9]{9}")

            // Compile the ReGex
            val p = Pattern.compile(regex)

            // If the string is empty
            // return false
            if (str == null) {
                return false
            }

            // Pattern class contains matcher()
            // method to find the matching
            // between the given string
            // and the regular expression.
            val m = p.matcher(str)

            // Return if the string
            // matched the ReGex
            return m.matches()
        }


        fun getFormattedNumber(mobileNo: String): String {
            val stringBuilder = StringBuilder(mobileNo)
            try {
                for (i in 0 until mobileNo.length) {
                    if (i == 4 || i == 8) {
                        stringBuilder.insert(i, " ")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return stringBuilder.toString()
        }

        fun formateAmount(value: String): String {
            var value = value
            if (value == "") {
                value = "0"
            }
            return String.format("%.02f", Float.valueOf(value))
        }

        fun validateEventDate(eventDate: String?): String {
            var date = "test"
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val format: DateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            val startDate: Date
            try {
                startDate = df.parse(eventDate)
                val newDateString = format.format(startDate)
                println("date >$newDateString")
                date = newDateString
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return date
        }

        fun validateDateWithTime(eventDate: String?): String {
            var date = "test"
            if (eventDate == null || eventDate.equals(
                    "", ignoreCase = true
                ) || eventDate.equals("null", ignoreCase = true)
            ) return ""
            val arr = eventDate.split(" ").toTypedArray()
            date = arr[0]
            return date
        }

        fun validateingString(value: String?): String {
            val respose = ""
            return if (value != null) if (!value.equals(
                    "null", ignoreCase = true
                )
            ) if (!value.equals("", ignoreCase = true)) value else "N/A" else "N/A" else "N/A"

//        if (value == null || value.equals("null") || value.equals(""))
//            return ;
//        else
//            return value;
        }

        @Throws(ParseException::class)
        fun getToadyAndYesterdayFoChat(date: String?): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateTime = simpleDateFormat.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dateTime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            return if (calendar[Calendar.YEAR] == today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR]) {
                "Today"
            } else {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                val date1 = dateFormatter.parse(date)
                // Get time from date
                val timeFormatter1 = SimpleDateFormat("dd MMM yyyy")
                timeFormatter1.format(date1)
            }
        }

        fun getCurrentTimeInMiliSecondUTC(date: String?): Long {
            var time: Long = 0
            try {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                format.timeZone = TimeZone.getTimeZone("GMT")
                val dateTime = format.parse(date)
                time = dateTime.time
                return time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return time
        }

        fun getCurrentTimeInMiliSecond(date: String?): Long {
            var time: Long = 0
            try {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val dateTime = format.parse(date)
                time = dateTime.time
                return time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return time
        }

        val currentTime: String
            get() {
                val sdf = SimpleDateFormat("HH:mm a")
                val date = Date()
                return sdf.format(date)
            }

        fun getDateForChat(dateFormat: String?): String {
            try {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = dateFormatter.parse(dateFormat)
                // Get time from date
                val timeFormatter = SimpleDateFormat("HH:mm")
                return timeFormatter.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        val currentUTC: String
            get() {
                val time = Calendar.getInstance().time
                val outputFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                outputFmt.timeZone = TimeZone.getTimeZone("GMT")
                return outputFmt.format(time)
            }
        val currentUTCTimeStampDate: String
            get() {
                val time = Calendar.getInstance().time
                val outputFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                outputFmt.timeZone = TimeZone.getTimeZone("GMT")
                return outputFmt.format(time)
            }
        val currentTimeStampDate: String
            get() {
                val time = Calendar.getInstance().time
                val outputFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                return outputFmt.format(time)
            }

        fun convertIntoLocalTime(datetime: String?): String {
            var datetimeLocale = ""
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                formatter.timeZone = TimeZone.getTimeZone("GMT")
                val value = formatter.parse(datetime)
                val dateFormatter = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                ) //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                datetimeLocale = dateFormatter.format(value)
                return datetimeLocale
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return datetimeLocale
        }

        fun convertMiliSecondIntoCurrentDateAndTime(miliSecond: String): String {
            var dateAndTime = ""
            try {
                val time = miliSecond.toLong()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                sdf.timeZone = TimeZone.getTimeZone("GMT")
                val resultdate = Date(time)
                var dateFormat = sdf.format(resultdate)
                dateFormat = convertIntoLocalTime(dateFormat)
                dateAndTime = getCurrentTimeInMiliSecond(dateFormat).toString()
                return dateAndTime
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dateAndTime
        }

        private fun getStateAbbervation(id: String): String {
            var abbervation = ""
            when (id) {
                "266" -> abbervation = "NSW"
                "269" -> abbervation = "Qld"
                "270" -> abbervation = "SA"
                "271" -> abbervation = "Tas"
                "273" -> abbervation = "Vic"
                "275" -> abbervation = "WA"
                "4122" -> abbervation = "ACT"
                "4123" -> abbervation = "NT"
            }
            return abbervation
        }

        fun toPx(context: Context, dp: kotlin.Float): kotlin.Float {
            val scale = context.resources.displayMetrics.density
            return dp * scale + 0.5f
        }

        fun getRadian(
            x1: kotlin.Float, y1: kotlin.Float, x2: kotlin.Float, y2: kotlin.Float
        ): Double {
            val width = x2 - x1
            val height = y1 - y2
            return Math.atan((Math.abs(height) / Math.abs(width)).toDouble())
        }

        /**
         * add common params in below method
         *
         * @return
         */
        fun getDeviceID(): String {
            return Settings.Secure.getString(
                MainApplication.getApplicationContext().contentResolver, Settings.Secure.ANDROID_ID
            )
        }

        fun setToast(_mContext: Context?, str: String?) {
            val toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        fun setLightStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = activity.window.decorView.systemUiVisibility // get current flag
                flags =
                    flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
                activity.window.decorView.systemUiVisibility = flags
                activity.window.statusBarColor = Color.WHITE // optional
            }
        }

        fun setColorStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= 21) {
                val window: Window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = activity.getColor(R.color.colorHeaderBg)
            }
        }

        fun setColorStatusBar(color: Int, activity: Activity) {
            if (Build.VERSION.SDK_INT >= 21) {
                val window: Window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = color
            }
        }

        fun clearLightStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = activity.window.decorView.systemUiVisibility // get current flag
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
                activity.window.decorView.systemUiVisibility = flags
                activity.window.statusBarColor =
                    activity.resources.getColor(R.color.colorPrimary) // optional
            }
        }

        fun getToken(mActivity: Context): String? {

            return SharePrefCheqUserId.getInstance(mActivity)
                ?.getString(SharePrefsKeys.ACCESS_TOKEN)
        }

        fun getRefreshToken(mActivity: Context): String? {

            return SharePrefCheqUserId.getInstance(mActivity)
                ?.getString(SharePrefsKeys.REFRESH_TOKEN)
        }

        fun isKeyboard(context: Context): Boolean {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            if (imm.isAcceptingText) {
                print("Software Keyboard was shown")
                return true
            } else {
                return false
                print("Software Keyboard was not shown")
            }
        }


        fun showKeyboard() {
            val inputMethodManager =
                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun createFreshChatUser() {
            SharePrefs.getInstance(MainApplication.getApplicationContext()).apply {
                FreshChatUtil.createFreshChatUser(
                    MainApplication.getApplicationContext(),
                    this?.getString("FIRST_NAME") ?: "",
                    this?.getString("LAST_NAME") ?: "",
                    this?.getString("USER_EMAIL") ?: "",
                    this?.getString("MOBILE_NUMBER") ?: "",
                    this?.getString("CHEQ_USER_ID") ?: ""

                )
            }


        }


        fun hideKeyBoard() {
            val inputMethodManager =
                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }


        private fun setDrawableStatusBar(context: Activity) {
            val window: Window = context.window
            val background = ContextCompat.getDrawable(context, R.drawable.ic_on_boarding_top_bg)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
            window.navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }

        fun decodeString(encoded: String): String? {
            val dataDec = Base64.decode(encoded, Base64.DEFAULT)
            var decodedString = ""
            try {
                decodedString = String(dataDec)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } finally {
                return decodedString
            }
        }

        fun setupFullHeight(bottomSheet: View, context: Context) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height =
                (context.resources.displayMetrics.heightPixels * 0.90).roundToInt()
            bottomSheet.layoutParams = layoutParams
        }

        fun isValidData(data: String?): Boolean {
            return !data.isNullOrEmpty()
        }

        fun roundupAmount(
            amount: Double,
            defaultPercentage: Double,
            tvPercentage: AppCompatTextView?,
            localBankId: String?,
            playerArray: List<SelectOfferResponseItem>?
        ): Int {
            var calculatedPoint = 0
            if (!playerArray.isNullOrEmpty()) {
                for (item in playerArray.indices) {
                    if (localBankId == playerArray[item].id) {
                        calculatedPoint = calculateRewardsPoint(
                            amount,
                            playerArray[item].rewardRate ?: 1.0,
                            tvPercentage
                        )
                        break
                    } else if (playerArray[item].id == "9999") {
                        calculatedPoint = calculateRewardsPoint(
                            amount,
                            playerArray[item].rewardRate ?: 1.0,
                            tvPercentage
                        )
                    }
                }
            } else {
                return calculateRewardsPoint(amount, defaultPercentage, tvPercentage)
            }

            return calculatedPoint

        }

        fun calculateRewardsPoint(
            amount: Double,
            percentage: Double,
            tvPercentage: AppCompatTextView?
        ): Int {
            val percentageAMount = ((percentage * (amount)) / 100)
            tvPercentage?.text = "$percentage%"
            return floor(percentageAMount).toInt()
        }
        fun calculateRewardsPoint(
            amount: Double,
            percentage: Double,
        ): Int {
            val percentageAMount = ((percentage * (amount)) / 100)
            return floor(percentageAMount).toInt()
        }
  /*      fun getRewardRate(
            localBankId: String?,
            playerArray: Array<SelectOfferResponseItem>?
        ): Double {
            var rewardRate = 1.0
            if (!playerArray.isNullOrEmpty()) {
                for (item in playerArray.indices) {
                    if (localBankId == playerArray[item].id) {
                        rewardRate = playerArray[item].rewardRate ?: 1.0
                        break
                    } else if (playerArray[item].id == "9999") {
                        rewardRate = playerArray[item].rewardRate ?: 1.0

                    }
                }
            } else {
                return 1.0
            }

            return rewardRate

        }*/
        fun getRewardRate(
            localBankId: String?,
            playerArray: List<SelectOfferResponseItem>?
        ): Double {
            var rewardRate = 1.0
            if (!playerArray.isNullOrEmpty()) {
                for (item in playerArray.indices) {
                    if (localBankId == playerArray[item].id) {
                        rewardRate = playerArray[item].rewardRate ?: 1.0
                        break
                    } else if (playerArray[item].id == "9999") {
                        rewardRate = playerArray[item].rewardRate ?: 1.0

                    }
                }
            } else {
                return 1.0
            }

            return rewardRate

        }
        fun spellOut(number: String): String {
            val words = arrayOf(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
            )
            return number.map { words[it.toString().toInt()] }.joinToString(" ")

        }

        fun setBoldText(boldView: AppCompatTextView, points: Int) {
            val ss = SpannableString(boldView.text)
            val boldSpan = StyleSpan(Typeface.BOLD)
            ss.setSpan(
                boldSpan,
                24,
                24 + points.toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            boldView.text = ss
        }

        fun startAnimation(duration: Long, viewToAnimate: View) {
            val animation: Animation =
                AnimationUtils.loadAnimation(TedPermissionProvider.context, R.anim.slide_up)
            viewToAnimate.startAnimation(animation)
            animation.duration = duration
        }

        fun dpToFloat(dp: Int): kotlin.Float {
            val scale = Resources.getSystem().displayMetrics.density
            return (dp * scale)
        }

        fun initFirebase(context: Context) {
            val mFirebaseDatabase: DatabaseReference?
            val mFirebaseInstance: FirebaseDatabase?
            var playerArray:List<SelectOfferResponseItem>
            mFirebaseInstance = FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
            mFirebaseDatabase = mFirebaseInstance.getReference("instrument_master")
            mFirebaseDatabase.keepSynced(true)
            val rateListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value!=null) {
                        val collectionType: Type =
                            object : TypeToken<List<SelectOfferResponseItem?>?>() {}.type
                        playerArray = Gson().fromJson(snapshot.value.toString(), collectionType) as List<SelectOfferResponseItem>
                        itemList.clear()
                        itemList.addAll(playerArray)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ProductFragment.TAG, "Failed to read id", error.toException())
                }

            }
            mFirebaseDatabase.addValueEventListener(rateListener)
        }

        fun getClipboardText(text: String) {
            val clipboardManager: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.text = text
        }

        fun getMasterKeyAlias(): String {
            return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)//it should be replaced with Builder once stable 1.1.0 is released
        }

        //To check if fragment is attached to activity in order to void IllegalStateException
        fun FragmentActivity.activityAttached(fragment: Fragment?): Boolean {
            return !isFinishing && fragment?.isVisible == true
        }

        fun getCreditHealthScore(score:Int):Pair<String,Int>{
            var creditScoreColor = ContextCompat.getColor(context,R.color.colorPrimary)
            var creditScoreText = ""
            when (score) {
                in 0..620 -> {
                    creditScoreColor = ContextCompat.getColor(context,R.color.amount_red)
                    creditScoreText = context.getString(R.string.poor)
                }
                in 620..699 -> {
                    creditScoreColor = ContextCompat.getColor(context,R.color.golden_color)
                    creditScoreText =  context.getString(R.string.fair)
                }
                in 700..749 -> {
                    creditScoreColor = ContextCompat.getColor(context,R.color.colorPrimary)
                    creditScoreText =  context.getString(R.string.good)
                }
                in 750..799 -> {
                    creditScoreColor = ContextCompat.getColor(context,R.color.colorPrimary)
                    creditScoreText =  context.getString(R.string.very_good)
                }
                in 800..900 -> {
                    creditScoreColor = ContextCompat.getColor(context,R.color.colorPrimary)
                    creditScoreText =  context.getString(R.string.excellent)
                }
            }
            return Pair(creditScoreText,creditScoreColor)
        }
    }

    }



package com.tencentcs.iotvideodemo.kt.utils

import android.graphics.Color
import android.text.TextUtils
import androidx.annotation.ColorInt
import java.text.NumberFormat
import java.util.*

object StringUtils {

    val randomStr: String
        get() = getRandomStr(16)

    fun isBlank(str: String?): Boolean {
        return str?.trim { it <= ' ' }?.isEmpty() ?: true
    }

    /**
     * 去掉价格结尾的0
     */
    fun getDisplayPrice(str: String?): String {
        if (str == null) {
            return ""
        }
        if (!str.contains(".")) {
            return str
        }
        val sb = StringBuffer(str.length)
        val arr = str.toCharArray()
        sb.append(str)
        for (x in (arr.size - 1 downTo 0)) {
            val c = arr[x]
            if (c == '0') {
                sb.deleteCharAt(x)
            } else {
                break
            }
        }
        if (sb.endsWith(".")) {
            return sb.substring(0, sb.length - 1)
        }
        return sb.toString()
    }

    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.toString().trim { it <= ' ' }.isEmpty()
    }

    fun length(str: CharSequence?): Int {
        return str?.length ?: 0
    }

    fun equalsIgnoreCase(str: String?, str2: String?): Boolean {
        return str?.equals(str2!!, ignoreCase = true) ?: (str2 == null)
    }

    fun getRandomStr(length: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number = random.nextInt(str.length)
            sb.append(str[number])
        }
        return sb.toString()
    }

    /**
     * 100,000   加逗号的格式化
     *
     * @param num
     * @return
     */
    fun numberFormat(num: Int): String {
        val cFormat = NumberFormat.getCurrencyInstance()
        cFormat.maximumFractionDigits = 0
        return cFormat.format(num.toLong()).substring(1)
    }

    fun numberFormatF2(num: Double): String {
        val cFormat = NumberFormat.getCurrencyInstance()
        cFormat.maximumFractionDigits = 2
        return cFormat.format(num).substring(1)
    }

    fun parseInt(s: String?): Int {
        return parseInt(s, 0)
    }

    fun parseInt(s: String?, def: Int): Int {
        return try {
            Integer.parseInt(s!!)
        } catch (e: Exception) {
            e.printStackTrace()
            def
        }
    }

    /**
     * @param msg         输入的待处理的字符串
     * @param lengthLimit 要求转换的字符串的最大长度
     * @return 如果输入的字符串的长度大于长度的上限，则省略中间字符并用“……”代替
     */
    fun getSubStr(msg: String, lengthLimit: Int): String {
        if (isBlank(msg)) {
            return ""
        }
        if (msg.length <= lengthLimit) {
            return msg
        }
        val length = Math.floor((lengthLimit / 2).toDouble()).toInt() - 1
        val preStr = msg.substring(0, length)
        val lastStr = msg.substring(msg.length - length)
        val builder = StringBuilder()
        builder.append(preStr).append("……").append(lastStr)
        return builder.toString()
    }

    fun getSubStrEnd(msg: String, lengthLimit: Int): String {
        if (isBlank(msg)) {
            return ""
        }
        if (msg.length <= lengthLimit) {
            return msg
        }
        val preStr = msg.substring(0, lengthLimit)
        val builder = StringBuilder()
        builder.append(preStr).append("……")
        return builder.toString()
    }

    fun getString(str: String?): String {
        return str ?: ""
    }

    fun getString(str: String?, defStr: String): String {
        return if (str.isNullOrBlank()) defStr else str!!
    }

    fun equalsExcludeNull(a: CharSequence, b: CharSequence): Boolean {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return false
        }
        val a1 = a.toString().trim { it <= ' ' }
        val b1 = b.toString().trim { it <= ' ' }
        return if (StringUtils.isBlank(a1) || StringUtils.isBlank(b1)) {
            false
        } else TextUtils.equals(a1, b1)
    }

    fun differsExcludeNull(a: CharSequence, b: CharSequence): Boolean {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return false
        }
        val a1 = a.toString().trim { it <= ' ' }
        val b1 = b.toString().trim { it <= ' ' }
        return if (StringUtils.isBlank(a1) || StringUtils.isBlank(b1)) {
            false
        } else !TextUtils.equals(a1, b1)
    }

    fun contains(orignS: String, contained: CharSequence): Boolean {
        return if (isBlank(orignS) || isEmpty(contained)) {
            false
        } else orignS.contains(contained)
    }

    fun endWiths(orignS: String, suffix: String): Boolean {
        return if (isBlank(orignS) || isBlank(suffix)) {
            false
        } else orignS.endsWith(suffix)
    }

    fun parseFloat(value: String): Float {
        if (isBlank(value)) {
            return 0f
        }
        return try {
            java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            0f
        }

    }

    fun parseFloat(value: String, defValue: Float): Float {
        if (isBlank(value)) {
            return defValue
        }
        return try {
            java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            defValue
        }

    }

    fun remedyUrlHttp(url: String): String {
        if (isBlank(url)) {
            return ""
        }
        if (url.toLowerCase().contains("www")) {
            if (url.toLowerCase().startsWith("https://")) {
                return url
            }
            return if (!url.toLowerCase().startsWith("http://")) {
                TextUtils.concat("http://", url).toString()
            } else url
        }
        return url
    }

    /**
     * 获取文件名后缀
     *
     * @param url
     * @return
     */
    fun getUrlSuffix(url: String): String? {
        if (TextUtils.isEmpty(url)) {
            return url
        }
        val split = url.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (split.isNotEmpty()) {
            split[split.size - 1]
        } else null
    }


    fun replaceApplyUrl(str: String, split: String, oldSt: String, newStr: String): String {
        val index = str.lastIndexOf(split)
        if (index == -1) {
            return str
        }
        val start = str.subSequence(0, index).toString()
        val end = str.subSequence(index, str.length).toString()
        return start + end.replace(oldSt, newStr)
    }
}

fun parseColor(color: String): Int {
    return parseColor(color, Color.BLACK)
}

fun parseColor(colorS: String, @ColorInt defColor: Int): Int {
    val newColor = colorS.compatColorText()
    return try {
        Color.parseColor(newColor)
    } catch (e: Exception) {
        defColor
    }
}

fun String.compatColorText(): String {
    if (this.toLowerCase().startsWith("0x")) {
        return replace("0x", "#")
    }
    if (!this.toLowerCase().startsWith("#")) {
        return "#$this"
    }
    return this
}

fun String?.getReal(def: String = ""): String {
    return StringUtils.getString(this, def)
}


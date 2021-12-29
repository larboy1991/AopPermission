package com.zhz.library.aop.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 权限工具类
 * <p>
 * Date: 2021-06-07
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: zhuanghongzhan
 */
object PermissionUtils {

    /**
     * 判断是否是拒绝并且不在提醒
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     * @param result Map<String, Boolean>
     * @return Boolean
     */
    fun shouldShowRequestPermission(activity: Activity, result: Map<String, Boolean>): Boolean {
        result.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, it.key)) {
                return true
            }
        }
        return false
    }


    /**
     * 检查授权是否允许了
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     * @param result Map<String, Boolean>
     */
    fun verifyPermissions(result: Map<String, Boolean>): Boolean {
        if (result.isEmpty()) return false
        result.forEach {
            if (!it.value) {
                return false
            }
        }
        return true
    }

    /**
     * 判断是否所有权限都同意了
     *
     * @param context     context
     * @param permissions permission list
     * @return return true if all permissions granted else false
     */
    fun hasSelfPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!hasSelfPermission(context, permission)) {
                return false
            }
        }
        return true
    }


    /**
     * 判断单个权限是否同意
     *
     * @param context    context
     * @param permission permission
     * @return return true if permission granted
     */
    fun hasSelfPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


}
package com.zhz.library.aop.permission

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zhz.library.aop.permission.interf.IPermission

/**
 * 申请权限的fragment
 * <p>
 * Date: 2021-06-04
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: zhuanghongzhan
 */
class PermissionRequestFragment : Fragment() {
    private var permissions: Array<String>? = null
    private var requestCode: Int = 0

    companion object {
        @JvmStatic
        private var permissionListener: IPermission? = null

        @JvmStatic
        val PERMISSION_KEY = "permission_list"

        @JvmStatic
        val REQUEST_CODE = "request_code"

        /**
         * 跳转到Activity申请权限
         *
         * @param context     Context
         * @param permissions Permission List
         * @param iPermission Interface
         */
        @JvmStatic
        fun permissionRequest(
            permissions: Array<String>,
            requestCode: Int,
            context: FragmentActivity,
            iPermission: IPermission
        ) {
            permissionListener = iPermission
            val bundle = Bundle()
            bundle.putStringArray(PERMISSION_KEY, permissions)
            bundle.putInt(REQUEST_CODE, requestCode)

            //这边在纠结要不要把他给remove掉，因为理论上每次请求结束后都会将fragment给remove掉，
            // 但是有一种情况会导致可能里面会出现多个，使用定时器，一直触发，导致上一个授权还没有走完，第二个请求又来了
            val old = context.supportFragmentManager.fragments.firstOrNull { it is PermissionRequestFragment } as? PermissionRequestFragment
            if (old != null) {
                context.supportFragmentManager.beginTransaction().remove(old).commitNowAllowingStateLoss()
            }
            val permissionRequestFragment = PermissionRequestFragment()
            permissionRequestFragment.arguments = bundle
            context.supportFragmentManager.beginTransaction()
                .add(permissionRequestFragment, "PermissionRequestFragment")
                .commitNowAllowingStateLoss()
        }
    }


    /**
     * 权限请求结果回调
     */
    private val requestMultiplePermissions by lazy {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), ::onRequestPermissionsResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            permissions = bundle.getStringArray(PERMISSION_KEY)
            requestCode = bundle.getInt(REQUEST_CODE, 0)
        }
        if (permissions?.size ?: 0 <= 0) {
            return
        }
        requestPermission(permissions!!)
    }

    /**
     * 申请权限
     *
     * @param permissions permission list
     */
    private fun requestPermission(permissions: Array<String>) {
        if (PermissionUtils.hasSelfPermissions(requireContext(), permissions)) {
            permissionListener?.permissionGranted()
            removeView()
        } else {
            requestMultiplePermissions.launch(permissions)
        }
    }

    /**
     * 授权回调
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     * @param result Map<String, Boolean>
     */
    private fun onRequestPermissionsResult(result: Map<String, Boolean>) {
        //如果所有的权限都授权了，则直接调用返回
        if (PermissionUtils.verifyPermissions(result)) {
            permissionListener?.permissionGranted()
            removeView()
        } else {
            if (!PermissionUtils.shouldShowRequestPermission(requireActivity(), result)) {
                //权限被拒绝并且选中不再提示
                permissionListener?.permissionDenied(requestCode, result)
            } else {
                //权限被取消
                permissionListener?.permissionCanceled(requestCode,result)
            }
            removeView()
        }
    }

    /**
     * 将fragment从FragmentManager里面移除
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     */
    private fun removeView() {
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this)
            .commitNowAllowingStateLoss()
    }

}
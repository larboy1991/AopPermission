package com.zhz.library.aop.permission.aspect


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.zhz.library.aop.permission.PermissionRequestFragment
import com.zhz.library.aop.permission.PermissionUtils
import com.zhz.library.aop.permission.annotation.NeedPermission
import com.zhz.library.aop.permission.annotation.PermissionCanceled
import com.zhz.library.aop.permission.annotation.PermissionDenied
import com.zhz.library.aop.permission.bean.CancelBean
import com.zhz.library.aop.permission.bean.DenyBean
import com.zhz.library.aop.permission.interf.IPermission
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import kotlin.Exception

/**
 * aop 权限切面类
 * <p>
 * Date: 2021-06-07
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: zhuanghongzhan
 */
@Suppress("unused", "UNUSED_PARAMETER")
@Aspect
class PermissionAspect {

    @Pointcut("execution(@com.zhz.library.aop.permission.annotation.NeedPermission * *(..)) && @annotation(needPermission)")
    fun requestPermissionMethod(needPermission: NeedPermission) {
        //do nothing
    }

    @Around("requestPermissionMethod(needPermission)")
    fun joinPoint(joinPoint: ProceedingJoinPoint, needPermission: NeedPermission) {
        val obj = joinPoint.getThis() ?: return
        val activity: FragmentActivity = getFragmentActivity(joinPoint) ?: return
        //如果所有的权限都允许了,则直接返回
        if (PermissionUtils.hasSelfPermissions(activity, needPermission.value)) {
            try {
                joinPoint.proceed()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }
        PermissionRequestFragment.permissionRequest(needPermission.value, needPermission.requestCode, activity, object : IPermission {
            override fun permissionGranted() {
                try {
                    joinPoint.proceed()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }

            override fun permissionDenied(requestCode: Int, denyList: Map<String, Boolean>) {
                permissionDenied(obj, requestCode, denyList)
            }

            override fun permissionCanceled(requestCode: Int, cancelList: Map<String, Boolean>) {
                permissionCanceled(obj, requestCode, cancelList)
            }
        })
    }

    /**
     * 获取FragmentActivity
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     */
    private fun getFragmentActivity(joinPoint: ProceedingJoinPoint): FragmentActivity? {
        return when (val obj = joinPoint.getThis()) {
            is FragmentActivity -> obj
            is Fragment -> obj.requireActivity()
            else -> {
                val topActivity = ActivityUtils.getTopActivity()
                if (topActivity != null && topActivity is FragmentActivity) {
                    topActivity
                } else {
                    null
                }
            }
        }
    }


    private fun permissionDenied(obj: Any, requestCode: Int, denyList: Map<String, Boolean>) {
        val cls = obj.javaClass
        val methods = cls.declaredMethods
        if (methods.isEmpty()) return
        for (method in methods) {
            //过滤不含自定义注解PermissionDenied的方法
            val isHasAnnotation = method.isAnnotationPresent(PermissionDenied::class.java)
            if (isHasAnnotation) {
                method.isAccessible = true
                //获取方法类型
                val types = method.parameterTypes
                if (types.isEmpty()) {
                    method.invoke(obj)
                    return
                }

                if (types.size == 1) {
                    val type = types.first().name
                    if (type == DenyBean::class.java.name) {
                        //解析注解上对应的信息
                        val bean = DenyBean(requestCode, denyList)
                        try {
                            method.invoke(obj, bean)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /**
     * 拒绝权限
     * <p>
     * Author: zhuanghongzhan
     * Date: 2021-06-07
     * @param obj Any
     * @param requestCode Int
     * @param cancelList Map<String, Boolean>
     */
    private fun permissionCanceled(obj: Any, requestCode: Int, cancelList: Map<String, Boolean>) {
        val cls = obj.javaClass
        val methods = cls.declaredMethods
        if (methods.isEmpty()) return
        for (method in methods) {
            //过滤不含自定义注解PermissionCanceled的方法
            val isHasAnnotation = method.isAnnotationPresent(PermissionCanceled::class.java)
            if (isHasAnnotation) {
                method.isAccessible = true
                //获取方法类型
                val types = method.parameterTypes

                if (types.isEmpty()) {
                    method.invoke(obj)
                    return
                }
                if (types.size == 1) {
                    val type = types.first().name
                    if (type == CancelBean::class.java.name) {
                        //解析注解上对应的信息
                        val bean = CancelBean(requestCode, cancelList)
                        try {
                            method.invoke(obj, bean)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


}
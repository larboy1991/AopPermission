package com.zhz.library.aop.permission.interf

interface IPermission {
    //同意权限
    fun permissionGranted()

    //拒绝权限并且选中不再提示
    fun permissionDenied(requestCode: Int, denyList: Map<String,Boolean>)

    //取消权限
    fun permissionCanceled(requestCode: Int,cancelList: Map<String, Boolean>)
}
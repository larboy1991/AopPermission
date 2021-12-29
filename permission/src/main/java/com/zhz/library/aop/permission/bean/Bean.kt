package com.zhz.library.aop.permission.bean

/**
 * 取消权限回调实体类
 * <p>
 * Date: 2021-06-04
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 * @param requestCode Int 请求码
 * @param resultList Map<String, Boolean> 权限结果，包括了已授权的和未授权的
 * @constructor
 *
 * Author: zhuanghongzhan
 */
data class CancelBean(val requestCode: Int = 0, val resultList: Map<String, Boolean>?)

/**
 * 拒绝权限回调实体类
 * <p>
 * Date: 2021-06-04
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 * @param requestCode Int
 * @param resultList Map<String, Boolean> 权限结果，包括了已授权的和未授权的
 * @constructor
 *
 * Author: zhuanghongzhan
 */
data class DenyBean(val requestCode: Int = 0, val resultList: Map<String, Boolean>?)
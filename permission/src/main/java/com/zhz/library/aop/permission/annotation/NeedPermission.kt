package com.zhz.library.aop.permission.annotation

import kotlin.annotation.Target
import kotlin.annotation.Retention

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class NeedPermission(val value: Array<String>, val requestCode: Int = 0)

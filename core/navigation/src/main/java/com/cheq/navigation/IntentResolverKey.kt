package com.cheq.navigation

import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class IntentResolverKey(val value: KClass<out IntentKey>)
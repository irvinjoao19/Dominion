package com.dsige.dominion.data.module

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass
import kotlin.annotation.MustBeDocumented

@MustBeDocumented
@MapKey
@kotlin.annotation.Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)
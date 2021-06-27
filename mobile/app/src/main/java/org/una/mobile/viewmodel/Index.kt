package org.una.mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y) = Transformations.map(this) { mapFunction(it) }
package com.atlunch.data.dto

import com.atlunch.domain.Photo

fun PhotoMediaDTO.toDomain(): Photo {
    return Photo(this.photoUri)
}
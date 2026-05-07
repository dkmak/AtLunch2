package dkmak.atlunch.data.dto

import dkmak.atlunch.domain.Photo

fun PhotoMediaDTO.toDomain(): Photo = Photo(this.photoUri)

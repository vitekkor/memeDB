package com.vitekkor.memeDB.misc

import com.vitekkor.memeDB.model.Media
import org.springframework.data.mongodb.repository.MongoRepository

interface MediaRepository: MongoRepository<Media, String>

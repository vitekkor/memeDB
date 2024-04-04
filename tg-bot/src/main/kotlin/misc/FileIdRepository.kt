package com.vitekkor.memeDB.misc

import com.vitekkor.memeDB.model.FileId
import org.springframework.data.mongodb.repository.MongoRepository

interface FileIdRepository : MongoRepository<FileId, String>

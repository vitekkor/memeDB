package com.vitekkor.memeDB.repository

import com.vitekkor.memeDB.model.FileData

import org.springframework.data.mongodb.repository.MongoRepository

interface FileDataRepository: MongoRepository<FileData, String>
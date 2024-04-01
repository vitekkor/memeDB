package com.vitekkor.memeDB.repository

import com.vitekkor.memeDB.model.SearchCommand
import org.springframework.data.mongodb.repository.MongoRepository

interface SearchCommandRepository : MongoRepository<SearchCommand, String>
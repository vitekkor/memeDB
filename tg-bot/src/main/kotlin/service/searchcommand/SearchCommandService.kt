package com.vitekkor.memeDB.service.searchcommand

interface SearchCommandService {
    fun findMemes(searchText: String): String
}
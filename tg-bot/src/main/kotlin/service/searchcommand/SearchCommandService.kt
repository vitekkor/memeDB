package com.vitekkor.memeDB.service.searchcommand

import java.io.File

interface SearchCommandService {
    fun findMemes(searchText: String): List<Pair<File, String>>
}

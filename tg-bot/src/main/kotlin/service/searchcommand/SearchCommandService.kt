package com.vitekkor.memeDB.service.searchcommand

import com.vitekkor.memeDB.model.MemDto
import java.io.File

interface SearchCommandService {
    fun findMemes(searchText: String): List<Pair<File, String>>

    fun findMemesIds(searchText: String): List<MemDto>
}

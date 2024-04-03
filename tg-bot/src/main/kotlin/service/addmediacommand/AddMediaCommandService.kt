package com.vitekkor.memeDB.service.addmediacommand

import com.vitekkor.memeDB.model.Media

interface AddMediaCommandService {

    fun addMedia(meduiaData: Media)
}
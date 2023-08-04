package ru.netology.nmedia.model

import android.net.Uri
import java.io.File

//Код из видео лекции
data class PhotoModel(val uri: Uri, val file: File)

//Код из кода лекции!!
//data class PhotoModel(val uri: Uri? = null, val file: File? = null)

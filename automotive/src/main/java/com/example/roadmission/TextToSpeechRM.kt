package com.example.roadmission

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeechRM(mContext: Context) {
    private lateinit var textToSpeech: TextToSpeech

    init {
        textToSpeech = TextToSpeech(mContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                textToSpeech.language = Locale.UK
            }
        })
    }

    fun speak(toSpeak: String) {
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,"")
    }
}
package com.example.roadmission

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TextToSpeechRM{

    companion object {
        @JvmStatic
        private lateinit var textToSpeech: TextToSpeech
        @JvmStatic
        private fun init(context: Context) {
            textToSpeech = TextToSpeech( context,TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    textToSpeech.language = Locale.UK
                }
            })
        }
        @JvmStatic
        fun speak(context: Context, toSpeak: String) {
            init(context)
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,"")
        }
    }
}
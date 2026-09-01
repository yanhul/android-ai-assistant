package com.yanhul.assistant.assistant

import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class AIAssistantSession(context: android.content.Context) : VoiceInteractionSession(context) {
    private lateinit var voiceIO: VoiceIO
    private val router = CommandRouter()

    override fun onCreate() {
        super.onCreate()
        voiceIO = VoiceIO(
            context = context,
            onText = ::handleSpeech,
            onError = { message -> voiceIO.speak(message) },
        )
    }

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        voiceIO.speak("Ready.")
        voiceIO.startListening()
    }

    private fun handleSpeech(text: String) {
        router.handle(context, text) { result ->
            voiceIO.speak(result.message)
            if (result.closeSession) hide()
        }
    }

    override fun onHide() {
        voiceIO.release()
        super.onHide()
    }
}

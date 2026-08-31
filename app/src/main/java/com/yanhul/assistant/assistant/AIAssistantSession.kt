package com.yanhul.assistant.assistant

import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class AIAssistantSession(context: Context) : VoiceInteractionSession(context) {
    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        CommandRouter().handle(context, "assistant_ready")
    }
}

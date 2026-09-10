package com.yanhul.assistant.assistant

import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyPolicyTest {
    private val policy = SafetyPolicy()

    @Test
    fun allows_non_destructive_explicit_actions() {
        assertTrue(policy.decision("back") is SafetyDecision.Allow)
        assertTrue(policy.decision("home") is SafetyDecision.Allow)
        assertTrue(policy.decision("click", "Settings") is SafetyDecision.Allow)
        assertTrue(policy.decision("tap", "100,200") is SafetyDecision.Allow)
    }

    @Test
    fun requires_confirmation_for_destructive_actions() {
        assertTrue(policy.decision("delete", "file.txt") is SafetyDecision.Confirm)
        assertTrue(policy.decision("uninstall", "Example app") is SafetyDecision.Confirm)
        assertTrue(policy.decision("open", "delete all data") is SafetyDecision.Confirm)
    }

    @Test
    fun requires_confirmation_or_specificity_for_ambiguous_actions() {
        assertTrue(policy.decision("send", "") is SafetyDecision.Confirm)
        assertTrue(policy.decision("open", "") is SafetyDecision.Confirm)
        assertTrue(policy.decision("click", "") is SafetyDecision.Confirm)
    }

    @Test
    fun blocks_missing_action() {
        assertTrue(policy.decision("   ") is SafetyDecision.Block)
    }
}

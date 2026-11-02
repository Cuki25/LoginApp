package com.example.loginapp.data

/**
 * In-memory holder for everything collected during the multi-step sign-up flow.
 *
 * Not persisted to disk — when Room is wired up this draft is what you map
 * into your entities at the end of the flow.
 *
 * Process-lifetime singleton so signup screens share state without threading
 * a large argument list through the nav graph.
 */
object SignUpDraft {

    // ── Step 1 — Auth (email + password, OTP) ─────────────────────────────────
    var email:    String = ""   // captured on Step 1, sent to /auth/register
    var password: String = ""   // captured on Step 2 (Basic Info)

    // Set by AppNavigation after /auth/register succeeds — used to save the session.
    var registeredToken:  String = ""
    var registeredUserId: String = ""

    // ── Step 2 — Basic info ───────────────────────────────────────────────────
    var campusId:      String  = ""
    var studentId:     String  = ""
    var firstName:     String  = ""
    var lastName:      String  = ""
    var middleName:    String  = ""
    var middleNameNA:  Boolean = false
    var birthdayMillis: Long?  = null

    // ── Step 3 — Medical profile (optional / skippable) ───────────────────────
    var gender:      String       = ""
    var bloodType:   String       = ""
    var allergies:   String       = ""
    var weight:      String       = ""
    var height:      String       = ""
    var conditions:  List<String> = emptyList()
    var medications: List<String> = emptyList()

    // ── Step 4 — Contact information (optional / skippable) ───────────────────
    var mother:        EmergencyContact       = EmergencyContact()
    var father:        EmergencyContact       = EmergencyContact()
    var extraContacts: List<EmergencyContact> = emptyList()

    /** Reset everything when the user abandons or completes the flow. */
    fun clear() {
        email             = ""
        password          = ""
        registeredToken   = ""
        registeredUserId  = ""
        campusId          = ""
        studentId         = ""
        firstName         = ""
        lastName          = ""
        middleName        = ""
        middleNameNA      = false
        birthdayMillis    = null
        gender            = ""
        bloodType         = ""
        allergies         = ""
        weight            = ""
        height            = ""
        conditions        = emptyList()
        medications       = emptyList()
        mother            = EmergencyContact()
        father            = EmergencyContact()
        extraContacts     = emptyList()
    }
}

data class EmergencyContact(
    val name:    String = "",
    val number:  String = "",
    val address: String = ""
)

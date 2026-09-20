package com.example.loginapp.data

/**
 * In-memory holder for everything collected during the multi-step sign-up flow.
 *
 * This is intentionally NOT persisted yet. When Room is wired up, this draft is
 * what you would map into your entities at the end of the flow.
 *
 * It's a simple process-lifetime singleton so the various signup screens can
 * contribute their slice of data without threading a huge argument list through
 * the navigation graph.
 */
object SignUpDraft {

    // Step 1 - Auth
    var contact: String = ""          // email or phone number

    // Step 2 - Basic info
    var campusId: String = ""
    var studentId: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var middleName: String = ""
    var middleNameNA: Boolean = false
    var birthdayMillis: Long? = null
    var password: String = ""

    // Step 3 - Medical profile (optional / skippable)
    var gender: String = ""
    var bloodType: String = ""
    var allergies: String = ""
    var weight: String = ""
    var height: String = ""
    var conditions: List<String> = emptyList()
    var medications: List<String> = emptyList()

    // Step 4 - Contact information (optional / skippable)
    var mother: EmergencyContact = EmergencyContact()
    var father: EmergencyContact = EmergencyContact()
    var extraContacts: List<EmergencyContact> = emptyList()

    /** Reset everything, e.g. when the user abandons or completes the flow. */
    fun clear() {
        contact = ""
        campusId = ""
        studentId = ""
        firstName = ""
        lastName = ""
        middleName = ""
        middleNameNA = false
        birthdayMillis = null
        password = ""
        gender = ""
        bloodType = ""
        allergies = ""
        weight = ""
        height = ""
        conditions = emptyList()
        medications = emptyList()
        mother = EmergencyContact()
        father = EmergencyContact()
        extraContacts = emptyList()
    }
}

data class EmergencyContact(
    val name: String = "",
    val number: String = "",
    val address: String = ""
)

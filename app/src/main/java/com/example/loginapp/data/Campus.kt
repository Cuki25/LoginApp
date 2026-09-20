package com.example.loginapp.data

/**
 * A campus/school option shown in the "Select Campus" dropdown.
 *
 * For the Taguig institution list the [id] (the backend "school_id") is the
 * same as the [displayName] -- the selected string is stored directly as the
 * school identifier. Using a fixed list prevents typos.
 */
data class Campus(val id: String, val displayName: String)

/** Official list of institutions in Taguig City. */
object Campuses {

    private val taguigCampuses = listOf(
        "Academia de San Bartolome de Taguig",
        "Asian Christian Technological School and Colleges (ACTS)",
        "Athens Academy Inc.",
        "Colegio de Santa Ana",
        "De La Salle University - Rufino Campus",
        "Enderun Colleges",
        "Good Shepherd of Taguig Colleges, Inc.",
        "Gracel Christian College Foundation Inc.",
        "HSL-Braille College",
        "Lyceum International College",
        "Metropolitan Institute of Arts and Sciences",
        "MINT College",
        "Polytechnic University of the Philippines - Taguig",
        "Saint Francis of Assisi College - Taguig Campus",
        "St. Chamel College",
        "St. Therese College of Taguig",
        "STI College Global City",
        "Taguig City University",
        "Technological University of the Philippines - Taguig Campus",
        "The Fisher Valley College",
        "Treston International College",
        "University of Makati",
        "University of the Philippines - BGC"
    )

    /** id == displayName for every campus, so the selected string is the school_id. */
    val all: List<Campus> = taguigCampuses.map { Campus(id = it, displayName = it) }

    /** Display names for the dropdown. */
    val displayNames: List<String> = taguigCampuses

    fun displayNameFor(id: String): String =
        all.firstOrNull { it.id == id }?.displayName ?: id

    /** Returns "" for a blank/unknown id (for pre-filling dropdowns). */
    fun displayNameForOrEmpty(id: String): String =
        if (id.isBlank()) "" else all.firstOrNull { it.id == id }?.displayName ?: ""
}

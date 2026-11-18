package com.example.loginapp.ui.firstaid

/** All first-aid modules bundled as static data. */
data class FirstAidModule(
    val id:         String,
    val title:      String,
    val icon:       String,
    val severity:   String,          // "Critical" | "Urgent" | "Moderate"
    val doSteps:    List<String>,
    val avoidSteps: List<String>,
    val symptoms:   List<String>
)

val firstAidModules: List<FirstAidModule> = listOf(
    FirstAidModule(
        id       = "bleeding",
        title    = "Severe Bleeding",
        icon     = "🩸",
        severity = "Critical",
        symptoms = listOf("bleeding", "blood", "cut", "wound"),
        doSteps  = listOf(
            "Apply firm, direct pressure with a clean cloth or bandage.",
            "Keep pressure constant — do not lift to check for 5–10 minutes.",
            "Elevate the injured area above heart level if possible.",
            "Call 911 if bleeding does not slow after 10 minutes."
        ),
        avoidSteps = listOf(
            "Do not remove an embedded object — stabilise it in place.",
            "Do not apply a tourniquet unless you are trained to do so.",
            "Do not use dirty materials directly on the wound."
        )
    ),
    FirstAidModule(
        id       = "choking",
        title    = "Choking",
        icon     = "😮‍💨",
        severity = "Critical",
        symptoms = listOf("choking", "can't breathe", "airway"),
        doSteps  = listOf(
            "Ask: 'Are you choking?' If they cannot speak, act immediately.",
            "Perform 5 back blows between the shoulder blades.",
            "Perform 5 abdominal thrusts (Heimlich manoeuvre).",
            "Alternate back blows and thrusts until the object dislodges or help arrives."
        ),
        avoidSteps = listOf(
            "Do not perform blind finger sweeps into the mouth.",
            "Do not slap on the back if the person can still cough forcefully."
        )
    ),
    FirstAidModule(
        id       = "burns",
        title    = "Minor Burns",
        icon     = "🔥",
        severity = "Moderate",
        symptoms = listOf("burn", "fire", "heat", "scald"),
        doSteps  = listOf(
            "Cool the burn under cool (not cold) running water for 10–20 minutes.",
            "Remove jewellery near the burn.",
            "Cover loosely with a sterile dressing or clean plastic wrap.",
            "Seek medical care for burns larger than your palm or on the face, hands, or genitals."
        ),
        avoidSteps = listOf(
            "Do not use ice, butter, or toothpaste.",
            "Do not break blisters.",
            "Do not remove clothing stuck to burned skin."
        )
    ),
    FirstAidModule(
        id       = "fracture",
        title    = "Fracture / Broken Bone",
        icon     = "🦴",
        severity = "Urgent",
        symptoms = listOf("fracture", "broken bone", "bone", "deformity"),
        doSteps  = listOf(
            "Immobilise the injured area in the position found.",
            "Improvise a splint using rigid items (rolled newspaper, sticks) padded with cloth.",
            "Apply ice wrapped in a cloth to reduce swelling.",
            "Call 911 if the bone is visibly broken through skin or the person cannot bear weight."
        ),
        avoidSteps = listOf(
            "Do not try to straighten a deformed limb.",
            "Do not move the person if a spine or neck injury is suspected."
        )
    ),
    FirstAidModule(
        id       = "fainting",
        title    = "Fainting",
        icon     = "😵",
        severity = "Urgent",
        symptoms = listOf("faint", "unconscious", "passed out", "dizzy collapsed"),
        doSteps  = listOf(
            "Lower the person to the ground on their back.",
            "Raise their legs 30 cm (12 inches) above heart level.",
            "Loosen tight clothing around the neck and chest.",
            "If not breathing normally, begin CPR and call 911."
        ),
        avoidSteps = listOf(
            "Do not give anything by mouth until fully conscious.",
            "Do not leave them alone until they recover completely."
        )
    ),
    FirstAidModule(
        id       = "seizure",
        title    = "Seizures",
        icon     = "⚡",
        severity = "Critical",
        symptoms = listOf("seizure", "convulsion", "fit", "shaking"),
        doSteps  = listOf(
            "Clear hard or sharp objects away from the area.",
            "Cushion their head with something soft.",
            "Time the seizure — call 911 if it lasts more than 5 minutes.",
            "After the seizure, place in recovery position (on their side)."
        ),
        avoidSteps = listOf(
            "Do not hold the person down or restrain movement.",
            "Do not put anything in their mouth."
        )
    ),
    FirstAidModule(
        id       = "allergic",
        title    = "Severe Allergic Reaction",
        icon     = "🤧",
        severity = "Critical",
        symptoms = listOf("allergy", "anaphylaxis", "rash", "hives", "swelling throat"),
        doSteps  = listOf(
            "Call 911 immediately if breathing is difficult or the throat is swelling.",
            "If the person has an epinephrine auto-injector (EpiPen), help them use it.",
            "Have them sit upright if breathing is difficult, lie down if dizzy.",
            "Be ready to perform CPR if they lose consciousness."
        ),
        avoidSteps = listOf(
            "Do not give antihistamines as the primary treatment for anaphylaxis.",
            "Do not leave the person unattended."
        )
    ),
    FirstAidModule(
        id       = "heatstroke",
        title    = "Heatstroke",
        icon     = "☀️",
        severity = "Critical",
        symptoms = listOf("heat stroke", "overheating", "hot dry skin", "heat"),
        doSteps  = listOf(
            "Move the person to a cool, shaded area immediately.",
            "Remove excess clothing and apply cool wet cloths to skin.",
            "Fan the person or use an air conditioner.",
            "Call 911 — heat stroke is life-threatening."
        ),
        avoidSteps = listOf(
            "Do not give fluids if the person is unconscious or vomiting.",
            "Do not apply ice directly to the skin."
        )
    ),
    FirstAidModule(
        id       = "sprains",
        title    = "Sprains & Strains",
        icon     = "🩹",
        severity = "Moderate",
        symptoms = listOf("sprain", "strain", "twisted ankle", "swollen joint"),
        doSteps  = listOf(
            "Rest the injured area and avoid putting weight on it.",
            "Apply ice wrapped in cloth for 20 minutes every 2 hours.",
            "Compress with an elastic bandage to reduce swelling.",
            "Elevate the limb above heart level."
        ),
        avoidSteps = listOf(
            "Do not apply heat in the first 48 hours.",
            "Do not massage the injury immediately — it may worsen swelling."
        )
    ),
    FirstAidModule(
        id       = "minorbleeding",
        title    = "Minor Bleeding",
        icon     = "🩸",
        severity = "Moderate",
        symptoms = listOf("minor cut", "scrape", "small wound", "graze"),
        doSteps  = listOf(
            "Rinse the wound with clean running water for 5 minutes.",
            "Apply gentle pressure with a clean cloth.",
            "Cover with an adhesive bandage or sterile dressing.",
            "Change the dressing daily and watch for signs of infection."
        ),
        avoidSteps = listOf(
            "Do not use hydrogen peroxide or iodine directly on wounds.",
            "Do not blow on the wound — it introduces bacteria."
        )
    ),
    FirstAidModule(
        id       = "asthma",
        title    = "Asthma Attack",
        icon     = "🫁",
        severity = "Urgent",
        symptoms = listOf("asthma", "wheezing", "shortness of breath", "inhaler"),
        doSteps  = listOf(
            "Help the person sit upright in a comfortable position.",
            "Assist them in using their reliever inhaler (usually blue).",
            "Take one puff every 30–60 seconds, up to 10 puffs.",
            "Call 911 if the inhaler has no effect after 10 puffs or the person is worsening."
        ),
        avoidSteps = listOf(
            "Do not leave the person alone.",
            "Do not give food or drink during an attack."
        )
    )
)

fun searchModules(query: String): List<FirstAidModule> {
    val q = query.trim().lowercase()
    if (q.isBlank()) return firstAidModules
    return firstAidModules.filter { m ->
        m.title.lowercase().contains(q) || m.symptoms.any { it.contains(q) }
    }
}

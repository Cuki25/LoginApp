package com.example.loginapp.ui.firstaid

/** All ten first-aid modules bundled as static data. */
data class FirstAidModule(
    val id:          String,
    val title:       String,
    val icon:        String,            // emoji used as a visual shortcut
    val doSteps:     List<String>,
    val avoidSteps:  List<String>,
    val symptoms:    List<String>       // symptom keywords for the checker flow
)

val firstAidModules: List<FirstAidModule> = listOf(
    FirstAidModule(
        id    = "bleeding",
        title = "Severe Bleeding",
        icon  = "🩸",
        symptoms = listOf("bleeding", "blood", "cut", "wound"),
        doSteps = listOf(
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
        id    = "choking",
        title = "Choking",
        icon  = "🫁",
        symptoms = listOf("choking", "can't breathe", "airway"),
        doSteps = listOf(
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
        id    = "burns",
        title = "Burns",
        icon  = "🔥",
        symptoms = listOf("burn", "fire", "heat", "scald"),
        doSteps = listOf(
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
        id    = "fracture",
        title = "Fracture / Broken Bone",
        icon  = "🦴",
        symptoms = listOf("fracture", "broken bone", "bone", "deformity"),
        doSteps = listOf(
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
        id    = "fainting",
        title = "Fainting / Loss of Consciousness",
        icon  = "😵",
        symptoms = listOf("faint", "unconscious", "passed out", "dizzy collapsed"),
        doSteps = listOf(
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
        id    = "seizure",
        title = "Seizure",
        icon  = "⚡",
        symptoms = listOf("seizure", "convulsion", "fit", "shaking"),
        doSteps = listOf(
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
        id    = "allergic",
        title = "Severe Allergic Reaction",
        icon  = "🤧",
        symptoms = listOf("allergy", "anaphylaxis", "rash", "hives", "swelling throat"),
        doSteps = listOf(
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
        id    = "heatstroke",
        title = "Heat Stroke",
        icon  = "☀️",
        symptoms = listOf("heat stroke", "overheating", "hot dry skin", "heat"),
        doSteps = listOf(
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
        id    = "nosebleed",
        title = "Nosebleed",
        icon  = "👃",
        symptoms = listOf("nosebleed", "nose bleed", "nose blood"),
        doSteps = listOf(
            "Sit upright and lean slightly forward.",
            "Pinch the soft part of the nose shut for 10–15 minutes without releasing.",
            "Breathe through the mouth.",
            "Seek medical help if bleeding does not stop after 20 minutes."
        ),
        avoidSteps = listOf(
            "Do not tilt the head back — blood may run into the throat.",
            "Do not pack the nose tightly with cotton."
        )
    ),
    FirstAidModule(
        id    = "diabetic",
        title = "Diabetic Emergency (Hypoglycaemia)",
        icon  = "🍬",
        symptoms = listOf("diabetic", "low blood sugar", "hypoglycaemia", "shaking confused"),
        doSteps = listOf(
            "If conscious and able to swallow, give 15–20 g of fast-acting sugar (3–4 glucose tablets, 150 ml juice).",
            "Have them sit and rest for 15 minutes.",
            "Re-check and repeat if symptoms persist.",
            "Call 911 if the person is unconscious or does not improve."
        ),
        avoidSteps = listOf(
            "Do not give food or drink to an unconscious person.",
            "Do not delay calling 911 if unsure of severity."
        )
    )
)

/** Returns modules whose symptom keywords match [query]. */
fun searchModules(query: String): List<FirstAidModule> {
    val q = query.trim().lowercase()
    if (q.isBlank()) return firstAidModules
    return firstAidModules.filter { m ->
        m.title.lowercase().contains(q) ||
        m.symptoms.any { it.contains(q) }
    }
}

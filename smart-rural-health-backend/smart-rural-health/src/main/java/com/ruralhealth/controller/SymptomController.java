package com.ruralhealth.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

/**
 * Symptom Checker Controller
 * Rule-based specialist suggestion engine.
 * Can be replaced with an ML model later.
 */
@RestController
@RequestMapping("/api/symptoms")
public class SymptomController {

    // Symptom → [specialist, urgency, advice]
    private static final Map<String, String[]> SYMPTOM_MAP = new LinkedHashMap<>() {{
        put("chest pain",     new String[]{"Cardiologist",      "HIGH",     "Go to hospital immediately. Do not delay."});
        put("breathing",      new String[]{"Pulmonologist",     "HIGH",     "Go to nearest hospital immediately."});
        put("unconscious",    new String[]{"Emergency",         "CRITICAL", "Call 108 ambulance NOW."});
        put("stroke",         new String[]{"Neurologist",       "CRITICAL", "Call 108 ambulance NOW."});
        put("fever",          new String[]{"General Physician", "MODERATE", "Drink water, rest. Take Paracetamol. Visit doctor if fever > 3 days."});
        put("cough",          new String[]{"Pulmonologist",     "MILD",     "Steam inhalation. Avoid cold drinks. Visit doctor if cough > 1 week."});
        put("cold",           new String[]{"General Physician", "MILD",     "Rest, warm water, steam inhalation."});
        put("headache",       new String[]{"Neurologist",       "MILD",     "Rest in dark room. Take Paracetamol. Drink water."});
        put("stomach",        new String[]{"Gastroenterologist","MODERATE", "Avoid spicy food. Drink ORS. See doctor if severe."});
        put("vomiting",       new String[]{"General Physician", "MODERATE", "Rest. Small sips of water. Visit doctor if does not stop."});
        put("diarrhea",       new String[]{"General Physician", "MODERATE", "Drink ORS. Eat light food. See doctor if > 5 times/day."});
        put("loose motion",   new String[]{"General Physician", "MODERATE", "Drink ORS. Eat light food. See doctor if > 5 times/day."});
        put("rash",           new String[]{"Dermatologist",     "MILD",     "Do not scratch. Keep skin clean. Visit skin doctor."});
        put("skin",           new String[]{"Dermatologist",     "MILD",     "Visit a dermatologist for examination."});
        put("eye",            new String[]{"Ophthalmologist",   "MILD",     "Do not rub eyes. Visit eye doctor soon."});
        put("ear",            new String[]{"ENT Specialist",    "MILD",     "Avoid inserting anything in ear. Visit ENT doctor."});
        put("tooth",          new String[]{"Dentist",           "MILD",     "Rinse with warm salt water. Visit dentist soon."});
        put("joint",          new String[]{"Orthopaedic",       "MILD",     "Rest the joint. Apply ice. Visit doctor if persistent."});
        put("bone",           new String[]{"Orthopaedic",       "MODERATE", "Do not move the affected area. Visit doctor."});
        put("child",          new String[]{"Paediatrician",     "MODERATE", "Visit a paediatrician."});
        put("baby",           new String[]{"Paediatrician",     "MODERATE", "Visit a paediatrician immediately."});
        put("pregnant",       new String[]{"Gynaecologist",     "MODERATE", "Visit your nearest PHC or gynaecologist."});
        put("diabetes",       new String[]{"Endocrinologist",   "MODERATE", "Monitor blood sugar. Visit doctor soon."});
        put("sugar",          new String[]{"Endocrinologist",   "MODERATE", "Monitor blood sugar. Visit doctor soon."});
        put("blood pressure", new String[]{"Cardiologist",      "MODERATE", "Rest. Avoid stress. Visit doctor soon."});
        put("bp",             new String[]{"Cardiologist",      "MODERATE", "Rest. Avoid stress. Visit doctor soon."});
        put("mental",         new String[]{"Psychiatrist",      "MODERATE", "You are not alone. Talk to a doctor or a trusted person."});
        put("depression",     new String[]{"Psychiatrist",      "MODERATE", "You are not alone. Please speak to a doctor."});
    }};

    @PostMapping("/check")
    public ResponseEntity<?> checkSymptoms(@RequestBody SymptomRequest req) {
        String input = (req.getSymptoms() == null ? "" : req.getSymptoms()).toLowerCase();

        String[] result = null;
        for (Map.Entry<String, String[]> entry : SYMPTOM_MAP.entrySet()) {
            if (input.contains(entry.getKey())) {
                result = entry.getValue();
                break;
            }
        }

        if (result == null) {
            result = new String[]{
                "General Physician",
                "MILD",
                "Visit your nearest doctor for proper examination and diagnosis."
            };
        }

        return ResponseEntity.ok(Map.of(
                "specialist",  result[0],
                "urgency",     result[1],
                "advice",      result[2],
                "sos",         result[1].equals("CRITICAL")
        ));
    }

    @Data
    static class SymptomRequest {
        String symptoms;
    }
}

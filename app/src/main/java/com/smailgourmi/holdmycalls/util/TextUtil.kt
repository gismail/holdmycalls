package com.smailgourmi.holdmycalls.util
import java.security.MessageDigest
import java.util.Base64
fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPhoneValid(phone: CharSequence): Boolean {
    return android.util.Patterns.PHONE.matcher(phone).matches()
}


fun isTextValid(minLength: Int, text: String?): Boolean {
    return !(text.isNullOrBlank() || text.length < minLength)
}



fun hashPhoneNumber(phoneNumber: String): String {
    try {
        val bytes = phoneNumber.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(bytes)
        val base64Hash = Base64.getEncoder().encodeToString(hashedBytes)
        return base64Hash.replace(Regex("[^a-zA-Z0-9]"), "")
    } catch (e: Exception) {
        throw RuntimeException("Failed to hash phone number: $phoneNumber", e)
    }
}

fun formatPhoneNumber(phoneNumber: String): String {
    val phoneDigitsRegex: Regex = Regex("""\d+""") // Match only digits
    val formattedPhoneNumber = phoneNumber.filter { it.isDigit() }

    if (!phoneDigitsRegex.matches(formattedPhoneNumber)) {
        return phoneNumber // Invalid phone number
    }

    // Check if the phone number starts with a country code
    val hasCountryCode = phoneNumber.startsWith("+") || phoneNumber.startsWith("00")

    if (!hasCountryCode) {
        // If the phone number doesn't have a country code, add the default country code
        val defaultCountryCode = "33" // Change to the appropriate country code

        // Remove leading '0' after the country code if it exists
        val formattedNumber = if (formattedPhoneNumber.startsWith("0")) {
            "+$defaultCountryCode" + formattedPhoneNumber.substring(1)
        } else {
            "+$defaultCountryCode$formattedPhoneNumber"
        }

        return formattedNumber
    }

    return "+$formattedPhoneNumber" // Phone number has a valid country code
}






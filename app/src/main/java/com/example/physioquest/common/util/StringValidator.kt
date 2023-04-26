package com.example.physioquest.common.util

import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASS_LENGTH = 6
private const val MIN_USERNAME_LENGTH = 6
private const val MAX_USERNAME_LENGTH = 20
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"


fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isAllowedEmail(): Boolean {
    return this.endsWith("@stud.fh-campuswien.ac.at", ignoreCase = true)
}

fun String.isValidUsername(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_USERNAME_LENGTH &&
            this.length <= MAX_USERNAME_LENGTH
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}
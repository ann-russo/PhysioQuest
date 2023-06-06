package com.example.physioquest.common.util

import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASS_LENGTH = 8
private const val MIN_USERNAME_LENGTH = 6
private const val MAX_USERNAME_LENGTH = 20
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"


fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isAllowedEmail(): Boolean {
    return this.endsWith("@stud.fh-campuswien.ac.at", ignoreCase = true)
}

fun String.isValidUsername(): Boolean {
    return this.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH
}

fun String.isValidPassword(): Boolean {
    return this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}
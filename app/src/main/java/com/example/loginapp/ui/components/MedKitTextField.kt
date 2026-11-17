package com.example.loginapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.loginapp.ui.theme.FieldShape
import com.example.loginapp.ui.theme.MedKitEmergencyRed
import com.example.loginapp.ui.theme.MedKitInkFaint
import com.example.loginapp.ui.theme.MedKitInkSoft
import com.example.loginapp.ui.theme.MedKitLine
import com.example.loginapp.ui.theme.MedKitPrimary
import com.example.loginapp.ui.theme.MedKitSurfaceSecondary

/**
 * MedKit-branded OutlinedTextField.
 *
 * Design tokens applied:
 *  - Fill:          #F4F8F6 (MedKitSurfaceSecondary)
 *  - Border idle:   1.5dp #DCE5E0
 *  - Border focus:  1.5dp #0D5C4A (primary)
 *  - Border error:  1.5dp #C1372E (emergency red)
 *  - Corner radius: 12.dp
 *  - Inline error text shown below when [errorMessage] is non-null.
 */
@Composable
fun MedKitTextField(
    value:               String,
    onValueChange:       (String) -> Unit,
    label:               String,
    modifier:            Modifier              = Modifier,
    placeholder:         String?               = null,
    leadingIcon:         @Composable (() -> Unit)? = null,
    trailingIcon:        @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions:     KeyboardOptions       = KeyboardOptions.Default,
    keyboardActions:     KeyboardActions       = KeyboardActions.Default,
    singleLine:          Boolean               = true,
    enabled:             Boolean               = true,
    readOnly:            Boolean               = false,
    errorMessage:        String?               = null
) {
    OutlinedTextField(
        value               = value,
        onValueChange       = onValueChange,
        label               = { Text(label) },
        placeholder         = placeholder?.let { { Text(it, color = MedKitInkFaint) } },
        leadingIcon         = leadingIcon,
        trailingIcon        = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions     = keyboardOptions,
        keyboardActions     = keyboardActions,
        singleLine          = singleLine,
        enabled             = enabled,
        readOnly            = readOnly,
        isError             = errorMessage != null,
        supportingText      = errorMessage?.let { msg -> { Text(msg, color = MedKitEmergencyRed) } },
        shape               = FieldShape,
        colors              = OutlinedTextFieldDefaults.colors(
            // Fill
            unfocusedContainerColor = MedKitSurfaceSecondary,
            focusedContainerColor   = MedKitSurfaceSecondary,
            errorContainerColor     = MedKitSurfaceSecondary,
            disabledContainerColor  = MedKitSurfaceSecondary,
            // Border
            unfocusedBorderColor    = MedKitLine,
            focusedBorderColor      = MedKitPrimary,
            errorBorderColor        = MedKitEmergencyRed,
            // Label
            unfocusedLabelColor     = MedKitInkSoft,
            focusedLabelColor       = MedKitPrimary,
            errorLabelColor         = MedKitEmergencyRed,
            // Cursor
            cursorColor             = MedKitPrimary,
            errorCursorColor        = MedKitEmergencyRed,
            // Text
            unfocusedTextColor      = MedKitInkSoft,
            focusedTextColor        = com.example.loginapp.ui.theme.MedKitInkMain,
        ),
        modifier = modifier
    )
}

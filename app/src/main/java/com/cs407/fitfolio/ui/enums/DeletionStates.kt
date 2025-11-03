package com.cs407.fitfolio.ui.enums

import androidx.annotation.StringRes
import com.cs407.fitfolio.R

// Enum class representing the different deletion states
enum class DeletionStates(@param:StringRes val title: Int) {
    Active(title = R.string.active_deletion_state),
    Inactive(title = R.string.inactive_deletion_state),
    Confirmed(title = R.string.confirmed_deletion_state)
}
package com.cs407.fitfolio.ui.enums

import androidx.annotation.StringRes
import com.cs407.fitfolio.R

// Enum class representing the different deletion states
// Active -- Items/Outfits can be clicked to be added to the deletion candidate list
// Inactive -- Items/Outfits cannot be added to the deletion candidate list
// Confirmed -- Items/Outfits are locked in as deletion candidates
enum class DeletionStates(@param:StringRes val title: Int) {
    Active(title = R.string.active_deletion_state),
    Inactive(title = R.string.inactive_deletion_state),
    Confirmed(title = R.string.confirmed_deletion_state)
}
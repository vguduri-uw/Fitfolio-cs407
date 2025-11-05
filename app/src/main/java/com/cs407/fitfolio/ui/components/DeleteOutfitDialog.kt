package com.cs407.fitfolio.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
fun DeleteOutfitDialog(outfitsViewModel: OutfitsViewModel) {
    // Observe the current UI state from the ViewModel
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = {
            outfitsViewModel.clearDeletionCandidates()
            outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name)
        },
        title = {
            Text("Are you sure you want to delete these outfit(s)?")
        },
        dismissButton = {
            Button(onClick = {
                outfitsViewModel.clearDeletionCandidates()
                outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name)
            }) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            Button(onClick = {
                outfitsViewModel.clearDeletionCandidates()
                outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name)
                outfitsViewModel.delete(outfitsState.deletionCandidates)
            }) {
                Text(text = "Delete")
            }
        }
    )
}
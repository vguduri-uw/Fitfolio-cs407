package com.cs407.fitfolio.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.viewModels.ClosetViewModel

@Composable
fun DeleteItemDialog(closetViewModel: ClosetViewModel) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = {
            closetViewModel.clearDeletionCandidates()
            closetViewModel.toggleDeleteState(DeletionStates.Inactive.name)
        },
        title = {
            Text("Are you sure you want to delete these item(s)?")
        },
        text = {
            Text("Deleting these items will delete all of the outfits they are featured in.")
        },
        dismissButton = {
            Button(onClick = {
                closetViewModel.clearDeletionCandidates()
                closetViewModel.toggleDeleteState(DeletionStates.Inactive.name)
            }) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            Button(onClick = {
                closetViewModel.deleteItem(closetState.deletionCandidates)
                closetViewModel.clearDeletionCandidates()
                closetViewModel.toggleDeleteState(DeletionStates.Inactive.name)
            }) {
                Text(text = "Delete")
            }
        }
    )
}
package com.cs407.fitfolio.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.enums.DeletionStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.io.Serializable

// Data class representing a single item of clothing
//data class ItemEntry(
//    var itemName: String, // the name of the item
//    var itemType: String, // the type of the item
//    var itemDescription: String, // the description of the item
//    var itemTags: List<String>, // the tags corresponding to the item
//    var isFavorite: Boolean, // whether or not the item is in favorites
//    var isDeletionCandidate: Boolean, // whether or not the item is selected to be deleted
//    var itemPhoto: Int, // TODO: figure out what type photo will be... if it is a drawable, it is Int (but itll likely be in room)
//    var outfitList: List<OutfitEntry>, // the outfits that the item is featured in
//    val itemId : String, // the unique id of the item
//) : Serializable

// Data class representing the entire closet of clothing
// TODO: update all of these methods to deal with database
data class ClosetState(
    val items: List<ItemEntry> = emptyList(), // all items in the closet
    val filteredItems : List<ItemEntry> = emptyList(), // the items currently rendered on the screen
    // TODO: should itemTypes be an enum?? also, confirm default item types
    val itemTypes: List<String> = listOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    ), // item types
    val activeItemType: String = "All", // item type that is currently rendered on the screen
    val isFavoritesActive: Boolean = false, // whether or not the favorites filter is active
    val isSearchActive: Boolean = false, // whether or not a search query filter is active
    val searchQuery: String = "",
    // TODO: do better here...
    val tags: List<String> = listOf("Spring", "Summer", "Fall", "Winter", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Black", "Brown", "Casual",), // all tags for closet items
    val activeTags: List<String> = emptyList(), // the tags currently rendered on the screen
    val deletionCandidates: List<ItemEntry> = emptyList(), // the items that can be potentially deleted
    val isDeleteActive: String = DeletionStates.Inactive.name, // the status of the deletion process
    val itemToShow: String = "" // itemId of the item to be shown
)

// ViewModel representing the state of the closet
class ClosetViewModel : ViewModel() {
    // Backing property (private) for state
    private val _closetState = MutableStateFlow(ClosetState())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closetState = _closetState.asStateFlow()

    // ITEM FUNCTIONS //

    // Adds an item to the closet to be used in add screen
    fun addItem(
        name: String, type: String, description: String, tags: List<String>,
        isFavorites: Boolean, photo: Int, outfitList: List<OutfitEntry>
    ) {
        val newItem = ItemEntry(
            itemName = name,
            itemType = type,
            itemDescription = description,
            itemTags = tags,
            isFavorite = isFavorites,
            isDeletionCandidate = false,
            itemPhoto = photo,
            itemId = UUID.randomUUID().toString(),
            outfitList = outfitList // emptyList() // TODO: turn back to emptyList() after testing
        )

        val updatedItems = _closetState.value.items + newItem
        _closetState.value = _closetState.value.copy(
            items = updatedItems
        )
    }

    // Deletes specified items from the closet
    // TODO: implement Room database (then I don't think the outfitsViewModel needs to be passed in)
    fun deleteItem(items: List<ItemEntry>, outfitsViewModel: OutfitsViewModel) {
        // delete the selected items
        for (item in items) {
            val updatedItems = _closetState.value.items - item
            _closetState.value = _closetState.value.copy(
                items = updatedItems
            )

            // delete outfits associated with the items
            var outfitsList: List<OutfitEntry> = emptyList()
            for (outfit in item.outfitList) {
                outfitsList += outfit
            }
            outfitsViewModel.delete(outfitsList)
        }
    }

    // Setters for item properties to be used in the add screen and item modal
    // TODO: gonna have to make a copy or something because the item for the closet state needs to be updated
    // also this is prob the same stale issue for all of these
    fun editItemName(item: ItemEntry, name: String) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) it.copy(itemName = name)
            else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun editItemType(item: ItemEntry, type: String) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) it.copy(itemType = type)
            else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun editItemDescription(item: ItemEntry, description: String) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) it.copy(itemDescription = description)
            else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun editItemTags(item: ItemEntry, tag: String, isRemoving: Boolean) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) {
                if (isRemoving) it.copy(itemTags = item.itemTags - tag)
                else it.copy(itemTags = item.itemTags + tag)
            } else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun toggleFavoritesProperty(item: ItemEntry) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) it.copy(isFavorite = !it.isFavorite)
            else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun editItemPhoto(item: ItemEntry, photo: Int) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) it.copy(itemPhoto = photo)
            else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }
    fun editOutfitList(item: ItemEntry, outfit: OutfitEntry, isRemoving: Boolean) {
        val updatedItems = _closetState.value.items.map {
            if (it.itemId == item.itemId) {
                if (isRemoving) it.copy(outfitList = item.outfitList - outfit)
                else it.copy(outfitList = item.outfitList + outfit)
            } else it
        }

        _closetState.value = _closetState.value.copy(items = updatedItems)
    }

    // CLOSET FUNCTIONS //

    // TODO: add CircularProgressIndicator? when calling this (in closet screen), do in coroutine
    fun applyFilters() {
        var updatedFilteredItems = _closetState.value.items.filter { item ->
            var passesAllFilters = true

            // Filter through type
            if (_closetState.value.activeItemType != "All") {
                if (item.itemType != _closetState.value.activeItemType) passesAllFilters = false
            }

            // Filter through favorites
            if (_closetState.value.isFavoritesActive) {
                if (!item.isFavorite) passesAllFilters = false
            }

            // Filter with search query
            if (_closetState.value.isSearchActive) {
                val query = _closetState.value.searchQuery.lowercase()
                if (!(item.itemName.lowercase().contains(query) ||
                            item.itemDescription.lowercase().contains(query))) {
                    passesAllFilters = false
                }
            }

            // Filter through active tags
            // TODO: decide if this should be inclusive (1 matching tag means its valid, how it currently is rn), or if it must match all tags
            if (_closetState.value.activeTags.isNotEmpty()) {
                val hasMatchingTag = item.itemTags.any { it in _closetState.value.activeTags }
                if (!hasMatchingTag) {
                    passesAllFilters = false
                }
            }

            passesAllFilters
        }

        // Update filteredItems
        _closetState.value = _closetState.value.copy(
            filteredItems = updatedFilteredItems
        )
    }

    // Adds a new tag option the user can choose from
    // TODO: if it already exists, then let them know
    fun addTag(newTag: String) {
        if (newTag !in _closetState.value.tags) {
            val updatedTags = _closetState.value.tags + newTag
            _closetState.value = _closetState.value.copy(
                tags = updatedTags
            )
        }
    }

    // Removes a tag from the list of selectable tags
    fun deleteTag(tag: String) {
        val updatedTags = _closetState.value.tags - tag
        _closetState.value = _closetState.value.copy(
            tags = updatedTags
        )

        // Remove tag from any item containing it
        val itemsWithTag = _closetState.value.items.filter { tag in it.itemTags }
        for (itemWithTag in itemsWithTag) {
            editItemTags(itemWithTag, tag, true)
        }
    }

    // Retrieves an ItemEntry based on it's itemId
    // TODO: make sure wherever we call this catches the exception and displays error accordingly
    fun itemFlow(itemId: String): Flow<ItemEntry?> =
        closetState.map { state -> state.items.find { it.itemId == itemId } }

    // Adds an item type to the itemTypes list (from the item modal)
    fun addItemType(itemType: String) {
        if (itemType !in _closetState.value.itemTypes) {
            val updatedItemTypes = _closetState.value.itemTypes + itemType
            _closetState.value = _closetState.value.copy(
                itemTypes = updatedItemTypes
            )
        }
    }

    // Removes the specified item type from the itemTypes list (from the item modal)
    // TODO: warn the user that deleting the type will delete all clothes of that type (in the modal)
    fun deleteItemType(itemType: String) {
        val updatedItemTypes = _closetState.value.itemTypes - itemType
        _closetState.value = _closetState.value.copy(
            itemTypes = updatedItemTypes
        )
    }

    // Updates the active item type
    fun updateActiveItemType(itemType: String) {
        _closetState.value = _closetState.value.copy(
            activeItemType = itemType
        )
        applyFilters()
    }

    // Updates whether the favorites filter is activated or not
    fun toggleFavoritesState() {
        val isToggled = _closetState.value.isFavoritesActive
        _closetState.value = _closetState.value.copy(
            isFavoritesActive = !isToggled
        )
        applyFilters()
    }

    // Shuffles the order of the filtered items list
    fun shuffleItems() {
        val shuffledItems = _closetState.value.filteredItems.shuffled()
        _closetState.value = _closetState.value.copy(
            filteredItems = shuffledItems
        )
    }

    // Updates whether the search filter is activated or not
    fun toggleSearchState(isActive: Boolean) {
        _closetState.value = _closetState.value.copy(
            isSearchActive = isActive
        )
    }

    // Updates the search query
    fun updateSearchQuery(query: String) {
        _closetState.value = _closetState.value.copy(
            searchQuery = query
        )
    }

    // Update the item to show in item modal
    fun updateItemToShow(itemId: String) {
        _closetState.value = _closetState.value.copy(
            itemToShow = itemId
        )
    }

    // Adds a tag to the active tags list
    fun addToActiveTags(tag: String) {
        val updatedTags = _closetState.value.activeTags + tag
        _closetState.value = _closetState.value.copy(
            activeTags = updatedTags
        )
        applyFilters()
    }

    // Removes a tag from the active tags list
    fun removeFromActiveTags(tag: String) {
        val updatedTags = _closetState.value.activeTags - tag
        _closetState.value = _closetState.value.copy(
            activeTags = updatedTags
        )
        applyFilters()
    }

    // Sets the deletion candidates list
    fun setDeletionCandidates(item: ItemEntry) {
        _closetState.value = _closetState.value.copy(
            // Update item isDeletionCandidate property
            items = _closetState.value.items.map {
                if (it.itemId == item.itemId) it.copy(isDeletionCandidate = true) else it
            },

            // Update list
            deletionCandidates = _closetState.value.deletionCandidates + item.copy(isDeletionCandidate = true)
        )
    }

    // Removes a candidate from the deletion list
    fun removeDeletionCandidate(item: ItemEntry) {
        // Update item isDeletionCandidate property
        _closetState.value = _closetState.value.copy(
            items = _closetState.value.items.map {
                if (it.itemId == item.itemId) it.copy(isDeletionCandidate = false) else it
            },

            // Update list
            deletionCandidates = _closetState.value.deletionCandidates.filterNot { it.itemId == item.itemId }
        )
    }

    // Clears the deletion candidates list
    fun clearDeletionCandidates() {
        // Update item isDeletionCandidate property
        val updatedItems = _closetState.value.items.map { it.copy(isDeletionCandidate = false) }

        // Update list
        _closetState.value = _closetState.value.copy(
            items = updatedItems,
            deletionCandidates = emptyList()
        )
    }

    // Toggle the deletion state
    // See DeletionStates.kt enum class
    fun toggleDeleteState(status: String) {
        _closetState.value = _closetState.value.copy(
            isDeleteActive = status
        )
    }

    // Clears any applied filters and resets properties
    fun clearFilters() {
        clearDeletionCandidates()

        _closetState.value = _closetState.value.copy(
            filteredItems = _closetState.value.items,
            activeItemType = "All",
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            isDeleteActive = DeletionStates.Inactive.name,
        )
    }
}

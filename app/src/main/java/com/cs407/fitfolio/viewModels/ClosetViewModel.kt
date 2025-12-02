package com.cs407.fitfolio.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.enums.DeletionStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemOutfitRelation
import com.cs407.fitfolio.data.ItemTag
import com.cs407.fitfolio.data.ItemType
import com.cs407.fitfolio.enums.DefaultItemTags
import com.cs407.fitfolio.enums.DefaultItemTypes
import kotlinx.coroutines.launch

// Data class representing the entire closet of clothing
data class ClosetState(
    val items: List<ItemEntry> = emptyList(), // all items in the closet
    val filteredItems : List<ItemEntry> = emptyList(), // the items currently rendered on the screen
    val itemTypes: List<String> = emptyList(), // item types
    val activeItemType: String = DefaultItemTypes.ALL.typeName, // item type that is currently rendered on the screen
    val isFavoritesActive: Boolean = false, // whether or not the favorites filter is active
    val isSearchActive: Boolean = false, // whether or not a search query filter is active
    val searchQuery: String = "",
    val tags: List<String> = emptyList(), // all tags for closet items
    val activeTags: List<String> = emptyList(), // the tags currently rendered on the screen
    val deletionCandidates: List<ItemEntry> = emptyList(), // the items that can be potentially deleted
    val isDeleteActive: String = DeletionStates.Inactive.name, // the status of the deletion process
    val itemToShow: Int = -1 // itemId of the item to be shown
)

// ViewModel representing the state of the closet
class ClosetViewModel(
    private val db: FitfolioDatabase,
    private val userId: Int
) : ViewModel() {
    // Backing property (private) for state
    private val _closetState = MutableStateFlow(ClosetState())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closetState = _closetState.asStateFlow()

    // Initialize closet state items and filtered items with data from db
    init {
        viewModelScope.launch {
            var itemTypes = db.itemDao().getAllItemTypes().map { it.itemType }
            var tags = db.itemDao().getAllItemTags().map { it.itemTag }
            if (itemTypes.isEmpty()) {
                // Insert defaults item types into DB
                DefaultItemTypes.entries.forEach { type ->
                    db.itemDao().insertItemType(ItemType(itemType = type.typeName))
                }

                itemTypes = DefaultItemTypes.entries.map { it.typeName }
            }
            if (tags.isEmpty()) {
                // Insert defaults tags into DB
                DefaultItemTags.entries.forEach { tag ->
                    db.itemDao().insertItemTag(ItemTag(itemTag = tag.tagName))
                }

                tags = DefaultItemTags.entries.map { it.tagName }
            }


            val items = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(
                items = items,
                filteredItems = items,
                itemTypes = itemTypes,
                tags = tags
            )
        }
    }

    // ITEM FUNCTIONS //

    // Adds an item to the closet to be used in add screen
    fun addItem(
        name: String, type: String, description: String, tags: List<String>,
        isFavorites: Boolean, photoUri: String
    ) {
        val newItem = ItemEntry(
            itemId = 0,
            itemName = name,
            itemType = type,
            itemDescription = description,
            itemTags = tags,
            isFavorite = isFavorites,
            isDeletionCandidate = false,
            itemPhotoUri = photoUri,
        )

        viewModelScope.launch {
            // Insert item into database
            db.itemDao().upsertItem(newItem, userId)

            val items = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(
                items = items,
                filteredItems = items)
        }
    }

    // Deletes specified items from the closet
    fun deleteItem(items: List<ItemEntry>) {
        viewModelScope.launch {
            // Remove duplicate outfits associated with the items
            val outfitIds = items.flatMap { item ->
                db.itemDao().getOutfitsByItemId(item.itemId).map { it.outfitId }
            }.toSet()

            // Delete associated outfits
            db.deleteDao().deleteOutfits(outfitIds.toList())

            // Delete the items
            db.deleteDao().deleteItems(items.map { it.itemId })

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }

    // Setters for item properties to be used in the add screen and item modal
    fun editItemName(item: ItemEntry, name: String) {
        viewModelScope.launch {
            // Update database
            val updatedItem = item.copy(itemName = name)
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    fun editItemType(item: ItemEntry, type: String) {
        viewModelScope.launch {
            // Update database
            val updatedItem = item.copy(itemType = type)
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    fun editItemDescription(item: ItemEntry, description: String) {
        viewModelScope.launch {
            // Update database
            val updatedItem = item.copy(itemDescription = description)
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    fun editItemTags(item: ItemEntry, tag: String, isRemoving: Boolean) {
        viewModelScope.launch {
            val updatedItem =
                if (isRemoving) item.copy(itemTags = item.itemTags - tag)
                else item.copy(itemTags = item.itemTags + tag)

            // Update database
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    fun toggleFavoritesProperty(item: ItemEntry) {
        viewModelScope.launch {
            val updatedItem = item.copy(isFavorite = !item.isFavorite)
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    fun editItemPhoto(item: ItemEntry, photo: String) {
        viewModelScope.launch {
            // Update database
            val updatedItem = item.copy(itemPhotoUri = photo)
            db.itemDao().upsert(updatedItem)

            val updatedItems = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = updatedItems)
        }
    }
    suspend fun getOutfitsList(itemId: Int): List<OutfitEntry> {
        return db.itemDao().getOutfitsByItemId(itemId)
    }

    // TODO: remove after testing
    fun addItemWithOutfitsTest(
        name: String,
        type: String,
        description: String,
        tags: List<String>,
        isFavorites: Boolean,
        photoUri: String,
        outfitList: List<OutfitEntry>
    ) {
        val newItem = ItemEntry(
            itemId = 0,
            itemName = name,
            itemType = type,
            itemDescription = description,
            itemTags = tags,
            isFavorite = isFavorites,
            isDeletionCandidate = false,
            itemPhotoUri = photoUri,
        )

        viewModelScope.launch {
            // Insert item
            val itemId = db.itemDao().upsertItem(newItem, userId)

            // Insert relations between the item and each outfit
            outfitList.forEach { outfit ->
                db.outfitDao().insertRelation(
                    ItemOutfitRelation(itemId, outfit.outfitId)
                )
            }

            val items = db.userDao().getItemsByUserId(userId)
            _closetState.value = _closetState.value.copy(items = items)
        }
    }

    // CLOSET FUNCTIONS //

    // TODO: add CircularProgressIndicator? when calling this (in closet screen), do in coroutine
    fun applyFilters() {
        val updatedFilteredItems = _closetState.value.items.filter { item ->
            var passesAllFilters = true

            // Filter through type
            if (_closetState.value.activeItemType != DefaultItemTypes.ALL.typeName) {
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
    fun addTag(newTag: String) {
        viewModelScope.launch {
            // Prevent empty tags
            if (newTag.trim().isEmpty()) return@launch

            // Prevent duplicates in the DB
            val existingTags = db.itemDao().getAllItemTags().map { it.itemTag }
            if (newTag.trim() !in existingTags) {
                // Update database
                db.itemDao().insertItemTag(ItemTag(itemTag = newTag.trim()))
            } else {
                return@launch
            }

            val updatedTags = db.itemDao().getAllItemTags().map { it.itemTag }
            _closetState.value = _closetState.value.copy(
                tags = updatedTags
            )
        }
    }

    // Removes a tag from the list of selectable tags
    fun deleteTag(tag: String) {
        viewModelScope.launch {
            // Remove the tag from the tag table
            db.deleteDao().deleteItemTag(tag)

            // Remove the tag from any items containing it
            val itemsWithTag = _closetState.value.items.filter { tag in it.itemTags }
            for (item in itemsWithTag) {
                val updatedItem = item.copy(itemTags = item.itemTags - tag)
                db.itemDao().upsert(updatedItem)
            }

            val updatedItems = db.userDao().getItemsByUserId(userId)
            val updatedTags = db.itemDao().getAllItemTags().map { it.itemTag }
            _closetState.value = _closetState.value.copy(
                items = updatedItems,
                filteredItems = updatedItems,
                tags = updatedTags
            )
        }
    }

    // Adds an item type to the itemTypes list (from the item modal)
    fun addItemType(itemType: String) {
        viewModelScope.launch {
            // Prevent empty types
            if (itemType.trim().isEmpty()) return@launch

            // Prevent duplicates
            val existingTypes = db.itemDao().getAllItemTypes().map { it.itemType }
            if (itemType.trim() !in existingTypes) {
                db.itemDao().insertItemType(ItemType(itemType = itemType.trim()))
            } else {
                return@launch
            }

            val updatedTypes = db.itemDao().getAllItemTypes().map { it.itemType }
            _closetState.value = _closetState.value.copy(
                itemTypes = updatedTypes
            )
        }
    }

    // Removes the specified item type from the itemTypes list (from the item modal)
    fun deleteItemType(itemType: String) {
        viewModelScope.launch {
            // Prevent deleting the default "All" type
            if (itemType == DefaultItemTypes.ALL.typeName) return@launch

            // Delete the type from DB
            db.deleteDao().deleteItemType(itemType)

            // Update items with this type to default "All" type
            val itemsWithType = _closetState.value.items.filter { it.itemType == itemType }
            for (item in itemsWithType) {
                val updatedItem = item.copy(itemType = DefaultItemTypes.ALL.typeName)
                db.itemDao().upsert(updatedItem)
            }

            val updatedItemTypes = db.itemDao().getAllItemTypes().map { it.itemType }
            val updatedItems = db.userDao().getItemsByUserId(userId)

            _closetState.value = _closetState.value.copy(
                itemTypes = updatedItemTypes,
                items = updatedItems,
                filteredItems = updatedItems
            )
        }
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
    fun updateItemToShow(itemId: Int) {
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
            activeItemType = DefaultItemTypes.ALL.typeName,
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            isDeleteActive = DeletionStates.Inactive.name,
        )
    }
}

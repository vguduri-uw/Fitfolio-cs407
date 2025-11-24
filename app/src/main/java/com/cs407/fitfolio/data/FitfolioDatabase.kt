package com.cs407.fitfolio.data

import android.R
import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Upsert
import com.cs407.fitfolio.enums.DeletionStates
import java.io.Serializable

@Entity(
    indices = [Index(
        value = ["userUID"], unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userUID: String,
    val username: String
)

// Data class representing a single item of clothing
@Entity
data class ItemEntry(
    @PrimaryKey(autoGenerate = true) val itemId : Int, // the unique id of the item
    var itemName: String, // the name of the item
    var itemType: String, // the type of the item
    var itemDescription: String, // the description of the item
    var itemTags: List<String>, // the tags corresponding to the item
    var isFavorite: Boolean, // whether or not the item is in favorites
    var isDeletionCandidate: Boolean, // whether or not the item is selected to be deleted
    var itemPhoto: Int, // TODO: figure out what type photo will be... if it is a drawable, it is Int (but itll likely be in room)
    var outfitList: List<OutfitEntry>, // the outfits that the item is featured in
)

@Entity
// data class representing a single saved outfit (a look made of multiple clothing items)
data class OutfitEntry(
    @PrimaryKey(autoGenerate = true) val outfitId: Int, // the unique id of the outfit
    var outfitName: String,            // the name of the outfit
    var outfitDescription: String,     // the description of the outfit
    var outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    var isFavorite: Boolean,           // whether or not the item is in favorites
    var isDeletionCandidate: Boolean,  // whether or not the item is selected to be deleted
    var outfitPhoto: Int,              // todo: figure out what type...drawable? int?
    var itemList: List<ItemEntry>,     // all the items featured in the outfit
)

@Entity(
    tableName = "user_item_relation",
    primaryKeys = ["userId", "itemId"],
    foreignKeys = [
        ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ItemEntry::class,
        parentColumns = ["itemId"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserItemRelation(
    val userId: Int,
    val itemId: Int
)

@Entity(
    tableName = "user_outfit_relation",
    primaryKeys = ["userId", "outfitId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OutfitEntry::class,
            parentColumns = ["outfitId"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class UserOutfitRelation(
    val userId: Int,
    val outfitId: Int
)

@Entity(
    tableName = "item_outfit_relation",
    primaryKeys = ["itemId", "outfitId"],
    foreignKeys = [
        ForeignKey(
            entity = ItemEntry::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OutfitEntry::class,
            parentColumns = ["outfitId"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class ItemOutfitRelation(
    val itemId: Int,
    val outfitId: Int
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE userUID = :uid")
    suspend fun getByUID(uid: String): User?

    @Insert(entity = User::class)
    suspend fun insert(user: User)

    @Query(
        """SELECT * FROM User, ItemEntry, user_item_relation
              WHERE User.userId = :id
                AND user_item_relation.userId = User.userId
                AND ItemEntry.itemId = user_item_relation.itemId 
              ORDER BY ItemEntry.itemId DESC"""
    )
    suspend fun getItemsByUserId(id: Int): List<ItemEntry>

    @Query(
        """SELECT * FROM User, OutfitEntry, user_outfit_relation
              WHERE User.userId = :id
                AND user_outfit_relation.userId = User.userId
                AND OutfitEntry.outfitId = user_outfit_relation.outfitId 
              ORDER BY OutfitEntry.outfitId DESC"""
    )
    suspend fun getOutfitsByUserId(id: Int): List<OutfitEntry>
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM user_item_relation WHERE itemId = :id")
    suspend fun getById(id: Int): ItemEntry

    @Query("SELECT itemId FROM user_item_relation WHERE rowid = :rowId")
    suspend fun getByRowId(rowId: Long): Int

    @Query(
        """SELECT * FROM ItemEntry, OutfitEntry, item_outfit_relation
              WHERE ItemEntry.itemId = :id
                AND item_outfit_relation.itemId = ItemEntry.itemId
                AND OutfitEntry.outfitId = item_outfit_relation.outfitId 
              ORDER BY OutfitEntry.outfitId DESC"""
    )
    suspend fun getOutfitsByItemId(id: Int): List<OutfitEntry>

    @Upsert(entity = ItemEntry::class)
    suspend fun upsert(item: ItemEntry): Long

    @Insert
    suspend fun insertRelation(userAndItem: UserItemRelation)

    @Transaction
    suspend fun upsertItem(item: ItemEntry, userId: Int): Int {
        val rowId = upsert(item)
        val itemId = getByRowId(rowId)
        if (item.itemId == 0) {
            insertRelation(UserItemRelation(userId, itemId))
        }
        return itemId
    }
}

@Dao
interface OutfitDao {
    @Query("SELECT * FROM user_outfit_relation WHERE outfitId = :id")
    suspend fun getOutfitById(id: Int): OutfitEntry

    @Query("SELECT outfitId FROM user_outfit_relation WHERE rowid = :rowId")
    suspend fun getOutfitByRowId(rowId: Long): Int

    @Query(
        """SELECT * FROM OutfitEntry, ItemEntry, item_outfit_relation
              WHERE OutfitEntry.outfitId = :id
                AND item_outfit_relation.outfitId = OutfitEntry.outfitId
                AND ItemEntry.itemId = item_outfit_relation.itemId 
              ORDER BY ItemEntry.itemId DESC"""
    )
    suspend fun getItemsByOutfitId(id: Int): List<ItemEntry>

    @Upsert(entity = OutfitEntry::class)
    suspend fun upsert(outfit: OutfitEntry): Long

    @Transaction
    suspend fun upsertOutfit(outfit: OutfitEntry, userId: Int): Int {
        val rowId = upsert(outfit)
        val outfitId = getOutfitByRowId(rowId)
        if (outfit.outfitId == 0) {
            insertRelation(UserOutfitRelation(userId, outfitId))
        }
        return outfitId
    }

    @Insert
    suspend fun insertRelation(userAndOutfit: UserOutfitRelation)

    @Query("SELECT * FROM item_outfit_relation WHERE outfitId = :id")
    suspend fun getItemById(id: Int): ItemEntry

    @Query("SELECT itemId FROM item_outfit_relation WHERE rowid = :rowId")
    suspend fun getItemByRowId(rowId: Long): Int

    @Upsert(entity = ItemEntry::class)
    suspend fun upsert(item: ItemEntry): Long

    @Insert
    suspend fun insertRelation(itemAndOutfit: ItemOutfitRelation)

    @Transaction
    suspend fun upsertItem(item: ItemEntry, outfitId: Int): Int {
        val rowId = upsert(item)
        val itemId = getItemByRowId(rowId)
        if (item.itemId == 0) {
            insertRelation(ItemOutfitRelation(itemId, outfitId))
        }
        return itemId
    }
}

@Dao
interface DeleteDao {

}

@Database(entities = [User::class], version = 1)
abstract class FitfolioDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: FitfolioDatabase? = null

        fun getDatabase(context: Context): FitfolioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitfolioDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.cs407.fitfolio.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import com.cs407.fitfolio.enums.CarouselTypes

// User table
@Entity(
    indices = [Index(
        value = ["userUID"], unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userUID: String,
    val username: String,
    val email: String,
    val avatarUri: String = "",
    val profilePictureUri: String = "",
    val newUser: Boolean
)

// Item table
@Entity
data class ItemEntry(
    @PrimaryKey(autoGenerate = true) val itemId : Int, // the unique id of the item
    var itemName: String,               // the name of the item
    var itemType: String,               // the type of the item
    var carouselType: CarouselTypes,
    var itemDescription: String,        // the description of the item
    var itemTags: List<String>,         // the tags corresponding to the item
    var isFavorite: Boolean,            // whether or not the item is in favorites
    var isDeletionCandidate: Boolean,   // whether or not the item is selected to be deleted
    var itemPhotoUri: String           // the item's photo
)

// Outfit table
@Entity
data class OutfitEntry(
    @PrimaryKey(autoGenerate = true) val outfitId: Int, // the unique id of the outfit
    var outfitName: String,            // the name of the outfit
    var outfitDescription: String,     // the description of the outfit
    var outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    var isFavorite: Boolean,           // whether or not the item is in favorites
    var isDeletionCandidate: Boolean,  // whether or not the item is selected to be deleted
    var outfitPhotoUri: String        // the outfit's photo
)

// Item tags table
@Entity
data class ItemTag(
    @PrimaryKey(autoGenerate = true) val tagId: Int = 0,
    val itemTag: String
)

// Item types table
@Entity
data class ItemType(
    @PrimaryKey(autoGenerate = true) val typeId: Int = 0,
    val itemType: String
)

// Outfits tags table
@Entity
data class OutfitTag(
    @PrimaryKey(autoGenerate = true) val tagId: Int = 0,
    val outfitTag: String
)
@Entity(tableName = "blocked_combinations")
data class BlockedCombination(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accessoryId: Int?,
    val topwearId: Int?,
    val bottomwearId: Int?,
    val shoesId: Int?
)

// Converter for storing List<String> in Room
class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromCarouselType(type: CarouselTypes): String = type.name

    @TypeConverter
    fun toCarouselType(data: String): CarouselTypes = CarouselTypes.valueOf(data)
}

// User <--> Item relation
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

// User <--> Outfit relation
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

// Item <--> Outfit relation
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

// Outfit <--> Calendar relation
@Entity(
    tableName = "scheduled_outfit",
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntry::class,
            parentColumns = ["outfitId"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class ScheduledOutfit(
    @PrimaryKey(autoGenerate = true) val scheduleId: Int = 0,
    val outfitId: Int,
    val scheduledDate: Long
)

// Item Type <--> User relation
@Entity(
    tableName = "user_item_type_relation",
    primaryKeys = ["userId", "typeId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemType::class,
            parentColumns = ["typeId"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class UserItemTypeRelation(
    val userId: Int,
    val typeId: Int
)

// Item Tags <--> User relation
@Entity(
    tableName = "user_item_tag_relation",
    primaryKeys = ["userId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemTag::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class UserItemTagsRelation(
    val userId: Int,
    val tagId: Int
)

// Outfit Tags <--> User relation
@Entity(
    tableName = "user_outfits_tag_relation",
    primaryKeys = ["userId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OutfitTag::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class UserOutfitsTagsRelation(
    val userId: Int,
    val tagId: Int
)

// User queries
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE userUID = :uid")
    suspend fun getByUID(uid: String): User?

    @Insert(entity = User::class)
    suspend fun insert(user: User): Long

    @Query("UPDATE user SET username = :username, email = :email WHERE userId = :id")
    suspend fun updateUser(id: Int, username: String, email: String)

    @Query("UPDATE user SET newUser = :newUser WHERE userId = :id")
    suspend fun updateUserFlag(id: Int, newUser: Boolean)

    @Query("UPDATE user SET avatarUri = :avatarUri WHERE userId = :id")
    suspend fun updateAvatar(id: Int, avatarUri: String)

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

    @Query(
        """SELECT * FROM User, ItemType, user_item_type_relation
              WHERE User.userId = :id
                AND user_item_type_relation.userId = User.userId
                AND ItemType.typeId = user_item_type_relation.typeId
              ORDER BY ItemType.itemType ASC"""
    )
    suspend fun getItemsTypesByUserId(id: Int): List<ItemType>

    @Query(
        """SELECT * FROM User, ItemTag, user_item_tag_relation
              WHERE User.userId = :id
                AND user_item_tag_relation.userId = User.userId
                AND ItemTag.tagId = user_item_tag_relation.tagId
              ORDER BY ItemTag.itemTag ASC"""
    )
    suspend fun getItemsTagsByUserId(id: Int): List<ItemTag>

    @Query(
        """SELECT * FROM User, OutfitTag, user_outfits_tag_relation
              WHERE User.userId = :id
                AND user_outfits_tag_relation.userId = User.userId
                AND OutfitTag.tagId = user_outfits_tag_relation.tagId
              ORDER BY OutfitTag.outfitTag ASC"""
    )
    suspend fun getOutfitsTagsByUserId(id: Int): List<OutfitTag>

    //Veda for profile pic
    @Query("UPDATE user SET profilePictureUri = :profilePictureUri WHERE userId = :userId")
    suspend fun updateProfilePicture(userId: Int, profilePictureUri: String)
}

// Item queries
@Dao
interface ItemDao {
    @Query("SELECT * FROM ItemEntry WHERE itemId = :id")
    suspend fun getById(id: Int): ItemEntry

    @Query("SELECT itemId FROM ItemEntry WHERE rowid = :rowId")
    suspend fun getItemsByRowId(rowId: Long): Int

    @Query(
        """SELECT * FROM ItemEntry, OutfitEntry, item_outfit_relation
              WHERE ItemEntry.itemId = :id
                AND item_outfit_relation.itemId = ItemEntry.itemId
                AND OutfitEntry.outfitId = item_outfit_relation.outfitId 
              ORDER BY OutfitEntry.outfitId DESC"""
    )
    suspend fun getOutfitsByItemId(id: Int): List<OutfitEntry>

    @Query("SELECT typeId FROM ItemType WHERE rowid = :rowId")
    suspend fun getTypeByRowId(rowId: Long): Int

    @Upsert(entity = ItemType::class)
    suspend fun upsert(type: ItemType): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(userAndItemType: UserItemTypeRelation)

    @Transaction
    suspend fun upsertItemType(type: ItemType, userId: Int) {
        val rowId = upsert(type)
        val typeId = getTypeByRowId(rowId)
        if (type.typeId == 0) {
            insertRelation(UserItemTypeRelation(userId, typeId))
        }
    }

    @Query("SELECT tagId FROM ItemTag WHERE rowid = :rowId")
    suspend fun getTagByRowId(rowId: Long): Int

    @Upsert(entity = ItemTag::class)
    suspend fun upsert(tag: ItemTag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(userAndItemTag: UserItemTagsRelation)

    @Transaction
    suspend fun upsertItemTag(tag: ItemTag, userId: Int) {
        val rowId = upsert(tag)
        val tagId = getTagByRowId(rowId)
        if (tag.tagId == 0) {
            insertRelation(UserItemTagsRelation(userId, tagId))
        }
    }

    @Upsert(entity = ItemEntry::class)
    suspend fun upsert(item: ItemEntry): Long

    @Insert
    suspend fun insertRelation(userAndItem: UserItemRelation)

    @Transaction
    suspend fun upsertItem(item: ItemEntry, userId: Int): Int {
        val rowId = upsert(item)
        val itemId = getItemsByRowId(rowId)
        if (item.itemId == 0) {
            insertRelation(UserItemRelation(userId, itemId))
        }
        return itemId
    }
}

// Outfit queries
@Dao
interface OutfitDao {
    @Query("SELECT * FROM OutfitEntry WHERE outfitId = :id")
    suspend fun getOutfitById(id: Int): OutfitEntry

    @Query("SELECT outfitId FROM OutfitEntry WHERE rowid = :rowId")
    suspend fun getOutfitByRowId(rowId: Long): Int

    @Query("SELECT tagId FROM OutfitTag WHERE rowid = :rowId")
    suspend fun getTagByRowId(rowId: Long): Int

    @Query(
        """SELECT * FROM OutfitEntry, ItemEntry, item_outfit_relation
              WHERE OutfitEntry.outfitId = :id
                AND item_outfit_relation.outfitId = OutfitEntry.outfitId
                AND ItemEntry.itemId = item_outfit_relation.itemId 
              ORDER BY ItemEntry.itemId DESC"""
    )
    suspend fun getItemsByOutfitId(id: Int): List<ItemEntry>

    @Upsert(entity = OutfitTag::class)
    suspend fun upsert(tag: OutfitTag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(userAndOutfitTag: UserOutfitsTagsRelation)

    @Transaction
    suspend fun upsertOutfitTag(tag: OutfitTag, userId: Int) {
        val rowId = upsert(tag)
        val tagId = getTagByRowId(rowId)
        if (tag.tagId == 0) {
            insertRelation(UserOutfitsTagsRelation(userId, tagId))
        }
    }

    @Upsert(entity = OutfitEntry::class)
    suspend fun upsert(outfit: OutfitEntry): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(userAndOutfit: UserOutfitRelation)

    @Transaction
    suspend fun upsertOutfit(outfit: OutfitEntry, userId: Int): Int {
        val rowId = upsert(outfit)
        val outfitId = getOutfitByRowId(rowId)
        if (outfit.outfitId == 0) {
            insertRelation(UserOutfitRelation(userId, outfitId))
        }

        return outfitId
    }

    @Query("SELECT * FROM ItemEntry WHERE itemId = :id")
    suspend fun getById(id: Int): ItemEntry

    @Query("SELECT itemId FROM item_outfit_relation WHERE rowid = :rowId")
    suspend fun getItemByRowId(rowId: Long): Int

    @Upsert(entity = ItemEntry::class)
    suspend fun upsert(item: ItemEntry): Long

    @Insert
    suspend fun insertRelation(itemAndOutfit: ItemOutfitRelation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun scheduleOutfit(scheduledOutfit: ScheduledOutfit)

    @Query("""
        SELECT OutfitEntry.* FROM OutfitEntry
        INNER JOIN scheduled_outfit ON OutfitEntry.outfitId = scheduled_outfit.outfitId
        WHERE scheduled_outfit.scheduledDate = :date
    """)
    suspend fun getOutfitsForDate(date: Long): List<OutfitEntry>

    @Query("SELECT scheduledDate FROM scheduled_outfit")
    suspend fun getAllScheduledDates(): List<Long>

    @Query("SELECT scheduledDate FROM scheduled_outfit WHERE outfitId = :outfitId")
    suspend fun getDatesForOutfit(outfitId: Int): List<Long>
}

//remove combinations
@Dao
interface BlockedCombinationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCombination(combination: BlockedCombination)

    @Query("SELECT * FROM blocked_combinations")
    suspend fun getAllBlockedCombinations(): List<BlockedCombination>
}

// Delete queries
@Dao
interface DeleteDao {
    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUser(userId: Int)

    @Query(
        """SELECT ItemEntry.itemId FROM User, ItemEntry, user_item_relation
              WHERE User.userId = :userId
                AND user_item_relation.userId = User.userId
                AND ItemEntry.itemId = user_item_relation.itemId"""
    )
    suspend fun getAllItemIdsByUserId(userId: Int): List<Int>

    @Query(
        """SELECT OutfitEntry.outfitId FROM User, OutfitEntry, user_outfit_relation
              WHERE User.userId = :userId
                AND user_outfit_relation.userId = User.userId
                AND OutfitEntry.outfitId = user_outfit_relation.outfitId"""
    )
    suspend fun getAllOutfitIdsByUserId(userId: Int): List<Int>

    @Query("DELETE FROM ItemEntry WHERE itemId IN (:itemsIds)")
    suspend fun deleteItems(itemsIds: List<Int>)

    @Query("DELETE FROM ItemTag WHERE itemTag = :tag")
    suspend fun deleteItemTag(tag: String)

    @Query("DELETE FROM ItemType WHERE itemType = :type")
    suspend fun deleteItemType(type: String)

    @Delete
    suspend fun deleteRelation(itemAndOutfit: ItemOutfitRelation)

    @Query("DELETE FROM OutfitEntry WHERE outfitId IN (:outfitsIds)")
    suspend fun deleteOutfits(outfitsIds: List<Int>)

    @Query("DELETE FROM OutfitTag WHERE outfitTag = :tag")
    suspend fun deleteOutfitTag(tag: String)

    @Query("DELETE FROM scheduled_outfit WHERE scheduledDate = :date AND outfitId = :outfitId")
    suspend fun removeOutfitFromDate(date: Long, outfitId: Int)

    @Query("DELETE FROM scheduled_outfit WHERE scheduledDate = :date")
    suspend fun removeScheduleForDate(date: Long)

    @Transaction
    suspend fun delete(userId: Int) {
        deleteItems(getAllItemIdsByUserId(userId))
        deleteOutfits(getAllOutfitIdsByUserId(userId))
        deleteUser(userId)
    }
}

// Room database for the app
@Database(
    entities = [
        User::class,
        ItemEntry::class,
        OutfitEntry::class,
        ScheduledOutfit::class,
        UserItemRelation::class,
        UserOutfitRelation::class,
        ItemOutfitRelation::class,
        ItemTag::class,
        ItemType::class,
        OutfitTag::class,
        UserItemTypeRelation::class,
        UserItemTagsRelation::class,
        UserOutfitsTagsRelation::class,
        BlockedCombination::class
    ],
    version = 11
)
@TypeConverters(Converters::class)
abstract class FitfolioDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun outfitDao(): OutfitDao
    abstract fun deleteDao(): DeleteDao

    abstract fun blockedCombinationDao(): BlockedCombinationDao

    companion object {
        @Volatile
        private var INSTANCE: FitfolioDatabase? = null

        fun getDatabase(context: Context): FitfolioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitfolioDatabase::class.java,
                    "fitfolio_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
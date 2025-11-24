package com.cs407.fitfolio.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
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
    @PrimaryKey(autoGenerate = true) val itemId : String, // the unique id of the item
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
    @PrimaryKey(autoGenerate = true) val outfitId: String, // the unique id of the outfit
    var outfitName: String,            // the name of the outfit
    var outfitDescription: String,     // the description of the outfit
    var outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    var isFavorite: Boolean,           // whether or not the item is in favorites
    var isDeletionCandidate: Boolean,  // whether or not the item is selected to be deleted
    var outfitPhoto: Int,              // todo: figure out what type...drawable? int?
    var itemList: List<ItemEntry>,     // all the items featured in the outfit
)

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE userUID = :uid")
    suspend fun getByUID(uid: String): User?

    @Insert(entity = User::class)
    suspend fun insert(user: User)

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
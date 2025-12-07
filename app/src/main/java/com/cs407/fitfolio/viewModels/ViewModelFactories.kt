package com.cs407.fitfolio.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs407.fitfolio.data.FitfolioDatabase

class ClosetViewModelFactory(
    private val db: FitfolioDatabase,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClosetViewModel(db, userId) as T
    }
}

class OutfitsViewModelFactory(
    private val db: FitfolioDatabase,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OutfitsViewModel(db, userId) as T
    }
}

class CarouselViewModelFactory(
    private val db: FitfolioDatabase,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CarouselViewModel(db, userViewModel) as T
    }
}
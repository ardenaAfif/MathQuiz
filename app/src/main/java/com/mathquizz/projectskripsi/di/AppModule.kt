package com.mathquizz.projectskripsi.di


import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage() = com.google.firebase.ktx.Firebase.storage

    @Provides
    @Singleton
    fun provideFirebaseCommon(): FirebaseCommon {
        return FirebaseCommon()
    }


}

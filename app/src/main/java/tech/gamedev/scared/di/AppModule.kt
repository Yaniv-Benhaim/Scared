package tech.gamedev.scared.di

import tech.gamedev.scared.adapters.SwipeSongAdapter
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import tech.gamedev.scared.R
import tech.gamedev.scared.data.remote.FirebaseDatabase
import tech.gamedev.scared.exoplayer.MusicServiceConnection
import tech.gamedev.scared.repo.LoginRepository
import tech.gamedev.scared.repo.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
            @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideSwipeSongAdapter() = SwipeSongAdapter()

    @Singleton
    @Provides
    fun provideGlideInstance(
            @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun provideGSO(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

    @Singleton
    @Provides
    fun provideGsoClient(@ApplicationContext context: Context): GoogleSignInClient =
        GoogleSignIn.getClient(context, provideGSO())


    @Singleton
    @Provides
    fun provideLoginRepo() : LoginRepository = LoginRepository()

    @Singleton
    @Provides
    fun provideFirebaseDatabase() : FirebaseDatabase = FirebaseDatabase()

    @Singleton
    @Provides
    fun provideMainRepo(firebaseDatabase: FirebaseDatabase) : MainRepository = MainRepository(firebaseDatabase)
}
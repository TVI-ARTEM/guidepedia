package com.zhulin.guideshop.core.di

import android.app.Application
import com.zhulin.guideshop.core.markdown.basic.BoldFormatter
import com.zhulin.guideshop.core.markdown.basic.Formatters
import com.zhulin.guideshop.core.markdown.basic.HeaderFormatter
import com.zhulin.guideshop.core.markdown.basic.HorizontalLineFormatter
import com.zhulin.guideshop.core.markdown.basic.ItalicFormatter
import com.zhulin.guideshop.core.markdown.basic.NewLineFormatter
import com.zhulin.guideshop.core.markdown.basic.NumericListFormatter
import com.zhulin.guideshop.core.markdown.basic.RoundListFormatter
import com.zhulin.guideshop.core.markdown.basic.SelectedFormatter
import com.zhulin.guideshop.core.markdown.basic.StrikethroughFormatter
import com.zhulin.guideshop.core.markdown.complex.CodeComplexFormatter
import com.zhulin.guideshop.core.markdown.complex.ComplexFormatters
import com.zhulin.guideshop.core.markdown.complex.ImageComplexFormatter
import com.zhulin.guideshop.core.markdown.complex.LinkComplexFormatter
import com.zhulin.guideshop.core.utils.Constants.BASE_URL
import com.zhulin.guideshop.feature_topic.data.local.UserStore
import com.zhulin.guideshop.feature_topic.data.remote.GuideAPI
import com.zhulin.guideshop.feature_topic.data.repository.ArticleRepositoryImp
import com.zhulin.guideshop.feature_topic.data.repository.StringResourceRepositoryImp
import com.zhulin.guideshop.feature_topic.data.repository.UserRepositoryImp
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideStringResourceRepository(application: Application): StringResourceRepository {
        return StringResourceRepositoryImp(application)
    }

    @Provides
    @Singleton
    fun provideFormatters(application: Application): Formatters {
        return Formatters(
            headerFormatter = HeaderFormatter(application),
            boldFormatter = BoldFormatter(application),
            italicFormatter = ItalicFormatter(application),
            strikethroughFormatter = StrikethroughFormatter(application),
            roundListFormatter = RoundListFormatter(application),
            numericListFormatter = NumericListFormatter(application),
            newLineFormatter = NewLineFormatter(application),
            selectedFormatter = SelectedFormatter(application),
            horizontalLineFormatter = HorizontalLineFormatter(application)
        )
    }

    @Provides
    @Singleton
    fun provideComplexFormatters(): ComplexFormatters {
        return ComplexFormatters(
            linkComplexFormatter = LinkComplexFormatter(),
            imageComplexFormatter = ImageComplexFormatter(),
            codeComplexFormatter = CodeComplexFormatter()
        )
    }

    @Provides
    @Singleton
    fun provideUserStore(application: Application): UserStore {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun provideGuideUserAPI(): GuideAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(GuideAPI::class.java)
    }


    @Provides
    @Singleton
    fun provideUserRepository(guideAPI: GuideAPI): UserRepository {
        return UserRepositoryImp(guideAPI)
    }

    @Provides
    @Singleton
    fun provideArticleRepository(guideAPI: GuideAPI): ArticleRepository {
        return ArticleRepositoryImp(guideAPI)
    }
}
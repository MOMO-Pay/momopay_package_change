/*
 * Copyright 2022 Stax
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.me.momopay.di

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.hover.sdk.database.HoverRoomDatabase
import com.me.momopay.accounts.AccountDetailViewModel
import com.me.momopay.accounts.AccountsViewModel
import com.me.momopay.actions.ActionSelectViewModel
import com.me.momopay.addChannels.ChannelsViewModel
import com.me.momopay.contacts.ContactRepo
import com.me.momopay.data.local.accounts.AccountRepo
import com.me.momopay.data.local.actions.ActionRepo
import com.me.momopay.data.local.parser.ParserRepo
import com.me.momopay.data.remote.StaxApi
import com.me.momopay.data.repository.AccountRepositoryImpl
import com.me.momopay.data.repository.AuthRepositoryImpl
import com.me.momopay.data.repository.BountyRepositoryImpl
import com.me.momopay.data.repository.FinancialTipsRepositoryImpl
import com.me.momopay.domain.repository.AccountRepository
import com.me.momopay.domain.repository.AuthRepository
import com.me.momopay.domain.repository.BountyRepository
import com.me.momopay.domain.repository.FinancialTipsRepository
import com.me.momopay.domain.use_case.bounties.GetChannelBountiesUseCase
import com.me.momopay.domain.use_case.financial_tips.TipsUseCase
import com.me.momopay.domain.use_case.sims.ListSimsUseCase
import com.me.momopay.domain.use_case.stax_user.StaxUserUseCase
import com.me.momopay.faq.FaqViewModel
import com.me.momopay.futureTransactions.FutureViewModel
import com.me.momopay.inapp_banner.BannerViewModel
import com.me.momopay.ktor.EnvironmentProvider
import com.me.momopay.ktor.KtorClientFactory
import com.me.momopay.languages.LanguageViewModel
import com.me.momopay.login.LoginViewModel
import com.me.momopay.merchants.MerchantRepo
import com.me.momopay.merchants.MerchantViewModel
import com.me.momopay.paybill.PaybillRepo
import com.me.momopay.paybill.PaybillViewModel
import com.me.momopay.preferences.DefaultSharedPreferences
import com.me.momopay.preferences.DefaultTokenProvider
import com.me.momopay.preferences.LocalPreferences
import com.me.momopay.preferences.TokenProvider
import com.me.momopay.presentation.bounties.BountyViewModel
import com.me.momopay.presentation.financial_tips.FinancialTipsViewModel
import com.me.momopay.presentation.home.BalancesViewModel
import com.me.momopay.presentation.home.HomeViewModel
import com.me.momopay.presentation.sims.SimViewModel
import com.me.momopay.requests.NewRequestViewModel
import com.me.momopay.requests.RequestDetailViewModel
import com.me.momopay.requests.RequestRepo
import com.me.momopay.schedules.ScheduleDetailViewModel
import com.me.momopay.schedules.ScheduleRepo
import com.me.momopay.transactionDetails.TransactionDetailsViewModel
import com.me.momopay.transactions.TransactionHistoryViewModel
import com.me.momopay.transactions.TransactionRepo
import com.me.momopay.transfers.TransferViewModel
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.me.momopay.database.AppDatabase

const val TIMEOUT = 10_000

val appModule = module {
    viewModelOf(::FaqViewModel)
    viewModelOf(::ActionSelectViewModel)
    viewModelOf(::ChannelsViewModel)
    viewModelOf(::AccountsViewModel)
    viewModelOf(::AccountDetailViewModel)
    viewModelOf(::NewRequestViewModel)
    viewModelOf(::TransferViewModel)
    viewModelOf(::ScheduleDetailViewModel)
    viewModelOf(::BalancesViewModel)
    viewModelOf(::TransactionHistoryViewModel)
    viewModelOf(::BannerViewModel)
    viewModelOf(::FutureViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::TransactionDetailsViewModel)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::BountyViewModel)
    viewModelOf(::FinancialTipsViewModel)
    viewModelOf(::PaybillViewModel)
    viewModelOf(::MerchantViewModel)
    viewModelOf(::RequestDetailViewModel)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SimViewModel)
}

val dataModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single { HoverRoomDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().channelDao() }
    single { get<HoverRoomDatabase>().simDao() }

    singleOf(::TransactionRepo)
    singleOf(::ActionRepo)
    singleOf(::ContactRepo)
    singleOf(::AccountRepo)
    singleOf(::RequestRepo)
    singleOf(::ScheduleRepo)
    singleOf(::PaybillRepo)
    singleOf(::MerchantRepo)
    singleOf(::ParserRepo)

    singleOf(::StaxApi)
}

val ktorModule = module {

    single { EnvironmentProvider(androidApplication(), get()) }

    single {
        KtorClientFactory(get(), get()).create(
            Android.create {
                connectTimeout = TIMEOUT
            }
        )
    }
}

val datastoreModule = module {
    single {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(
                SharedPreferencesMigration(
                    androidContext(),
                    sharedPreferencesName = "stax.datastore"
                )
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { androidContext().preferencesDataStoreFile(name = "stax.datastore") }
        )
    }
}

val repositories = module {
    single(named("CoroutineDispatcher")) {
        Dispatchers.IO
    }

    single<TokenProvider> { DefaultTokenProvider(get()) }
    single<LocalPreferences> { DefaultSharedPreferences(androidApplication()) }

    single<AccountRepository> { AccountRepositoryImpl(get(), get(), get()) }
    single<BountyRepository> { BountyRepositoryImpl(get(), get(named("CoroutineDispatcher"))) }

    singleOf(::FinancialTipsRepositoryImpl) { bind<FinancialTipsRepository>() }

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
}

val useCases = module {
    single(named("CoroutineDispatcher")) {
        Dispatchers.IO
    }
    single { ListSimsUseCase(get(), get(), get(), get(named("CoroutineDispatcher"))) }

    factoryOf(::TipsUseCase)

    factoryOf(::GetChannelBountiesUseCase)

    singleOf(::StaxUserUseCase)
}
package ru.shirobokov.reanimation.di

import androidx.room.Room
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.shirobokov.reanimation.data.RemoteConfig
import ru.shirobokov.reanimation.data.SharedPreferencesRepository
import ru.shirobokov.reanimation.data.database.DataBase
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.domain.ServicesDataInteractor
import ru.shirobokov.reanimation.presentation.HostViewModel
import ru.shirobokov.reanimation.presentation.MetronomeType
import ru.shirobokov.reanimation.presentation.abot.AboutViewModel
import ru.shirobokov.reanimation.presentation.helplist.HelpListViewModel
import ru.shirobokov.reanimation.presentation.history.HistoryViewModel
import ru.shirobokov.reanimation.presentation.reanimation.ReanimationViewModel
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.store.AdultPatientStore
import ru.shirobokov.reanimation.presentation.reanimation.store.ChildPatientStore
import ru.shirobokov.reanimation.presentation.reanimation.store.NewbornPatientStore
import ru.shirobokov.reanimation.utils.ResourcesHandler
import ru.shirobokov.reanimation.utils.ResourcesHandlerImpl

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
val diModules = module {
    single { Room.databaseBuilder(get(), DataBase::class.java, "database").build() }
    single { SharedPreferencesRepository(get()) }
    single { RemoteConfig() }

    single { ReanimationInteractor(get(), get()) }
    single { ServicesDataInteractor(get(), get()) }

    single { ResourcesHandlerImpl(get()) } bind ResourcesHandler::class

    viewModel { HostViewModel(get()) }
    viewModel { (metronomeType: MetronomeType) ->
        when (metronomeType) {
            MetronomeType.ZMS_CHILD -> {
                ReanimationViewModel(ChildPatientStore(get()), ReanimationModel.getChildPatient())
            }
            MetronomeType.ZMS_NEWBORN -> {
                ReanimationViewModel(NewbornPatientStore(get()), ReanimationModel.getNewbornPatient())
            }
            else -> ReanimationViewModel(AdultPatientStore(get()), ReanimationModel.getAdultPatient())
        }
    }
    viewModel { HelpListViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { AboutViewModel(get()) }
}

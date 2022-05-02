package com.thunder.thunderplane.module

import com.thunder.thunderplane.*
import org.koin.dsl.module


val appModule = module {
    single { JetHandler() }
    single { BigBossHandler(get() as JetHandler) }
    single { UFOHandler(get() as JetHandler, get() as BigBossHandler) }
    single { SmallBossHandler(get() as JetHandler, get() as UFOHandler) }
    single { BackgroundHandler(get() as JetHandler) }

}
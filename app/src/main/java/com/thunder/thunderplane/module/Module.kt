package com.thunder.thunderplane.module

import com.thunder.thunderplane.background.BackgroundHandler
import com.thunder.thunderplane.big_boss.BigBossHandler
import com.thunder.thunderplane.small_boss.SmallBossHandler
import com.thunder.thunderplane.ufo.UFOHandler
import com.thunder.thunderplane.user.JetHandler
import org.koin.dsl.module


val appModule = module {
    single { JetHandler() }
    single { BigBossHandler(get() as JetHandler) }
    single { UFOHandler(get() as JetHandler, get() as BigBossHandler) }
    single { SmallBossHandler(get() as JetHandler, get() as UFOHandler) }
    single { BackgroundHandler(get() as JetHandler) }

}
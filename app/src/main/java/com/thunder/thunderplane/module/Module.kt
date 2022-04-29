package com.thunder.thunderplane.module

import com.thunder.thunderplane.BigBossHandler
import com.thunder.thunderplane.JetHandler
import com.thunder.thunderplane.SmallBossHandler
import com.thunder.thunderplane.UFOHandler
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {
    single { BigBossHandler() }
    single { JetHandler() }
    single { UFOHandler(get(), get()) }
    single { SmallBossHandler(get(),get()) }
}
package com.thunder.thunderplane.module

import com.thunder.thunderplane.BigBossHandler
import com.thunder.thunderplane.JetHandler
import com.thunder.thunderplane.UFOHandler
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module{
    single { BigBossHandler() }
    single { JetHandler() }
    factory { UFOHandler(get(),get()) }
}
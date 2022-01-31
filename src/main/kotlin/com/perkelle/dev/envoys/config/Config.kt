package com.perkelle.dev.envoys.config

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.utils.config.FileName
import com.perkelle.dev.envoys.utils.config.YMLWrapper

fun getConfig() = Envoys.instance.config

@FileName
class Config: YMLWrapper()
package com.perkelle.dev.envoys.config

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.utils.config.FileName
import com.perkelle.dev.envoys.utils.config.YMLWrapper

fun getData() = Envoys.instance.data

@FileName("data.yml")
class Data: YMLWrapper()
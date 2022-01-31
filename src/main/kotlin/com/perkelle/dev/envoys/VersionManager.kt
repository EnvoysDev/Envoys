package com.perkelle.dev.envoys

import com.perkelle.dev.envoys.utils.sync
import org.bukkit.Bukkit
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.read
import kotlin.concurrent.write

object VersionManager {
    private var latestVersion: String? = null
    private var lastCheckTime = Instant.now()
    private val mu = ReentrantReadWriteLock()

    val CHECK_FREQUENCY: Duration = Duration.ofHours(3)

    fun getLatestVersion(callback: (String?) -> Unit) {
        mu.read {
            if (latestVersion != null && Instant.now().minus(CHECK_FREQUENCY) < lastCheckTime) {
                callback(latestVersion!!) // Can't be null since protected by mutex
                return
            }
        }

        Bukkit.getServer().scheduler.runTaskAsynchronously(Envoys.instance.pl, Runnable {
            val conn = URL("https://api.spigotmc.org/legacy/update.php?resource=20357").openConnection() as HttpsURLConnection
            with(conn) {
                connectTimeout = TimeUnit.SECONDS.toMillis(5).toInt()

                inputStream.bufferedReader().useLines { lines ->
                    val body = lines.joinToString("\n").trim()

                    if (responseCode != 200) {
                        Envoys.instance.pl.logger.warning("HTTP status code %d while retrieving version data: %s".format(responseCode, body))

                        sync {
                            callback(null)
                        }

                        return@Runnable
                    }

                    mu.write {
                        latestVersion = body
                        lastCheckTime = Instant.now()
                    }

                    sync {
                        callback(body)
                    }
                }
            }
        })
    }
}
package com.arm8086.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.CommandContext
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.utcdatetime.UtcDateTime
import org.jsoup.Jsoup

@AliucordPlugin(requiresRestart=false)
class eddsworld : Plugin() {
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        val url = "https://eddsworld.co.uk"
        val comicNum = Utils.createCommandOption(
            ApplicationCommandType.NUMBER,
            "num",
            "# of the comic to view",
            null,
            false,
            default = false,
            channelTypes = emptyList(),
            choices = emptyList(), subCommandOptions = emptyList(), autocomplete = false
        )
        commands.registerCommand(
            "ewc",
            "View a comic from eddsworld.co.uk",
            listOf(comicNum)
        ) { cctx ->
            val comicNumber = cctx.getLong("num")
            var comic
            if (comicNumber == null) { // idk if it returns null or 0 if empty
                return@registerCommand CommandsAPI.CommandResult("Invalid number")
            } else {
                comic = try {
                    Jsoup.connect("$url/comic/$comicNumber").get()
                } catch (throwable) {
                    return@registerCommand CommandsAPI.CommandResult("Cant get comic")
                }
                val imgSrc = comic.select(".comic").absUrl("src")
                val alt = comic.select(".comic").attr("alt")
                val embed = MessageEmbedBuilder().setTitle("Comic")
                    .setUrl("$url/comic/$comicNumber")
                    .setImage(imgSrc, imgSrc, 1500, 625)
                    .setFooter(alt)
                CommandsAPI.CommandResult(null, listOf(embed.build()), false)
            }
        }
    }

    override fun stop(ctx: Context) {
        commands.unregisterAll()
    }
}
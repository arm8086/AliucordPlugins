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
import com.lytefast.flexinput.R
import java.text.SimpleDateFormat
import org.jsoup

@AliucordPlugin
class eddsworld : Plugin() {

    var pluginIcon: Drawable? = null
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_search)
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
            "edds",
            "View a comic from eddsworld.co.uk",
            listOf(comicNum)
        ) { cctx: CommandContext ->
            val comicNumber = cctx.getLong("num")
            val comic: Document
            if (comicNumber == null) { // idk if it returns null or 0 if empty
                logger.error(throwable)
                return@registerCommand CommandsAPI.CommandResult("Well the comic couldn't be fetched, sorry lol")
            } else {
                comic = try {
                    Jsoup.connect("$url/comic/$comicNumber").get()
                } catch (throwable: Throwable) {
                    logger.error(throwable)
                    return@registerCommand CommandsAPI.CommandResult("Well the comic couldn't be fetched, sorry lol")
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
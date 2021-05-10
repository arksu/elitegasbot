package su.ark.elitegasbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook

@SpringBootApplication
class ElitegasbotApplication {

    @Bean
    fun telegramBotApi(
        bot: EliteGasBot
    ): TelegramBotsApi {
        val webhook = DefaultWebhook()
        webhook.setInternalUrl("http://127.0.0.1:8090")
//        webhook.

        val api = TelegramBotsApi(DefaultBotSession::class.java, webhook)

        val s = SetWebhook()
        s.url = "https://ark.su/elitegasbot"

        api.registerBot(bot, s)
        return api
    }
}

fun main(args: Array<String>) {

    runApplication<ElitegasbotApplication>(*args)
}

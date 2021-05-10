package su.ark.elitegasbot

import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import su.ark.elitegasbot.entity.Log
import su.ark.elitegasbot.repo.LogRepo
import javax.annotation.PostConstruct


@Component
class EliteGasBot(
    val logRepo: LogRepo
) : TelegramWebhookBot() {

    var logger = LoggerFactory.getLogger(EliteGasBot::class.java)

    val mapper = ObjectMapper();

    val fd: DateTimeFormatter = DateTimeFormat.forPattern("d-MM-y")
    val fdd: DateTimeFormatter = DateTimeFormat.forPattern("d_MM_y")

    val ft: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
    val ftd: DateTimeFormatter = DateTimeFormat.forPattern("HH_mm")


    @PostConstruct
    fun init() {
    }

    override fun getBotToken(): String {
        return "1838114683:AAGVLg80ruhRYnmqUZpvJdVOgi6u6MgaxCU"
    }

    override fun getBotUsername(): String {
        return "EliteGasBot"
    }

    override fun getBotPath(): String {
        return "elitegas"
    }

    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        logger.warn("onWebhookUpdateReceived")


        val msg: String = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(update)
        val log = Log(
            0, msg
        )
        logRepo.save(log)

        val s = SendMessage()
        if (update.hasMessage()) {
            logger.debug("msg text: ${update.message.text}")
            s.chatId = update.message.chatId.toString()
            val text: String? = update.message.text
            if (!text.isNullOrEmpty()) {
                if (text.equals("/start", true)) {
                    val sb = StringBuilder()
                    sb.append("Вас приветствует бот ЭлитГаз\n")
                    sb.append("С моей помощью вы можете записаться в сервис на определенное время\n")
                    sb.append("Узнать стоимость установки ГБО и многое другое\n")

                    s.text = sb.toString()
                    s.replyMarkup =
                        KeyboardBuilder.start().newRow()
                            .addButton("Записаться", "reserve_time")
                            .build()
                } else {
                    s.text = "Извините, я вас не понял"
                }
                return s
            }
        } else if (update.hasCallbackQuery()) {
            val cb = update.callbackQuery
            logger.debug("callbackQuery: ${cb.data}")
            s.chatId = cb.message.chatId.toString()
            if (cb.data.equals("reserve_time", true)) {
                s.text = "На какую дату вы хотите записаться?"


                val now = DateTime()
                val d1 = now.plusDays(1)
                val d2 = now.plusDays(2)
                val d3 = now.plusDays(3)
                val d4 = now.plusDays(4)
                val d5 = now.plusDays(5)

                s.replyMarkup =
                    KeyboardBuilder.start().newRow()
                        .addButton(d1.toString(fd), "reserve_date_" + d1.toString(fdd)).newRow()
                        .addButton(d2.toString(fd), "reserve_date_" + d2.toString(fdd)).newRow()
                        .addButton(d3.toString(fd), "reserve_date_" + d3.toString(fdd)).newRow()
                        .addButton(d4.toString(fd), "reserve_date_" + d4.toString(fdd)).newRow()
                        .addButton(d5.toString(fd), "reserve_date_" + d5.toString(fdd)).newRow()
                        .build()
                return s
            } else if (cb.data.startsWith("reserve_date_", true)) {
                val st = cb.data.removePrefix("reserve_date_")
                val sl = st.split("_")


                val t1 = DateTime(sl[2].toInt(), sl[1].toInt(), sl[0].toInt(), 10, 0)
                val t2 = t1.plusHours(1)
                val t3 = t2.plusHours(1)
                val t4 = t3.plusHours(1)
                val t5 = t4.plusHours(1)
                val t6 = t5.plusHours(1)

                s.text = t1.toString(fd) + " на какое время вы хотите записаться?"
                s.replyMarkup =
                    KeyboardBuilder.start().newRow()
                        .addButton(t1.toString(ft), "reserve_time_" + t1.toString(ftd)).newRow()
                        .addButton(t2.toString(ft), "reserve_time_" + t2.toString(ftd)).newRow()
                        .addButton(t3.toString(ft), "reserve_time_" + t3.toString(ftd)).newRow()
                        .addButton(t4.toString(ft), "reserve_time_" + t4.toString(ftd)).newRow()
                        .addButton(t5.toString(ft), "reserve_time_" + t5.toString(ftd)).newRow()
                        .addButton(t6.toString(ft), "reserve_time_" + t6.toString(ftd)).newRow()
                        .build()

                return s
            } else if (cb.data.startsWith("reserve_time_", true)) {
                s.text = "Запись оформлена! Ждем вас в назначенное время."
                return s
            }
        }
        return null
    }

}
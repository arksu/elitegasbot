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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
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

    val formatDate: DateTimeFormatter = DateTimeFormat.forPattern("d-MM-y")
    val formatDateData: DateTimeFormatter = DateTimeFormat.forPattern("d_MM_y")

    val formatTime: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
    val formatTimeData: DateTimeFormatter = DateTimeFormat.forPattern("HH_mm")


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

        if (update.hasMessage()) {
            logger.debug("msg text: ${update.message.text}")
            val s = SendMessage()
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
            val data = cb.data
            logger.debug("callbackQuery: $data")
            val s = EditMessageText()
            s.chatId = cb.message.chatId.toString()
            s.messageId = cb.message.messageId

            when {
                data.equals("reserve_time", true) -> {
                    s.text = "На какую дату вы хотите записаться?"

                    val days = 5

                    val keys = KeyboardBuilder.start().newRow()

                    val now = DateTime()
                    val dates = Array<DateTime>(days) {
                        now.plusDays(it + 1)
                    }
                    dates.forEach {
                        keys.addButton(it.toString(formatDate), "reserve_date_" + it.toString(formatDateData)).newRow()
                    }

                    s.replyMarkup = keys.build()
                    return s
                }
                data.startsWith("reserve_date_", true) -> {
                    val st = data.removePrefix("reserve_date_")
                    val sl = st.split("_")

                    // со скольки начинается время для записи
                    val hourStart = 10
                    // сколько всего часов (диапазон)
                    val hours = 9

                    // дата начала
                    val timeStart = DateTime(sl[2].toInt(), sl[1].toInt(), sl[0].toInt(), hourStart, 0)

                    val times = Array<DateTime>(hours) {
                        timeStart.plusHours(it)
                    }

                    s.text = timeStart.toString(formatDate) + " на какое время вы хотите записаться?"

                    val keys = KeyboardBuilder.start().newRow()
                    times.forEach {
                        keys.addButton(it.toString(formatTime), "reserve_time_" + it.toString(formatTimeData)).newRow()

                    }

                    s.replyMarkup = keys.build()

                    return s
                }
                data.startsWith("reserve_time_", true) -> {
                    s.text = "Запись оформлена! Ждем вас в назначенное время."
                    return s
                }
            }
        }
        return null
    }

}
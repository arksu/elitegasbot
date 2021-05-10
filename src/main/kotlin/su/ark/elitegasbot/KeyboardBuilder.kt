package su.ark.elitegasbot

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class KeyboardBuilder {
    private val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()
    private var lastRow: MutableList<InlineKeyboardButton> = ArrayList()

    companion object {
        fun start(): KeyboardBuilder {
            return KeyboardBuilder().newRow()
        }
    }

    fun newRow(): KeyboardBuilder {
        lastRow = ArrayList()
        buttons.add(lastRow)
        return this
    }

    fun addButton(name: String, data: String? = null): KeyboardBuilder {
        val btn = InlineKeyboardButton()
        btn.text = name
        btn.callbackData = data
        lastRow.add(btn)
        return this
    }

    fun build(): InlineKeyboardMarkup {
        val markupKeyboard = InlineKeyboardMarkup()
        markupKeyboard.keyboard = buttons
        return markupKeyboard
    }
}
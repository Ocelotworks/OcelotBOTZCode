package pw.dvd604.ocelot.consumer

import com.rabbitmq.client.*
import com.zaxsoft.zax.zmachine.ZUserInterface
import org.json.simple.JSONObject
import pw.dvd604.ocelot.util.EscapeUtils
import java.awt.Dimension
import java.awt.Point
import java.lang.StringBuilder
import java.util.*


class RPCInterface : ZUserInterface {

    lateinit var container: Game
    var printLoadHelp: Boolean = false
    var loadGame: Boolean = false
    var text: String = ""
    var lastDelivery: Delivery? = null
    lateinit var channel: Channel
    var buffer = StringBuilder()
    lateinit var id: String

    override fun readLine(line: StringBuffer?, time: Int): Int {
        if (buffer.isNotEmpty()) sendMessage(buffer.toString())

        try {
            while (!Thread.interrupted()) {
                Thread.sleep(Long.MAX_VALUE)
            }
        } catch (e: InterruptedException) { }

        for (s in text) {
            line?.append(s)
        }

        text = ""

        return 13
    }

    override fun readChar(time: Int): Int {
        if (buffer.isNotEmpty()) sendMessage(buffer.toString())

        try {
            while (!Thread.interrupted()) {
                Thread.sleep(Long.MAX_VALUE)
            }
        } catch (e: Exception) { }

        return text[0].toInt()
    }

    override fun showString(string: String?) {
        if (!loadGame)
            buffer.append(string)
    }

    override fun getFilename(title: String?, suggested: String?, saveFlag: Boolean): String {
        return "$id.z5Save"
    }

    private fun sendMessage(message: String): Boolean {
        lastDelivery?.let {
            if (message.isEmpty()) return false

            val props = AMQP.BasicProperties().builder()
                .correlationId(it.properties.correlationId)
                .build()

            channel.basicPublish(
                "",
                it.properties.replyTo,
                props,
                "{\"name\":\"gameText\",\"text\":\"${EscapeUtils.encodeURIComponent(message)}\",\"loaded\":$printLoadHelp, \"players\":${container.getPlayers()}}".toByteArray()
            )
            printLoadHelp = false
            buffer.clear()
            lastDelivery = null
            return true
        }
        return false
    }

    override fun initialize(version: Int) {}

    override fun setTerminatingCharacters(characters: Vector<*>?) {}

    override fun hasStatusLine(): Boolean {
        return false
    }

    override fun hasUpperWindow(): Boolean {
        return false
    }

    override fun defaultFontProportional(): Boolean {
        return false
    }

    override fun hasColors(): Boolean {
        return false
    }

    override fun hasBoldface(): Boolean {
        return true
    }

    override fun hasItalic(): Boolean {
        return true
    }

    override fun hasFixedWidth(): Boolean {
        return false
    }

    override fun hasTimedInput(): Boolean {
        return false
    }

    override fun getScreenCharacters(): Dimension {
        return Dimension(32, 32)
    }

    override fun getScreenUnits(): Dimension {
        return Dimension(32, 32)
    }

    override fun getFontSize(): Dimension {
        return Dimension(32, 32)
    }

    override fun getWindowSize(window: Int): Dimension {
        return Dimension(32, 32)
    }

    override fun getDefaultForeground(): Int {
        return 1
    }

    override fun getDefaultBackground(): Int {
        return 1
    }

    override fun getCursorPosition(): Point {
        return Point(0, 0)
    }

    override fun showStatusBar(s: String?, a: Int, b: Int, flag: Boolean) {}

    override fun splitScreen(lines: Int) {}

    override fun setCurrentWindow(window: Int) {}

    override fun setCursorPosition(x: Int, y: Int) {}

    override fun setColor(foreground: Int, background: Int) {}

    override fun setTextStyle(style: Int) {}

    override fun setFont(font: Int) {}

    override fun scrollWindow(lines: Int) {}

    override fun eraseLine(size: Int) {}

    override fun eraseWindow(window: Int) {}

    override fun quit() {}

    override fun restart() {}

    override fun fatal(message: String?) {}
}
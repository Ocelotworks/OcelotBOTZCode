package pw.dvd604.ocelot.consumer

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.zaxsoft.zax.zmachine.ZCPU
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private val connectionFactory = ConnectionFactory()
val games = ArrayList<Game>(0)
private var rabbitURL = ""
private val timer = TimerThread()

fun main(args: Array<String>) {
    val props = Properties()
    val propsFile = File("Z5.props")

    if(propsFile.exists()){
        props.load(propsFile.inputStream())
        rabbitURL = props.getProperty("rabbit.url")
    } else {
        throw FileNotFoundException("Z5.props not found")
    }

    connectionFactory.setUri(rabbitURL)

    try {
        val connection = connectionFactory.newConnection()
        val channel = connection.createChannel()
        timer.start()

        channel.queueDeclare("z5", false, false, false, null)
        println(" [*] Waiting for messages.")
        channel.basicConsume("z5", true, DeliverCallback { _, delivery ->
            val message = String(delivery.body)
            val jsonObject: JSONObject = JSONParser().parse(message) as JSONObject
            val serverID = jsonObject["server"] as String
            print("Got message from $serverID")

            getGame(serverID)?.let {
                if(it.canDestroy){
                    games.remove(it)
                }
            }

            if (getGame(serverID) == null) {
                val ui = RPCInterface()
                ui.channel = channel
                ui.lastDelivery = delivery
                ui.id = serverID
                val cpu = ZCPU(ui)
                cpu.initialize("zork1.z5")
                ui.loadGame = File("$serverID.z5Save").exists()

                val gameContainer = Game(serverID, ui, cpu, cpu.start(), Date().time)
                games.add(gameContainer)

                if(ui.loadGame){
                    gameContainer.ui.text = "restore"
                    gameContainer.ui.buffer.clear()
                    ui.loadGame = false
                    gameContainer.thread.interrupt()
                    ui.printLoadHelp = true
                }

                gameContainer.addPlayer(jsonObject["player"] as String)

                println(" - New game")
            } else {
                //Game is running
                val inputText = jsonObject["data"] as String

                getGame(serverID)?.let {
                    it.ui.text = inputText
                    it.thread.interrupt()
                    it.ui.lastDelivery = delivery
                    it.lastPlayTime = Date().time

                    it.addPlayer(jsonObject["player"] as String)
                }
                println(" - \"${inputText}\"")
            }
        }, CancelCallback { })

    } catch (e: Exception) {
        println("An error occurred: ${e.message} \nSend Neil the stacktrace. Thanks")
        e.printStackTrace()
        e.cause?.printStackTrace()
    }
}

fun getGame(serverID: String): Game? {
    return if (games.any { it.id == serverID }) {
        games.filter { it.id == serverID }[0]
    } else {
        null
    }
}



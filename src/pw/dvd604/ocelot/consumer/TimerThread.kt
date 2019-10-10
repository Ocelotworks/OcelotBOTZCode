package pw.dvd604.ocelot.consumer

import java.util.*

class TimerThread {
    fun start(){
        Thread(TimerRunnable()).start()
    }

    class TimerRunnable : Runnable{
        private val timeoutInMinutes = 5
        private val timeout = timeoutInMinutes * 60000

        override fun run() {
            while(!Thread.interrupted()) {
                print("Disposing of dead games: ")
                val currentTime = Date().time
                for (game in games) {
                    if (currentTime > game.lastPlayTime + timeout && !game.canDestroy) {
                        print(game.id + ", ")
                        game.saveAndDestroy()
                    }
                }
                println("done.")
                Thread.sleep(60000)
            }
        }
    }
}
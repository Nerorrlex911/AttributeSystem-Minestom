package com.github.zimablue.attrsystem.fight.internal.feature.hologram

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.time.TimeUnit
import taboolib.common5.cdouble
import java.util.*

/**
 * 全息文本构建
 *
 * @constructor Create empty Hologram builder
 * @property location
 */
class HologramBuilder(private val instance: Instance,private val pos: Pos) {
    private var content: MutableList<Component> = ArrayList()
    private val viewers: MutableSet<Player> = Collections.synchronizedSet(HashSet())
    private var stay: Long = -1
    private var time: Int = -1
    private var each: Vec? = null

    /**
     * 持续时间
     *
     * @param stay
     * @return
     */
    fun stay(stay: Long): HologramBuilder {
        this.stay = stay
        return this
    }

    /**
     * 内容
     *
     * @param content 内容
     * @return 自身
     */
    fun content(content: Collection<Component>): HologramBuilder {
        this.content.clear()
        this.content.addAll(content)
        return this
    }

    /**
     * @param viewers 可以看到的玩家
     * @return 自身
     */
    fun viewers(vararg viewers: Player): HologramBuilder {
        this.viewers.clear()
        this.viewers.addAll(viewers)
        return this
    }

    /**
     * @param viewers 可以看到的玩家
     * @return 自身
     */
    fun viewers(viewers: MutableList<Player>): HologramBuilder {
        this.viewers.clear()
        this.viewers.addAll(viewers)
        return this
    }

    /**
     * 添加观察者
     *
     * @param player
     */
    fun addViewer(player: Player) {
        this.viewers.add(player)
    }

    /**
     * 动画
     *
     * @param time 持续时间
     * @param finalLocation 最终地点
     * @return 自身
     */
    fun animation(time: Int, finalLocation: Pos): HologramBuilder {
        if (stay == -1L || time == -1) return this
        val reduce = finalLocation.sub(pos).asVec()
        val multiply = 1.0.div(time)
        this.each = reduce.mul(multiply)
        this.time = time
        return this
    }

    /**
     * 构建全息
     *
     * @return 构建好的Hologram
     */
    fun build(): Hologram {
        val hologram = Hologram(instance, pos, content)
        if (stay != -1L) {
            each?.also { vector ->
                var count = 0
                instance.scheduler().submitTask {
                    if (count > time - 1) {
                        hologram.remove()
                        return@submitTask TaskSchedule.stop()
                    }
                    hologram.teleport(pos.add(vector.mul(count.cdouble)))
                    count++
                    return@submitTask TaskSchedule.nextTick()
                }
            } ?: instance.scheduler().buildTask {
                hologram.remove()
            }.delay(stay,TimeUnit.SERVER_TICK).schedule()
        }
        return hologram
    }

}
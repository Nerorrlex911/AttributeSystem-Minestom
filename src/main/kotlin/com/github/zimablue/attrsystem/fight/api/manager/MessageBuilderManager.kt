package com.github.zimablue.attrsystem.fight.api.manager

import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.devoutserver.util.map.LowerKeyMap


/**
 * MessageType type manager
 *
 * 用于注册自定义消息类型
 *
 * 编写Message的实现类，并注册它的Builder以自定义消息类型
 *
 * @constructor Create empty MessageType type manager
 */
abstract class MessageBuilderManager {
    /** Attack */
    abstract val attack: LowerKeyMap<MessageBuilder>

    /** Defend */
    abstract val defend: LowerKeyMap<MessageBuilder>
}

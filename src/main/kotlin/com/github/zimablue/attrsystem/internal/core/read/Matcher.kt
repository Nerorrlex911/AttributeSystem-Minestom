package com.github.zimablue.attrsystem.internal.core.read

import com.github.zimablue.attrsystem.api.operation.Operation
import com.github.zimablue.devoutserver.util.map.component.Keyable

/**
 * @className Matcher
 *
 * @author Glom
 * @date 2022/8/7 22:36 Copyright 2022 user. All rights reserved.
 */
class Matcher<A>(override val key: String, val operation: Operation<A>) : Keyable<String>, Operation<A> by operation
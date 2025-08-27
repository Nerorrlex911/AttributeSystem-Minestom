package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.operation.Operation
import com.github.zimablue.devoutserver.util.map.LowerKeyMap

abstract class OperationManager : LowerKeyMap<Operation<*>>()
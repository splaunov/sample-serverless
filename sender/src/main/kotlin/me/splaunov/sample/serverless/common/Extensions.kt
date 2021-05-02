package me.splaunov.sample.serverless.common

import org.slf4j.Logger

fun Logger.debug(supplier: () -> String) {
    if (isDebugEnabled) debug(supplier())
}
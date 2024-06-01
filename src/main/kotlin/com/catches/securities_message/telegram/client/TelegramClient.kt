package com.catches.securities_message.telegram.client

import com.catches.securities_message.telegram.config.TelegramConfig
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class TelegramClient(
    private val telegramConfig: TelegramConfig
) {
    private lateinit var telegramBot: Bot

    @PostConstruct
    fun start() {
        telegramBot = bot {
            token = telegramConfig.getTelegramToken()
            dispatch {
                command("bond") {

                    //TODO 수익률 좋은 채권 리스트 가져오기


                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Help message")
                    // 성공하였을 시 처리
                    result.fold({
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
                }
                command("bond_info") {
                    val args = args.joinToString()

                    //TODO 채권 상세 정보 가져오기
                }

                command("my_bond") {

                    // TODO 내가 등록한 채권 정보 가져오기

                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                    // 성공하였을 시 처리
                    result.fold({
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
                }

                command("my_bond_save") {
                    val registerInfo = args.joinToString()

                    // TODO 내가 입력한 채권 정보가 DB에 있는지 조회하여 저장하기
                    // TODO 채권 정보가 없을 경우 비슷한 채권 정보를 메시지로 뿌려주던가 없다고 하기
                    // 이후 배치를 통한 좋은 채권 정보가 있는지 알려주기 위함

                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                    // 성공하였을 시 처리
                    result.fold({
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
                }
                command("my_bond_remove") {
                    // TODO 내가 등록한 채권 정보 삭제하기
                }
            }
        }

        telegramBot.startPolling()
    }

    fun sendMessage(chatId: String, message: String) {
        val chatId = ChatId.fromId(chatId.toLong())
        telegramBot.sendMessage(chatId, message)
    }
}
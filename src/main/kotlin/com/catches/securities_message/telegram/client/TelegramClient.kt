package com.catches.securities_message.telegram.client

import com.catches.securities_message.retrofit.api.SecuritiesApiInterface
import com.catches.securities_message.telegram.config.TelegramConfig
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class TelegramClient(
    private val telegramConfig: TelegramConfig,
    @Qualifier("securitiesApiRetrofit") private val securitiesApiInterface: SecuritiesApiInterface
) {
    private lateinit var telegramBot: Bot

    @PostConstruct
    fun start() {
        telegramBot = bot {
            token = telegramConfig.getTelegramToken()
            dispatch {
                command("bond") {
                    val args = args.joinToString()
                    val msg =
                        if(args.isNullOrEmpty()) {
                            val data = securitiesApiInterface.getBondList().execute()

                            if(data.isSuccessful && data.body()?.data != null) {
                                val sb = StringBuffer()

                                sb.append(
                                    """
                                        *등급별 표면 수익률이 높은 채권 리스트*
                                        각 등급별 3개씩 제공합니다.
                                        
                                        채권명 / 표면 수익률 / 만기일자 / 등급
                                    """.trimIndent()
                                ).append("\n\n")

                                data.body()!!.data?.forEach {
                                    sb.append(
                                        """
                                            *${it.grade}*
                                        """.trimIndent()
                                    ).append("\n")
                                    it.bondList.forEach {
                                        sb.append(
                                            """
                                                ${it.name}  ${it.surfaceInterestRate}%  ${it.expiredDate}  ${it.grade}
                                            """.trimIndent()
                                        ).append("\n")
                                    }
                                    sb.append("\n")
                                }
                                sb.toString()
                            } else {
                                """
                                    채권 정보를 가져오는 도중 문제가 발생하였습니다.
                                """
                            }
                        } else {
                            // 채권 정보가 있을 경우 해당 채권 정보 가져오기
                            val data = securitiesApiInterface.getBondDetail(args).execute()

                            if(data.isSuccessful && data.body()?.data != null) {
                                """
                                *채권명:*        ${data.body()?.data?.bondName}
                                *표면 이자율:*     ${data.body()?.data?.surfaceInterestRate}
                                *발행인:*        ${data.body()?.data?.issuerName}
                                *발행일자:*       ${data.body()?.data?.issueDate}
                                *만기일자:*       ${data.body()?.data?.expiredDate}
                                *금리변동 구분:*   ${data.body()?.data?.interestChange}
                                *이자 유형:*      ${data.body()?.data?.interestType}
                                *종가:*          ${data.body()?.data?.price}
                                *종가 기준일:*     ${data.body()?.data?.priceDate}
                                """
                            } else {
                                """
                                    채권 정보를 가져오는 도중 문제가 발생하였습니다.
                                """
                            }
                        }.trimIndent()

                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), parseMode= ParseMode.MARKDOWN, text = msg)
                    //TODO 메시지 발송 결과에 따라 핸들링
                    result.fold({
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
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
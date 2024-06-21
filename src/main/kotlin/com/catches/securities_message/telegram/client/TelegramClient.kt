package com.catches.securities_message.telegram.client

import com.catches.securities_message.retrofit.api.SecuritiesApiInterface
import com.catches.securities_message.retrofit.api.request.UserBondCreateRequest
import com.catches.securities_message.telegram.config.TelegramConfig
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
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

                    if(args.isNullOrEmpty()) {
                        val data = securitiesApiInterface.getBondList().execute()

                        val messageBody =
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

                        telegramBot.sendMessage(ChatId.fromId(message.chat.id), messageBody)

                    } else {
                        // 채권 정보가 있을 경우 해당 채권 정보 가져오기
                        val data = securitiesApiInterface.searchBondList(args).execute()

                        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                            data.body()?.data?.map {
                                listOf(
                                    InlineKeyboardButton.Url(
                                        text = it.bondName,
                                        url = "https://www.google.com", // TODO 버튼 클릭 후 URL 처리 필요
                                    )
                                )
                            } ?: emptyList() // TODO 체크 필요
                        )

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            text = "정보를 확인하고 싶은 채권 선택",
                            replyMarkup = inlineKeyboardMarkup
                        )
                    }
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

                command("mb") {
                    val registerInfo = args.joinToString()

                    if(registerInfo.isNullOrEmpty()) {
                        bot.sendMessage(ChatId.fromId(message.chat.id), "등록할 채권 정보를 입력해주세요.")
                    } else {
                        // 채권 정보가 있을 경우 해당 채권 정보 가져오기
                        val data = securitiesApiInterface.searchBondList(registerInfo).execute()

                        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                            data.body()?.data?.map {
                                listOf(
                                    InlineKeyboardButton.CallbackData(
                                        text = it.bondName,
                                        callbackData = it.bondId
                                    )
                                )
                            } ?: emptyList() // TODO 체크 필요
                        )

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            text = "등록할 채권 선택",
                            replyMarkup = inlineKeyboardMarkup
                        )
                    }

                    // TODO 채권과 유저가 존재하는지 확인하는 예외처리가 필요함

                    // TODO 내가 입력한 채권 정보가 DB에 있는지 조회하여 저장하기
                    // TODO 채권 정보가 없을 경우 비슷한 채권 정보를 메시지로 뿌려주던가 없다고 하기
                    // 이후 배치를 통한 좋은 채권 정보가 있는지 알려주기 위함

                }
                command("my_bond_remove") {
                    // TODO 내가 등록한 채권 정보 삭제하기
                }

                // TODO 콜백 데이터를 여러개 처리하려면 아래 구조를 바꾸어야 한다.
                // invoke 메서드로 각각의 데이터를 분기하여 처리하는 방식으로 변
                callbackQuery {
                    val bondId = this.callbackQuery.data
                    val apiResponse = securitiesApiInterface.createUserBond(
                        UserBondCreateRequest(
                            userId = this.callbackQuery.message?.from?.id.toString(),
                            bondId = bondId
                        )
                    ).execute()

                    val responseMessage =
                        if (apiResponse.isSuccessful) {
                            "처리가 완료되었습니다."
                        } else if(apiResponse.code() == 404) {
                            "유저 정보가 존재하지 않습니다."
                        } else {
                            "처리 중 문제가 발생했습니다."
                        }

                    telegramBot.answerCallbackQuery(this.callbackQuery.id, responseMessage)
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
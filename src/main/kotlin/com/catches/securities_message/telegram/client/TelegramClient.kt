package com.catches.securities_message.telegram.client

import com.catches.securities_message.retrofit.api.SecuritiesApiInterface
import com.catches.securities_message.retrofit.api.request.UserBondCreateRequest
import com.catches.securities_message.retrofit.api.request.UserCreateRequest
import com.catches.securities_message.telegram.config.TelegramConfig
import com.catches.securities_message.telegram.enum.CallbackEnum
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
                    // 채권 정보 확인
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
                command("mb") {
                    // 내가 등록한 채권 정보 확인
                    val registerInfo = args.joinToString()

                    if(registerInfo.isNullOrEmpty()) {
                        val apiResponse = securitiesApiInterface.getUserBondList(this.message.from!!.id.toString()).execute()

                        if(apiResponse.isSuccessful) {

                            val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                                apiResponse.body()?.data?.map {
                                    listOf(
                                        InlineKeyboardButton.Url(
                                            text = it.bondName,
                                            url = "https://www.google.com", // TODO 버튼 클릭 후 URL 처리 필요
                                        )
                                    )
                                } ?: emptyList()
                            )

                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                parseMode = ParseMode.MARKDOWN,
                                text = "상세정보를 확인할 채권 선택",
                                replyMarkup = inlineKeyboardMarkup
                            )

                        } else {
                            telegramBot.sendMessage(ChatId.fromId(message.chat.id), "채권 정보를 가져오는 도중 문제가 발생하였습니다.")
                        }
                    } else {
                        // 채권 정보가 있을 경우 해당 채권 정보 가져오기
                        val data = securitiesApiInterface.searchBondList(registerInfo).execute()

                        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                            data.body()?.data?.map {
                                listOf(
                                    InlineKeyboardButton.CallbackData(
                                        text = it.bondName,
                                        callbackData = "${CallbackEnum.SAVE.prefix}:${it.bondId}"
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

                }
                command("mbr") {
                    // 등록한 채권 정보 삭제하기
                    val apiResponse = securitiesApiInterface.getUserBondList(this.message.from!!.id.toString()).execute()

                    if(apiResponse.isSuccessful) {

                        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                            apiResponse.body()?.data?.map {
                                listOf(
                                    InlineKeyboardButton.CallbackData(
                                        text = it.bondName,
                                        callbackData = "${CallbackEnum.DELETE.prefix}:${it.bondId}"
                                    )
                                )
                            } ?: emptyList()
                        )

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            text = "삭제할 채권 선택",
                            replyMarkup = inlineKeyboardMarkup
                        )

                    } else {
                        telegramBot.sendMessage(ChatId.fromId(message.chat.id), "채권 정보를 가져오는 도중 문제가 발생하였습니다.")
                    }
                }

                callbackQuery {

                    val (callbackType, data) = this.callbackQuery.data.split(":")

                    val responseMessage =
                        when (callbackType) {
                            CallbackEnum.SAVE.prefix -> {
                                // Handle SAVE callback
                                val userId = this.callbackQuery.from.id.toString()
                                saveUserBond(userId, data)
                            }
                            CallbackEnum.DELETE.prefix -> {
                                // Handle DELETE callback
                                val userId = this.callbackQuery.from.id.toString()
                                deleteUserBond(userId, data)
                            }
                            else -> {
                                "처리 중 오류가 발생하였습니다."
                            }
                        }

                    telegramBot.answerCallbackQuery(this.callbackQuery.id, responseMessage)
                }
            }
        }

        telegramBot.startPolling()
    }

    private fun saveUserBond(userId: String, bondId: String): String {
        val apiResponse = securitiesApiInterface.createUserBond(
            UserBondCreateRequest(
                userId = userId,
                bondId = bondId
            )
        ).execute()

        val responseMessage =
            if (apiResponse.isSuccessful) {
                "처리가 완료되었습니다."
            } else if(apiResponse.code() == 404) {

                val userCreateApiResponse = securitiesApiInterface.createUser(
                    userCreateRequest = UserCreateRequest(
                        id = userId
                    )
                ).execute()

                if(userCreateApiResponse.isSuccessful && userCreateApiResponse.code() == 201) {
                    val retryResponse = securitiesApiInterface.createUserBond(
                        UserBondCreateRequest(
                            userId = userId,
                            bondId = bondId
                        )
                    ).execute()

                    if(retryResponse.isSuccessful) {
                        "처리가 완료되었습니다."
                    } else {
                        "처리 중 문제가 발생했습니다."
                    }
                } else {
                    "처리 중 문제가 발생했습니다."
                }
            } else {
                "처리 중 문제가 발생했습니다."
            }

        return responseMessage
    }

    private fun deleteUserBond(userId: String, bondId: String): String {
        val apiResponse = securitiesApiInterface.deleteUserBond(
            userId = userId,
            bondId = bondId
        ).execute()

        val responseMessage =
            if (apiResponse.isSuccessful) {
                "처리가 완료되었습니다."
            } else {
                "처리 중 문제가 발생했습니다."
            }

        return responseMessage
    }
}
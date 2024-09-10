package com.carsharingapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(LongPollingBot myBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            registerBotWithHandling(botsApi, myBot);
            return botsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to initialize Telegram Bots API", e);
        }
    }

    private void registerBotWithHandling(TelegramBotsApi botsApi, LongPollingBot myBot) {
        try {
            botsApi.registerBot(myBot);
        } catch (TelegramApiRequestException e) {
            if (e.getMessage().contains("Error removing old webhook")) {
                System.out.println("Webhook not found, delete skipped");
            } else {
                throw new RuntimeException("Failed to register bot", e);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}

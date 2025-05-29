package com.comedor.telegrambot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Bot extends TelegramLongPollingBot {
    static String calendarId = "sn0ir2b77hr77k7f1it9q2u730@group.calendar.google.com";
    static String apiKey = "AIzaSyDFr0DZIi03Yp0Fv9ZT3WxGWuTBCCm_eUY";  // Poné tu API Key aquí
     /**
     * Devuelve el summary del primer evento del día de hoy en el calendario dado.
     * Si no hay eventos hoy, devuelve cadena vacía "".
     *
     * @param calendarId El ID del calendario público de Google Calendar.
     * @param apiKey La API Key para acceso a Google Calendar API.
     * @return El summary del evento de hoy o "" si no hay eventos.
     * @throws Exception En caso de error de conexión o parseo.
     */
    public static String obtenerResumenEventoHoy(String calendarId, String apiKey) throws Exception {
        // Fecha hoy en zona de Buenos Aires (UTC-3)
        LocalDate today = LocalDate.now();
        OffsetDateTime timeMin = today.atStartOfDay().atOffset(ZoneOffset.of("-03:00"));
        OffsetDateTime timeMax = today.plusDays(1).atStartOfDay().atOffset(ZoneOffset.of("-03:00"));

        String url = String.format(
                "https://www.googleapis.com/calendar/v3/calendars/%s/events?key=%s&timeMin=%s&timeMax=%s&singleEvents=true&orderBy=startTime",
                calendarId,
                apiKey,
                timeMin.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                timeMax.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parsear JSON
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray items = root.getAsJsonArray("items");

        if (items == null || items.size() == 0) {
            return ""; // No hay eventos hoy
        }

        JsonObject event = items.get(0).getAsJsonObject();
        if (event.has("summary")) {
            return event.get("summary").getAsString();
        } else {
            return ""; // Evento sin summary
        }
    }



    @Override
    public String getBotUsername() {
        // Reemplaza con el nombre de usuario de tu bot
        return "comedorexactas_bot";
    }

    @Override
    public String getBotToken() {
        // Reemplaza con el token de tu bot
        return "7336576655:AAFub-EA7jcI07YYNN5Yb2hmzFvB4uYc_4o";
    }
    @Override
    public void onUpdateReceived(Update update){
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String resumen = "";
            try{
                resumen = obtenerResumenEventoHoy(calendarId, apiKey);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            String chatId = update.getMessage().getChatId().toString();


            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if(resumen.isEmpty()){
                message.setText("Hoy no hay menú del día. :(");
            }
            else{
                message.setText("Hola! El menú del día es: " + resumen.toLowerCase());
            }
            

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
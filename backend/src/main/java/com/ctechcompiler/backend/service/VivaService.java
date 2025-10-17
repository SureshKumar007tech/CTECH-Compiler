package com.ctechcompiler.backend.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VivaService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final OkHttpClient httpClient = new OkHttpClient();

    public String generateVivaQuestionFromCode(String studentCode) throws IOException {
        String prompt = "You are a computer science professor conducting a viva (oral exam). " +
                "Analyze the following Java code snippet and generate one single, concise, open-ended question " +
                "to test the student's understanding of their own code. Do not ask for definitions. " +
                "Ask about their specific implementation. Here is the code:\n\n" + studentCode;

        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONArray parts = new JSONArray();
        parts.put(textPart);

        JSONObject contents = new JSONObject();
        contents.put("parts", parts);

        JSONObject payload = new JSONObject();
        payload.put("contents", new JSONArray().put(contents));

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.get("application/json; charset=utf-f")
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=" + geminiApiKey)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + " - " + response.body().string());
            }

            // Parse the response to get the generated text
            JSONObject responseBody = new JSONObject(response.body().string());
            return responseBody.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim();
        }
    }
    public JSONObject evaluateVivaAnswer(String studentCode, String question, String answer) throws IOException {
        String prompt = "You are a computer science professor evaluating a student's viva answer. " +
                "Based on the original code, the question you asked, and the student's answer, " +
                "provide a score out of 10 and brief, constructive feedback. " +
                "Return your response as a JSON object with two keys: \"score\" (an integer) and \"feedback\" (a string).\n\n" +
                "Original Code:\n" + studentCode + "\n\n" +
                "Question Asked:\n" + question + "\n\n" +
                "Student's Answer:\n" + answer;

        // The API call structure is the same as generating a question
        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);
        JSONArray parts = new JSONArray().put(textPart);
        JSONObject contents = new JSONObject().put("parts", parts);
        JSONObject payload = new JSONObject().put("contents", new JSONArray().put(contents));

        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=" + geminiApiKey)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + " - " + response.body().string());
            }

            String responseString = response.body().string();
            // Extract the text part which should contain our JSON
            String jsonText = new JSONObject(responseString).getJSONArray("candidates")
                    .getJSONObject(0).getJSONObject("content")
                    .getJSONArray("parts").getJSONObject(0).getString("text");

            // Clean up the response from the model, which might include markdown backticks
            jsonText = jsonText.replace("```json", "").replace("```", "").trim();

            return new JSONObject(jsonText); // Return the JSON object with score and feedback
        }
    }
}

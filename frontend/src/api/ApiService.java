package api;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static String authToken = null; // To store the JWT token

    // Private helper for making API calls
    private static String makeRequest(String method, String endpoint, JSONObject jsonBody, boolean requiresAuth) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        if (requiresAuth) {
            if (authToken == null) {
                throw new Exception("Authentication token is missing. Please log in again.");
            }
            con.setRequestProperty("Authorization", "Bearer " + authToken);
        }

        if (jsonBody != null) {
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Check for a successful HTTP status code
            if (code >= 200 && code < 300) {
                return response.toString();
            } else {
                // Try to parse error message from JSON response, otherwise use the full response
                try {
                    JSONObject errorJson = new JSONObject(response.toString());
                    if (errorJson.has("message")) {
                        throw new Exception(errorJson.getString("message") + " (HTTP " + code + ")");
                    }
                } catch (Exception e) {
                    // Fallback if the error response is not JSON
                }
                throw new Exception("Error: " + response.toString() + " (HTTP " + code + ")");
            }
        }
    }

    public static JSONObject login(String username, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        String response = makeRequest("POST", "/auth/login", body, false);
        JSONObject responseJson = new JSONObject(response);

        // On successful login, extract and store the token
        if (responseJson.has("token")) {
            authToken = responseJson.getString("token");
        } else {
            // This case handles your old AuthResponse which didn't have a token.
            // We should update the backend to always return the token.
            // For now, we will proceed, but authenticated calls will fail.
            System.err.println("Warning: Login response did not contain a JWT token.");
        }
        return responseJson;
    }

    public static String registerMentor(String username, String password, String name) throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("name", name);
        return makeRequest("POST", "/auth/register-mentor", body, false);
    }

    public static String registerStudent(String mentorUsername, String mentorPassword,
                                         String studentUsername, String studentPassword, String studentName) throws Exception {
        JSONObject body = new JSONObject();
        body.put("mentorUsername", mentorUsername);
        body.put("mentorPassword", mentorPassword);
        body.put("studentUsername", studentUsername);
        body.put("studentPassword", studentPassword);
        body.put("studentName", studentName);
        return makeRequest("POST", "/auth/register-student", body, false);
    }

    // --- MENTOR APIS (NOW REQUIRE AUTH) ---
    public static String assignSnippet(String title, String snippet) throws Exception {
        JSONObject body = new JSONObject();
        // The body no longer sends the student's username
        body.put("title", title);
        body.put("snippet", snippet);
        return makeRequest("POST", "/mentor/assign-snippet", body, true);
    }

    // --- STUDENT APIS (NOW REQUIRE AUTH) ---
    public static String getAvailableSnippets() throws Exception {
        // This is a GET request, so it has no body (the third argument is null)
        return makeRequest("GET", "/student/snippets", null, true);
    }
    public static String submitCode(String username, String snippetTitle, String submittedCode) throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("snippetTitle", snippetTitle);
        body.put("submittedCode", submittedCode);
        return makeRequest("POST", "/student/submit", body, true);
    }

    //  for calling the AI viva service
    public static String generateVivaQuestion(String submittedCode) throws Exception {
        JSONObject body = new JSONObject();
        body.put("code", submittedCode);
        return makeRequest("POST", "/viva/generate-question", body, true);
    }
    public static String evaluateViva(long submissionId, String code, String question, String answer) throws Exception {
        JSONObject body = new JSONObject();
        body.put("submissionId", submissionId);
        body.put("code", code);
        body.put("question", question);
        body.put("answer", answer);
        return makeRequest("POST", "/viva/evaluate-answer", body, true);
    }
    public static String getAllSubmissions() throws Exception {
        // This is a GET request to a protected mentor endpoint
        return makeRequest("GET", "/mentor/submissions", null, true);
    }
}

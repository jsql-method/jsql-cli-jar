package io.jsql.taskRunners.common;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import io.jsql.taskRunners.common.run.Process;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Hasher {

    // Parser
    private Parser parser;

    // Substitutes
    private Map<String, Map<String, String>> substitutes;

    // Api Key
    private final String apiKey;

    // Session
    private final String user;

    // Source files pattern
//    private final String pattern;

    // Distribution search directory
//    private final String dist;

    // Statistics
    private Long hashingExecTime;
    private List<Long> queriesExecTimes;
    private List<Long> queriesParseTimes;

    // Konstruktor
    public Hasher(String apiKey, String user, String src, String target) {
        // Finals initialization
        this.apiKey = apiKey;
        this.user = user;
//        this.pattern = pattern;
//        this.dist = dist;

        // Timer evaluation
        long timer0 = System.currentTimeMillis();

        // Instantiate parser
        this.parser = new Parser(src, target);
        this.parser.run();

        // Instantiate statistics
        this.hashingExecTime = new Long(0);
        this.queriesExecTimes = new ArrayList<>();
        this.queriesParseTimes = new ArrayList<>();

        // Prepare for substitution
        Retrieve();

        // Hash files
        Hash();

        // Store time result
        this.hashingExecTime = System.currentTimeMillis() - timer0;

        // Print statistics
        this.Print();
    }

    // Metoda wypisuje statystyki dotyczące wykonania operacji
    @SuppressWarnings("unchecked")
    private void Print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------------------------------------");
        sb.append("\nExecution statistics");
        sb.append("\n\tFiles parsed: " + parser.getScriptFiles().size());
        sb.append("\n\tCharacters parsed: " + parser.getScriptFiles()
                .entrySet()
                .stream()
                .mapToInt(e -> e.getValue().length())
                .sum());
        sb.append("\n\tUnique queries detected: " + parser.getQueries()
                .entrySet()
                .stream()
                .mapToInt(e -> e.getValue().size())
                .sum()
        );
        sb.append("\n\tExecuted in: " + hashingExecTime + " milliseconds");
        sb.append("\n\tQueries completion times: " + (queriesExecTimes.isEmpty() ? "None" : ""));
        for (int i = 0; i < queriesExecTimes.size(); ++i) {
            sb.append("\n\t\tQuery [" + (i + 1) + "]: Request: " + queriesExecTimes.get(i) + " milliseconds\t| Parse: " + queriesParseTimes.get(i) + " milliseconds");
        }
        sb.append("\n----------------------------------------");
        System.out.println(sb);
    }

    // Metoda przygotowuje zapytania oraz przetwarza żądania do api
    @SuppressWarnings("unchecked")
    private void Retrieve() {
        substitutes = parser.getQueries().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> {
                                    try {
                                        return SendQueries(e.getValue());
                                    } catch (IOException ex) {
                                        throw new IllegalArgumentException("Result's value is not a proper hashmap!");
                                    }
                                }

                        )
                );
    }

    // Metoda modyfikuje pliki na podstawie uzyskanych wyników
    private void Hash() {
        // Prepare content holder
        String content = "";

        // Substitute
        for (Map.Entry<String, Map<String, String>> e : substitutes.entrySet()) {
            System.out.println("e: " + e.getKey());
            content = parser.getScriptFiles().get(e.getKey());

            for (Map.Entry<String, String> v : e.getValue().entrySet()) {
                System.out.println("\tk: " + v.getKey());
                System.out.println("\tv: " + v.getValue());
                System.out.println("");
                content = content.replace("\"" + v.getKey() + "\"", "\"" + v.getValue() + "\"");
                content = content.replace("'" + v.getKey() + "'", "'" + v.getValue() + "'");

            }

            // Write replaced files
            ReplaceFile(e.getKey(), content);
        }
    }

    // Metoda zapisuje zmodyfikowane dane do pliku
    private void ReplaceFile(String file, String content) {

        try {
            Path p = Paths.get(file);

            String filePath = null;
            if (Process.TARGET.contains(".")) {
                filePath = Process.TARGET;
            } else {
                filePath = Process.TARGET + "/" + p.getFileName().toString();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false));

            bw.write(content);
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Exception at ReplaceFile: " + ex.toString());
        }
    }

    // Metoda wysyła zapytanie do api w celu uzyskania zahaszowanych zapytań
    @SuppressWarnings("unchecked")
    private HashMap<String, String> SendQueries(HashSet<String> queries) throws IOException {

        System.out.println("apiKey : " + apiKey);
        System.out.println("user : " + user);
        // Prepare request data
        String requestData = new Gson().toJson(queries);

        // Query execution time
        long QueryTimer0 = System.currentTimeMillis();
        // Prepare response object
        Map<String, Object> Response =
                Request.Post("http://softwarecartoon.com:9391/api/request/hashes")
                        .addHeader("apiKey", apiKey)
                        .addHeader("memberKey", user)
                        .bodyString(requestData, ContentType.APPLICATION_JSON)
                        .execute().handleResponse(

                        response -> {
                            // Instantiate status and entity variables
                            StatusLine statusLine = response.getStatusLine();
                            HttpEntity entity = response.getEntity();

                            // Check entity existance
                            if (entity == null) {
                                throw new ClientProtocolException("Response contains no content");
                            }

                            // Prepare content type
                            ContentType contentType = ContentType.getOrDefault(entity);

                            // Prepare result Json string
                            String Json = IOUtils.toString(entity.getContent(), contentType.getCharset());

                            // Check response status code
                            if (statusLine.getStatusCode() >= 300) {
                                throw new IllegalArgumentException("Request returned code: " + statusLine.getStatusCode() + "\n" + statusLine.getReasonPhrase() + "\n Response: " + Json + "\n Request: " + requestData);
                            }

                            // Return HashMapped object
                            return new Gson().fromJson(Json, HashMap.class);
                        }

                );

        // Store query time
        this.queriesExecTimes.add((System.currentTimeMillis() - QueryTimer0));

        // Query parse time
        QueryTimer0 = System.currentTimeMillis();

        // Parse received data
        if (Response.containsKey("data")) {
            // Prepare data object
            Object raw = Response.get("data");

            // Check instance type
            if (!(raw instanceof ArrayList)) {
                throw new IllegalArgumentException("Response 'data' is not a proper ArrayList castable object");
            }

            // Check further type or element 0
            if (!(
                    ((ArrayList) raw).isEmpty() || ((ArrayList) raw).get(0) instanceof LinkedTreeMap
            )
                    ) {
                throw new IllegalArgumentException("Response 'data's list object is not a proper LinkedTreeMap castable object");
            }

            // Cast data into determined arraylist
            ArrayList<LinkedTreeMap> data = (ArrayList) raw;

            // Prepare hashmap with results
            HashMap<String, String> result = new HashMap<>();

            // Parse data
            data.forEach(
                    e -> {

                        // Check if 'query' and 'token' values are mapped
                        if (e.containsKey("query") && e.containsKey("token")) {
                            result.put((String) e.get("query"), (String) e.get("token"));
                        } else {
                            throw new IllegalArgumentException("Response 'data's list object does not contain 'query' or 'token' keys");
                        }

                    }
            );

            // Store query time
            this.queriesParseTimes.add((System.currentTimeMillis() - QueryTimer0));

            // Return parse result
            return result;
        } else {
            // Throw error
            throw new IllegalArgumentException("Response did not contain 'data' object" + Response);
        }
    }
}

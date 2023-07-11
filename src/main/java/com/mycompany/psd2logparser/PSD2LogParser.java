/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.psd2logparser;

/**
 *
 * @author a12_vts
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PSD2LogParser {
    public static void main(String[] args) {
        String logFilePath = "D:\\a12_vts\\Desktop\\Projects\\PSD2LogParser\\log.txt";
        String logData = readLogFile(logFilePath);

        // Extract error messages and their associated status
        List<ErrorMessage> errorMessages = extractErrorMessages(logData);

        // Display error messages with their associated status (without duplicates)
        Set<String> uniqueMessages = new HashSet<>();
        for (ErrorMessage errorMessage : errorMessages) {
            String message = errorMessage.getStatus() + errorMessage.getCode() + errorMessage.getText();
            if (uniqueMessages.add(message)) {
                System.out.println("Status: " + errorMessage.getStatus());
                System.out.println("Code: " + errorMessage.getCode());
                System.out.println("Text: " + errorMessage.getText());
                System.out.println();
            }
        }

        // Save output to a text file (without duplicates)
        String outputFilePath = "D:\\a12_vts\\Desktop\\Projects\\PSD2LogParser\\output.txt";
        saveOutputToFile(uniqueMessages, outputFilePath);
    }

    private static String readLogFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static List<ErrorMessage> extractErrorMessages(String logData) {
        Pattern responsePattern = Pattern.compile("={15}/Response={15}");
        String[] responseSections = responsePattern.split(logData);

        List<ErrorMessage> errorMessages = new ArrayList<>();

        for (String response : responseSections) {
            String status = extractStatus(response);
            List<ErrorMessage> errors = extractErrors(response);

            if (status != null && errors != null) {
                for (ErrorMessage error : errors) {
                    error.setStatus(status);
                    errorMessages.add(error);
                }
            }
        }

        return errorMessages;
    }

    private static String extractStatus(String response) {
        Pattern statusPattern = Pattern.compile("Status: (\\d+)");
        Matcher matcher = statusPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static List<ErrorMessage> extractErrors(String response) {
        List<ErrorMessage> errorMessages = new ArrayList<>();

        Pattern errorPattern = Pattern.compile("\"code\": \"(.*?)\",\\s+\"text\": \"(.*?)\"");
        Matcher matcher = errorPattern.matcher(response);

        while (matcher.find()) {
            String errorCode = matcher.group(1);
            String errorText = matcher.group(2).replaceAll("\\s+", " ");

            ErrorMessage errorMessage = new ErrorMessage(null, errorCode, errorText);
            errorMessages.add(errorMessage);
        }

        return errorMessages;
    }

    private static void saveOutputToFile(Set<String> uniqueMessages, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String message : uniqueMessages) {
                writer.write("Status: " + message.substring(0, 3));
                writer.newLine();
                writer.write("Code: " + message.substring(3, 17));
                writer.newLine();
                writer.write("Text: " + message.substring(17));
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ErrorMessage {
        private String status;
        private final String code;
        private final String text;

        public ErrorMessage(String status, String code, String text) {
            this.status = status;
            this.code = code;
            this.text = text;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }
}
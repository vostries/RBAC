import java.util.ArrayList;
import java.util.List;

final class FormatUtils {

    private FormatUtils() {
    }

    static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null) {
            headers = new String[0];
        }
        if (rows == null) {
            rows = List.of();
        }
        int cols = headers.length;
        for (String[] row : rows) {
            if (row.length > cols) {
                cols = row.length;
            }
        }
        int[] widths = new int[cols];
        for (int c = 0; c < cols; c++) {
            widths[c] = 1;
        }
        for (int c = 0; c < headers.length; c++) {
            widths[c] = Math.max(widths[c], headers[c] != null ? headers[c].length() : 0);
        }
        for (String[] row : rows) {
            for (int c = 0; c < row.length; c++) {
                widths[c] = Math.max(widths[c], row[c] != null ? row[c].length() : 0);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(buildBorder(widths));
        sb.append(System.lineSeparator());
        // header row
        sb.append("|");
        for (int c = 0; c < cols; c++) {
            String h = c < headers.length && headers[c] != null ? headers[c] : "";
            sb.append(" ").append(padRight(h, widths[c])).append(" |");
        }
        sb.append(System.lineSeparator());
        sb.append(buildBorder(widths));
        sb.append(System.lineSeparator());
        for (String[] row : rows) {
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                String cell = c < row.length && row[c] != null ? row[c] : "";
                sb.append(" ").append(padRight(cell, widths[c])).append(" |");
            }
            sb.append(System.lineSeparator());
        }
        sb.append(buildBorder(widths));
        return sb.toString();
    }

    private static String buildBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    static String formatBox(String text) {
        if (text == null) {
            text = "";
        }
        String[] lines = text.split("\\R");
        int max = 0;
        for (String line : lines) {
            max = Math.max(max, line.length());
        }
        String border = "+" + "-".repeat(max + 2) + "+";
        StringBuilder sb = new StringBuilder();
        sb.append(border).append(System.lineSeparator());
        for (String line : lines) {
            sb.append("| ").append(padRight(line, max)).append(" |").append(System.lineSeparator());
        }
        sb.append(border);
        return sb.toString();
    }

    static String formatHeader(String text) {
        if (text == null) {
            text = "";
        }
        String cleaned = text.trim();
        String line = "=".repeat(Math.max(3, cleaned.length() + 4));
        return line + System.lineSeparator() +
                "= " + cleaned + " =" + System.lineSeparator() +
                line;
    }

    static String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (maxLength <= 0 || text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    static String padRight(String text, int length) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= length) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    static String padLeft(String text, int length) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= length) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() + text.length() < length) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }
}


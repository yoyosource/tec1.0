package tec.codescanner.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Compression {

    private List<String> strings;
    private List<Count> counts = new ArrayList<>();

    public Compression(String content, String name) {
        strings = Arrays.stream(splitString(content, new String[]{" ", "\n", ",", ".", "!", "?"}, true, false)).collect(Collectors.toList());
        count();
        if (counts.isEmpty()) {
            return;
        }

        long totalLoss = 0;
        for (Count count : counts) {
            totalLoss += count.getLoss();
        }

        System.out.println(name);
        System.out.println(counts.size() + " " + counts.get(0).getByteRange() +  " " + counts);
        System.out.println(totalLoss + " " + (content.length() - totalLoss) + " " + content.length());
        System.out.println();
    }

    private void count() {
        for (String string : strings) {
            if (string.trim().isEmpty()) {
                continue;
            }
            if (counts.contains(new Count(string))) {
                counts.get(counts.indexOf(new Count(string))).increment();
            } else {
                counts.add(new Count(string));
            }
        }
        for (Count count : counts) {
            count.setByteRange(counts.size());
        }
        for (int i = counts.size() - 1; i >= 0; i--) {
            if (counts.get(i).getLoss() <= 0) {
                counts.remove(i);
            }
        }
        for (Count count : counts) {
            count.setByteRange(counts.size());
        }
        counts.sort(Comparator.comparingLong(Count::getLoss));
    }

    private static String[] splitString(String string, String[] splitStrings, boolean reviveSplitted, boolean addToLast) {
        char[] chars = string.toCharArray();
        if (chars.length == 0) {
            throw new NullPointerException("No String");
        }
        if (splitStrings.length == 0) {
            throw new NullPointerException("No Split Strings");
        }

        List<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        int lastSplit = 0;

        boolean inString = false;
        boolean escape = false;

        while (i < chars.length) {
            int splitStringTest = 0;
            char c = chars[i];
            if (c == '"' && !escape) {
                inString = !inString;
            }
            if (inString && c == '\\') {
                escape = true;
                i++;
                stringBuilder.append(c);
                continue;
            }
            if (inString) {
                i++;
                stringBuilder.append(c);
                continue;
            }
            String s = "";
            for (String st : splitStrings) {
                StringBuilder sb = new StringBuilder();
                int index = i;
                int currentIndex = i;
                while (currentIndex < chars.length && currentIndex < index + st.length()) {
                    sb.append(chars[currentIndex]);
                    currentIndex++;
                }
                if (sb.toString().equals(st)) {
                    if (s.length() == 0) {
                        s = st;
                    }
                    splitStringTest++;
                }
            }

            if (splitStringTest == 0) {
                stringBuilder.append(c);
            } else {
                i += s.length() - 1;
                if (stringBuilder.length() == 0) {
                    if (reviveSplitted && !addToLast) {
                        words.add(s);
                    } else if (reviveSplitted && addToLast) {
                        words.add(stringBuilder + s);
                    }
                    stringBuilder = new StringBuilder();
                    lastSplit = i;
                    i++;
                    continue;
                }
                if (reviveSplitted) {
                    if (addToLast) {
                        words.add(stringBuilder.toString() + s);
                    } else {
                        words.add(stringBuilder.toString());
                        words.add(s);
                    }
                } else {
                    words.add(stringBuilder.toString());
                }
                stringBuilder = new StringBuilder();
                lastSplit = i;
            }
            i++;
        }
        if (lastSplit != string.length()) {
            if (stringBuilder.length() == 0) {
                return words.toArray(new String[0]);
            }
            words.add(stringBuilder.toString());
        }
        return words.toArray(new String[0]);
    }

}

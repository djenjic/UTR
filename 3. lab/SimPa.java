import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        final String dollarSign = "$";

        Function<String, List<String>> getContent = s -> {
            List<String> ret = new ArrayList<>();
            Pattern pattern = Pattern.compile("(,)|(->)");
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                ret.add(matcher.group());
            }
            return ret;
        };

        Map<Tuple<String, String, String>, Pair<String, String>> transitions = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        String s = scanner.nextLine();
        Pattern pattern = Pattern.compile("\\|");
        List<List<String>> testCases = Arrays.stream(pattern.split(s))
                .map(getContent)
                .collect(Collectors.toList());

        s = scanner.nextLine();
        List<String> states = getContent.apply(s);

        s = scanner.nextLine();
        List<String> alphabet = getContent.apply(s);

        s = scanner.nextLine();
        List<String> stack = getContent.apply(s);

        s = scanner.nextLine();
        List<String> acceptedState = getContent.apply(s);

        s = scanner.nextLine();
        String startState = s;

        s = scanner.nextLine();
        String startStack = s;

        while (scanner.hasNextLine()) {
            s = scanner.nextLine();
            List<String> getTransition = getContent.apply(s);

            String currentState = getTransition.get(0);
            String currentKey = getTransition.get(1);
            String currentStack = getTransition.get(2);
            String currentNextState = getTransition.get(3);
            String currentNextStack = getTransition.get(4);

            Collections.reverse(currentStack);
            Collections.reverse(currentNextStack);

            transitions.put(new Tuple<>(currentState, currentKey, currentStack), new Pair<>(currentNextState, currentNextStack));
        }

        Function<List<String>, String> join = vecString -> vecString.stream()
                .collect(Collectors.joining(","));

        BiFunction<String, String, Boolean> isSuffix = (s1, t) -> {
            if (t.equals(dollarSign)) {
                return true;
            }
            if (s1.length() < t.length()) {
                return false;
            }
            return s1.substring(s1.length() - t.length()).equals(t);
        };

        Function<String, String, String, Tuple3<String, String, String>> findBestCandidate = (currentState, currentSymbol, currentStack) -> {
            List<Map.Entry<Tuple<String, String, String>, Pair<String, String>>> entries = new ArrayList<>(transitions.entrySet());
            for (int i = entries.size() - 1; i >= 0; i--) {
                Map.Entry<Tuple<String, String, String>, Pair<String, String>> entry = entries.get(i);
                Tuple<String, String, String> key = entry.getKey();
                if (key.getItem1().equals(currentState) && key.getItem2().equals(currentSymbol) && isSuffix.apply(currentStack, key.getItem3())) {
                    return new Tuple3<>(entry.getValue().getItem1(), key.getItem3(), entry.getValue().getItem2());
                }
            }
            return new Tuple3<>("", "", "");
        };

        BiFunction<String, String, Pair<String, Boolean>> replaceSuffix = (s1, suffix) -> {
            String newSuffix = suffix;
            StringBuilder stringBuilder = new StringBuilder(s1);
            if (suffix.equals(dollarSign)) {
                if (!newSuffix.equals(dollarSign)) {
                    stringBuilder.append(newSuffix);
                }
                return new Pair<>(stringBuilder.toString(), true);
            }
            if (newSuffix.equals(dollarSign)) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                return new Pair<>(stringBuilder.toString(), true);
            }
            if (stringBuilder.length() >= suffix.length() && stringBuilder.substring(stringBuilder.length() - suffix.length()).equals(suffix)) {
                stringBuilder.delete(stringBuilder.length() - suffix.length(), stringBuilder.length());
                stringBuilder.append(newSuffix);
                return new Pair<>(stringBuilder.toString(), true);
            }
            return new Pair<>(stringBuilder.toString(), false);
        };

        Function<String, String, String, String, List<String>, Boolean> processTransition = (currStack, replacedStack, nextStack, currState, track) -> {
            Pair<String, Boolean> pair = replaceSuffix.apply(currStack, replacedStack);
            currStack = pair.getItem1();
            if (pair.getItem2()) {
                currState = nextStack;
                Collections.reverse(currStack);
                track.add(currState + "#" + (currStack.isEmpty() ? dollarSign : currStack));
                Collections.reverse(currStack);
                return true;
            }
            return false;
        };

        List<List<String>> testResults = new ArrayList<>();
        for (List<String> testCase : testCases) {
            String currStack = startStack;
            String currState = startState;
            List<String> track = new ArrayList<>();

            Collections.reverse(currStack);
            track.add(currState + "#" + currStack);
            Collections.reverse(currStack);

            for (String symbol : testCase) {

                while (true) {
                    Tuple3<String, String, String> tuple = findBestCandidate.apply(currState, dollarSign, currStack);
                    if (tuple.getItem1().isEmpty()) {
                        break;
                    }
                    boolean replaced = processTransition.apply(currStack, tuple.getItem2(), tuple.getItem3(), currState, track);
                    if (!replaced) {
                        break;
                    }
                }

                Tuple3<String, String, String> tuple = findBestCandidate.apply(currState, symbol, currStack);
                if (tuple.getItem1().isEmpty()) {
                    track.add("fail");
                    break;
                }
                processTransition.apply(currStack, tuple.getItem2(), tuple.getItem3(), currState, track);
            }

            while (acceptedState.stream().noneMatch(state -> state.equals(currState))) {
                Tuple3<String, String, String> tuple = findBestCandidate.apply(currState, dollarSign, currStack);
                if (tuple.getItem1().isEmpty()) {
                    break;
                }
                processTransition.apply(currStack, tuple.getItem2(), tuple.getItem3(), currState, track);
            }

            if (acceptedState.stream().anyMatch(state -> state.equals(currState)) && !track.get(track.size() - 1).equals("fail")) {
                track.add("1");
            } else {
                track.add("0");
            }

            testResults.add(track);
        }

        for (List<String> testResult : testResults) {
            System.out.println(join.apply(testResult, "|"));
        }
    }

    static class Tuple<T1, T2, T3> {
        private final T1 item1;
        private final T2 item2;
        private final T3 item3;

        public Tuple(T1 item1, T2 item2, T3 item3) {
            this.item1 = item1;
            this.item2 = item2;
            this.item3 = item3;
        }

        public T1 getItem1() {
            return item1;
        }

        public T2 getItem2() {
            return item2;
        }

        public T3 getItem3() {
            return item3;
        }
    }

    static class Pair<T1, T2> {
        private final T1 item1;
        private final T2 item2;

        public Pair(T1 item1, T2 item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        public T1 getItem1() {
            return item1;
        }

        public T2 getItem2() {
            return item2;
        }
    }

    static class Tuple3<T1, T2, T3> {
        private final T1 item1;
        private final T2 item2;
        private final T3 item3;

        public Tuple3(T1 item1, T2 item2, T3 item3) {
            this.item1 = item1;
            this.item2 = item2;
            this.item3 = item3;
        }

        public T1 getItem1() {
            return item1;
        }

        public T2 getItem2() {
            return item2;
        }

        public T3 getItem3() {
            return item3;
        }
    }
}
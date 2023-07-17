#include <iostream>
#include <regex>
#include <string>
#include <algorithm>
#include <map>
#include <numeric>

using namespace std;

int main() {

    const string dollarSign = "$";

    auto getContent = [](const string &s) {
        vector<string> ret;
        regex re("(,)|(->)");
        for (sregex_token_iterator it(s.begin(), s.end(), re, -1); it != sregex_token_iterator(); ++it)
            ret.emplace_back(*it);
        return ret;
    };

    map<tuple<string,string,string>, pair<string,string> > transitions;

    string s;

    regex re("\\|");
    vector<vector<string> > testCases;
    getline(cin, s);
    for (sregex_token_iterator it(s.begin(), s.end(), re, -1); it != sregex_token_iterator(); ++it)
        testCases.emplace_back(getContent(*it));

    getline(cin, s);
    vector<string> states = getContent(s);
    getline(cin, s);
    vector<string> alphabet = getContent(s);
    getline(cin, s);
    vector<string> stack = getContent(s);
    getline(cin, s);
    vector<string> acceptedState = getContent(s);
    getline(cin, s);
    string startState = s;
    getline(cin, s);
    string startStack = s;


    while (getline(cin, s)) {
        vector<string> getTransition = getContent(s);

        string currentState = getTransition[0];
        string currentKey = getTransition[1];
        string currentStack = getTransition[2];
        string currentNextState = getTransition[3];
        string currentNextStack = getTransition[4];

        reverse(currentStack.begin(), currentStack.end());
        reverse(currentNextStack.begin(), currentNextStack.end());

        transitions[{currentState, currentKey, currentStack}] = {currentNextState, currentNextStack};
    }

    auto join = [&](vector<string>& vecString, const string& delimiter = ",") {
        string ret = accumulate(vecString.begin(), vecString.end(), string(), [&delimiter](string s,  string t) {
            return s.empty() ? t : s + delimiter + t;
        });
        return ret;
    };

    auto isSuffix = [&dollarSign](const string &s, const string &t) {
        if (t == dollarSign) return true;
        if ( (int) s.size() < (int) t.size()) return false;
        return s.substr((int) s.size() - (int) t.size()) == t;
    };

    auto findBestCandidate = [&transitions, &isSuffix](const string &currentState, const string &currentSymbol, const string &currentStack) {
        auto start = find_if(transitions.rbegin(), transitions.rend(), [&currentState, &currentSymbol, &currentStack, &isSuffix](const auto &t) {
            return get<0>(t.first) == currentState && get<1>(t.first) == currentSymbol && isSuffix(currentStack, get<2>(t.first));
        });
        if (start == transitions.rend() || get<0>(start->first) != currentState || get<1>(start->first) != currentSymbol) return tuple<string, string, string>("", "", "");
        return make_tuple(start->second.first, get<2>(start->first) ,start->second.second);
    };

   auto replaceSuffix = [&dollarSign](string &s, const string &suffix, string &newSuffix) {
        if (suffix == dollarSign) { if(newSuffix != dollarSign) s += newSuffix; return true; }
        if (newSuffix == dollarSign) {
            if (!s.empty()) s.pop_back();
            return true;
        }
        if ((int) s.size() < (int) suffix.size()) return false;
        if (s.substr((int) s.size() - (int) suffix.size()) == suffix) {
            s.erase((int) s.size() - (int) suffix.size());
            s += newSuffix;
            return true;
        }
        return false;
    };

   auto processTransition = [&replaceSuffix](string& currStack, string& replacedStack, string& nextStack, string& currState, vector<string>& track, string &nextState) {
       replaceSuffix(currStack, replacedStack, nextStack);
       currState = nextState;
       reverse(currStack.begin(), currStack.end());
       track.emplace_back(currState + "#" + (currStack.empty() ? "$" : currStack));
       reverse(currStack.begin(), currStack.end());
   };

    vector<vector<string> > testResults;
    for (auto& testCase : testCases) {
        string currStack = startStack;
        string currState = startState;
        vector<string> track;

        reverse(currStack.begin(), currStack.end());
        track.emplace_back(currState + "#" + currStack);
        reverse(currStack.begin(), currStack.end());

        for (auto& symbol : testCase) {

            while (true) {
                auto [nextState, replacedStack, nextStack] = findBestCandidate(currState, dollarSign, currStack);
                if (nextState.empty()) break;
                processTransition(currStack, replacedStack, nextStack, currState, track, nextState);
            }

            auto [nextState, replacedStack, nextStack] = findBestCandidate(currState, symbol, currStack);
            if (nextState.empty()) {
                track.emplace_back("fail");
                break;
            }
            processTransition(currStack, replacedStack, nextStack, currState, track, nextState);
        }

        while ( find(acceptedState.begin(), acceptedState.end(), currState) == acceptedState.end() ) {
            auto [nextState, replacedStack, nextStack] = findBestCandidate(currState, dollarSign, currStack);
            if (nextState.empty()) break;
            processTransition(currStack, replacedStack, nextStack, currState, track, nextState);
        }

        if (find(acceptedState.begin(), acceptedState.end(), currState) != acceptedState.end() && track.back() != "fail")
            track.emplace_back("1");
        else track.emplace_back("0");

        testResults.emplace_back(move(track));
    }

    for (auto& testResult : testResults)
        cout << join(testResult, "|") << endl;

    return 0;
}


#include <iostream>
#include <map>
#include <cctype>
#include <algorithm>
#include <vector>

using namespace std;

int main() {


    auto splitInsert = [&](string & s, string::iterator & it, string & replaced) {
        auto firstPart = s.substr(0, it - s.begin());
        auto secondPart = s.substr(it - s.begin());
        secondPart.erase(secondPart.begin());
        s = firstPart + replaced + secondPart;
    };


    vector< tuple<string, string, string> > V = {
            make_tuple("S", "a", "aAB"),
            make_tuple("S", "b", "bBA"),
            make_tuple("A", "b", "bC"),
            make_tuple("A", "a", "a"),
            make_tuple("B", "c", "ccSbc"),
            make_tuple("B", "$", ""),
            make_tuple("C", "any", "AA"),
            make_tuple("", "", "")
    };

    auto findPerfectCandidate = [&](char upper, char sign) {
        auto it = find_if(V.begin(), V.end(), [&](auto & t) { return get<0>(t)[0] == upper && (get<1>(t)[0] == sign || get<1>(t) == "any" || get<1>(t)=="$");});
        return it == V.end() ? *V.rbegin() : *it;
    };

    string s;
    cin >> s;

    s += '$';

    string constructedString = "S";
    string output;

    for (auto sit = s.begin(); sit != s.end(); ) {

        while ( true ) {
            auto it = find_if(constructedString.begin(), constructedString.end(), [&](char k) { return isupper(k); });
            auto [solChar, inputChar, replaced] = findPerfectCandidate(*it, *sit);
            output += *it;
            if ( *it != solChar[0] ) goto end;
            splitInsert(constructedString, it, replaced);
            if (solChar != "C") break;
        }

        auto it = find_if(constructedString.begin(), constructedString.end(), [&](char k) { return isupper(k); });
        if (it == constructedString.end()) break;
        sit = s.begin() + (it - constructedString.begin());

        auto sitColne = sit;
        auto itClone = it;
        while (sitColne != s.begin() && itClone != constructedString.begin()) { sitColne--; itClone--; if (*sitColne != *itClone) goto end; }

    }

    end:

    cout << output << endl;
    cout << ((  (constructedString + "$") == s) ? "DA" : "NE");

    return 0;
}
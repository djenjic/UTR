import java.util.*;

public class MinDka {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        LinkedHashSet<String> skupStanja = new LinkedHashSet<>(List.of(input.split(",")));
        input = sc.nextLine();
        LinkedHashSet<String> skupSimbola = new LinkedHashSet<>(List.of(input.split(",")));
        input = sc.nextLine();
        LinkedHashSet<String> prihvatljivaStanja = new LinkedHashSet<>(List.of(input.split(",")));
        input = sc.nextLine();
        String pocetnoStanje = input;
        LinkedHashMap<String, String> skupPrijelaza = new LinkedHashMap<>();
        while(sc.hasNextLine()){
            input = sc.nextLine();
            String[] tren_sim_novo = input.split("->");
            skupPrijelaza.put(tren_sim_novo[0], tren_sim_novo[1]);
        }

        //izbacivanje nedohvatljivih stanja
        LinkedHashSet<String> dohvatljivaStanja = new LinkedHashSet<>();
        LinkedHashSet<String> novaStanja = new LinkedHashSet<>();
        dohvatljivaStanja.add(pocetnoStanje);
        novaStanja.add(pocetnoStanje);
        do{
            LinkedHashSet<String> temp = new LinkedHashSet<>();
            for(String stanje : novaStanja){
                for(String simbol : skupSimbola){
                    temp.add(skupPrijelaza.get(stanje + "," + simbol));
                }
            }
            novaStanja.clear();
            temp.removeAll(dohvatljivaStanja);
            novaStanja.addAll(temp);
            dohvatljivaStanja.addAll(novaStanja);
        }while(!novaStanja.isEmpty());

//        minimiziranje and stuff
        LinkedHashSet<String> dohvatljivaPrihvatljivaStanja = new LinkedHashSet<>(prihvatljivaStanja);
        dohvatljivaPrihvatljivaStanja.retainAll(dohvatljivaStanja);
        LinkedHashSet<String> dohvatljivaNeprihvatljivaStanja = new LinkedHashSet<>(dohvatljivaStanja);
        dohvatljivaNeprihvatljivaStanja.removeAll(prihvatljivaStanja);
        LinkedHashSet<LinkedHashSet<String>> W = new LinkedHashSet<>();
        LinkedHashSet<LinkedHashSet<String>> P = new LinkedHashSet<>();
        LinkedHashSet<LinkedHashSet<String>> Piterator = new LinkedHashSet<>();
        P.add(dohvatljivaPrihvatljivaStanja);
        P.add(dohvatljivaNeprihvatljivaStanja);
        Piterator.add(dohvatljivaPrihvatljivaStanja);
        Piterator.add(dohvatljivaNeprihvatljivaStanja);
        W.add(dohvatljivaPrihvatljivaStanja);
        W.add(dohvatljivaNeprihvatljivaStanja);
        while (!W.isEmpty()) {
            LinkedHashSet<String> A = W.iterator().next();
            W.remove(A);
            for (String symbol : skupSimbola) {
                LinkedHashSet<String> X = new LinkedHashSet<>();
                for (String stanje : dohvatljivaStanja) {
                    if(A.contains(skupPrijelaza.get(stanje + "," + symbol))){
                        X.add(stanje);
                    }
                }
                for (LinkedHashSet<String> Y : Piterator) {
                    LinkedHashSet<String> presjek = new LinkedHashSet<>(Y);
                    presjek.retainAll(X);
                    LinkedHashSet<String> razlika = new LinkedHashSet<>(Y);
                    razlika.removeAll(X);
                    if (!presjek.isEmpty() && !razlika.isEmpty()) {
                        P.remove(Y);
                        P.add(presjek);
                        P.add(razlika);
                        if (W.contains(Y)) {
                            W.remove(Y);
                            W.add(presjek);
                            W.add(razlika);
                        } else {
                            if (presjek.size() <= razlika.size()) {
                                W.add(presjek);
                            } else {
                                W.add(razlika);
                            }
                        }
                    }
                }
                Piterator.clear();
                Piterator.addAll(P);
            }
        }

        TreeSet<String> ispisStanja = new TreeSet<>();
        for(LinkedHashSet<String> ekvivaletna : P){
            String prvo = "";
            boolean prvi = true;
            for(String stanje : ekvivaletna){
                if(stanje.isEmpty())
                    continue;
                if(prvi){
                    prvo = stanje;
                    prvi = false;
                }
                if(prvo.compareTo(stanje) > 0)
                    prvo = stanje;
            }
            if(ekvivaletna.contains(pocetnoStanje))
                pocetnoStanje = prvo;
            for(String prijelaz : skupPrijelaza.keySet()){
                if(ekvivaletna.contains(skupPrijelaza.get(prijelaz)))
                    skupPrijelaza.replace(prijelaz, prvo);
            }
            ispisStanja.add(prvo);
        }
        boolean prvi = true;
        for(String stanje : ispisStanja){
            if(stanje.isEmpty())
                continue;
            if(prvi){
                prvi = false;
                System.out.print(stanje);
                continue;
            }
            System.out.print( "," + stanje);
            }
        System.out.print("\n");
        boolean prvi1 = true;
        for (String simbol : skupSimbola){
            if(simbol.isEmpty())
                continue;
            if(prvi1){
                prvi1 = false;
                System.out.print(simbol);
                continue;
            }
            System.out.print( "," + simbol);
        }
        System.out.print("\n");
        boolean prvi2 = true;
        for(String stanje : ispisStanja){
            if(prihvatljivaStanja.contains(stanje)){
                if(stanje.isEmpty())
                    continue;
                if(prvi2){
                    prvi2 = false;
                    System.out.print(stanje);
                    continue;
                }
                System.out.print( "," + stanje);
            }

        }
        System.out.print("\n");
        System.out.println(pocetnoStanje);
        for (String prijelaz : skupPrijelaza.keySet()){
            if(ispisStanja.contains(prijelaz.split(",")[0]))
                System.out.println(prijelaz + "->" + skupPrijelaza.get(prijelaz));
        }








    }


}

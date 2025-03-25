import java.util.ArrayList;
import java.util.List;

public class playertester {
    public static void main(String[] args) {
        Player p1 = new PerfectInfoUCTPlayer("UCTPlayer");
        Player p2 = new HighLowPlayAI("HighLowPlayer");

        ArrayList<ArrayList<Player>> playerCombos = new ArrayList<>();

        playerCombos.add(new ArrayList<>(
            List.of(new PerfectInfoUCTPlayer("UCTPlayer"), 
            new PerfectInfoUCTPlayer("UCTPlayer"), 
            new PerfectInfoUCTPlayer("UCTPlayer"), 
            new HighLowPlayAI("HighLowPlayer"))));

        p2.points = 1;

        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p1.resetPlayer(), p1.resetPlayer(), p2.resetPlayer())));

        p2.points = 2;

        ArrayList<Player> combo1 = new ArrayList<>();
        combo1.add(p1);
        combo1.add(p1);
        combo1.add(p2);
        combo1.add(p1);
        playerCombos.add(combo1);

        p2.points = 3;
    }
}
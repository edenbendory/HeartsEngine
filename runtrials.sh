#!/bin/bash

javac *.java

> "output.log"

for i in {1..10}; do
    echo "Starting Trial $i" 
    java RunTwoPlayerGames >> "output.log" &
done

wait 
echo "All Trials Complete"

output_file="output.log"

# To process and compute averages
awk '
/HighLowPlayer:/ { highlow_sum += $2; highlow_count++ }
/UCTPlayer_HighLowPlayer:/     { uct_highlow_sum += $2; uct_highlow_count++ }
/RandomPlayer:/   { random_sum += $2; random_count++ }
/UCTPlayer_RandomPlayer:/     { uct_random_sum += $2; uct_random_count++ }
/MCTSPlayer:/     { mcts_sum += $2; mcts_count++ }
/UCTPlayer_MCTSPlayer:/     { uct_mcts_sum += $2; uct_mcts_count++ }
/LookAheadPlayer:/     { lookahead_sum += $2; lookahead_count++ }
/UCTPlayer_LookAheadPlayer:/     { uct_lookahead_sum += $2; uct_lookahead_count++ }
END {
    printf "HighLowPlayer Average: %.2f\n", highlow_sum / highlow_count;
    printf "UCTPlayer Average: %.2f\n", uct_highlow_sum / uct_highlow_count;
    printf"\n";

    printf "RandomPlayer Average: %.2f\n", random_sum / random_count;
    printf "UCTPlayer Average: %.2f\n", uct_random_sum / uct_random_count;
    printf"\n";

    printf "MCTSPlayer Average: %.2f\n", mcts_sum / mcts_count;
    printf "UCTPlayer Average: %.2f\n", uct_mcts_sum / uct_mcts_count;
    printf"\n";

    printf "LookAheadPlayer Average: %.2f\n", lookahead_sum / lookahead_count;
    printf "UCTPlayer Average: %.2f\n", uct_lookahead_sum / uct_lookahead_count;
    printf"\n";
}
' "$output_file"
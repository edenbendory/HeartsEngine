# ChatGPT was used to help compose this file 
#!/bin/bash

javac *.java

> "perfectInfoOutput.log"

for i in {1..10}; do
    echo "Starting Trial $i" 
    java RunTwoPlayerGames >> "perfectInfoOutput.log" &
done

wait 
echo "All Trials Complete"

output_file="perfectInfoOutput.log"

# To process and compute averages
awk '
/UCTPlayer:/ { uct_sum += $2; uct_count++ }
/PerfectInfoUCTPlayer_UCTPlayer:/     { perfectuct_uct_sum += $2; perfectuct_uct_count++ }
# /HighLowPlayer:/ { highlow_sum += $2; highlow_count++ }
# /PerfectInfoUCTPlayer_HighLowPlayer:/     { perfectuct_highlow_sum += $2; perfectuct_highlow_count++ }
# /RandomPlayer:/   { random_sum += $2; random_count++ }
# /PerfectInfoUCTPlayer_RandomPlayer:/     { perfectuct_random_sum += $2; perfectuct_random_count++ }
# /MCTSPlayer:/     { mcts_sum += $2; mcts_count++ }
# /PerfectInfoUCTPlayer_MCTSPlayer:/     { perfectuct_mcts_sum += $2; perfectuct_mcts_count++ }
END {
    printf "UCTPlayer Average: %.2f\n", uct_sum / uct_count;
    printf "PerfectInfoUCTPlayer Average: %.2f\n", perfectuct_uct_sum / perfectuct_uct_count;
    printf"\n";

    # printf "HighLowPlayer Average: %.2f\n", highlow_sum / highlow_count;
    # printf "PerfectInfoUCTPlayer Average: %.2f\n", perfectuct_highlow_sum / perfectuct_highlow_count;
    # printf"\n";

    # printf "RandomPlayer Average: %.2f\n", random_sum / random_count;
    # printf "PerfectInfoUCTPlayer Average: %.2f\n", perfectuct_random_sum / perfectuct_random_count;
    # printf"\n";

    # printf "MCTSPlayer Average: %.2f\n", mcts_sum / mcts_count;
    # printf "PerfectInfoUCTPlayer Average: %.2f\n", perfectuct_mcts_sum / perfectuct_mcts_count;
    # printf"\n";
}
' "$output_file"
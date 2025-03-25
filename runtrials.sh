#!/bin/bash

javac *.java

> "output.log"

for i in {1..10}; do
    echo "Starting Trial $i" 
    java RunTwoPlayerGames >> "output.log" &
done

wait 
echo "All Trials Complete"

# output_file="output.log"

# # To process and compute averages
# awk '
# /HighLowPlayer:/ { highlow_sum += $2; highlow_count++ }
# /UCTPlayer_HighLowPlayer:/     { uct_highlow_sum += $2; uct_highlow_count++ }
# /RandomPlayer:/   { random_sum += $2; random_count++ }
# /UCTPlayer_RandomPlayer:/     { uct_random_sum += $2; uct_random_count++ }
# /MCTSPlayer:/     { mcts_sum += $2; mcts_count++ }
# /UCTPlayer_MCTSPlayer:/     { uct_mcts_sum += $2; uct_mcts_count++ }
# # /LookAheadPlayer:/     { lookahead_sum += $2; lookahead_count++ }
# # /UCTPlayer_LookAheadPlayer:/     { uct_lookahead_sum += $2; uct_lookahead_count++ }
# END {
#     printf "HighLowPlayer Average: %.2f\n", highlow_sum / highlow_count;
#     printf "UCTPlayer Average: %.2f\n", uct_highlow_sum / uct_highlow_count;
#     printf"\n";

#     printf "RandomPlayer Average: %.2f\n", random_sum / random_count;
#     printf "UCTPlayer Average: %.2f\n", uct_random_sum / uct_random_count;
#     printf"\n";

#     printf "MCTSPlayer Average: %.2f\n", mcts_sum / mcts_count;
#     printf "UCTPlayer Average: %.2f\n", uct_mcts_sum / uct_mcts_count;
#     printf"\n";

#     # printf "LookAheadPlayer Average: %.2f\n", lookahead_sum / lookahead_count;
#     # printf "UCTPlayer Average: %.2f\n", uct_lookahead_sum / uct_lookahead_count;
#     # printf"\n";
# }
# ' "$output_file"


output_file="output.log"

# To process and compute averages and differences
awk '
/HighLowPlayer:/ { highlow_sum += $2; highlow_count++ }
/UCTPlayer_HighLowPlayer:/ { uct_highlow_sum += $2; uct_highlow_count++ }
/RandomPlayer:/ { random_sum += $2; random_count++ }
/UCTPlayer_RandomPlayer:/ { uct_random_sum += $2; uct_random_count++ }
/MCTSPlayer:/ { mcts_sum += $2; mcts_count++ }
/UCTPlayer_MCTSPlayer:/ { uct_mcts_sum += $2; uct_mcts_count++ }

# Capture differences
/^[A-Za-z]+_[A-Za-z]+_Difference:/ {
    key = $1;
    gsub(":", "", key);
    diff_map[key] += $2;
    diff_sq_map[key] += ($2)^2;
    count_map[key]++;
    diff_list[key, count_map[key]] = $2;
}

END {
    # Print averages
    printf "HighLowPlayer Average: %.2f\n", highlow_sum / highlow_count;
    printf "UCTPlayer Average: %.2f\n", uct_highlow_sum / uct_highlow_count;
    printf "\n";

    printf "RandomPlayer Average: %.2f\n", random_sum / random_count;
    printf "UCTPlayer Average: %.2f\n", uct_random_sum / uct_random_count;
    printf "\n";

    printf "MCTSPlayer Average: %.2f\n", mcts_sum / mcts_count;
    printf "UCTPlayer Average: %.2f\n", uct_mcts_sum / uct_mcts_count;
    printf "\n";

    # Ordered difference keys
    printf "=== Player Differences ===\n";

    diff_keys[1] = "UCTPlayer_HighLowPlayer_Difference";
    diff_keys[2] = "UCTPlayer_RandomPlayer_Difference";
    diff_keys[3] = "UCTPlayer_MCTSPlayer_Difference";

    for (dk = 1; dk <= 3; dk++) {
        k = diff_keys[dk];
        n = count_map[k];
        if (n == 0) continue;

        mean = diff_map[k] / n;
        stddev = sqrt(diff_sq_map[k]/n - mean^2);

        # Build sorted array for median
        split("", sorted);
        for (i = 1; i <= n; i++) {
            sorted[i] = diff_list[k, i];
        }
        for (i = 1; i <= n; i++) {
            for (j = i + 1; j <= n; j++) {
                if (sorted[i] > sorted[j]) {
                    tmp = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = tmp;
                }
            }
        }

        if (n % 2 == 1) {
            median = sorted[(n + 1) / 2];
        } else {
            median = (sorted[n / 2] + sorted[n / 2 + 1]) / 2;
        }

        # Pretty print
        split(k, parts, "_");
        p1 = parts[1];
        p2 = parts[2];

        printf "%s vs. %s Difference Stats - Avg: %.2f | Median: %.2f | Std Dev: %.2f\n", p1, p2, mean, median, stddev;
    }
}
' "$output_file"

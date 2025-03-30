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

# To process and compute averages and differences
awk '
/RandomPlayer:/ { random_sum += $2; random_count++ }
/HighLowPlayer_RandomPlayer:/ { highlow_random_sum += $2; highlow_random_count++ }
/MCTSPlayer:/ { mcts_sum += $2; mcts_count++ }
/HighLowPlayer_MCTSPlayer:/ { highlow_mcts_sum += $2; highlow_mcts_count++ }
/LowPlayer:/ { low_sum += $2; low_count++ }
/HighLowPlayer_LowPlayer:/ { highlow_low_sum += $2; highlow_low_count++ }
/LookAheadPlayer:/ { look_sum += $2; look_count++ }
/HighLowPlayer_LookAheadPlayer:/ { highlow_look_sum += $2; highlow_look_count++ }

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
    printf "RandomPlayer Average: %.2f\n", random_sum / random_count;
    printf "HighLowPlayer Average: %.2f\n", highlow_random_sum / highlow_random_count;
    printf "\n";

    printf "MCTSPlayer Average: %.2f\n", mcts_sum / mcts_count;
    printf "HighLowPlayer Average: %.2f\n", highlow_mcts_sum / highlow_mcts_count;
    printf "\n";

    printf "LowPlayer Average: %.2f\n", low_sum / low_count;
    printf "HighLowPlayer Average: %.2f\n", highlow_low_sum / highlow_low_count;
    printf "\n";

    printf "LookAheadPlayer Average: %.2f\n", look_sum / look_count;
    printf "HighLowPlayer Average: %.2f\n", highlow_look_sum / highlow_look_count;
    printf "\n";

    # Ordered difference keys
    printf "=== Player Differences ===\n";

    diff_keys[1] = "HighLowPlayer_RandomPlayer_Difference";
    diff_keys[2] = "HighLowPlayer_MCTSPlayer_Difference";
    diff_keys[3] = "HighLowPlayer_LowPlayer_Difference";
    diff_keys[4] = "HighLowPlayer_LookAheadPlayer_Difference";

    for (dk = 1; dk <= 4; dk++) {
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

# ChatGPT was used to help @edenbendory compose this file 
#!/bin/bash

javac *.java

> "output.log"

for i in {1..10}; do
    echo "Starting Trial $i" 
    java -ea RunTwoPlayerGames >> "output.log" &
done

wait 
echo "All Trials Complete"


output_file="output.log"

# To process and compute averages and differences
awk '
$1 == "HighLowPlayer:" { highlow_sum += $2; highlow_count++ }
$1 == "UCTPlayer_HighLowPlayer:" { uct_highlow_sum += $2; uct_highlow_count++ }

$1 == "RandomPlayer:" { random_sum += $2; random_count++ }
$1 == "UCTPlayer_RandomPlayer:" { uct_random_sum += $2; uct_random_count++ }

$1 == "MCTSPlayer:" { mcts_sum += $2; mcts_count++ }
$1 == "UCTPlayer_MCTSPlayer:" { uct_mcts_sum += $2; uct_mcts_count++ }

# Differences
/^[A-Za-z]+_[A-Za-z]+_Difference:/ {
    key = $1; gsub(":", "", key);
    n = ++count_map[key];
    diff_sum[key] += $2;
    diff_sq_sum[key] += $2^2;
    diff_values[key, n] = $2;
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

    printf "=== Player Differences ===\n";
    diff_keys[1] = "UCTPlayer_HighLowPlayer_Difference";
    diff_keys[2] = "UCTPlayer_RandomPlayer_Difference";
    diff_keys[3] = "UCTPlayer_MCTSPlayer_Difference";

    # Compute mean and median 
    for (dk = 1; dk <= 3; dk++) {
        key = diff_keys[dk];
        n = count_map[key];
        if (n == 0) continue;

        mean = diff_sum[key] / n;
        stddev = sqrt(diff_sq_sum[key] / n - mean^2);

        # Copy values to local array and clear it each time
        delete vals;
        for (i = 1; i <= n; i++) {
            vals[i] = diff_values[key, i];
        }

        # Sort (bubble sort)
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= n-i; j++) {
                if (vals[j] > vals[j+1]) {
                    temp = vals[j];
                    vals[j] = vals[j+1];
                    vals[j+1] = temp;
                }
            }
        }

        # Median
        if (n % 2 == 1) {
            median = vals[int((n + 1) / 2)];
        } else {
            median = (vals[n / 2] + vals[(n / 2) + 1]) / 2;
        }

        split(key, parts, "_");
        p1 = parts[1];
        p2 = parts[2];

        printf "%s vs. %s Difference Stats - Avg: %.2f | Median: %.2f | Std Dev: %.2f\n", p1, p2, mean, median, stddev;
    }
}


' "$output_file"
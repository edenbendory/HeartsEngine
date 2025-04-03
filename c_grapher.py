# ChatGPT was used to help compose this file
import matplotlib.pyplot as plt


def read_tricks_by_c(filename):
    """
    Reads simulation data from the file and groups tricks by each 'c=' value.
    Returns a dictionary: { 'c=0.0': [ (x_list, y_list), ... ], ... }
    """
    data_by_c = {}
    current_c = None
    current_x, current_y = [], []

    try:
        with open(filename, "r") as file:
            for line in file:
                stripped = line.strip()

                # Start of a new c= block
                if stripped.startswith("c="):
                    if current_c is not None and current_x:
                        # Save the last trick before switching c
                        if current_c not in data_by_c:
                            data_by_c[current_c] = []
                        data_by_c[current_c].append((current_x, current_y))
                        current_x, current_y = [], []
                    current_c = stripped
                    continue

                values = stripped.split()
                if len(values) < 2:
                    continue  # Skip malformed lines

                try:
                    x = float(values[0])
                    y = float(values[1])

                    if x == 0 and current_x:
                        # New trick starts, save the current one
                        if current_c not in data_by_c:
                            data_by_c[current_c] = []
                        data_by_c[current_c].append((current_x, current_y))
                        current_x, current_y = [], []

                    current_x.append(x)
                    current_y.append(y)
                except ValueError:
                    print(f"Skipping invalid line in {filename}: {stripped}")

        # Save the final trick
        if current_c and current_x:
            if current_c not in data_by_c:
                data_by_c[current_c] = []
            data_by_c[current_c].append((current_x, current_y))

    except FileNotFoundError:
        print(f"Error: File '{filename}' not found.")

    return data_by_c


def plot_tricks_by_c(data_by_c, y_label, title_prefix):
    """
    Plots one graph per c= value with multiple tricks overlaid.
    """
    num_c_values = len(data_by_c)
    if num_c_values == 0:
        print("No data to plot.")
        return

    fig, axes = plt.subplots(
        1, num_c_values, figsize=(6 * num_c_values, 5), squeeze=False
    )

    for idx, (c_value, tricks) in enumerate(sorted(data_by_c.items())):
        ax = axes[0][idx]
        for i, (x_vals, y_vals) in enumerate(tricks):
            if i > 11:
                break
            ax.plot(
                x_vals,
                y_vals,
                marker="o",
                linestyle="-",
                linewidth=1.5,
                markersize=2,
                label=f"Trick {i+1}",
            )
        ax.set_title(f"{title_prefix} - {c_value}")
        ax.set_xlabel("Simulation Number")
        ax.set_ylabel(y_label)
        ax.legend(fontsize="xx-small", loc="upper right")

    plt.tight_layout()
    plt.show()


# --- MAIN EXECUTION ---

# Plot for bestScore.log
score_data = read_tricks_by_c("bestScorewithC.log")
plot_tricks_by_c(score_data, y_label="Win Score", title_prefix="Best Score")

# Plot for bestChild.log
child_data = read_tricks_by_c("bestChild.log")
plot_tricks_by_c(child_data, y_label="Best Child", title_prefix="Best Child")

# import matplotlib.pyplot as plt

# # Read the file and parse data
# x, y = [], []
# with open("bestChild4.log", "r") as file:  # Use the correct file name
#     # with open("bestScore4.log", "r") as file:  # Use the correct file name
#     for line in file:
#         values = line.strip().split()
#         if len(values) < 2:
#             continue  # Skip lines that don't have both x and y
#         try:
#             x.append(float(values[0]))
#             y.append(float(values[1]))
#         except ValueError:
#             print(f"Skipping invalid line: {line.strip()}")

# # Check if we have valid data
# if not x or not y:
#     print("No valid data found.")
#     exit()

# # Plot the data with smaller dots
# plt.plot(
#     x, y, marker="o", linestyle="-", linewidth=1.5, markersize=2
# )  # Adjust markersize

# # Set labels and title
# plt.xlabel("Simulation Number")
# # plt.ylabel("Best Win Score")
# plt.ylabel("Best Child")
# # plt.title("Simulation 1000 Best Win Score = 13.39039039039039")
# plt.title("Simulation Number vs. Best Child")

# # Show the plot
# plt.show()


# Multiple curves
import matplotlib.pyplot as plt


def read_data(filename):
    """Reads simulation data from a file and returns a list of rounds."""
    rounds = []  # List to store multiple (x, y) rounds
    current_x, current_y = [], []

    try:
        with open(filename, "r") as file:
            for line in file:
                values = line.strip().split()
                if len(values) < 2:
                    continue  # Skip empty or malformed lines

                try:
                    x_val = float(values[0])
                    y_val = float(values[1])

                    # If x=0 and we already have some data, start a new round
                    if x_val == 0 and current_x:
                        rounds.append((current_x, current_y))  # Save previous round
                        current_x, current_y = [], []  # Reset for new round

                    current_x.append(x_val)
                    current_y.append(y_val)

                except ValueError:
                    print(f"Skipping invalid line in {filename}: {line.strip()}")

        # Don't forget to save the last round
        if current_x:
            rounds.append((current_x, current_y))

    except FileNotFoundError:
        print(f"Error: File '{filename}' not found.")

    return rounds


def plot_data(rounds, title, y_label):
    """Plots multiple rounds from a data file."""
    plt.figure(figsize=(8, 6))  # Create a new figure for each file
    for i, (x, y) in enumerate(rounds):
        plt.plot(
            x,
            y,
            marker="o",
            linestyle="-",
            linewidth=1.5,
            markersize=2,
            label=f"Trick {i+1}",
        )

    # Set labels and title
    plt.xlabel("Simulation Number")
    plt.ylabel(y_label)
    plt.title(title)

    # Move legend outside the plot
    plt.legend(loc="upper left", bbox_to_anchor=(1, 1))
    plt.tight_layout(rect=[0, 0, 0.85, 1])  # Adjust layout to fit legend

    # Show the plot
    plt.show()


# Read data from both files
best_child_rounds = read_data("bestChild.log")
best_score_rounds = read_data("bestScore.log")

# Plot data for both files
if best_child_rounds:
    plot_data(best_child_rounds, "Simulation Number vs. Best Child", "Best Child")

if best_score_rounds:
    plot_data(best_score_rounds, "Simulation Number vs. Best Win Score", "Win Score")

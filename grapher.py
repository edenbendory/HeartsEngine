import matplotlib.pyplot as plt

# Read the file and parse data
x, y = [], []
with open("bestChild4.log", "r") as file:  # Use the correct file name
    # with open("bestScore4.log", "r") as file:  # Use the correct file name
    for line in file:
        values = line.strip().split()
        if len(values) < 2:
            continue  # Skip lines that don't have both x and y
        try:
            x.append(float(values[0]))
            y.append(float(values[1]))
        except ValueError:
            print(f"Skipping invalid line: {line.strip()}")

# Check if we have valid data
if not x or not y:
    print("No valid data found.")
    exit()

# Plot the data with smaller dots
plt.plot(
    x, y, marker="o", linestyle="-", linewidth=1.5, markersize=2
)  # Adjust markersize

# Set labels and title
plt.xlabel("Simulation Number")
# plt.ylabel("Best Win Score")
plt.ylabel("Best Child")
# plt.title("Simulation 1000 Best Win Score = 13.39039039039039")
plt.title("Simulation Number vs. Best Child")

# Show the plot
plt.show()

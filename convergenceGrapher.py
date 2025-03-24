# ChatGPT was used to help compose this file 
import matplotlib.pyplot as plt


def plot_convergence_graph(
    score_filename="convergenceScore.log", child_filename="convergenceChild.log"
):
    """Reads convergence data from two files and plots graphs where:
    X = Trick #, Y = Simulation # of Convergence, with separate curves for different 'c' values.
    """

    def parse_file(filename):
        """Helper function to parse a convergence log file."""
        data = {}  # Dictionary to store curves based on 'c' values

        current_c = "Default"  # Default category if no 'c' value is encountered

        try:
            with open(filename, "r") as file:
                for line in file:
                    values = line.strip().split()

                    # Detect 'c=' lines and use them as new curve labels
                    if line.startswith("c="):
                        current_c = line.strip()  # Store c-value as key
                        if current_c not in data:
                            data[current_c] = ([], [])  # Initialize empty lists
                        continue

                    if len(values) < 3:
                        continue  # Skip malformed lines

                    try:
                        trick_number = int(values[1])  # Extract Trick #
                        convergence_point = int(
                            values[2]
                        )  # Extract Simulation # of Convergence

                        if current_c not in data:
                            data[current_c] = ([], [])  # Initialize if missing

                        data[current_c][0].append(trick_number)
                        data[current_c][1].append(convergence_point)

                    except ValueError:
                        print(f"Skipping invalid line in {filename}: {line.strip()}")

        except FileNotFoundError:
            print(f"Error: File '{filename}' not found.")

        return data

    # Parse data from both files
    score_data = parse_file(score_filename)
    child_data = parse_file(child_filename)

    # Create the first graph for convergenceScore.log
    plt.figure(figsize=(8, 6))
    for c_value, (tricks, convergence_points) in score_data.items():
        plt.plot(
            tricks,
            convergence_points,
            marker="o",
            linestyle="-",
            linewidth=1.5,
            markersize=5,
            label=c_value,
        )

    plt.xlabel("Trick #")
    plt.ylabel("Simulation # of Convergence")
    plt.title("Convergence on Win Score")
    plt.legend(title="c values")
    plt.show()

    # Create the second graph for convergenceChild.log
    plt.figure(figsize=(8, 6))
    for c_value, (tricks, convergence_points) in child_data.items():
        plt.plot(
            tricks,
            convergence_points,
            marker="o",
            linestyle="-",
            linewidth=1.5,
            markersize=5,
            label=c_value,
        )

    plt.xlabel("Trick #")
    plt.ylabel("Simulation # of Convergence")
    plt.title("Convergence on Best Child")
    plt.legend(title="c values")
    plt.show()


# Generate the graphs from "convergenceScore.log" and "convergenceChild.log"
plot_convergence_graph()

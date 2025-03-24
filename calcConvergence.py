# ChatGPT was used to help compose this file 
def process_score_log(input_filename, output_filename, tolerance=0.5):
    """
    Processes a log file (bestScore.log) to find convergence points where the value is
    within 'tolerance' of the value at data point 999.
    Saves results to the specified output file.
    """
    trick_number = 0
    real_scores = {}

    try:
        with open(input_filename, "r") as file:
            lines = file.readlines()

        # First pass: Identify all real_score values for each trick (999 values)
        for line in lines:
            values = line.strip().split()
            if len(values) < 2:
                continue

            try:
                data_point = int(values[0])
                value = float(values[1])

                if data_point == 0:
                    trick_number += 1
                    continue

                if data_point == 999:
                    real_scores[trick_number] = value

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        # Second pass: Find convergence points
        trick_number = 0
        convergence_point = -1
        convergence_points = {}

        for line in lines:
            values = line.strip().split()
            if len(values) < 2:
                continue

            try:
                data_point = int(values[0])
                value = float(values[1])

                if data_point == 0:
                    trick_number += 1
                    convergence_point = -1

                if trick_number not in real_scores:
                    continue

                if data_point == 999:
                    convergence_points[trick_number] = convergence_point
                    continue

                difference = abs(value - real_scores[trick_number])

                if difference > tolerance:
                    convergence_point = -1
                elif difference <= tolerance and convergence_point == -1:
                    convergence_point = data_point

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        with open(output_filename, "a") as out_file:
            for trick, convergence in convergence_points.items():
                out_file.write(f"Trick {trick} {convergence}\n")

        print(f"Convergence results saved to {output_filename}")

    except FileNotFoundError:
        print(f"Error: File '{input_filename}' not found.")


def process_child_log(input_filename, output_filename):
    """
    Processes a log file (bestChild.log) to find convergence points where the value
    is EXACTLY equal to the value at data point 999.
    Saves results to the specified output file.
    """
    trick_number = 0
    real_scores = {}

    try:
        with open(input_filename, "r") as file:
            lines = file.readlines()

        # First pass: Identify all real_score values for each trick (999 values)
        for line in lines:
            values = line.strip().split()
            if len(values) < 2:
                continue

            try:
                data_point = int(values[0])
                value = float(values[1])

                if data_point == 0:
                    trick_number += 1
                    continue

                if data_point == 999:
                    real_scores[trick_number] = value

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        # Second pass: Find convergence points
        trick_number = 0
        convergence_point = -1
        convergence_points = {}

        for line in lines:
            values = line.strip().split()
            if len(values) < 2:
                continue

            try:
                data_point = int(values[0])
                value = float(values[1])

                if data_point == 0:
                    trick_number += 1
                    convergence_point = -1

                if trick_number not in real_scores:
                    continue

                if data_point == 999:
                    convergence_points[trick_number] = convergence_point
                    continue

                if value == real_scores[trick_number] and convergence_point == -1:
                    convergence_point = data_point

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        with open(output_filename, "a") as out_file:
            for trick, convergence in convergence_points.items():
                out_file.write(f"Trick {trick} {convergence}\n")

        print(f"Convergence results saved to {output_filename}")

    except FileNotFoundError:
        print(f"Error: File '{input_filename}' not found.")


# Run the function on bestScore.log and save to convergenceScore.log
process_score_log("bestScore.log", "convergenceScore.log", tolerance=0.5)

# Run the function on bestChild.log and save to convergenceChild.log
process_child_log("bestChild.log", "convergenceChild.log")

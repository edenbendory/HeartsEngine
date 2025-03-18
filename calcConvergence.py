def process_best_score_log(input_filename, output_filename):
    """
    Parses bestScore.log, calculates the convergencePoint for each section,
    and writes results to convergencePoints.log.
    """
    convergence_results = []  # Stores results to write to the output file

    try:
        with open(input_filename, "r") as file:
            lines = file.readlines()

        current_convergence_point = -1
        real_score = None
        processing_section = False  # Track if we're in a valid section

        for i, line in enumerate(lines):
            values = line.strip().split()
            if len(values) < 2:
                continue  # Skip empty/malformed lines

            try:
                data_point = int(values[0])
                value = float(values[1])

                # If we hit 999, set realScore and reset processing state
                if data_point == 999:
                    real_score = value
                    processing_section = True  # Start tracking differences
                    current_convergence_point = -1  # Reset for new section
                    continue

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        for i, line in enumerate(lines):
            values = line.strip().split()
            if len(values) < 2:
                continue  # Skip empty/malformed lines

            try:
                data_point = int(values[0])
                value = float(values[1])

                # If we hit 999, set realScore and reset processing state
                if data_point == 999:
                    real_score = value
                    processing_section = True  # Start tracking differences
                    current_convergence_point = -1  # Reset for new section
                    continue

                # If we're processing a section (i.e., after seeing 999)
                if processing_section and real_score is not None:
                    difference = abs(value - real_score)

                    if difference > 0.5:
                        current_convergence_point = -1  # Reset if difference exceeds 0.5
                    elif difference <= 0.5 and current_convergence_point == -1:
                        current_convergence_point = data_point  # Store first valid convergence point

            except ValueError:
                print(f"Skipping invalid line in {input_filename}: {line.strip()}")

        # Write results to file
        with open(output_filename, "w") as out_file:
            out_file.write(f"{input_filename} {current_convergence_point}\n")

        print(f"Convergence results saved to {output_filename}")

    except FileNotFoundError:
        print(f"Error: File '{input_filename}' not found.")


# Run the function on bestScore.log and save to convergencePoints.log
process_best_score_log("bestScore.log", "convergencePoints.log")

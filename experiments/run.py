import glob
import subprocess

STDLIB_LINES = 33689
ITERATIONS = 3

def average(lst):
    return float(sum(lst)) / float(len(lst))

def sec(x):
    return round(float(x) / 1_000_000_000, 1)

for pkg in glob.glob("*.fpkg"):
    print(pkg, end='')

    # Baseline
    times1 = []
    for i in range(ITERATIONS):
        run1 = subprocess.run(["java", "-jar", "flix.jar", "--Xno-optimizer", "--Xno-reifyeff", pkg], capture_output=True, text=True)
        _, time1, _, _ = run1.stdout.split(",")
        times1.append(int(time1))
    lines1, _, totalBytes1, totalClasses1 = run1.stdout.split(",")
    lines1 = int(lines1) - STDLIB_LINES

    # Extended
    times2 = []
    for i in range(ITERATIONS):
        run2 = subprocess.run(["java", "-jar", "flix.jar", "--Xno-optimizer", pkg], capture_output=True, text=True)
        _, time2, _, _ = run1.stdout.split(",")
        times2.append(int(time2))
    _, _, totalBytes2, totalClasses2 = run2.stdout.split(",")

    avgTime1 = average(times1)
    avgTime2 = average(times2)
    timeRatio = round(((100 * avgTime1) / avgTime2) - 100)
    classRatio = round((100 * int(totalClasses2)) / int(totalClasses1) - 100)
    bytesRatio = round((100 * int(totalBytes2)) / int(totalBytes1) - 100)

    print(f' & {int(lines1)}', end='')
    print(f' & {sec(avgTime1)}s', end='')
    print(f' & {int(totalBytes1):,}', end='')
    print(f' & {int(totalClasses1):,}', end='')
    print(f' & {sec(avgTime2)}s (+{timeRatio}\%)', end='')
    print(f' & {int(totalBytes2):,} (+{bytesRatio}\%)', end='')
    print(f' & {int(totalClasses2):,} (+{classRatio}\%)', end='')
    print(" \\\\")

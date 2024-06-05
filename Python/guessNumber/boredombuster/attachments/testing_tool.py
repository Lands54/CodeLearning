#!/usr/bin/env python3
#
# Testing tool for the Boredom Buster problem
#
# Usage:
#
#   python3 testing_tool.py [-h] [-f inputfile] [-n N] [-q] program
#
# If neither the '-f' nor the '-n' parameter is specified, the sample is used.
# To run on a random test case with n = N use parameter '-n N'.
# Otherwise, an input file is needed.
# The first line should be the seed for the RNG.
# Then follows a line with the number of cards (an even number).
# Then come the cards.
# For example:
#
# 12324
# 6
# 3 2 2 1 3 1
#
# If you have a Java solution that you would run using
# "java MyClass", you could invoke the testing tool with:
#
#   python3 testing_tool.py java MyClass
#
# If you have a Python solution that you would run using
# "python solution.py", you could invoke the testing tool with:
#
#   python3 testing_tool.py python solution.py
#
# If you have a C++ solution stored in a file called "sol.cpp",
# you must first compile using "g++ sol.cpp -o sol" and then
# invoke the testing tool with:
#  
#   python3 testing_tool.py ./sol
#
# If you want, for example, to test your solution on a random test case
# with the largest legal n and only print the number of guesses your
# submission used you can use:
#
#   python3 testing_tool.py -q -n -1 ./sol
#
# The tool is provided as-is, and you should feel free to make
# whatever alterations or augmentations you like to it.
#
# The tool attempts to detect and report common errors, but it
# is not guaranteed that a program that passes the testing tool
# will be accepted.
#
# On windows you may need to change '/' to '\' in the above examples.
#
import argparse
import subprocess
import sys
import random

def write(p, line):
    assert p.poll() is None, 'Program terminated early'
    if not args.quiet: print('Write: {}'.format(line), flush=True)
    p.stdin.write('{}\n'.format(line))
    p.stdin.flush()

def read(p):
    assert p.poll() is None, 'Program terminated early'
    line = p.stdout.readline().strip()
    assert line != '', 'Read empty line or closed output pipe'
    if not args.quiet: print('Read: %s' % line, flush=True)
    return line

def wrong_answer(p, msg):
    # this kills the program.
    p.kill()
    print("Wrong answer:", msg)
    sys.exit(1)
    

parser = argparse.ArgumentParser(description='Testing tool for the Boredom Buster problem.')
parser.add_argument('-f', dest='inputfile', metavar='inputfile', default=None, type=argparse.FileType('r'),
                    help='Custom input file (defaults to sample 1).')
parser.add_argument('-n', dest='random', metavar='N', type=int,
                    help='Runs the submission on a random test case with N cards (remember that N must be even). This overwrites the -f flag.')
parser.add_argument('-q', action='store_true', dest='quiet', default=False,
                    help='Only print the number of moves required.')
parser.add_argument('program', nargs='+', help='Your solution')
args = parser.parse_args()
q = 0
if args.random is not None:
    s = random.randint(1, 10**9)
    random.seed(s)
    n = args.random
    if n == -1: n = 10**5
    assert n%2 == 0, 'The number of cards must be even.'
    a = [i//2 + 1 for i in range(n)]
    random.shuffle(a)
elif args.inputfile is not None:
    # Read the input file
    with args.inputfile as f:
        try:
            s = int(f.readline())
        except:
            assert False, "The seed in the input must be an integer."
        random.seed(s)
        try:
            n = int(f.readline())
        except:
            assert False, "The second line of the input must contain an integer."
        assert 2 <= n <= 10**5, "The integer n must be 2 <= n <= 10^5."
        assert n%2 == 0, "The integer n must be even."
        try:
            a = list(map(int, f.readline().split()))
        except:
            assert False, "The third line of the input must contain only integers."
        assert len(a) == n, "The integer n must match the number of integers given."
        for i, e in enumerate(sorted(a)):
            assert i//2 == e - 1, "Each 1, 2, ..., n must appear precisely twice in the input."
        assert f.readline() == '', 'Extra data at end of input file.'
else:
    random.seed(12324)
    n = 6
    a = [3, 2, 2, 1, 3, 1]
with subprocess.Popen(" ".join(args.program), shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, universal_newlines=True) as p:
    try:
        write(p, n)
        while True:
            line = read(p)
            if line == "": wrong_answer(p, "Command must contain at least one character.")
            if line[0] == '!': break;
            if line[0] != '?': wrong_answer(p, "Command must start with '?' or '!'.")
            try:
                x, y = map(int, line[2:].split())
            except:
                wrong_answer(p, "Queries should be two integers.")
            if x == y: wrong_answer(p, "Queried indices must differ.")
            if x <= 0 or y <= 0 or x > n or y > n: wrong_answer(p, "Queried indices must be on range.")
            x -= 1
            y -= 1
            if random.randint(0, 1) == 0: a[x], a[y] = a[y], a[x]
            write(p, str(a[x]) + ' ' + str(a[y]))
            if random.randint(0, 1) == 0: a[x], a[y] = a[y], a[x]
            q += 1
        try:
            l = list(map(int, line[2:].split()))
        except:
            wrong_answer(p, "Answer should be only integers.")
        if len(l) != n: wrong_answer(p, "Answer must contain n numbers.")
        for i in range(n):
            if a[i] != l[i]: wrong_answer(p, "Answer must match cards on the table.")
    except:
        sys.stdout.flush()
        sys.exit(1)
    if not args.quiet: sys.stdout.write('Total number of guesses: {}\n'.format(q))
    else: sys.stdout.write('{}\n'.format(q))
    if not args.quiet: sys.stdout.write('exit code: {}\n'.format(p.wait()))
    sys.stdout.flush()


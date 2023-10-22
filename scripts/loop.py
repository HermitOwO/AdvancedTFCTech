import os

cmd = "python obj_simplify.py"

for root, dirs, files in os.walk('./', topdown=False):
    for file in files:
        os.system(" ".join([cmd, file]))

#!/usr/bin/env python3
import sys

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    # Nettoyage simple : lettres seulement + minuscule
    words = line.lower().replace("[^a-zàâéèêëîïôùûüç ]", " ").split()
    for word in words:
        if word:
            print(f"{word}\t1")

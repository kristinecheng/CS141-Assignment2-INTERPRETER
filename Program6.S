set 0, 2
jumpt 6, D[D[0]] != 0
set 1, 2 * D[0]
set D[1], 1
set 1, D[1] + D[0]
jumpt 3, D[1] < 100
set 0, D[0] + 1
jumpt 1, D[0] < 50
halt

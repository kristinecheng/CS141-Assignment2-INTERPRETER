set 0, 1
set 1, 2
set write, D[1]
set D[1], D[0]
set 0, D[0] + 1
set 1, D[1] * 2
jumpt 2, D[1] < 100
halt

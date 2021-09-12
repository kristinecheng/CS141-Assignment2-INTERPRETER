set 0, read
set 1, read
jumpt 8, D[0] == D[1]
jumpt 6, D[0] < D[1]
set 0, D[0] - D[1]
jump 7
set 1, D[1] - D[0]
jump 2
set write, D[0]
halt
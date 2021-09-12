set 0, 2
set 1, 1
set D[0], D[1]
set 0, D[0] + 1
set 1, (2 * D[1]) % 13
jumpt 2, D[0] < 100
set 0, 2
set 1, D[0] + 1
jumpt 15, D[1] >= 100
jumpt 13, D[D[0]] <= D[D[1]]
set D[0], D[D[0]] + D[D[1]]
set D[1], D[D[0]] - D[D[1]]
set D[0], D[D[0]] - D[D[1]]
set 1, D[1] + 1
jump 8
set 0, D[0] + 1
jumpt 7, D[0] < 100
halt 
